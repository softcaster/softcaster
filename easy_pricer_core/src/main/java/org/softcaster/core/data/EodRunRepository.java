package org.softcaster.core.data;

import java.util.Optional;
import org.softcaster.engine.enums.EodStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EodRunRepository extends JpaRepository<EodRun, Integer> {

    // al massimo un lancio in corso: vincolo uq_eod_run_running
    public Optional<EodRun> findByStatus(EodStatus status);
}
