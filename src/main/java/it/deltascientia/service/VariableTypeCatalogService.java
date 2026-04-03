package it.deltascientia.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.deltascientia.model.VariableType;
import it.deltascientia.repository.VariableTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

/**
 * Handles bootstrap loading of the standard variable type catalog from
 * {@code variable-types.json} on application startup.
 * <p>
 * If the catalog is already populated (at least one row exists), this
 * component does nothing, ensuring idempotent startup behavior.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VariableTypeCatalogService {

    private final VariableTypeRepository variableTypeRepository;
    private final ObjectMapper objectMapper;

    /**
     * Seeds the variable_type table from {@code variable-types.json}
     * on first application start. Skipped if data already exists.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void loadCatalogIfEmpty() {
        if (variableTypeRepository.count() > 0) {
            log.info("Variable type catalog already populated, skipping bootstrap");
            return;
        }

        try {
            ClassPathResource resource = new ClassPathResource("variable-types.json");
            try (InputStream is = resource.getInputStream()) {
                List<VariableType> types = objectMapper.readValue(is, new TypeReference<>() {});
                variableTypeRepository.saveAll(types);
                log.info("Seeded {} variable types from catalog", types.size());
            }
        } catch (Exception e) {
            log.warn("Failed to load variable type catalog: {}", e.getMessage());
        }
    }
}
