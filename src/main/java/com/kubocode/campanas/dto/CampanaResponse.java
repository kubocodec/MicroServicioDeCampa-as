package com.kubocode.campanas.dto;

import com.kubocode.campanas.domain.DecisionComite;
import com.kubocode.campanas.domain.EstadoCampana;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CampanaResponse {

    private UUID id;
    private LocalDate fechaSolicitud;

    // 1. Información general
    private String solicitante;
    private String cargo;
    private String sucursal;
    private String areaSolicitante;
    private String areaSolicitanteOtro;

    // 2. Nombre preliminar
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

    // Revisión de Marketing
    private LocalDate revisionFechaRecepcion;
    private String revisionResponsable;
    private String revisionObservaciones;
    private UUID revisionByUserId;
    private LocalDateTime revisionAt;

    // Decisión del Comité
    private DecisionComite decisionComite;
    private String decisionObservaciones;
    private UUID decisionByUserId;
    private LocalDateTime decisionAt;

    // Estado / auditoría
    private EstadoCampana estado;
    private UUID companyId;
    private UUID branchId;
    private UUID createdByUserId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
