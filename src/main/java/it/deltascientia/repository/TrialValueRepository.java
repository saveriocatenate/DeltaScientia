package it.deltascientia.repository;

import it.deltascientia.model.TrialValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for {@link TrialValue} entity data access.
 * Inherits full CRUD operations from {@link JpaRepository}.
 */
public interface TrialValueRepository extends JpaRepository<TrialValue, Long> {

    /**
     * Finds all measured values for a given trial.
     *
     * @param trialId the trial ID
     * @return list of measured values for the trial
     */
    List<TrialValue> findByTrialId(Long trialId);

    /**
     * Finds all measured values for a given variable across all trials.
     *
     * @param variableId the variable ID
     * @return list of measured values for the variable
     */
    List<TrialValue> findByVariableId(Long variableId);

    /**
     * Finds the measured value for a specific trial-variable pair.
     *
     * @param trialId the trial ID
     * @param variableId the variable ID
     * @return list of values (typically one entry) for the trial-variable combination
     */
    List<TrialValue> findByTrialIdAndVariableId(Long trialId, Long variableId);
}
