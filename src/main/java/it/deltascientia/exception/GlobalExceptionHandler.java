package it.deltascientia.exception;

import it.deltascientia.service.ExperimentService.ExperimentNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Global exception handler that maps application exceptions to HTTP response codes.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles cases where an experiment is not found, returning HTTP 404.
     *
     * @param ex the experiment-not-found exception
     * @return 404 Not Found with empty body
     */
    @ExceptionHandler(ExperimentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleExperimentNotFound(ExperimentNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }

    /**
     * Handles request body validation failures, returning HTTP 400
     * with a list of field-level error details.
     *
     * @param ex the validation exception
     * @return 400 Bad Request with field error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new FieldError(fe.getField(), fe.getDefaultMessage()))
                .toList();
        return ResponseEntity.badRequest().body(new ValidationErrorResponse(errors));
    }

    private record ErrorResponse(String error, Long id) {}
    private record ValidationErrorResponse(List<FieldError> details) {}
    private record FieldError(String field, String message) {}
}
