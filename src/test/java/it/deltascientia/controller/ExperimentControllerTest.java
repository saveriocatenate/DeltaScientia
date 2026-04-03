package it.deltascientia.controller;

import it.deltascientia.dto.ExperimentCreateRequest;
import it.deltascientia.dto.ExperimentResponse;
import it.deltascientia.exception.model.ExperimentNotFoundException;
import it.deltascientia.service.ExperimentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExperimentController.class)
class ExperimentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExperimentService experimentService;

    private ExperimentResponse emptyResponse = ExperimentResponse.builder()
            .id(1L)
            .name("Test Experiment")
            .description("A test")
            .category("research")
            .status("DRAFT")
            .createdBy("testuser")
            .notes("Notes")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .variables(List.of())
            .trials(List.of())
            .build();

    @Test
    void listExperiments_shouldReturn200() throws Exception {
        Pageable pageable = PageRequest.of(0, 20);
        Page<ExperimentResponse> page = new PageImpl<>(List.of(emptyResponse));
        when(experimentService.listAll(any())).thenReturn(page);

        mockMvc.perform(get("/api/experiments")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getExperimentById_shouldReturn200() throws Exception {
        when(experimentService.getById(1L)).thenReturn(emptyResponse);

        mockMvc.perform(get("/api/experiments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getExperimentById_shouldReturn404() throws Exception {
        when(experimentService.getById(99L)).thenThrow(new ExperimentNotFoundException(99L));

        mockMvc.perform(get("/api/experiments/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createExperiment_shouldReturn201() throws Exception {
        when(experimentService.create(any(ExperimentCreateRequest.class))).thenReturn(emptyResponse);

        String body = """
                {"name":"Test","description":"A test","category":"research","status":"DRAFT","createdBy":"user","notes":"","variables":[]}
                """;

        mockMvc.perform(post("/api/experiments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createExperiment_shouldReturn400_whenNameMissing() throws Exception {
        String body = """
                {"name":"","description":"","variables":[]}
                """;

        mockMvc.perform(post("/api/experiments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteExperiment_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/experiments/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteExperiment_shouldReturn404() throws Exception {
        doThrow(new ExperimentNotFoundException(99L)).when(experimentService).deleteById(99L);

        mockMvc.perform(delete("/api/experiments/99"))
                .andExpect(status().isNotFound());
    }
}
