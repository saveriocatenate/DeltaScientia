package it.deltascientia.mapper;

import it.deltascientia.dto.VariableTypeResponse;
import it.deltascientia.dto.VariableTypeSummary;
import it.deltascientia.model.VariableType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class VariableTypeMapperTest {

    @Test
    void toNewEntity_shouldBuildVariableType() {
        VariableType result = VariableTypeMapper.toNewEntity("Temp", "°C", "NUMERIC", "Ambient temp", false);

        assertThat(result.getName()).isEqualTo("Temp");
        assertThat(result.getUnitOfMeasure()).isEqualTo("°C");
        assertThat(result.getDataType()).isEqualTo("NUMERIC");
        assertThat(result.getDescription()).isEqualTo("Ambient temp");
        assertThat(result.getIsCustom()).isFalse();
    }

    @Test
    void toSummary_shouldConvertEntityToSummary() {
        VariableType vt = VariableType.builder()
                .id(1L)
                .name("pH")
                .unitOfMeasure("pH")
                .dataType("NUMERIC")
                .description("Acidity level")
                .isCustom(false)
                .createdAt(Instant.parse("2025-04-01T08:00:00Z"))
                .build();

        VariableTypeSummary summary = VariableTypeMapper.toSummary(vt);

        assertThat(summary.id()).isEqualTo(1L);
        assertThat(summary.name()).isEqualTo("pH");
        assertThat(summary.dataType()).isEqualTo("NUMERIC");
    }

    @Test
    void toResponse_shouldWrapSummariesInResponse() {
        var summary = VariableTypeSummary.builder()
                .id(1L)
                .name("pH")
                .dataType("NUMERIC")
                .unitOfMeasure("pH")
                .description("Acidity")
                .isCustom(false)
                .createdAt(Instant.now())
                .build();

        VariableTypeResponse response = VariableTypeMapper.toResponse(List.of(summary), 0, 10, 1, 1, true);

        assertThat(response).isNotNull();
        assertThat(response.page()).isEqualTo(0);
        assertThat(response.totalElements()).isEqualTo(1);
        assertThat(response.last()).isTrue();
    }
}
