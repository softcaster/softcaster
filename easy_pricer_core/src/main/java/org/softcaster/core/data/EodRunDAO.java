package org.softcaster.core.data;

import jakarta.annotation.Resource;
import java.util.Optional;
import org.softcaster.engine.enums.EodStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("eodRunDAO")
public class EodRunDAO {

    @Resource
    private EodRunRepository repository;

    @Transactional(readOnly = true)
    public Optional<EodRun> findRunning() {
        return repository.findByStatus(EodStatus.RUNNING);
    }

    @Transactional
    public EodRun saveOrUpdate(EodRun eodRun) {
        return repository.save(eodRun);
    }
}
