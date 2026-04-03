package it.deltascientia.service;

import it.deltascientia.dto.VariableTypeResponse;
import it.deltascientia.dto.VariableTypeSummary;
import it.deltascientia.mapper.VariableTypeMapper;
import it.deltascientia.model.VariableType;
import it.deltascientia.repository.VariableTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Manages variable type resolution against the catalog.
 * Handles lookup of existing types and creation of new custom ones.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VariableTypeService {

    private final VariableTypeRepository variableTypeRepository;

    /**
     * Resolves a variable type from the given name or typeName.
     * When {@code typeName} is provided, the catalog type is looked up and returned.
     * When only {@code name} is provided, an existing type is looked up; if not found,
     * a new custom type is created with the supplied metadata.
     */
    @Transactional
    public VariableType resolveFromRequest(String typeName, String name,
                                           String unitOfMeasure, String dataType,
                                           String description) {
        if (typeName != null) {
            log.debug("Looking up existing variable type by name: typeName={}", typeName);
            return variableTypeRepository.findByNameIgnoreCase(typeName)
                    .orElseThrow(() -> {
                        log.warn("Variable type not found in catalog: typeName={}", typeName);
                        return new IllegalArgumentException(
                                "Variable type '" + typeName + "' not found in catalog");
                    });
        }

        if (name == null) {
            log.warn("Variable resolution failed: both typeName and name are null");
            throw new IllegalArgumentException("Either typeName or name must be provided");
        }

        log.debug("Resolving by name: name={}", name);
        return variableTypeRepository.findByNameIgnoreCase(name)
                .map(type -> {
                    log.debug("Found existing variable type by name: name={}", name);
                    return type;
                })
                .orElseGet(() -> {
                    log.info("Creating new custom variable type: name={}, dataType={}, unitOfMeasure={}",
                            name, dataType != null ? dataType : "TEXT", unitOfMeasure);
                    VariableType newType = VariableTypeMapper.toNewEntity(name, unitOfMeasure,
                            dataType != null ? dataType : "TEXT", description, true);
                    return variableTypeRepository.save(newType);
                });
    }

    /**
     * Deletes a variable type by its database identifier.
     *
     * @param id the variable type ID
     * @throws IllegalArgumentException if the type does not exist
     */
    @Transactional
    public void deleteById(Long id) {
        log.info("Deleting variable type: id={}", id);
        var type = variableTypeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Variable type not found for deletion: id={}", id);
                    return new IllegalArgumentException("Variable type not found with id: " + id);
                });
        variableTypeRepository.delete(type);
        log.info("Variable type deleted: id={}", id);
    }

    /**
     * Lists all variable types with pagination support, ordered by name.
     */
    @Transactional(readOnly = true)
    public VariableTypeResponse listAll(Pageable pageable) {
        log.debug("Listing variable types: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<VariableType> page = variableTypeRepository.findAll(pageable);
        List<VariableTypeSummary> summaries = page.getContent().stream()
                .map(VariableTypeMapper::toSummary)
                .toList();
        return VariableTypeMapper.toResponse(summaries, page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(), page.isLast());
    }
}
