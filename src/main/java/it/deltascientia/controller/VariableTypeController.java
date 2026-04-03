package it.deltascientia.controller;

import it.deltascientia.dto.VariableTypeResponse;
import it.deltascientia.service.VariableTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing VariableType catalog endpoints.
 */
@RestController
@RequestMapping("/api/variable-types")
@RequiredArgsConstructor
public class VariableTypeController {

    private final VariableTypeService variableTypeService;

    /**
     * Lists all available variable types with pagination support,
     * ordered alphabetically by name.
     *
     * @param pageable page number, size, and optional sort parameters
     * @return 200 OK with paginated variable type data
     */
    @GetMapping
    public ResponseEntity<VariableTypeResponse> listVariableTypes(
            @PageableDefault(page = 0, size = 20, sort = "name") Pageable pageable) {
        VariableTypeResponse response = variableTypeService.listAll(pageable);
        return ResponseEntity.ok(response);
    }
}
