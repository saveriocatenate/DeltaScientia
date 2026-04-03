package it.deltascientia.repository;

import it.deltascientia.model.Experiment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for {@link Experiment} entity data access.
 * Inherits full CRUD operations from {@link JpaRepository}.
 */
public interface ExperimentRepository extends JpaRepository<Experiment, Long> {

    /**
     * Finds all experiments with the given category.
     *
     * @param category the experiment category
     * @return list of experiments matching the category
     */
    List<Experiment> findByCategory(String category);

    /**
     * Finds all experiments with the given status.
     *
     * @param status the experiment lifecycle status
     * @return list of experiments matching the status
     */
    List<Experiment> findByStatus(String status);

    /**
     * Finds experiments whose name contains the given substring (case-insensitive).
     *
     * @param name the substring to search for
     * @return list of experiments with matching names
     */
    List<Experiment> findByNameContainingIgnoreCase(String name);

    /**
     * Finds all experiments created by the given user.
     *
     * @param createdBy the username or identifier of the creator
     * @return list of experiments created by the user
     */
    List<Experiment> findByCreatedBy(String createdBy);
}
