package it.deltascientia.service;

import it.deltascientia.dto.VariableTypeResponse;
import it.deltascientia.dto.VariableTypeSummary;
import it.deltascientia.mapper.VariableTypeMapper;
import it.deltascientia.model.VariableType;
import it.deltascientia.repository.VariableTypeRepository;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VariableTypeServiceTest {

    @Mock
    private VariableTypeRepository variableTypeRepository;

    @InjectMocks
    private VariableTypeService variableTypeService;

    private VariableType standardType;
    private VariableType customType;

    @BeforeEach
    void setUp() {
        standardType = VariableType.builder()
                .id(1L)
                .name("Temperature")
                .dataType("NUMERIC")
                .unitOfMeasure("°C")
                .description("Ambient temperature")
                .isCustom(false)
                .createdAt(Instant.parse("2025-04-01T08:00:00Z"))
                .build();

        customType = VariableType.builder()
                .id(2L)
                .name("Custom Metric")
                .dataType("NUMERIC")
                .unitOfMeasure("units")
                .description("A custom metric")
                .isCustom(true)
                .createdAt(Instant.parse("2025-04-02T10:00:00Z"))
                .build();
    }

    @Test
    void resolveFromRequest_shouldReturnExistingCatalogType_whenTypeNameProvided() {
        when(variableTypeRepository.findByNameIgnoreCase("Temperature")).thenReturn(Optional.of(standardType));

        VariableType result = variableTypeService.resolveFromRequest("Temperature", null, null, null, null);

        assertThat(result).isEqualTo(standardType);
    }

    @Test
    void resolveFromRequest_shouldThrow_whenTypeNameNotFound() {
        when(variableTypeRepository.findByNameIgnoreCase("Missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> variableTypeService.resolveFromRequest("Missing", null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Missing");
    }

    @Test
    void resolveFromRequest_shouldThrow_whenBothNull() {
        assertThatThrownBy(() -> variableTypeService.resolveFromRequest(null, null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void resolveFromRequest_shouldCreateNew_whenNameNotFound() {
        when(variableTypeRepository.findByNameIgnoreCase("Custom Metric")).thenReturn(Optional.empty());
        when(variableTypeRepository.save(any(VariableType.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VariableType result = variableTypeService.resolveFromRequest(null, "Custom Metric", "units", "NUMERIC", "A custom metric");

        assertThat(result.getName()).isEqualTo("Custom Metric");
        assertThat(result.getIsCustom()).isTrue();
        assertThat(result.getDataType()).isEqualTo("NUMERIC");
        verify(variableTypeRepository).save(any(VariableType.class));
    }

    @Test
    void resolveFromRequest_shouldReturnExistingName_whenFoundByName() {
        when(variableTypeRepository.findByNameIgnoreCase("Temperature")).thenReturn(Optional.of(standardType));

        VariableType result = variableTypeService.resolveFromRequest(null, "Temperature", null, null, null);

        assertThat(result).isEqualTo(standardType);
        verify(variableTypeRepository, never()).save(any());
    }

    @Test
    void listAll_shouldReturnPaginatedResponse() {
        Page<VariableType> page = new PageImpl<>(List.of(standardType, customType));
        Pageable pageable = PageRequest.of(0, 20);
        when(variableTypeRepository.findAll(pageable)).thenReturn(page);

        VariableTypeResponse response = variableTypeService.listAll(pageable);

        assertThat(response.content()).hasSize(2);
        assertThat(response.page()).isEqualTo(0);
        assertThat(response.totalElements()).isEqualTo(2);
    }

    @Test
    void deleteById_shouldDelete() {
        when(variableTypeRepository.findById(1L)).thenReturn(Optional.of(standardType));

        assertThatCode(() -> variableTypeService.deleteById(1L)).doesNotThrowAnyException();

        verify(variableTypeRepository).delete(standardType);
    }

    @Test
    void deleteById_shouldThrow_whenNotFound() {
        when(variableTypeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> variableTypeService.deleteById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("99");
    }
}
