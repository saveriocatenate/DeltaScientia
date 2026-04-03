package it.deltascientia.dto;

import lombok.Builder;

/**
 * Variable definition for experiment creation.
 * Exactly one of {@code typeName} or {@code name} must be provided:
 * <ul>
 *   <li>{@code typeName} — references an existing catalog type (case-insensitive match)</li>
 *   <li>{@code name} — defines a new type with optional metadata, added to the catalog</li>
 * </ul>
 *
 * @param typeName      name of an existing variable type in the catalog
 * @param name          name for a new custom type (added to catalog)
 * @param unitOfMeasure unit of measurement for the new type (only when creating)
 * @param dataType      data type for the new type (only when creating, defaults to "TEXT")
 * @param description   description for the new type (only when creating)
 */
@Builder
public record VariableRequest(String typeName, String name,
                              String unitOfMeasure, String dataType, String description) {
}
