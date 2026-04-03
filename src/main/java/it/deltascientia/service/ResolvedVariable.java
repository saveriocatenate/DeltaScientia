package it.deltascientia.service;

import it.deltascientia.model.VariableType;

/**
 * Holds a resolved variable type reference.
 *
 * @param type the resolved VariableType entity
 */
public record ResolvedVariable(VariableType type) {}
