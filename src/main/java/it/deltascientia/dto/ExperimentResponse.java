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
        List<ExperimentVariableSummary> variables,
        List<ExperimentTrialSummary> trials
) {}
