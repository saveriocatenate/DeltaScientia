package it.deltascientia.exception.model;

/**
 * Exception thrown when a trial cannot be found within an experiment.
 */
public class TrialNotFoundException extends RuntimeException {

    public TrialNotFoundException(Long trialId, Long experimentId) {
        super("Trial not found with id " + trialId + " in experiment " + experimentId);
    }
}
