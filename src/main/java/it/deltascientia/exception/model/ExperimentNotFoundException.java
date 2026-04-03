package it.deltascientia.exception.model;

/**
 * Exception thrown when an experiment cannot be found by ID.
 */
public class ExperimentNotFoundException extends RuntimeException {

    public ExperimentNotFoundException(Long id) {
        super("Experiment not found with id: " + id);
    }
}
