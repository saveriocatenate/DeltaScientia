package it.deltascientia.repository;

import it.deltascientia.model.TrialValue;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for {@link TrialValue} entity data access.
 * Inherits full CRUD operations from {@link JpaRepository}.
 */
public interface TrialValueRepository extends JpaRepository<TrialValue, Long> {
}
