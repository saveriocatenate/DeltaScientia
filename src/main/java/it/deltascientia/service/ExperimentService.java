package it.deltascientia.service;

import it.deltascientia.dto.ExperimentCreateRequest;
import it.deltascientia.dto.ExperimentResponse;
import it.deltascientia.mapper.ExperimentMapper;
import it.deltascientia.model.VariableType;
import it.deltascientia.repository.ExperimentRepository;
import it.deltascientia.service.VariableTypeService.ResolvedVariable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Business logic layer for Experiment operations.
 * Handles creation, retrieval, and domain-specific validation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExperimentService {

    private final ExperimentRepository experimentRepository;
    private final VariableTypeService variableTypeService;

    /**
     * Creates a new experiment from the given request payload.
     * Delegates variable type resolution to {@link VariableTypeService}.
     *
     * @param request the creation request
     * @return the persisted experiment as a response DTO
     */
    @Transactional
    public ExperimentResponse create(ExperimentCreateRequest request) {
        log.info("Creating experiment: name={}", request.name());

        var resolvedVars = request.variables().stream()
                .map(varReq -> {
                    log.debug("Resolving variable: typeName={}, name={}", varReq.typeName(), varReq.name());
                    VariableType type = variableTypeService.resolveFromRequest(varReq.typeName(),
                            varReq.name(), varReq.unitOfMeasure(), varReq.dataType(), varReq.description());
                    return new ResolvedVariable(type);
                })
                .toList();

        log.info("Resolved {} variables for experiment: name={}", resolvedVars.size(), request.name());

        var experiment = ExperimentMapper.toEntity(request, resolvedVars);
        var saved = experimentRepository.save(experiment);
        log.info("Experiment created with id={}", saved.getId());
        return ExperimentMapper.toResponse(saved);
    }

    /**
     * Retrieves an experiment by its database identifier.
     *
     * @param id the experiment ID
     * @return the experiment as a response DTO with nested variables and trials
     * @throws ExperimentNotFoundException if no experiment exists for the given ID
     */
    @Transactional(readOnly = true)
    public ExperimentResponse getById(Long id) {
        var experiment = experimentRepository.findById(id)
                .orElseThrow(() -> new ExperimentNotFoundException(id));
        return ExperimentMapper.toResponse(experiment);
    }

    /**
     * Exception thrown when an experiment cannot be found by ID.
     */
    public static class ExperimentNotFoundException extends RuntimeException {
        public ExperimentNotFoundException(Long id) {
            super("Experiment not found with id: " + id);
        }
    }

}
