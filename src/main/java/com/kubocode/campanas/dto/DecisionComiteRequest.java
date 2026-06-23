package com.kubocode.campanas.dto;

import com.kubocode.campanas.domain.DecisionComite;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Datos de la sección "DECISIÓN DEL COMITÉ COMERCIAL" del formato.
 */
@Data
public class DecisionComiteRequest {

    @NotNull(message = "La decisión del comité es obligatoria")
    private DecisionComite decision;

    private String observaciones;
}
