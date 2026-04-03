package it.deltascientia.repository;

import it.deltascientia.model.Variable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for {@link Variable} entity data access.
 * Inherits full CRUD and pag findAll operations from {@link JpaRepository}.
 */
public interface VariableRepository extends JpaRepository<Variable, Long> {
}
