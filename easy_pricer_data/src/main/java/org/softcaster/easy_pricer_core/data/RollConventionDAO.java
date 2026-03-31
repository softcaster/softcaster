package org.softcaster.easy_pricer_core.data;

import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("rollConventionDAO")
public class RollConventionDAO {

    @Resource
    private RollConventionRepository repository;

    @Transactional(readOnly = true)
    public RollConvention findByIdRollConvention(Integer idRollConvention) {
        return repository.findByIdRollConvention(idRollConvention);
    }

    @Transactional
    public RollConvention saveOrUpdate(RollConvention rollConvention) {
        return repository.save(rollConvention);
    }

    @Transactional
    public void delete(RollConvention rollConvention) {
        repository.delete(rollConvention);
    }

    @Transactional(readOnly = true)
    public List<RollConvention> findAll() {
        return repository.findAll();
    }

    public RollConvention findByCode(String code) {
        return repository.findByCode(code);
    }
}
