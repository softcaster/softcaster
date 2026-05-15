package org.softcaster.core.data;

import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("yieldCurveDAO")
public class YieldCurveDAO {

    @Resource
    private YieldCurveRepository repository;

    private final Sort sortByCode = Sort.by(Sort.Direction.ASC, "code");

    @Transactional(readOnly = true)
    public YieldCurve findByIdYieldCurve(Integer idYieldCurve) {
        return repository.findByIdYieldCurve(idYieldCurve);
    }

    @Transactional(readOnly = true)
    public YieldCurve findByCode(String code) {
        return repository.findByCode(code);
    }

    @Transactional
    public YieldCurve saveOrUpdate(YieldCurve yieldCurve) {
        return repository.save(yieldCurve);
    }

    @Transactional
    public void delete(YieldCurve yieldCurve) {
        repository.delete(yieldCurve);
    }

    @Transactional(readOnly = true)
    public List<YieldCurve> findAll() {
        return repository.findAll(sortByCode);
    }

    @Transactional(readOnly = true)
    public List<String> findNames() {
        return repository.findNames();
    }
}
