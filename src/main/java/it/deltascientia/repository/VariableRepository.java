package it.deltascientia.repository;

import it.deltascientia.model.Variable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for {@link Variable} entity data access.
 * Inherits full CRUD operations from {@link JpaRepository}.
 */
public interface VariableRepository extends JpaRepository<Variable, Long> {

    /**
     * Finds all variables defined in the given experiment.
     *
     * @param experimentId the parent experiment ID
     * @return list of variables for the experiment
     */
    List<Variable> findByExperimentId(Long experimentId);

    /**
     * Finds variables of a specific data type within an experiment.
     *
     * @param experimentId the parent experiment ID
     * @param dataType the variable data type (e.g. "NUMERIC")
     * @return list of variables matching the data type
     */
    List<Variable> findByExperimentIdAndDataType(Long experimentId, String dataType);

    /**
     * Finds variables whose name contains the given substring within an experiment.
     *
     * @param name the substring to search for
     * @param experimentId the parent experiment ID
     * @return list of matching variables
     */
    List<Variable> findByNameContainingIgnoreCaseAndExperimentId(String name, Long experimentId);
}
