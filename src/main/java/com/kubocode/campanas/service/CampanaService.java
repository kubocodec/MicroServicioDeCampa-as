package com.kubocode.campanas.service;

import com.kubocode.campanas.domain.Campana;
import com.kubocode.campanas.domain.DecisionComite;
import com.kubocode.campanas.domain.EstadoCampana;
import com.kubocode.campanas.dto.CampanaRequest;
import com.kubocode.campanas.dto.CampanaResponse;
import com.kubocode.campanas.dto.DecisionComiteRequest;
import com.kubocode.campanas.dto.RevisionMarketingRequest;
import com.kubocode.campanas.exception.ResourceNotFoundException;
import com.kubocode.campanas.repository.CampanaRepository;
import com.kubocode.campanas.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CampanaService {

    /** Estados en los que el solicitante todavía puede editar / enviar. */
    private static final Set<EstadoCampana> EDITABLES = EnumSet.of(EstadoCampana.BORRADOR, EstadoCampana.DEVUELTA);

    private final CampanaRepository repository;

    // ────────────────────────────────────────────────────────────────
    // CREAR
    // ────────────────────────────────────────────────────────────────
    @Transactional
    public CampanaResponse crear(CampanaRequest req, AuthenticatedUser user) {
        Campana campana = Campana.builder()
                .estado(EstadoCampana.BORRADOR)
                .companyId(user.getCompanyId())
                .branchId(user.getBranchId())
                .createdByUserId(user.getUserId())
                .fechaSolicitud(req.getFechaSolicitud() != null ? req.getFechaSolicitud() : LocalDate.now())
                .build();
        aplicarDatos(campana, req);
        campana = repository.save(campana);
        return toResponse(campana);
    }

    // ────────────────────────────────────────────────────────────────
    // ACTUALIZAR (solo creador y en estado editable)
    // ────────────────────────────────────────────────────────────────
    @Transactional
    public CampanaResponse actualizar(UUID id, CampanaRequest req, AuthenticatedUser user) {
        Campana campana = obtener(id, user);

        boolean esCreador = user.getUserId().equals(campana.getCreatedByUserId());
        if (!esCreador && !user.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo el solicitante puede editar esta campaña");
        }
        if (!EDITABLES.contains(campana.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La campaña no se puede editar en estado " + campana.getEstado());
        }

        aplicarDatos(campana, req);
        campana = repository.save(campana);
        return toResponse(campana);
    }

    // ────────────────────────────────────────────────────────────────
    // ENVIAR a revisión de Marketing
    // ────────────────────────────────────────────────────────────────
    @Transactional
    public CampanaResponse enviar(UUID id, AuthenticatedUser user) {
        Campana campana = obtener(id, user);

        boolean esCreador = user.getUserId().equals(campana.getCreatedByUserId());
        if (!esCreador && !user.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo el solicitante puede enviar esta campaña");
        }
        if (!EDITABLES.contains(campana.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Solo se pueden enviar campañas en estado BORRADOR o DEVUELTA");
        }

        campana.setEstado(EstadoCampana.ENVIADA);
        campana = repository.save(campana);
        return toResponse(campana);
    }

    // ────────────────────────────────────────────────────────────────
    // REVISIÓN DE MARKETING (supervisor / admin)
    // ────────────────────────────────────────────────────────────────
    @Transactional
    public CampanaResponse registrarRevision(UUID id, RevisionMarketingRequest req, AuthenticatedUser user) {
        Campana campana = obtener(id, user);

        if (campana.getEstado() == EstadoCampana.BORRADOR) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La campaña aún no ha sido enviada por el solicitante");
        }

        campana.setRevisionFechaRecepcion(
                req.getFechaRecepcion() != null ? req.getFechaRecepcion() : LocalDate.now());
        campana.setRevisionResponsable(req.getResponsable());
        campana.setRevisionObservaciones(req.getObservaciones());
        campana.setRevisionByUserId(user.getUserId());
        campana.setRevisionAt(LocalDateTime.now());

        // Una vez revisada por Marketing pasa a EN_REVISION (pendiente del Comité),
        // salvo que ya tenga una decisión final tomada.
        if (campana.getEstado() == EstadoCampana.ENVIADA) {
            campana.setEstado(EstadoCampana.EN_REVISION);
        }

        campana = repository.save(campana);
        return toResponse(campana);
    }

    // ────────────────────────────────────────────────────────────────
    // DECISIÓN DEL COMITÉ COMERCIAL (supervisor / admin)
    // ────────────────────────────────────────────────────────────────
    @Transactional
    public CampanaResponse decidir(UUID id, DecisionComiteRequest req, AuthenticatedUser user) {
        Campana campana = obtener(id, user);

        if (campana.getEstado() == EstadoCampana.BORRADOR) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede decidir sobre una campaña que sigue en borrador");
        }

        DecisionComite decision = req.getDecision();
        campana.setDecisionComite(decision);
        campana.setDecisionObservaciones(req.getObservaciones());
        campana.setDecisionByUserId(user.getUserId());
        campana.setDecisionAt(LocalDateTime.now());
        campana.setEstado(mapDecisionAEstado(decision));

        campana = repository.save(campana);
        return toResponse(campana);
    }

    private EstadoCampana mapDecisionAEstado(DecisionComite decision) {
        return switch (decision) {
            case APROBADA -> EstadoCampana.APROBADA;
            case APROBADA_CON_CAMBIOS -> EstadoCampana.APROBADA_CON_CAMBIOS;
            case DEVUELTA -> EstadoCampana.DEVUELTA;
            case RECHAZADA -> EstadoCampana.RECHAZADA;
        };
    }

    // ────────────────────────────────────────────────────────────────
    // LISTADOS / DETALLE / ELIMINAR
    // ────────────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<CampanaResponse> listarMias(AuthenticatedUser user) {
        return repository
                .findByCompanyIdAndCreatedByUserIdOrderByCreatedAtDesc(user.getCompanyId(), user.getUserId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CampanaResponse> listarTodas(String estado, AuthenticatedUser user) {
        List<Campana> campanas;
        if (estado != null && !estado.isBlank()) {
            EstadoCampana e;
            try {
                e = EstadoCampana.valueOf(estado.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado inválido: " + estado);
            }
            campanas = repository.findByCompanyIdAndEstadoOrderByCreatedAtDesc(user.getCompanyId(), e);
        } else {
            campanas = repository.findByCompanyIdOrderByCreatedAtDesc(user.getCompanyId());
        }
        return campanas.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CampanaResponse obtenerDetalle(UUID id, AuthenticatedUser user) {
        return toResponse(obtener(id, user));
    }

    @Transactional
    public void eliminar(UUID id, AuthenticatedUser user) {
        Campana campana = obtener(id, user);
        boolean esCreador = user.getUserId().equals(campana.getCreatedByUserId());
        if (!esCreador && !user.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo el solicitante puede eliminar esta campaña");
        }
        if (campana.getEstado() != EstadoCampana.BORRADOR && !user.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Solo se pueden eliminar campañas en estado BORRADOR");
        }
        repository.delete(campana);
    }

    // ────────────────────────────────────────────────────────────────
    // Helpers
    // ────────────────────────────────────────────────────────────────
    private Campana obtener(UUID id, AuthenticatedUser user) {
        Campana campana = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaña no encontrada"));
        if (!user.getCompanyId().equals(campana.getCompanyId())) {
            // No exponer recursos de otra empresa
            throw new ResourceNotFoundException("Campaña no encontrada");
        }
        return campana;
    }

    /** Copia los datos editables del request a la entidad (no toca estado ni auditoría). */
    private void aplicarDatos(Campana c, CampanaRequest r) {
        if (r.getFechaSolicitud() != null) c.setFechaSolicitud(r.getFechaSolicitud());

        c.setSolicitante(r.getSolicitante());
        c.setCargo(r.getCargo());
        c.setSucursal(r.getSucursal());
        c.setAreaSolicitante(r.getAreaSolicitante());
        c.setAreaSolicitanteOtro(r.getAreaSolicitanteOtro());

        c.setNombrePreliminar(r.getNombrePreliminar());

        c.setObjetivoPrincipal(r.getObjetivoPrincipal());
        c.setObjetivoOtro(r.getObjetivoOtro());
        c.setObjetivoDetalle(r.getObjetivoDetalle());

        c.setMarcas(r.getMarcas());
        c.setProductos(r.getProductos());

        c.setInvProductoCodigo(r.getInvProductoCodigo());
        c.setInvCantidadDisponible(r.getInvCantidadDisponible());
        c.setInvActualUsd(r.getInvActualUsd());
        c.setInvTiempoEstimado(r.getInvTiempoEstimado());
        c.setInvProblema(r.getInvProblema());
        c.setInvProblemaDetalle(r.getInvProblemaDetalle());

        c.setMetaVentasProyectadas(r.getMetaVentasProyectadas());
        c.setMetaFacturacionUsd(r.getMetaFacturacionUsd());
        c.setMetaUnidadesObjetivo(r.getMetaUnidadesObjetivo());
        c.setMetaTicketPromedioUsd(r.getMetaTicketPromedioUsd());

        c.setFechaInicio(r.getFechaInicio());
        c.setFechaFin(r.getFechaFin());
        c.setDuracionEstimada(r.getDuracionEstimada());

        c.setPropuestaTipos(r.getPropuestaTipos());
        c.setPropuestaDetalle(r.getPropuestaDetalle());

        c.setJustificacion(r.getJustificacion());

        c.setImpactoTipos(r.getImpactoTipos());
        c.setImpactoDetalle(r.getImpactoDetalle());
    }

    private CampanaResponse toResponse(Campana c) {
        return CampanaResponse.builder()
                .id(c.getId())
                .fechaSolicitud(c.getFechaSolicitud())
                .solicitante(c.getSolicitante())
                .cargo(c.getCargo())
                .sucursal(c.getSucursal())
                .areaSolicitante(c.getAreaSolicitante())
                .areaSolicitanteOtro(c.getAreaSolicitanteOtro())
                .nombrePreliminar(c.getNombrePreliminar())
                .objetivoPrincipal(c.getObjetivoPrincipal())
                .objetivoOtro(c.getObjetivoOtro())
                .objetivoDetalle(c.getObjetivoDetalle())
                .marcas(c.getMarcas())
                .productos(c.getProductos())
                .invProductoCodigo(c.getInvProductoCodigo())
                .invCantidadDisponible(c.getInvCantidadDisponible())
                .invActualUsd(c.getInvActualUsd())
                .invTiempoEstimado(c.getInvTiempoEstimado())
                .invProblema(c.getInvProblema())
                .invProblemaDetalle(c.getInvProblemaDetalle())
                .metaVentasProyectadas(c.getMetaVentasProyectadas())
                .metaFacturacionUsd(c.getMetaFacturacionUsd())
                .metaUnidadesObjetivo(c.getMetaUnidadesObjetivo())
                .metaTicketPromedioUsd(c.getMetaTicketPromedioUsd())
                .fechaInicio(c.getFechaInicio())
                .fechaFin(c.getFechaFin())
                .duracionEstimada(c.getDuracionEstimada())
                .propuestaTipos(c.getPropuestaTipos())
                .propuestaDetalle(c.getPropuestaDetalle())
                .justificacion(c.getJustificacion())
                .impactoTipos(c.getImpactoTipos())
                .impactoDetalle(c.getImpactoDetalle())
                .revisionFechaRecepcion(c.getRevisionFechaRecepcion())
                .revisionResponsable(c.getRevisionResponsable())
                .revisionObservaciones(c.getRevisionObservaciones())
                .revisionByUserId(c.getRevisionByUserId())
                .revisionAt(c.getRevisionAt())
                .decisionComite(c.getDecisionComite())
                .decisionObservaciones(c.getDecisionObservaciones())
                .decisionByUserId(c.getDecisionByUserId())
                .decisionAt(c.getDecisionAt())
                .estado(c.getEstado())
                .companyId(c.getCompanyId())
                .branchId(c.getBranchId())
                .createdByUserId(c.getCreatedByUserId())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
