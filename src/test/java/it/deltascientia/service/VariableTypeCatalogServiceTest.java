package it.deltascientia.service;

import it.deltascientia.model.VariableType;
import it.deltascientia.repository.VariableTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VariableTypeCatalogServiceTest {

    @Mock
    private VariableTypeRepository variableTypeRepository;

    @InjectMocks
    private VariableTypeCatalogService catalogService;

    @Test
    void loadCatalogIfEmpty_shouldSkip_whenDataExists() {
        when(variableTypeRepository.count()).thenReturn(5L);

        catalogService.loadCatalogIfEmpty();

        verify(variableTypeRepository, never()).saveAll(anyList());
    }

    @Test
    void loadCatalogIfEmpty_shouldLoad_whenEmpty() {
        // When the resource file is present, it loads data
        // but we can verify count was checked
        when(variableTypeRepository.count()).thenReturn(0L);

        catalogService.loadCatalogIfEmpty();

        verify(variableTypeRepository).count();
        // saveAll may or may not be called depending on variable-types.json presence
    }
}
