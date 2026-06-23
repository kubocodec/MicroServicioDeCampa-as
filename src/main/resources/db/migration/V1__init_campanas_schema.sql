-- Solicitud de Campaña Comercial — FORM-COM-001 (SOPRINT CÍA. LTDA.)
CREATE TABLE campanas (
    id UUID PRIMARY KEY,
    fecha_solicitud DATE,

    -- 1. Información general
    solicitante VARCHAR(255),
    cargo VARCHAR(150),
    sucursal VARCHAR(50),
    area_solicitante VARCHAR(50),
    area_solicitante_otro VARCHAR(150),

    -- 2. Nombre preliminar
    nombre_preliminar VARCHAR(255) NOT NULL,

    -- 3. Objetivo comercial
    objetivo_principal VARCHAR(80),
    objetivo_otro VARCHAR(150),
    objetivo_detalle TEXT,

    -- 4. Productos involucrados
    marcas TEXT,
    productos TEXT,

    -- 5. Inventario a rotar
    inv_producto_codigo TEXT,
    inv_cantidad_disponible VARCHAR(100),
    inv_actual_usd NUMERIC(15, 2),
    inv_tiempo_estimado VARCHAR(150),
    inv_problema VARCHAR(255),
    inv_problema_detalle TEXT,

    -- 6. Meta comercial
    meta_ventas_proyectadas TEXT,
    meta_facturacion_usd NUMERIC(15, 2),
    meta_unidades_objetivo INTEGER,
    meta_ticket_promedio_usd NUMERIC(15, 2),

    -- 7. Duración
    fecha_inicio DATE,
    fecha_fin DATE,
    duracion_estimada VARCHAR(150),

    -- 8. Propuesta comercial inicial
    propuesta_tipos VARCHAR(255),
    propuesta_detalle TEXT,

    -- 9. Justificación
    justificacion TEXT,

    -- 10. Impacto esperado
    impacto_tipos VARCHAR(255),
    impacto_detalle TEXT,

    -- Revisión de Marketing
    revision_fecha_recepcion DATE,
    revision_responsable VARCHAR(255),
    revision_observaciones TEXT,
    revision_by_user_id UUID,
    revision_at TIMESTAMP,

    -- Decisión del Comité Comercial
    decision_comite VARCHAR(30),
    decision_observaciones TEXT,
    decision_by_user_id UUID,
    decision_at TIMESTAMP,

    -- Estado / multi-tenant / auditoría
    estado VARCHAR(30) NOT NULL DEFAULT 'BORRADOR',
    company_id UUID NOT NULL,
    branch_id UUID,
    created_by_user_id UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_campana_company ON campanas(company_id);
CREATE INDEX idx_campana_created_by ON campanas(created_by_user_id);
CREATE INDEX idx_campana_estado ON campanas(estado);
