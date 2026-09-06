package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RefRateIndexRepository extends JpaRepository<RefRateIndex,Integer>{
	public RefRateIndex findByRefRateIndexId(Integer refRateIndexId);

    @Query(value = "SELECT code FROM ref_rate_index ORDER BY code", nativeQuery = true)
    public List<String> findNames();
}
