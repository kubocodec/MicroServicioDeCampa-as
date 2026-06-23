package com.kubocode.campanas.repository;

import com.kubocode.campanas.domain.Campana;
import com.kubocode.campanas.domain.EstadoCampana;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CampanaRepository extends JpaRepository<Campana, UUID> {

    /** Campañas creadas por un usuario (su bandeja de "Mis campañas"). */
    List<Campana> findByCompanyIdAndCreatedByUserIdOrderByCreatedAtDesc(UUID companyId, UUID createdByUserId);

    /** Todas las campañas de la empresa (bandeja de revisión / comité). */
    List<Campana> findByCompanyIdOrderByCreatedAtDesc(UUID companyId);

    /** Campañas de la empresa en un estado dado. */
    List<Campana> findByCompanyIdAndEstadoOrderByCreatedAtDesc(UUID companyId, EstadoCampana estado);
}
