package it.deltascientia.controller;

import it.deltascientia.dto.ExperimentCreateRequest;
import it.deltascientia.dto.ExperimentResponse;
import it.deltascientia.service.ExperimentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller exposing Experiment endpoints.
 */
@RestController
@RequestMapping("/api/experiments")
@RequiredArgsConstructor
public class ExperimentController {

    private final ExperimentService experimentService;

    /**
     * Lists all experiments with pagination support.
     *
     * @param pageable pagination parameters
     * @return 200 OK with paginated experiment data
     */
    @GetMapping
    public ResponseEntity<Page<ExperimentResponse>> listExperiments(
            @PageableDefault(page = 0, size = 20) Pageable pageable) {
        Page<ExperimentResponse> page = experimentService.listAll(pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * Retrieves a single experiment by its ID.
     *
     * @param id the database identifier of the experiment
     * @return 200 OK with the experiment data, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExperimentResponse> getExperimentById(@PathVariable Long id) {
        ExperimentResponse response = experimentService.getById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Creates a new experiment, optionally with inline variable definitions.
     *
     * @param request the creation request payload
     * @return 201 Created with the persisted experiment data, or 400 if validation fails
     */
    @PostMapping
    public ResponseEntity<ExperimentResponse> createExperiment(@Valid @RequestBody ExperimentCreateRequest request) {
        ExperimentResponse response = experimentService.create(request);
        return ResponseEntity.status(201).body(response);
    }

    /**
     * Deletes an experiment by its ID, cascading to its trials and variables.
     *
     * @param id the database identifier of the experiment
     * @return 204 No Content on success, or 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExperiment(@PathVariable Long id) {
        experimentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
