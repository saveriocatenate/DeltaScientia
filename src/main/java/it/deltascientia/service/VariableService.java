package it.deltascientia.service;

import it.deltascientia.model.Variable;
import it.deltascientia.repository.VariableRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Owns all {@link Variable} data access and validation logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VariableService {

    private final VariableRepository variableRepository;

    /**
     * Finds a variable by ID and validates it belongs to the given experiment.
     *
     * @param variableId   the variable database identifier
     * @param experimentId the expected parent experiment ID
     * @return the variable if found and validated
     * @throws IllegalArgumentException if the variable does not exist or belongs to another experiment
     */
    @Transactional(readOnly = true)
    public Variable findByIdAndExperimentId(Long variableId, Long experimentId) {
        Variable variable = variableRepository.findById(variableId)
                .orElse(null);
        if (variable == null || !variable.getExperiment().getId().equals(experimentId)) {
            throw new IllegalArgumentException(
                    "Variable with id " + variableId + " does not belong to experiment " + experimentId);
        }
        log.debug("Resolved variable: id={}, name={}, experimentId={}",
                variable.getId(), variable.getVariableType().getName(), experimentId);
        return variable;
    }
}
