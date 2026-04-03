package it.deltascientia.service;

import it.deltascientia.dto.VariableTypeResponse;
import it.deltascientia.dto.VariableTypeResponse.VariableTypeSummary;
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
     *
     * @param typeName      the exact catalog type name to look up, or null
     * @param name          the variable name to create/lookup when typeName is null
     * @param unitOfMeasure optional unit of measurement (used only when creating a new type)
     * @param dataType      optional data type (used only when creating a new type, defaults to "TEXT")
     * @param description   optional description (used only when creating a new type)
     * @return the resolved VariableType entity (persisted if newly created)
     * @throws IllegalArgumentException if typeName is provided but not found, or both are null
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
                    VariableType newType = VariableType.builder()
                            .name(name)
                            .unitOfMeasure(unitOfMeasure)
                            .dataType(dataType != null ? dataType : "TEXT")
                            .description(description)
                            .isCustom(true)
                            .build();
                    return variableTypeRepository.save(newType);
                });
    }

    /**
     * Holds a resolved variable type reference.
     */
    public record ResolvedVariable(VariableType type) {}

    /**
     * Lists all variable types with pagination support, ordered by name.
     *
     * @param pageable pagination and sorting parameters
     * @return paginated variable type summaries
     */
    @Transactional(readOnly = true)
    public VariableTypeResponse listAll(Pageable pageable) {
        log.debug("Listing variable types: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<VariableType> page = variableTypeRepository.findAll(pageable);
        List<VariableTypeSummary> summaries = page.getContent().stream()
                .map(vt -> VariableTypeSummary.builder()
                        .id(vt.getId())
                        .name(vt.getName())
                        .unitOfMeasure(vt.getUnitOfMeasure())
                        .dataType(vt.getDataType())
                        .description(vt.getDescription())
                        .isCustom(vt.getIsCustom())
                        .createdAt(vt.getCreatedAt())
                        .build())
                .toList();
        return VariableTypeResponse.builder()
                .content(summaries)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
