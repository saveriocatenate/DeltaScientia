package it.deltascientia.service;

import it.deltascientia.dto.ExperimentCreateRequest;
import it.deltascientia.dto.ExperimentResponse;
import it.deltascientia.dto.VariableRequest;
import it.deltascientia.exception.model.ExperimentNotFoundException;
import it.deltascientia.mapper.ExperimentMapper;
import it.deltascientia.model.Experiment;
import it.deltascientia.model.VariableType;
import it.deltascientia.repository.ExperimentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExperimentServiceTest {

    @Mock
    private ExperimentRepository experimentRepository;

    @Mock
    private VariableTypeService variableTypeService;

    @InjectMocks
    private ExperimentService experimentService;

    private ExperimentCreateRequest createRequest;
    private VariableType variableType;

    @BeforeEach
    void setUp() {
        variableType = VariableType.builder()
                .id(1L)
                .name("Temperature")
                .dataType("NUMERIC")
                .unitOfMeasure("°C")
                .isCustom(false)
                .build();

        createRequest = new ExperimentCreateRequest(
                "Test Experiment",
                "A test experiment",
                "thermodynamics",
                "DRAFT",
                "testuser",
                "Some notes",
                List.of(new VariableRequest("Temperature", null, null, null, null)));
    }

    @Test
    void create_shouldPersistsExperiment() {
        when(variableTypeService.resolveFromRequest("Temperature", null, null, null, null)).thenReturn(variableType);
        when(experimentRepository.save(any(Experiment.class))).thenAnswer(invocation -> {
            Experiment exp = invocation.getArgument(0);
            exp.setId(1L);
            exp.setCreatedAt(Instant.now());
            exp.setUpdatedAt(Instant.now());
            return exp;
        });

        ExperimentResponse response = experimentService.create(createRequest);

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("Test Experiment");
        verify(experimentRepository).save(any(Experiment.class));
    }

    @Test
    void getById_shouldReturnResponse_whenExperimentExists() {
        Experiment experiment = buildExperiment(1L);
        when(experimentRepository.findById(1L)).thenReturn(Optional.of(experiment));

        ExperimentResponse response = experimentService.getById(1L);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Test Experiment");
    }

    @Test
    void getById_shouldThrowException_whenExperimentNotFound() {
        when(experimentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> experimentService.getById(99L))
                .isInstanceOf(ExperimentNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deleteById_shouldDelete_whenExperimentExists() {
        Experiment experiment = buildExperiment(1L);
        when(experimentRepository.findById(1L)).thenReturn(Optional.of(experiment));

        assertThatCode(() -> experimentService.deleteById(1L)).doesNotThrowAnyException();

        verify(experimentRepository).delete(experiment);
    }

    @Test
    void deleteById_shouldThrowException_whenExperimentNotFound() {
        when(experimentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> experimentService.deleteById(99L))
                .isInstanceOf(ExperimentNotFoundException.class);
    }

    @Test
    void listAll_shouldReturnPaginatedResponse() {
        Experiment experiment = buildExperiment(1L);
        Page<Experiment> page = new PageImpl<>(List.of(experiment));
        Pageable pageable = PageRequest.of(0, 20);
        when(experimentRepository.findAll(pageable)).thenReturn(page);

        var result = experimentService.listAll(pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).name()).isEqualTo("Test Experiment");
    }

    private Experiment buildExperiment(Long id) {
        return Experiment.builder()
                .id(id)
                .name("Test Experiment")
                .description("A test")
                .category("thermodynamics")
                .status("DRAFT")
                .createdBy("testuser")
                .notes("Notes")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }
}
