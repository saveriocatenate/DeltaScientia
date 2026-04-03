package it.deltascientia.service;

import it.deltascientia.dto.ExperimentCreateRequest;
import it.deltascientia.dto.ExperimentCreateRequest.VariableRequest;
import it.deltascientia.dto.ExperimentResponse;
import it.deltascientia.mapper.ExperimentMapper;
import it.deltascientia.model.VariableType;
import it.deltascientia.repository.ExperimentRepository;
import it.deltascientia.repository.VariableTypeRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Business logic layer for Experiment operations.
 * Handles creation, retrieval, and domain-specific validation.
 */
@Service
@RequiredArgsConstructor
public class ExperimentService {

    private final ExperimentRepository experimentRepository;
    private final ExperimentMapper experimentMapper;
    private final VariableTypeRepository variableTypeRepository;

    /**
     * Creates a new experiment from the given request payload.
     * For each variable in the request:
     * <ul>
     *   <li>If {@code typeName} is provided, looks up the existing type (case-insensitive).</li>
     *   <li>If only {@code name} is provided, creates a new {@link VariableType}
     *       in the catalog and links it.</li>
     * </ul>
     *
     * @param request the creation request
     * @return the persisted experiment as a response DTO
     */
    @Transactional
    public ExperimentResponse create(ExperimentCreateRequest request) {
        List<ResolvedVariable> resolvedVars = new ArrayList<>();

        for (VariableRequest varReq : request.variables()) {
            if (varReq.typeName() != null) {
                VariableType type = variableTypeRepository.findByNameIgnoreCase(varReq.typeName())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Variable type '" + varReq.typeName() + "' not found in catalog"));
                resolvedVars.add(ResolvedVariable.builder().type(type)
                        .unitOfMeasure(varReq.unitOfMeasure()).dataType(varReq.dataType()).description(varReq.description()).build());
            } else if (varReq.name() != null) {
                VariableType existingType = variableTypeRepository.findByNameIgnoreCase(varReq.name()).orElse(null);
                if (existingType != null) {
                    resolvedVars.add(ResolvedVariable.builder().type(existingType)
                            .unitOfMeasure(varReq.unitOfMeasure()).dataType(varReq.dataType()).description(varReq.description()).build());
                } else {
                    VariableType newType = VariableType.builder()
                            .name(varReq.name())
                            .unitOfMeasure(varReq.unitOfMeasure())
                            .dataType(varReq.dataType() != null ? varReq.dataType() : "TEXT")
                            .description(varReq.description())
                            .isCustom(true)
                            .build();
                    variableTypeRepository.save(newType);
                    resolvedVars.add(ResolvedVariable.builder().type(newType)
                            .unitOfMeasure(varReq.unitOfMeasure()).dataType(varReq.dataType()).description(varReq.description()).build());
                }
            }
        }

        var experiment = experimentMapper.toEntity(request, resolvedVars);
        var saved = experimentRepository.save(experiment);
        return experimentMapper.toResponse(saved);
    }

    /**
     * Retrieves an experiment by its database identifier.
     *
     * @param id the experiment ID
     * @return the experiment as a response DTO with nested variables and trials
     * @throws ExperimentNotFoundException if no experiment exists for the given ID
     */
    @Transactional(readOnly = true)
    public ExperimentResponse getById(Long id) {
        var experiment = experimentRepository.findById(id)
                .orElseThrow(() -> new ExperimentNotFoundException(id));
        return experimentMapper.toResponse(experiment);
    }

    /**
     * Exception thrown when an experiment cannot be found by ID.
     */
    public static class ExperimentNotFoundException extends RuntimeException {
        public ExperimentNotFoundException(Long id) {
            super("Experiment not found with id: " + id);
        }
    }

    /**
     * Holds a resolved variable type reference and any user-provided overrides.
     */
    @Builder
    public record ResolvedVariable(
            VariableType type,
            String unitOfMeasure,
            String dataType,
            String description
    ) {}
}
