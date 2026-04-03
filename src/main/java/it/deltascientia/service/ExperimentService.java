package it.deltascientia.service;

import it.deltascientia.dto.ExperimentCreateRequest;
import it.deltascientia.dto.ExperimentResponse;
import it.deltascientia.dto.ResolvedVariable;
import it.deltascientia.mapper.ExperimentMapper;
import it.deltascientia.model.Experiment;
import it.deltascientia.model.VariableType;
import it.deltascientia.repository.ExperimentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
     * @throws it.deltascientia.exception.model.ExperimentNotFoundException if no experiment exists for the given ID
     */
    @Transactional(readOnly = true)
    public ExperimentResponse getById(Long id) {
        var experiment = experimentRepository.findById(id)
                .orElseThrow(() -> new it.deltascientia.exception.model.ExperimentNotFoundException(id));
        return ExperimentMapper.toResponse(experiment);
    }

    /**
     * Deletes an experiment by its database identifier, cascading to its trials and variables.
     *
     * @param id the experiment ID
     * @throws it.deltascientia.exception.model.ExperimentNotFoundException if no experiment exists for the given ID
     */
    @Transactional
    public void deleteById(Long id) {
        log.info("Deleting experiment: id={}", id);
        var experiment = experimentRepository.findById(id)
                .orElseThrow(() -> new it.deltascientia.exception.model.ExperimentNotFoundException(id));
        experimentRepository.delete(experiment);
        log.info("Experiment deleted: id={}", id);
    }

    /**
     * Lists all experiments with pagination support.
     *
     * @param pageable pagination parameters
     * @return paginated experiment responses
     */
    @Transactional(readOnly = true)
    public Page<ExperimentResponse> listAll(Pageable pageable) {
        log.debug("Listing experiments: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<Experiment> page = experimentRepository.findAll(pageable);
        return page.map(ExperimentMapper::toResponse);
    }
}
