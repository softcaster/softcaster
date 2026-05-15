package org.softcaster.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface YieldCurveItemRepository extends JpaRepository<YieldCurveItem, Integer> {

    public YieldCurveItem findByIdYieldCurveItem(Integer idYieldCurveItem);
}
