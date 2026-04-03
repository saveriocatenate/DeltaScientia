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
 * Produces composable criteria that can be combined with {@code AND}.
 */
public final class TrialSpecification {

    private TrialSpecification() {
    }

    /**
     * Builds a compound specification for filtering trials within an experiment.
     *
     * @param experimentId the parent experiment to constrain the search to
     * @param request      the search criteria (may have null/empty fields)
     * @return a specification that can be passed to {@code TrialRepository.findAll}
     */
    public static Specification<Trial> matchesFilters(Long experimentId, TrialSearchRequest request) {
        Specification<Trial> spec = byExperimentId(experimentId);

        if (request == null) {
            return spec;
        }

        if (request.label() != null && !request.label().isBlank()) {
            spec = spec.and(byLabel(request.label()));
        }

        if (request.fromDate() != null) {
            spec = spec.and(executionDateFrom(request.fromDate()));
        }

        if (request.toDate() != null) {
            spec = spec.and(executionDateTo(request.toDate()));
        }

        if (request.values() != null && !request.values().isEmpty()) {
            spec = spec.and(byVariableValues(request.values()));
        }

        return spec;
    }

    private static Specification<Trial> byExperimentId(Long experimentId) {
        return (root, query, cb) -> cb.equal(root.get("experiment").get("id"), experimentId);
    }

    private static Specification<Trial> byLabel(String label) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("label")), "%" + label.toLowerCase() + "%");
    }

    private static Specification<Trial> executionDateFrom(Instant fromDate) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("executionDate"), fromDate);
    }

    private static Specification<Trial> executionDateTo(Instant toDate) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("executionDate"), toDate);
    }

    /**
     * Builds composite value filters: one inner join per variable filter,
     * AND'ed together so the trial must satisfy all value constraints.
     */
    private static Specification<Trial> byVariableValues(List<VariableValueFilter> filters) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            for (VariableValueFilter filter : filters) {
                Join<Object, Object> valuesJoin = root.join("values");
                Join<Object, Object> variableJoin = valuesJoin.join("variable");
                Join<Object, Object> variableTypeJoin = variableJoin.join("variableType");

                predicates.add(cb.equal(variableTypeJoin.get("id"), filter.variableTypeId()));

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

            // De-duplicate results when multiple joins match
            if (predicates.isEmpty()) {
                return cb.conjunction();
            }
            if (query != null) {
                query.distinct(true);
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
