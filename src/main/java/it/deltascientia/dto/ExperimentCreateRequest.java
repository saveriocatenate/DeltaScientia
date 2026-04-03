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
}
