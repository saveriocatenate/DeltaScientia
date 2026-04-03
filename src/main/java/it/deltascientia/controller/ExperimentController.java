package it.deltascientia.controller;

import it.deltascientia.dto.ExperimentCreateRequest;
import it.deltascientia.dto.ExperimentResponse;
import it.deltascientia.service.ExperimentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
}
