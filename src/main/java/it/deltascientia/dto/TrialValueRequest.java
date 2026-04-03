package it.deltascientia.dto;

import lombok.Builder;

/**
 * Measured value for a single variable within a trial.
 *
 * @param variableId    the variable to record the value for
 * @param valueText     textual value
 * @param valueNumeric  numeric value for quantitative measurements
 * @param valueLongText long-form observations
 */
@Builder
public record TrialValueRequest(
        Long variableId,
        String valueText,
        Double valueNumeric,
        String valueLongText
) {}
