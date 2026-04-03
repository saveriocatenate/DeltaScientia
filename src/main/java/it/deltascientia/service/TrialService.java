package it.deltascientia.service;

import it.deltascientia.dto.TrialCreateRequest;
import it.deltascientia.dto.TrialResponse;
import it.deltascientia.dto.TrialSearchRequest;
import it.deltascientia.mapper.TrialMapper;
import it.deltascientia.model.Trial;
import it.deltascientia.repository.TrialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Business logic layer for Trial operations.
 * Handles creation, retrieval, and paginated search with value filtering.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TrialService {

    private final TrialRepository trialRepository;
    private final ExperimentService experimentService;
    private final TrialValueService trialValueService;

    /**
     * Creates a new trial for the given experiment, persisting measured values.
     * If trial number is null, auto-assigns the next sequential number.
     *
     * @param experimentId the parent experiment ID
     * @param request      the trial creation request
     * @return the persisted trial as a response DTO
     * @throws it.deltascientia.exception.model.ExperimentNotFoundException if experiment doesn't exist
     * @throws IllegalArgumentException if a referenced variable doesn't belong to the experiment
     */
    @Transactional
    public TrialResponse create(Long experimentId, TrialCreateRequest request) {
        log.info("Creating trial for experiment: id={}", experimentId);

        experimentService.getById(experimentId);

        Integer trialNumber = request.trialNumber();
        if (trialNumber == null) {
            trialNumber = trialRepository.findMaxTrialNumberByExperimentId(experimentId).orElse(0) + 1;
            log.debug("Auto-assigned trial number: {} for experiment: {}", trialNumber, experimentId);
        }

        Trial trial = TrialMapper.toEntity(request, trialNumber);
        if (request.values() != null) {
            for (var valueRequest : request.values()) {
                trial.getValues().add(trialValueService.createFromRequest(valueRequest, trial));
            }
        }

        var saved = trialRepository.save(trial);
        log.info("Trial created with id={}, trialNumber={}", saved.getId(), saved.getTrialNumber());
        return TrialMapper.toResponse(saved, experimentId);
    }

    /**
     * Lists all trials for an experiment with pagination support, ordered by ID.
     *
     * @param experimentId the parent experiment ID
     * @param pageable     pagination parameters
     * @return paginated trial responses
     */
    @Transactional(readOnly = true)
    public Page<TrialResponse> listAll(Long experimentId, Pageable pageable) {
        log.debug("Listing trials for experiment: id={}, page={}, size={}", experimentId, pageable.getPageNumber(), pageable.getPageSize());
        return trialRepository.findByExperimentIdOrderById(experimentId, pageable)
                .map(trial -> TrialMapper.toResponse(trial, experimentId));
    }

    /**
     * Retrieves a single trial by its ID within the context of an experiment.
     *
     * @param experimentId the parent experiment ID
     * @param trialId      the trial ID
     * @return the trial response DTO
     * @throws it.deltascientia.exception.model.TrialNotFoundException if the trial doesn't exist or doesn't belong to the experiment
     */
    @Transactional(readOnly = true)
    public TrialResponse getById(Long experimentId, Long trialId) {
        log.debug("Retrieving trial: trialId={}, experimentId={}", trialId, experimentId);
        Trial trial = trialRepository.findByIdAndExperimentId(trialId, experimentId)
                .orElseThrow(() -> new it.deltascientia.exception.model.TrialNotFoundException(trialId, experimentId));
        return TrialMapper.toResponse(trial, experimentId);
    }

    /**
     * Searches trials within an experiment with composite filtering support.
     *
     * @param experimentId the parent experiment ID
     * @param request      the search criteria
     * @param pageable     pagination parameters
     * @return paginated trial responses
     */
    @Transactional(readOnly = true)
    public Page<TrialResponse> search(Long experimentId, TrialSearchRequest request, Pageable pageable) {
        log.debug("Searching trials for experiment: id={}, filters: label={}, fromDate={}, toDate={}, valueFilters={}",
                experimentId, request != null ? request.label() : null,
                request != null ? request.fromDate() : null,
                request != null ? request.toDate() : null,
                request != null && request.values() != null ? request.values().size() : 0);

        var spec = TrialSpecification.matchesFilters(experimentId, request);
        return trialRepository.findAll(spec, pageable)
                .map(trial -> TrialMapper.toResponse(trial, experimentId));
    }

    /**
     * Deletes a trial by its ID within the context of an experiment.
     *
     * @param experimentId the parent experiment ID
     * @param trialId      the trial ID
     * @throws it.deltascientia.exception.model.TrialNotFoundException if the trial doesn't exist or doesn't belong to the experiment
     */
    @Transactional
    public void deleteById(Long experimentId, Long trialId) {
        log.info("Deleting trial: trialId={}, experimentId={}", trialId, experimentId);
        Trial trial = trialRepository.findByIdAndExperimentId(trialId, experimentId)
                .orElseThrow(() -> new it.deltascientia.exception.model.TrialNotFoundException(trialId, experimentId));
        trialRepository.delete(trial);
        log.info("Trial deleted: trialId={}", trialId);
    }
}
