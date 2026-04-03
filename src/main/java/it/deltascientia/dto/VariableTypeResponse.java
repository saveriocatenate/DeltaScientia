package it.deltascientia.dto;

import lombok.Builder;

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
) {}
