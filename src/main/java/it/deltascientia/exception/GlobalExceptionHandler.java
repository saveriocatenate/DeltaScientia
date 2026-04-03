package it.deltascientia.exception;

import it.deltascientia.exception.model.ErrorResponse;
import it.deltascientia.exception.model.ExperimentNotFoundException;
import it.deltascientia.exception.model.FieldError;
import it.deltascientia.exception.model.TrialNotFoundException;
import it.deltascientia.exception.model.ValidationErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * Global exception handler that maps application exceptions to HTTP response codes.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles cases where an experiment is not found, returning HTTP 404.
     *
     * @param ex the experiment-not-found exception
     * @return 404 Not Found with error details
     */
    @ExceptionHandler(ExperimentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleExperimentNotFound(ExperimentNotFoundException ex) {
        return ResponseEntity.status(404).body(new ErrorResponse(ex.getMessage(), null));
    }

    /**
     * Handles cases where a trial is not found, returning HTTP 404.
     *
     * @param ex the trial-not-found exception
     * @return 404 Not Found with error details
     */
    @ExceptionHandler(TrialNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTrialNotFound(TrialNotFoundException ex) {
        return ResponseEntity.status(404).body(new ErrorResponse(ex.getMessage(), null));
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
}
