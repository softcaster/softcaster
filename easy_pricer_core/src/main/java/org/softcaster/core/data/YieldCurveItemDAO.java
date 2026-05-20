package org.softcaster.core.data;

import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("yieldCurveItemDAO")
public class YieldCurveItemDAO {

    @Resource
    private YieldCurveItemRepository repository;

    @Transactional(readOnly = true)
    public YieldCurveItem findByIdYieldCurveItem(Integer idYieldCurveItem) {
        return repository.findByIdYieldCurveItem(idYieldCurveItem);
    }

    @Transactional
    public YieldCurveItem saveOrUpdate(YieldCurveItem yieldCurveItem) {
        return repository.save(yieldCurveItem);
    }

    @Transactional
    public void delete(YieldCurveItem yieldCurveItem) {
        repository.delete(yieldCurveItem);
    }

    public List<YieldCurveItem> findAll() {
        return repository.findAll();
    }
}
