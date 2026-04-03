package it.deltascientia.exception.model;

import java.util.List;

/**
 * Validation error response containing a list of field-level errors.
 *
 * @param details list of field validation errors
 */
public record ValidationErrorResponse(List<FieldError> details) {}
