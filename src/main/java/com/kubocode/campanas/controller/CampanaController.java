package com.kubocode.campanas.controller;

import com.kubocode.campanas.dto.CampanaRequest;
import com.kubocode.campanas.dto.CampanaResponse;
import com.kubocode.campanas.dto.DecisionComiteRequest;
import com.kubocode.campanas.dto.RevisionMarketingRequest;
import com.kubocode.campanas.security.SecurityUtils;
import com.kubocode.campanas.service.CampanaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/campanas")
@RequiredArgsConstructor
@Tag(name = "Campañas", description = "Solicitud de Campaña Comercial (FORM-COM-001) y su flujo de aprobación")
public class CampanaController {

    private final CampanaService service;

    // ─── SOLICITANTE ─────────────────────────────────────────────────

    @PostMapping
    @Operation(summary = "Crear una nueva solicitud de campaña (BORRADOR)")
    public ResponseEntity<CampanaResponse> crear(@Valid @RequestBody CampanaRequest request) {
        return ResponseEntity.ok(service.crear(request, SecurityUtils.getCurrentUser()));
    }

    @GetMapping("/mias")
    @Operation(summary = "Listar mis solicitudes de campaña")
    public ResponseEntity<List<CampanaResponse>> listarMias() {
        return ResponseEntity.ok(service.listarMias(SecurityUtils.getCurrentUser()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalle de una solicitud de campaña")
    public ResponseEntity<CampanaResponse> detalle(@PathVariable UUID id) {
        return ResponseEntity.ok(service.obtenerDetalle(id, SecurityUtils.getCurrentUser()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una solicitud (solo BORRADOR o DEVUELTA)")
    public ResponseEntity<CampanaResponse> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody CampanaRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request, SecurityUtils.getCurrentUser()));
    }

    @PostMapping("/{id}/enviar")
    @Operation(summary = "Enviar la solicitud a revisión de Marketing")
    public ResponseEntity<CampanaResponse> enviar(@PathVariable UUID id) {
        return ResponseEntity.ok(service.enviar(id, SecurityUtils.getCurrentUser()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una solicitud (solo BORRADOR)")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        service.eliminar(id, SecurityUtils.getCurrentUser());
        return ResponseEntity.noContent().build();
    }

    // ─── REVISIÓN / COMITÉ (SUPERVISOR o ADMIN) ──────────────────────

    @GetMapping
    @Operation(summary = "Listar todas las campañas de la empresa (filtro opcional por estado) — SUPERVISOR/ADMIN")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    public ResponseEntity<List<CampanaResponse>> listarTodas(
            @RequestParam(required = false) String estado) {
        return ResponseEntity.ok(service.listarTodas(estado, SecurityUtils.getCurrentUser()));
    }

    @PostMapping("/{id}/revision")
    @Operation(summary = "Registrar la revisión de Marketing — SUPERVISOR/ADMIN")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    public ResponseEntity<CampanaResponse> registrarRevision(
            @PathVariable UUID id,
            @Valid @RequestBody RevisionMarketingRequest request) {
        return ResponseEntity.ok(service.registrarRevision(id, request, SecurityUtils.getCurrentUser()));
    }

    @PostMapping("/{id}/decision")
    @Operation(summary = "Registrar la decisión del Comité Comercial — SUPERVISOR/ADMIN")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    public ResponseEntity<CampanaResponse> decidir(
            @PathVariable UUID id,
            @Valid @RequestBody DecisionComiteRequest request) {
        return ResponseEntity.ok(service.decidir(id, request, SecurityUtils.getCurrentUser()));
    }
}
