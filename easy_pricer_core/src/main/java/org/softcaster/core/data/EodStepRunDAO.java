package org.softcaster.core.data;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import org.softcaster.engine.enums.EodStatus;
import org.softcaster.engine.enums.EodStep;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("eodStepRunDAO")
public class EodStepRunDAO {

    @Resource
    private EodStepRunRepository repository;

    @Transactional(readOnly = true)
    public List<EodStepRun> findByEodRun(Integer eodRun) {
        return repository.findByEodRunOrderByStartedAt(eodRun);
    }

    /**
     * Step gia' riusciti per la data, in qualunque lancio: al rilancio di una
     * chiusura fallita non si rieseguono.
     */
    @Transactional(readOnly = true)
    public List<EodStep> findCompletedSteps(LocalDate businessDate) {
        return repository.findStepsByBusinessDateAndStatusIn(businessDate, List.of(EodStatus.OK, EodStatus.WARNING));
    }

    @Transactional
    public EodStepRun saveOrUpdate(EodStepRun eodStepRun) {
        return repository.save(eodStepRun);
    }
}
