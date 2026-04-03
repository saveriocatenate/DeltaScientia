package it.deltascientia.dto;

import lombok.Builder;

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
public record ExperimentVariableSummary(
        Long id,
        String name,
        String unitOfMeasure,
        String dataType,
        String description
) {}
