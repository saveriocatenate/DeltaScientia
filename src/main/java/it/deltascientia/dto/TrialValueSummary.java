package it.deltascientia.dto;

import lombok.Builder;

/**
 * Summary of a single measured value within a trial.
 *
 * @param id            value database identifier
 * @param variableId    the variable this value belongs to
 * @param variableName  the variable type name
 * @param valueText     textual value
 * @param valueNumeric  numeric value
 * @param valueLongText long-form observations
 */
@Builder
public record TrialValueSummary(
        Long id,
        Long variableId,
        String variableName,
        String valueText,
        Double valueNumeric,
        String valueLongText
) {}
