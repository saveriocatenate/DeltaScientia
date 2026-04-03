package it.deltascientia.mapper;

import it.deltascientia.dto.TrialCreateRequest;
import it.deltascientia.dto.TrialResponse;
import it.deltascientia.model.Trial;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;

class TrialMapperTest {

    @Test
    void toEntity_shouldConvertRequestToTrial() {
        var request = TrialCreateRequest.builder()
                .trialNumber(1)
                .label("Trial A")
                .notes("Some notes")
                .executionDate(Instant.parse("2025-04-01T09:00:00Z"))
                .build();

        Trial result = TrialMapper.toEntity(request, 3);

        assertThat(result.getTrialNumber()).isEqualTo(3);
        assertThat(result.getLabel()).isEqualTo("Trial A");
        assertThat(result.getNotes()).isEqualTo("Some notes");
        assertThat(result.getExecutionDate()).isEqualTo(Instant.parse("2025-04-01T09:00:00Z"));
        assertThat(result.getValues()).isEmpty();
    }

    @Test
    void toResponse_shouldConvertTrialToResponse() {
        Trial trial = Trial.builder()
                .id(5L)
                .trialNumber(3)
                .label("Trial B")
                .notes("More notes")
                .executionDate(Instant.parse("2025-04-02T10:00:00Z"))
                .createdAt(Instant.now())
                .values(new ArrayList<>())
                .build();

        TrialResponse response = TrialMapper.toResponse(trial, 10L);

        assertThat(response.id()).isEqualTo(5L);
        assertThat(response.experimentId()).isEqualTo(10L);
        assertThat(response.trialNumber()).isEqualTo(3);
        assertThat(response.label()).isEqualTo("Trial B");
        assertThat(response.executionDate()).isEqualTo(Instant.parse("2025-04-02T10:00:00Z"));
        assertThat(response.values()).isEmpty();
    }
}
