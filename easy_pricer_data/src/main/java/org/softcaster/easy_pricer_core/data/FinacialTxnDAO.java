package org.softcaster.easy_pricer_core.data;

import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("finacialTxnDAO")
public class FinacialTxnDAO {

    @Resource
    private FinacialTxnRepository repository;

    @Transactional(readOnly = true)
    public FinacialTxn findByIdFinacialTxn(Integer idFinacialTxn) {
        return repository.findByIdFinacialTxn(idFinacialTxn);
    }

    @Transactional(readOnly = true)
    public List<FinacialTxn> findAll() {
        return repository.findAll();
    }

    @Transactional
    public FinacialTxn saveOrUpdate(FinacialTxn finacialTxn) {
        return repository.save(finacialTxn);
    }

    @Transactional
    public void delete(FinacialTxn finacialTxn) {
        repository.delete(finacialTxn);
    }

}
