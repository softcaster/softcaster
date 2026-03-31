package org.softcaster.easy_pricer_core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PositionDetailRepository extends JpaRepository<PositionDetail, Integer> {

    public PositionDetail findByIdPositionDetail(Integer idPositionDetail);
}
