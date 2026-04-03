package it.deltascientia.service;

import it.deltascientia.dto.TrialCreateRequest.TrialValueRequest;
import it.deltascientia.model.Trial;
import it.deltascientia.model.TrialValue;
import it.deltascientia.model.Variable;
import it.deltascientia.service.VariableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrialValueService {

    private final VariableService variableService;

    public TrialValue createFromRequest(TrialValueRequest request, Trial trial) {
        Variable variable = variableService.findByIdAndExperimentId(
                request.variableId(), trial.getExperiment().getId());

        log.debug("Creating trial value for variable: id={}, name={}",
                variable.getId(), variable.getVariableType().getName());

        return TrialValue.builder()
                .trial(trial)
                .variable(variable)
                .valueText(request.valueText())
                .valueNumeric(request.valueNumeric())
                .valueLongText(request.valueLongText())
                .build();
    }
}
