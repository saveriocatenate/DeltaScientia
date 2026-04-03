package it.deltascientia.exception.model;

/**
 * Single field-level validation error.
 *
 * @param field   the field that failed validation
 * @param message the reason for the failure
 */
public record FieldError(String field, String message) {}
