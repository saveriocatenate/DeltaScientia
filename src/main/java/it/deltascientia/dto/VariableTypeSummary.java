package it.deltascientia.dto;

import lombok.Builder;

import java.time.Instant;

/**
 * Summary of a single variable type.
 *
 * @param id            type database identifier
 * @param name          type name
 * @param unitOfMeasure unit of measurement
 * @param dataType      data type
 * @param description   type description
 * @param isCustom      whether user-defined
 * @param createdAt     creation timestamp
 */
@Builder
public record VariableTypeSummary(
        Long id,
        String name,
        String unitOfMeasure,
        String dataType,
        String description,
        Boolean isCustom,
        Instant createdAt
) {}
