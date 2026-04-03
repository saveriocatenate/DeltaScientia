package it.deltascientia.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

/**
 * Request payload for creating a new experiment.
 * The name field is required. Variables may reference an existing
 * {@code VariableType} by name or define a new one organically.
 */
@Builder
public record ExperimentCreateRequest(
        @NotBlank String name,
        String description,
        String category,
        String status,
        String createdBy,
        String notes,
        List<VariableRequest> variables
) {
    /** Canonical constructor providing a default empty variable list. */
    public ExperimentCreateRequest(String name, String description, String category,
                                   String status, String createdBy, String notes,
                                   List<VariableRequest> variables) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.status = status;
        this.createdBy = createdBy;
        this.notes = notes;
        this.variables = variables != null ? variables : new ArrayList<>();
    }

    /**
     * Variable definition for inline creation.
     * Exactly one of {@code typeName} or {@code name} must be provided:
     * <ul>
     *   <li>{@code typeName} — references an existing catalog type (case-insensitive match)</li>
     *   <li>{@code name} — defines a new type, which will be added to the catalog</li>
     * </ul>
     *
     * @param typeName      name of an existing variable type in the catalog
     * @param name          name for a new custom type (seeded automatically)
     * @param unitOfMeasure unit override (uses catalog default when null and typeName is set)
     * @param dataType      type override (uses catalog default when null and typeName is set)
     * @param description   description override (uses catalog default when null and typeName is set)
     */
    public record VariableRequest(
            String typeName,
            String name,
            String unitOfMeasure,
            String dataType,
            String description
    ) {
    }
}
