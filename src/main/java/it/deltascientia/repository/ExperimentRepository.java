package it.deltascientia.repository;

import it.deltascientia.model.Experiment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for {@link Experiment} entity data access.
 * Inherits full CRUD and pag findAll operations from {@link JpaRepository}.
 */
public interface ExperimentRepository extends JpaRepository<Experiment, Long> {
}
