package it.deltascientia.service;

import it.deltascientia.model.Experiment;
import it.deltascientia.model.Variable;
import it.deltascientia.model.VariableType;
import it.deltascientia.repository.VariableRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VariableServiceTest {

    @Mock
    private VariableRepository variableRepository;

    @InjectMocks
    private VariableService variableService;

    @Test
    void findByIdAndExperimentId_shouldReturnVariable_whenFoundAndBelongsToExperiment() {
        Experiment experiment = Experiment.builder().id(1L).name("Test").build();
        Variable variable = Variable.builder()
                .id(5L)
                .experiment(experiment)
                .variableType(VariableType.builder().name("Temp").build())
                .build();
        when(variableRepository.findById(5L)).thenReturn(Optional.of(variable));

        Variable result = variableService.findByIdAndExperimentId(5L, 1L);

        assertThat(result).isEqualTo(variable);
        assertThat(result.getId()).isEqualTo(5L);
    }

    @Test
    void findByIdAndExperimentId_shouldThrow_whenVariableNotFound() {
        when(variableRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> variableService.findByIdAndExperimentId(5L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("5");
    }

    @Test
    void findByIdAndExperimentId_shouldThrow_whenBelongsToDifferentExperiment() {
        Experiment experiment = Experiment.builder().id(2L).name("Other").build();
        Variable variable = Variable.builder()
                .id(5L)
                .experiment(experiment)
                .build();
        when(variableRepository.findById(5L)).thenReturn(Optional.of(variable));

        assertThatThrownBy(() -> variableService.findByIdAndExperimentId(5L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("experiment 1");
    }
}
