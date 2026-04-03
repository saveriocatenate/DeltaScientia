package it.deltascientia.dto;

import lombok.Builder;

/**
 * Filter criteria for a single variable's measured values.
 * At least one of {@code exactValue}, {@code minValue} or {@code maxValue} should be set.
 *
 * @param variableTypeId the variable type to filter on
 * @param exactValue     exact numeric value match (when set, min/max are ignored)
 * @param minValue       inclusive lower bound (requires variableTypeId)
 * @param maxValue       inclusive upper bound (requires variableTypeId)
 */
@Builder
public record VariableValueFilter(
        Long variableTypeId,
        Double exactValue,
        Double minValue,
        Double maxValue
) {}
