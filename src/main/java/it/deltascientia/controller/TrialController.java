package it.deltascientia.controller;

import it.deltascientia.dto.TrialCreateRequest;
import it.deltascientia.dto.TrialResponse;
import it.deltascientia.dto.TrialSearchRequest;
import it.deltascientia.service.TrialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller exposing Trial endpoints nested under experiments.
 */
@RestController
@RequestMapping("/api/experiments/{experimentId}/trials")
@RequiredArgsConstructor
public class TrialController {

    private final TrialService trialService;

    /**
     * Creates a new trial with measured values for an experiment.
     *
     * @param experimentId the parent experiment
     * @param request      the trial creation request
     * @return 201 Created with the persisted trial data
     */
    @PostMapping
    public ResponseEntity<TrialResponse> createTrial(@PathVariable Long experimentId,
                                                     @Valid @RequestBody TrialCreateRequest request) {
        TrialResponse response = trialService.create(experimentId, request);
        return ResponseEntity.status(201).body(response);
    }

    /**
     * Retrieves a single trial by ID.
     *
     * @param experimentId the parent experiment
     * @param trialId      the trial identifier
     * @return 200 OK with the trial data, or 404 if not found
     */
    @GetMapping("/{trialId}")
    public ResponseEntity<TrialResponse> getTrialById(@PathVariable Long experimentId,
                                                      @PathVariable Long trialId) {
        TrialResponse response = trialService.getById(experimentId, trialId);
        return ResponseEntity.ok(response);
    }

    /**
     * Searches trials within an experiment with composite filtering support.
     * Accepts a JSON body for structured filters on labels, dates, and multiple variable values.
     *
     * @param experimentId the parent experiment
     * @param request      the search criteria (may be empty or null to retrieve all)
     * @param pageable     pagination parameters
     * @return 200 OK with paginated trial data
     */
    @PostMapping("/search")
    public ResponseEntity<Page<TrialResponse>> searchTrials(
            @PathVariable Long experimentId,
            @RequestBody(required = false) TrialSearchRequest request,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TrialResponse> page = trialService.search(experimentId, request, pageable);
        return ResponseEntity.ok(page);
    }
}
