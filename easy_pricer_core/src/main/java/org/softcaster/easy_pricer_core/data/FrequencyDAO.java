package org.softcaster.easy_pricer_core.data;

import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("frequencyDAO")
public class FrequencyDAO {

    @Resource
    private FrequencyRepository repository;

    @Transactional(readOnly = true)
    public Frequency findByIdFrequency(Integer idFrequency) {
        return repository.findByIdFrequency(idFrequency);
    }

    @Transactional
    public Frequency saveOrUpdate(Frequency frequency) {
        return repository.save(frequency);
    }

    @Transactional
    public void delete(Frequency frequency) {
        repository.delete(frequency);
    }

    @Transactional(readOnly = true)
    public List<Frequency> findAll() {
        return repository.findAll();
    }

    public Frequency findByCode(String code) {
        return repository.findByCode(code);
    }
}
