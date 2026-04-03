package it.deltascientia.service;

import it.deltascientia.dto.TrialSearchRequest;
import it.deltascientia.dto.VariableValueFilter;
import it.deltascientia.model.Trial;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Static factory for JPA {@link Specification} predicates used in trial filtering.
 * <p>
 * Converts a {@link TrialSearchRequest} DTO into a tree of composable JPA
 * Criteria API predicates, all combined with {@code AND}. This is the
 * translation layer between the frontend's structured search API and the
 * database query engine.
 * <p>
 * The join path traversed by {@code byVariableValues} is:
 * {@code Trial → trialValue → Variable → VariableType}, matching against
 * {@code trialValue.value_numeric}.
 */
public final class TrialSpecification {

    private TrialSpecification() {
    }

    /**
     * Builds a compound specification for filtering trials within an experiment.
     * <p>
     * Each non-null filter field in the request adds an additional {@code AND}
     * predicate. The experiment constraint is always applied first to scope the
     * query to a single experiment's trials.
     *
     * @param experimentId the parent experiment to constrain the search to
     * @param request      the search criteria (may have null/empty fields)
     * @return a specification that can be passed to {@code TrialRepository.findAll}
     */
    public static Specification<Trial> matchesFilters(Long experimentId, TrialSearchRequest request) {
        // Always constrain to the parent experiment
        Specification<Trial> spec = byExperimentId(experimentId);

        if (request == null) {
            return spec;
        }

        // Partial text match on trial label (case-insensitive)
        if (request.label() != null && !request.label().isBlank()) {
            spec = spec.and(byLabel(request.label()));
        }

        // Execution date range: inclusive on both bounds
        if (request.fromDate() != null) {
            spec = spec.and(executionDateFrom(request.fromDate()));
        }

        if (request.toDate() != null) {
            spec = spec.and(executionDateTo(request.toDate()));
        }

        // Composite value filters across multiple variable types
        if (request.values() != null && !request.values().isEmpty()) {
            spec = spec.and(byVariableValues(request.values()));
        }

        return spec;
    }

    /**
     * Constrains results to a single experiment.
     */
    private static Specification<Trial> byExperimentId(Long experimentId) {
        return (root, query, cb) -> cb.equal(root.get("experiment").get("id"), experimentId);
    }

    /**
     * Performs a case-insensitive "LIKE %label%" match on the trial label.
     */
    private static Specification<Trial> byLabel(String label) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("label")), "%" + label.toLowerCase() + "%");
    }

    /**
     * Filters trials executed on or after the given instant (inclusive lower bound).
     */
    private static Specification<Trial> executionDateFrom(Instant fromDate) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("executionDate"), fromDate);
    }

    /**
     * Filters trials executed on or before the given instant (inclusive upper bound).
     */
    private static Specification<Trial> executionDateTo(Instant toDate) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("executionDate"), toDate);
    }

    /**
     * Builds composite value filters by creating an inner join per filter,
     * all AND'ed together so the trial must satisfy every value constraint.
     * <p>
     * For each {@link VariableValueFilter}, the method joins against:
     * <pre>
     *   Trial → trialValue → Variable → VariableType
     * </pre>
     * When {@code exactValue} is set, it matches precisely. Otherwise,
     * {@code minValue} and {@code maxValue} define an inclusive range.
     * <p>
     * Multiple filters produce duplicate rows via SQL joins, so
     * {@code query.distinct(true)} is applied to de-duplicate.
     *
     * @param filters list of per-variable-type value constraints
     * @return a specification that requires all filters to match
     */
    private static Specification<Trial> byVariableValues(List<VariableValueFilter> filters) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            for (VariableValueFilter filter : filters) {
                // Separate join per filter — each one represents an independent join condition
                Join<Object, Object> valuesJoin = root.join("values");
                Join<Object, Object> variableJoin = valuesJoin.join("variable");
                Join<Object, Object> variableTypeJoin = variableJoin.join("variableType");

                // Match the specific variable type
                predicates.add(cb.equal(variableTypeJoin.get("id"), filter.variableTypeId()));

                // Exact match, or range filter on value_numeric
                if (filter.exactValue() != null) {
                    predicates.add(cb.equal(valuesJoin.get("valueNumeric"), filter.exactValue()));
                } else {
                    if (filter.minValue() != null) {
                        predicates.add(cb.greaterThanOrEqualTo(valuesJoin.get("valueNumeric"), filter.minValue()));
                    }
                    if (filter.maxValue() != null) {
                        predicates.add(cb.lessThanOrEqualTo(valuesJoin.get("valueNumeric"), filter.maxValue()));
                    }
                }
            }

            // Safety: no filters means no restriction
            if (predicates.isEmpty()) {
                return cb.conjunction();
            }

            // De-duplicate results when multiple inner joins match the same trial
            if (query != null) {
                query.distinct(true);
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
