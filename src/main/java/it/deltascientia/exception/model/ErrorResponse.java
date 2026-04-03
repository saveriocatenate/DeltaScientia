package it.deltascientia.exception.model;

/**
 * Generic error response containing a message and an optional reference ID.
 *
 * @param error error message
 * @param id    optional resource identifier
 */
public record ErrorResponse(String error, Long id) {}
