package it.deltascientia.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

/**
 * Response payload for experiment retrieval.
 * Contains the experiment details along with its variables and trials.
 *
 * @param id          experiment database identifier
 * @param name        experiment name
 * @param description purpose and scope
 * @param category    categorisation tag
 * @param status      lifecycle state
 * @param createdBy   creator username
 * @param notes       free-form notes
 * @param createdAt   row creation timestamp
 * @param updatedAt   last update timestamp
 * @param variables   list of variable definitions
 * @param trials      list of trial records
 */
@Builder
public record ExperimentResponse(
        Long id,
        String name,
        String description,
        String category,
        String status,
        String createdBy,
        String notes,
        Instant createdAt,
        Instant updatedAt,
        List<VariableSummary> variables,
        List<TrialSummary> trials
) {
    /**
     * Summarised variable included in an experiment response.
     *
     * @param id            variable database identifier
     * @param name          variable name
     * @param unitOfMeasure measurement unit
     * @param dataType      declared type (e.g. "NUMERIC")
     * @param description   variable description
     */
    @Builder
    public record VariableSummary(
            Long id,
            String name,
            String unitOfMeasure,
            String dataType,
            String description
    ) {}

    /**
     * Summarised trial included in an experiment response.
     *
     * @param id            trial database identifier
     * @param trialNumber   sequential trial number
     * @param label         trial display label
     * @param notes         trial notes
     * @param executionDate when the trial was executed
     */
    @Builder
    public record TrialSummary(
            Long id,
            Integer trialNumber,
            String label,
            String notes,
            Instant executionDate
    ) {}
}