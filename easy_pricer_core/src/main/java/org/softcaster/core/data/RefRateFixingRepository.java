package org.softcaster.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RefRateFixingRepository extends JpaRepository<RefRateFixing, Integer> {

    public RefRateFixing findByRefRateFixingId(Integer refRateFixingId);
}
