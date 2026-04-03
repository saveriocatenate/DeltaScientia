package it.deltascientia.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

/**
 * Request payload for creating a new trial within an experiment.
 *
 * @param trialNumber  optional sequential number; auto-assigned when null
 * @param label        optional human-readable label
 * @param notes        optional free-form observations
 * @param executionDate when the trial was executed
 * @param values       measured values for experiment variables
 */
@Builder
public record TrialCreateRequest(
        Integer trialNumber,
        String label,
        String notes,
        Instant executionDate,
        List<TrialValueRequest> values
) {
    /**
     * Measured value for a single variable within a trial.
     *
     * @param variableId    the variable to record the value for
     * @param valueText     textual value
     * @param valueNumeric  numeric value for quantitative measurements
     * @param valueLongText long-form observations
     */
    public record TrialValueRequest(
            Long variableId,
            String valueText,
            Double valueNumeric,
            String valueLongText
    ) {}
}
