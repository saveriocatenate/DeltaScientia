package it.deltascientia.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

/**
 * Response payload for a single trial with its measured values.
 *
 * @param id            trial database identifier
 * @param experimentId  parent experiment ID
 * @param trialNumber   sequential trial number
 * @param label         trial display label
 * @param notes         free-form observations
 * @param executionDate when the trial was executed
 * @param createdAt     creation timestamp
 * @param values        measured values for each variable
 */
@Builder
public record TrialResponse(
        Long id,
        Long experimentId,
        Integer trialNumber,
        String label,
        String notes,
        Instant executionDate,
        Instant createdAt,
        List<ValueSummary> values
) {
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
    public record ValueSummary(
            Long id,
            Long variableId,
            String variableName,
            String valueText,
            Double valueNumeric,
            String valueLongText
    ) {}
}
