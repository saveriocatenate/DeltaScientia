package it.deltascientia.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Search criteria for finding trials within an experiment.
 * Supports composite filtering on metadata and multiple variable values.
 *
 * @param label     partial label match (case-insensitive) when provided
 * @param fromDate  inclusive start of execution date range
 * @param toDate    inclusive end of execution date range
 * @param values    composite value filters on multiple variables
 */
@Builder
public record TrialSearchRequest(
        String label,
        Instant fromDate,
        Instant toDate,
        List<VariableValueFilter> values
) {
    /** Canonical constructor defaulting values list. */
    public TrialSearchRequest(String label, Instant fromDate, Instant toDate, List<VariableValueFilter> values) {
        this.label = label;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.values = values != null ? values : new ArrayList<>();
    }
}
