package com.kubocode.campanas.domain;

/**
 * Estados del ciclo de vida de una Solicitud de Campaña Comercial (FORM-COM-001).
 */
public enum EstadoCampana {
    /** El solicitante aún la está editando. */
    BORRADOR,
    /** Enviada al área de Marketing para revisión. */
    ENVIADA,
    /** Marketing registró su revisión; pendiente de decisión del Comité. */
    EN_REVISION,
    /** El Comité Comercial la aprobó. */
    APROBADA,
    /** Aprobada con cambios solicitados por el Comité. */
    APROBADA_CON_CAMBIOS,
    /** Devuelta al solicitante para ajustes. */
    DEVUELTA,
    /** Rechazada por el Comité Comercial. */
    RECHAZADA
}
