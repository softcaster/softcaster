package org.softcaster.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RollConventionRepository extends JpaRepository<RollConvention, Integer> {

    public RollConvention findByIdRollConvention(Integer idRollConvention);

    public RollConvention findByCode(String code);
}
