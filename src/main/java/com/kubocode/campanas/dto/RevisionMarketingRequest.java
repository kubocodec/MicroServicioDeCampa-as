package com.kubocode.campanas.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * Datos de la sección "REVISIÓN DE MARKETING" del formato.
 */
@Data
public class RevisionMarketingRequest {
    private LocalDate fechaRecepcion;
    private String responsable;
    private String observaciones;
}
