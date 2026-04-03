package it.deltascientia.repository;

import it.deltascientia.model.Trial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repository for {@link Trial} entity data access.
 * Inherits full CRUD operations from {@link JpaRepository} and
 * dynamic query support via {@link JpaSpecificationExecutor}.
 */
public interface TrialRepository extends JpaRepository<Trial, Long>, JpaSpecificationExecutor<Trial> {

    /**
     * Finds all trials belonging to the given experiment, ordered by ID.
     *
     * @param experimentId the parent experiment ID
     * @return list of trials for the experiment
     */
    Page<Trial> findByExperimentIdOrderById(Long experimentId, Pageable pageable);

    /**
     * Returns the highest trial number for the given experiment.
     *
     * @param experimentId the parent experiment ID
     * @return the max trial number, or empty if no trials exist
     */
    @Query("SELECT MAX(t.trialNumber) FROM Trial t WHERE t.experiment.id = :experimentId")
    Optional<Integer> findMaxTrialNumberByExperimentId(@Param("experimentId") Long experimentId);

    /**
     * Finds a single trial by ID that belongs to the given experiment.
     *
     * @param experimentId the parent experiment ID
     * @param trialId      the trial ID
     * @return the trial if found
     */
    Optional<Trial> findByIdAndExperimentId(Long trialId, Long experimentId);
}
