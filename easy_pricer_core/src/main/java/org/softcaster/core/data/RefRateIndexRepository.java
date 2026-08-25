package org.softcaster.core.data;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefRateIndexRepository extends JpaRepository<RefRateIndex, Integer> {
    
    @EntityGraph(attributePaths = {"currency"})
    public RefRateIndex findByRefRateIndexId(Integer refRateIndexId);
}
