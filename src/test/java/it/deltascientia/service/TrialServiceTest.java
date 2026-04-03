package it.deltascientia.service;

import it.deltascientia.dto.*;
import it.deltascientia.exception.model.TrialNotFoundException;
import it.deltascientia.model.Experiment;
import it.deltascientia.model.Trial;
import it.deltascientia.model.TrialValue;
import it.deltascientia.model.VariableType;
import it.deltascientia.model.Variable;
import it.deltascientia.repository.TrialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrialServiceTest {

    @Mock
    private TrialRepository trialRepository;

    @Mock
    private VariableService variableService;

    @Mock
    private ExperimentService experimentService;

    @InjectMocks
    private TrialService trialService;

    private Long experimentId = 1L;
    private Trial trial;

    @BeforeEach
    void setUp() {
        Experiment experiment = Experiment.builder()
                .id(experimentId)
                .name("Test Experiment")
                .build();

        trial = Trial.builder()
                .id(1L)
                .trialNumber(1)
                .label("Trial A")
                .notes("Some notes")
                .executionDate(Instant.parse("2025-04-01T09:00:00Z"))
                .createdAt(Instant.now())
                .experiment(experiment)
                .values(new ArrayList<>())
                .build();
    }

    @Test
    void create_shouldReturnResponse() {
        var request = TrialCreateRequest.builder()
                .label("Trial A")
                .executionDate(Instant.parse("2025-04-01T09:00:00Z"))
                .build();

        when(experimentService.getById(experimentId)).thenReturn(null);
        when(trialRepository.findMaxTrialNumberByExperimentId(experimentId)).thenReturn(Optional.of(1));
        when(trialRepository.save(any(Trial.class))).thenAnswer(invocation -> {
            Trial saved = invocation.getArgument(0);
            saved.setId(1L);
            saved.setCreatedAt(Instant.now());
            return saved;
        });

        TrialResponse response = trialService.create(experimentId, request);

        assertThat(response).isNotNull();
        assertThat(response.trialNumber()).isEqualTo(2);
        verify(trialRepository).save(any(Trial.class));
    }

    @Test
    void getById_shouldReturnResponse() {
        when(trialRepository.findByIdAndExperimentId(1L, experimentId)).thenReturn(Optional.of(trial));

        TrialResponse response = trialService.getById(experimentId, 1L);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.label()).isEqualTo("Trial A");
    }

    @Test
    void getById_shouldThrowException_whenNotFound() {
        when(trialRepository.findByIdAndExperimentId(99L, experimentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trialService.getById(experimentId, 99L))
                .isInstanceOf(TrialNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void listAll_shouldReturnPaginatedResponse() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Trial> page = new PageImpl<>(List.of(trial));
        when(trialRepository.findByExperimentIdOrderById(experimentId, pageable)).thenReturn(page);

        var result = trialService.listAll(experimentId, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).label()).isEqualTo("Trial A");
    }

    @Test
    void search_shouldReturnPaginatedResponse() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Trial> page = new PageImpl<>(List.of(trial));
        when(trialRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), eq(pageable))).thenReturn(page);

        var request = TrialSearchRequest.builder().build();
        var result = trialService.search(experimentId, request, pageable);

        assertThat(result).hasSize(1);
    }

    @Test
    void deleteById_shouldDelete() {
        when(trialRepository.findByIdAndExperimentId(1L, experimentId)).thenReturn(Optional.of(trial));

        assertThatCode(() -> trialService.deleteById(experimentId, 1L)).doesNotThrowAnyException();

        verify(trialRepository).delete(trial);
    }

    @Test
    void deleteById_shouldThrowException_whenNotFound() {
        when(trialRepository.findByIdAndExperimentId(99L, experimentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> trialService.deleteById(experimentId, 99L))
                .isInstanceOf(TrialNotFoundException.class);
    }
}
