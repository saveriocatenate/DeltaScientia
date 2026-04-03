package it.deltascientia.controller;

import it.deltascientia.dto.VariableTypeResponse;
import it.deltascientia.dto.VariableTypeSummary;
import it.deltascientia.service.VariableTypeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VariableTypeController.class)
class VariableTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VariableTypeService variableTypeService;

    private VariableTypeResponse sampleResponse() {
        var summary = VariableTypeSummary.builder()
                .id(1L)
                .name("Temperature")
                .dataType("NUMERIC")
                .unitOfMeasure("°C")
                .description("Ambient temperature")
                .isCustom(false)
                .createdAt(Instant.parse("2025-04-01T08:00:00Z"))
                .build();

        return new VariableTypeResponse(
                List.of(summary), 0, 20, 1, 1, true);
    }

    @Test
    void listVariableTypes_shouldReturn200() throws Exception {
        when(variableTypeService.listAll(any())).thenReturn(sampleResponse());

        mockMvc.perform(get("/api/variable-types")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Temperature"));
    }

    @Test
    void deleteVariableType_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/variable-types/1"))
                .andExpect(status().isNoContent());
    }
}
