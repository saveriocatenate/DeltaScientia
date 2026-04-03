package it.deltascientia.mapper;

import it.deltascientia.dto.TrialCreateRequest;
import it.deltascientia.dto.TrialResponse;
import it.deltascientia.dto.TrialResponse.ValueSummary;
import it.deltascientia.model.Trial;

import java.util.ArrayList;

/**
 * Mapper responsible for converting between Trial entities and DTOs.
 */
public final class TrialMapper {

    private TrialMapper() {
        throw new UnsupportedOperationException("TrialMapper is a static utility class and must not be instantiated");
    }

    /**
     * Converts a trial creation request into a Trial entity without values.
     *
     * @param request the creation request
     * @param trialNumber resolved trial number (auto-assigned when request was null)
     * @return a new Trial entity ready for value attachment
     */
    public static Trial toEntity(TrialCreateRequest request, Integer trialNumber) {
        return Trial.builder()
                .trialNumber(trialNumber)
                .label(request.label())
                .notes(request.notes())
                .executionDate(request.executionDate())
                .values(new ArrayList<>())
                .build();
    }

    /**
     * Converts a Trial entity into a response DTO, including nested ValueSummary projections.
     *
     * @param trial        the entity to convert
     * @param experimentId the parent experiment ID
     * @return the response DTO
     */
    public static TrialResponse toResponse(Trial trial, Long experimentId) {
        var values = trial.getValues().stream()
                .map(tv -> ValueSummary.builder()
                        .id(tv.getId())
                        .variableId(tv.getVariable().getId())
                        .variableName(tv.getVariable().getVariableType().getName())
                        .valueText(tv.getValueText())
                        .valueNumeric(tv.getValueNumeric())
                        .valueLongText(tv.getValueLongText())
                        .build())
                .toList();

        return TrialResponse.builder()
                .id(trial.getId())
                .experimentId(experimentId)
                .trialNumber(trial.getTrialNumber())
                .label(trial.getLabel())
                .notes(trial.getNotes())
                .executionDate(trial.getExecutionDate())
                .createdAt(trial.getCreatedAt())
                .values(values)
                .build();
    }
}