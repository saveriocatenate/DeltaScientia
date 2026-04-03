package it.deltascientia.controller;

import it.deltascientia.dto.*;
import it.deltascientia.exception.model.ExperimentNotFoundException;
import it.deltascientia.exception.model.TrialNotFoundException;
import it.deltascientia.service.TrialService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrialController.class)
class TrialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrialService trialService;

    private TrialResponse sampleResponse() {
        return TrialResponse.builder()
                .id(1L)
                .experimentId(1L)
                .trialNumber(1)
                .label("Trial A")
                .notes("Some notes")
                .executionDate(Instant.parse("2025-04-01T09:00:00Z"))
                .createdAt(Instant.now())
                .values(List.of())
                .build();
    }

    @Test
    void createTrial_shouldReturn201() throws Exception {
        when(trialService.create(eq(1L), any(TrialCreateRequest.class))).thenReturn(sampleResponse());

        String body = """
                {"label":"Trial A","executionDate":"2025-04-01T09:00:00Z","values":[]}
                """;

        mockMvc.perform(post("/api/experiments/1/trials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.label").value("Trial A"));
    }

    @Test
    void listTrials_shouldReturn200() throws Exception {
        Page<TrialResponse> page = new PageImpl<>(List.of(sampleResponse()));
        Pageable pageable = PageRequest.of(0, 20);
        when(trialService.listAll(eq(1L), any())).thenReturn(page);

        mockMvc.perform(get("/api/experiments/1/trials")
                        .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getTrialById_shouldReturn200() throws Exception {
        when(trialService.getById(1L, 1L)).thenReturn(sampleResponse());

        mockMvc.perform(get("/api/experiments/1/trials/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getTrialById_shouldReturn404() throws Exception {
        when(trialService.getById(1L, 99L)).thenThrow(new TrialNotFoundException(99L, 1L));

        mockMvc.perform(get("/api/experiments/1/trials/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchTrials_shouldReturn200() throws Exception {
        Page<TrialResponse> page = new PageImpl<>(List.of(sampleResponse()));
        when(trialService.search(eq(1L), any(), any())).thenReturn(page);

        mockMvc.perform(post("/api/experiments/1/trials/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteTrial_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/experiments/1/trials/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTrial_shouldReturn404() throws Exception {
        doThrow(new TrialNotFoundException(99L, 1L)).when(trialService).deleteById(1L, 99L);

        mockMvc.perform(delete("/api/experiments/1/trials/99"))
                .andExpect(status().isNotFound());
    }
}
