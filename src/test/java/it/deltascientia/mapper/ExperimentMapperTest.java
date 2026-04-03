package it.deltascientia.mapper;

import it.deltascientia.dto.ExperimentCreateRequest;
import it.deltascientia.dto.ResolvedVariable;
import it.deltascientia.dto.VariableRequest;
import it.deltascientia.model.Experiment;
import it.deltascientia.model.VariableType;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ExperimentMapperTest {

    @Test
    void toEntity_shouldBuildExperimentWithVariables() {
        VariableType type = VariableType.builder().name("Temperature").build();
        VariableRequest varReq = new VariableRequest("Temperature", null, null, null, null);
        ExperimentCreateRequest request = new ExperimentCreateRequest(
                "Name", "Desc", "Cat", "DRAFT", "user", "Notes",
                List.of(varReq));
        List<ResolvedVariable> resolvedVars = List.of(new ResolvedVariable(type));

        Experiment result = ExperimentMapper.toEntity(request, resolvedVars);

        assertThat(result.getName()).isEqualTo("Name");
        assertThat(result.getVariables()).hasSize(1);
        assertThat(result.getVariables().get(0).getVariableType()).isEqualTo(type);
    }

    @Test
    void toResponse_shouldConvertExperimentToResponse() {
        Experiment experiment = Experiment.builder()
                .id(1L)
                .name("Test Experiment")
                .description("A test")
                .category("research")
                .status("DRAFT")
                .createdBy("user")
                .notes("Notes")
                .trials(new ArrayList<>())
                .variables(new ArrayList<>())
                .build();

        var response = ExperimentMapper.toResponse(experiment);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Test Experiment");
        assertThat(response.description()).isEqualTo("A test");
        assertThat(response.category()).isEqualTo("research");
        assertThat(response.status()).isEqualTo("DRAFT");
        assertThat(response.createdBy()).isEqualTo("user");
        assertThat(response.notes()).isEqualTo("Notes");
        assertThat(response.variables()).isNotNull();
        assertThat(response.trials()).isNotNull();
    }

    @Test
    void constructorShouldThrow() {
        assertThatThrownBy(() -> {
            Constructor<?> ctor = ExperimentMapper.class.getDeclaredConstructor();
            ctor.setAccessible(true);
            ctor.newInstance();
        }).hasCauseInstanceOf(UnsupportedOperationException.class);
    }
}
