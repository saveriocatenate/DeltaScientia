package it.deltascientia.mapper;

import it.deltascientia.dto.VariableTypeResponse;
import it.deltascientia.dto.VariableTypeResponse.VariableTypeSummary;
import it.deltascientia.model.VariableType;

import java.util.List;

/**
 * Mapper for {@link VariableType} entity to DTO conversion.
 */
public final class VariableTypeMapper {

    private VariableTypeMapper() {
        throw new UnsupportedOperationException("VariableTypeMapper is a static utility class and must not be instantiated");
    }

    /**
     * Constructs a VariableType entity from raw fields.
     *
     * @param name          type name
     * @param unitOfMeasure optional unit
     * @param dataType      data type
     * @param description   optional description
     * @param isCustom      whether user-defined
     * @return a new VariableType entity (not yet persisted)
     */
    public static VariableType toNewEntity(String name, String unitOfMeasure, String dataType,
                                   String description, boolean isCustom) {
        return VariableType.builder()
                .name(name)
                .unitOfMeasure(unitOfMeasure)
                .dataType(dataType)
                .description(description)
                .isCustom(isCustom)
                .build();
    }

    /**
     * Converts a VariableType entity to a summary DTO.
     *
     * @param variableType the entity
     * @return the summary DTO
     */
    public static VariableTypeSummary toSummary(VariableType variableType) {
        return VariableTypeSummary.builder()
                .id(variableType.getId())
                .name(variableType.getName())
                .unitOfMeasure(variableType.getUnitOfMeasure())
                .dataType(variableType.getDataType())
                .description(variableType.getDescription())
                .isCustom(variableType.getIsCustom())
                .createdAt(variableType.getCreatedAt())
                .build();
    }

    /**
     * Wraps a page of summaries into a paginated response.
     *
     * @param summaries     page content
     * @param page          current page number
     * @param size          page size
     * @param totalElements total count
     * @param totalPages    total pages
     * @param last          whether this is the last page
     * @return the paginated response
     */
    public static VariableTypeResponse toResponse(List<VariableTypeSummary> summaries,
                                           int page, int size, long totalElements,
                                           int totalPages, boolean last) {
        return VariableTypeResponse.builder()
                .content(summaries)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .last(last)
                .build();
    }
}