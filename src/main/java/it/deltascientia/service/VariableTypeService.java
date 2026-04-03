package it.deltascientia.service;

import it.deltascientia.dto.VariableTypeResponse;
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
    private final VariableTypeMapper variableTypeMapper;

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
                    VariableType newType = variableTypeMapper.toNewEntity(name, unitOfMeasure,
                            dataType != null ? dataType : "TEXT", description, true);
                    return variableTypeRepository.save(newType);
                });
    }

    public record ResolvedVariable(VariableType type) {}

    /**
     * Lists all variable types with pagination support, ordered by name.
     */
    @Transactional(readOnly = true)
    public VariableTypeResponse listAll(Pageable pageable) {
        log.debug("Listing variable types: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<VariableType> page = variableTypeRepository.findAll(pageable);
        List<VariableTypeResponse.VariableTypeSummary> summaries = page.getContent().stream()
                .map(variableTypeMapper::toSummary)
                .toList();
        return variableTypeMapper.toResponse(summaries, page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(), page.isLast());
    }
}
