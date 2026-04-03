package it.deltascientia.dto;

import lombok.Builder;

import java.time.Instant;

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
public record ExperimentTrialSummary(
        Long id,
        Integer trialNumber,
        String label,
        String notes,
        Instant executionDate
) {}
