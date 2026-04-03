package it.deltascientia.service;

import it.deltascientia.dto.TrialValueRequest;
import it.deltascientia.mapper.TrialValueMapper;
import it.deltascientia.model.TrialValue;
import it.deltascientia.model.Variable;
import it.deltascientia.model.Trial;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Owns all {@link TrialValue} data access and validation logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TrialValueService {

    private final VariableService variableService;

    /**
     * Creates a TrialValue entity from a request, resolving the variable
     * against the parent experiment via {@link VariableService}.
     *
     * @param request the value creation request
     * @param trial the parent trial
     * @return a new TrialValue entity ready to be persisted
     * @throws IllegalArgumentException if the variable doesn't belong to the experiment
     */
    public TrialValue createFromRequest(TrialValueRequest request, Trial trial) {
        Variable variable = variableService.findByIdAndExperimentId(
                request.variableId(), trial.getExperiment().getId());

        log.debug("Creating trial value for variable: id={}, name={}",
                variable.getId(), variable.getVariableType().getName());

        return TrialValueMapper.toEntity(request, trial, variable);
    }
}
