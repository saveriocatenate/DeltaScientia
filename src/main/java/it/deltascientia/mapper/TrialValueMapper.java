package it.deltascientia.mapper;

import it.deltascientia.dto.TrialValueRequest;
import it.deltascientia.model.Trial;
import it.deltascientia.model.TrialValue;
import it.deltascientia.model.Variable;

/**
 * Mapper for converting between TrialValue DTOs and entities.
 */
public final class TrialValueMapper {

    private TrialValueMapper() {
        throw new UnsupportedOperationException("TrialValueMapper is a static utility class and must not be instantiated");
    }

    /**
     * Converts a TrialValueRequest into a TrialValue entity.
     *
     * @param request the creation request
     * @param trial the parent trial
     * @param variable the resolved variable reference
     * @return a new TrialValue entity ready to be persisted
     */
    public static TrialValue toEntity(TrialValueRequest request, Trial trial, Variable variable) {
        return TrialValue.builder()
                .trial(trial)
                .variable(variable)
                .valueText(request.valueText())
                .valueNumeric(request.valueNumeric())
                .valueLongText(request.valueLongText())
                .build();
    }
}
