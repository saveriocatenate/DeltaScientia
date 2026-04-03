package it.deltascientia.exception;

import it.deltascientia.exception.model.ErrorResponse;
import it.deltascientia.exception.model.ExperimentNotFoundException;
import it.deltascientia.exception.model.TrialNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleExperimentNotFound_shouldReturn404() {
        ResponseEntity<ErrorResponse> response = handler.handleExperimentNotFound(
                new ExperimentNotFoundException(42L));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).contains("42");
    }

    @Test
    void handleTrialNotFound_shouldReturn404() {
        ResponseEntity<ErrorResponse> response = handler.handleTrialNotFound(
                new TrialNotFoundException(10L, 5L));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).contains("10").contains("5");
    }
}
