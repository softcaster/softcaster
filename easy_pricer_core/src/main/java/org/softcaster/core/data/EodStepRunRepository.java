package org.softcaster.core.data;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import org.softcaster.engine.enums.EodStatus;
import org.softcaster.engine.enums.EodStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EodStepRunRepository extends JpaRepository<EodStepRun, Integer> {

    public List<EodStepRun> findByEodRunOrderByStartedAt(Integer eodRun);

    @Query("""
        select distinct s.step
        from EodStepRun s, EodRun r
        where s.eodRun = r.eodRunId
          and r.businessDate = :businessDate
          and s.status in :statuses
        """)
    public List<EodStep> findStepsByBusinessDateAndStatusIn(@Param("businessDate") LocalDate businessDate,
            @Param("statuses") Collection<EodStatus> statuses);
}
