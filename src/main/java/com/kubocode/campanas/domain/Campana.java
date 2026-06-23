package com.kubocode.campanas.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Solicitud de Campaña Comercial — FORM-COM-001 (SOPRINT CÍA. LTDA.).
 * Cada instancia representa una propuesta de campaña que recorre el flujo:
 * BORRADOR → ENVIADA → EN_REVISION → decisión del Comité.
 */
@Entity
@Table(name = "campanas")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Campana {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "fecha_solicitud")
    private LocalDate fechaSolicitud;

    // ── 1. Información general ───────────────────────────────────────
    @Column(length = 255)
    private String solicitante;

    @Column(length = 150)
    private String cargo;

    /** Sucursal: QUITO | GUAYAQUIL | CUENCA | MATRIZ */
    @Column(length = 50)
    private String sucursal;

    /** Área: COMERCIAL | PUNTO_DE_VENTA | GERENCIA_COMERCIAL | GERENCIA_GENERAL | OTRO */
    @Column(name = "area_solicitante", length = 50)
    private String areaSolicitante;

    @Column(name = "area_solicitante_otro", length = 150)
    private String areaSolicitanteOtro;

    // ── 2. Nombre preliminar ────────────────────────────────────────
    @Column(name = "nombre_preliminar", length = 255, nullable = false)
    private String nombrePreliminar;

    // ── 3. Objetivo comercial ───────────────────────────────────────
    /** Objetivo principal (un valor del catálogo o OTRO). */
    @Column(name = "objetivo_principal", length = 80)
    private String objetivoPrincipal;

    @Column(name = "objetivo_otro", length = 150)
    private String objetivoOtro;

    @Column(name = "objetivo_detalle", columnDefinition = "TEXT")
    private String objetivoDetalle;

    // ── 4. Productos involucrados ───────────────────────────────────
    @Column(columnDefinition = "TEXT")
    private String marcas;

    @Column(columnDefinition = "TEXT")
    private String productos;

    // ── 5. Inventario a rotar ───────────────────────────────────────
    @Column(name = "inv_producto_codigo", columnDefinition = "TEXT")
    private String invProductoCodigo;

    @Column(name = "inv_cantidad_disponible", length = 100)
    private String invCantidadDisponible;

    /** Inventario actual en USD. */
    @Column(name = "inv_actual_usd", precision = 15, scale = 2)
    private BigDecimal invActualUsd;

    @Column(name = "inv_tiempo_estimado", length = 150)
    private String invTiempoEstimado;

    /** Problema(s) identificado(s); valores separados por coma. */
    @Column(name = "inv_problema", length = 255)
    private String invProblema;

    @Column(name = "inv_problema_detalle", columnDefinition = "TEXT")
    private String invProblemaDetalle;

    // ── 6. Meta comercial ───────────────────────────────────────────
    @Column(name = "meta_ventas_proyectadas", columnDefinition = "TEXT")
    private String metaVentasProyectadas;

    @Column(name = "meta_facturacion_usd", precision = 15, scale = 2)
    private BigDecimal metaFacturacionUsd;

    @Column(name = "meta_unidades_objetivo")
    private Integer metaUnidadesObjetivo;

    @Column(name = "meta_ticket_promedio_usd", precision = 15, scale = 2)
    private BigDecimal metaTicketPromedioUsd;

    // ── 7. Duración de la campaña ────────────────────────────────────
    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "duracion_estimada", length = 150)
    private String duracionEstimada;

    // ── 8. Propuesta comercial inicial ──────────────────────────────
    /** Tipos de propuesta; valores separados por coma. */
    @Column(name = "propuesta_tipos", length = 255)
    private String propuestaTipos;

    @Column(name = "propuesta_detalle", columnDefinition = "TEXT")
    private String propuestaDetalle;

    // ── 9. Justificación comercial ──────────────────────────────────
    @Column(columnDefinition = "TEXT")
    private String justificacion;

    // ── 10. Impacto esperado ────────────────────────────────────────
    /** Impactos esperados; valores separados por coma. */
    @Column(name = "impacto_tipos", length = 255)
    private String impactoTipos;

    @Column(name = "impacto_detalle", columnDefinition = "TEXT")
    private String impactoDetalle;

    // ── Revisión de Marketing ───────────────────────────────────────
    @Column(name = "revision_fecha_recepcion")
    private LocalDate revisionFechaRecepcion;

    @Column(name = "revision_responsable", length = 255)
    private String revisionResponsable;

    @Column(name = "revision_observaciones", columnDefinition = "TEXT")
    private String revisionObservaciones;

    @Column(name = "revision_by_user_id")
    private UUID revisionByUserId;

    @Column(name = "revision_at")
    private LocalDateTime revisionAt;

    // ── Decisión del Comité Comercial ───────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(name = "decision_comite", length = 30)
    private DecisionComite decisionComite;

    @Column(name = "decision_observaciones", columnDefinition = "TEXT")
    private String decisionObservaciones;

    @Column(name = "decision_by_user_id")
    private UUID decisionByUserId;

    @Column(name = "decision_at")
    private LocalDateTime decisionAt;

    // ── Estado / multi-tenant / auditoría ───────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 30, nullable = false)
    @Builder.Default
    private EstadoCampana estado = EstadoCampana.BORRADOR;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "branch_id")
    private UUID branchId;

    @Column(name = "created_by_user_id")
    private UUID createdByUserId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
