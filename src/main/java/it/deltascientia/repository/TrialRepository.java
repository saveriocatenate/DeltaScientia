package it.deltascientia.repository;

import it.deltascientia.model.Trial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for {@link Trial} entity data access.
 * Inherits full CRUD operations from {@link JpaRepository}.
 */
public interface TrialRepository extends JpaRepository<Trial, Long> {

    /**
     * Finds all trials belonging to the given experiment, ordered by ID.
     *
     * @param experimentId the parent experiment ID
     * @return list of trials for the experiment
     */
    List<Trial> findByExperimentId(Long experimentId);
}
