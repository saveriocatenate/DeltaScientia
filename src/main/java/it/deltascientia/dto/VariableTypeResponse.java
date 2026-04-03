package it.deltascientia.dto;

import lombok.Builder;
import java.time.Instant;

import java.util.List;

/**
 * Response payload for paginated variable type listing.
 *
 * @param content    page of variable types
 * @param page       current page number (0-based)
 * @param size       page size requested
 * @param totalElements total number of variable types
 * @param totalPages total pages available
 * @param last       whether this is the last page
 */
@Builder
public record VariableTypeResponse(
        List<VariableTypeSummary> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {
    /**
     * Summary of a single variable type.
     *
     * @param id            type database identifier
     * @param name          type name
     * @param unitOfMeasure unit of measurement
     * @param dataType      data type
     * @param description   type description
     * @param isCustom      whether user-defined
     * @param createdAt     creation timestamp
     */
    @Builder
    public record VariableTypeSummary(
            Long id,
            String name,
            String unitOfMeasure,
            String dataType,
            String description,
            Boolean isCustom,
            Instant createdAt
    ) {}
}
