package com.kubocode.campanas.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Payload para crear o actualizar una Solicitud de Campaña Comercial.
 * Solo el nombre preliminar es obligatorio para permitir guardar borradores.
 */
@Data
public class CampanaRequest {

    private LocalDate fechaSolicitud;

    // 1. Información general
    private String solicitante;
    private String cargo;
    private String sucursal;
    private String areaSolicitante;
    private String areaSolicitanteOtro;

    // 2. Nombre preliminar
    @NotBlank(message = "El nombre preliminar es obligatorio")
    private String nombrePreliminar;

    // 3. Objetivo comercial
    private String objetivoPrincipal;
    private String objetivoOtro;
    private String objetivoDetalle;

    // 4. Productos involucrados
    private String marcas;
    private String productos;

    // 5. Inventario a rotar
    private String invProductoCodigo;
    private String invCantidadDisponible;
    private BigDecimal invActualUsd;
    private String invTiempoEstimado;
    private String invProblema;
    private String invProblemaDetalle;

    // 6. Meta comercial
    private String metaVentasProyectadas;
    private BigDecimal metaFacturacionUsd;
    private Integer metaUnidadesObjetivo;
    private BigDecimal metaTicketPromedioUsd;

    // 7. Duración
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String duracionEstimada;

    // 8. Propuesta comercial inicial
    private String propuestaTipos;
    private String propuestaDetalle;

    // 9. Justificación
    private String justificacion;

    // 10. Impacto esperado
    private String impactoTipos;
    private String impactoDetalle;
}
