package it.deltascientia.mapper;

import it.deltascientia.dto.ExperimentCreateRequest;
import it.deltascientia.dto.ExperimentResponse;
import it.deltascientia.dto.ExperimentTrialSummary;
import it.deltascientia.dto.ExperimentVariableSummary;
import it.deltascientia.model.Experiment;
import it.deltascientia.model.Variable;
import it.deltascientia.service.ResolvedVariable;

import java.util.List;

/**
 * Mapper responsible for converting between Experiment entities and DTOs.
 */
public final class ExperimentMapper {

    private ExperimentMapper() {
        throw new UnsupportedOperationException("ExperimentMapper is a static utility class and must not be instantiated");
    }

    /**
     * Converts a creation request into a fully wired Experiment entity,
     * using pre-resolved VariableType references from the service layer.
     *
     * @param request          the creation request payload
     * @param resolvedVariables variable types already resolved by the service
     * @return a new Experiment entity ready to be persisted
     */
    public static Experiment toEntity(ExperimentCreateRequest request,
                              List<ResolvedVariable> resolvedVariables) {
        var experiment = Experiment.builder()
                .name(request.name())
                .description(request.description())
                .category(request.category())
                .status(request.status())
                .createdBy(request.createdBy())
                .notes(request.notes())
                .variables(new java.util.ArrayList<>())
                .build();

        for (ResolvedVariable resolved : resolvedVariables) {
            Variable variable = Variable.builder()
                    .experiment(experiment)
                    .variableType(resolved.type())
                    .build();
            experiment.getVariables().add(variable);
        }

        return experiment;
    }

    /**
     * Converts an Experiment entity into a response DTO,
     * including nested VariableSummary and TrialSummary projections.
     *
     * @param experiment the entity to convert
     * @return the response DTO
     */
    public static ExperimentResponse toResponse(Experiment experiment) {
        return ExperimentResponse.builder()
                .id(experiment.getId())
                .name(experiment.getName())
                .description(experiment.getDescription())
                .category(experiment.getCategory())
                .status(experiment.getStatus())
                .createdBy(experiment.getCreatedBy())
                .notes(experiment.getNotes())
                .createdAt(experiment.getCreatedAt())
                .updatedAt(experiment.getUpdatedAt())
                .variables(experiment.getVariables().stream()
                        .map(v -> {
                            var type = v.getVariableType();
                            return ExperimentVariableSummary.builder()
                                    .id(v.getId())
                                    .name(type.getName())
                                    .unitOfMeasure(type.getUnitOfMeasure())
                                    .dataType(type.getDataType())
                                    .description(type.getDescription())
                                    .build();
                        })
                        .toList())
                .trials(experiment.getTrials().stream()
                        .map(t -> ExperimentTrialSummary.builder()
                                .id(t.getId())
                                .trialNumber(t.getTrialNumber())
                                .label(t.getLabel())
                                .notes(t.getNotes())
                                .executionDate(t.getExecutionDate())
                                .build())
                        .toList())
                .build();
    }
}