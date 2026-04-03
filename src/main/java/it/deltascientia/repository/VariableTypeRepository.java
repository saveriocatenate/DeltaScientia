package it.deltascientia.repository;

import it.deltascientia.model.VariableType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repository for {@link VariableType} entity data access.
 * Inherits full CRUD operations from {@link JpaRepository}.
 */
public interface VariableTypeRepository extends JpaRepository<VariableType, Long> {

    /**
     * Finds a variable type by name, ignoring case.
     *
     * @param name the type name
     * @return the type if found
     */
    @Query("SELECT vt FROM VariableType vt WHERE LOWER(vt.name) = LOWER(:name)")
    Optional<VariableType> findByNameIgnoreCase(@Param("name") String name);
}
