package org.softcaster.easy_pricer_core.data;

import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("amortizationScheduleDAO")
public class AmortizationScheduleDAO {

    @Resource
    private AmortizationScheduleRepository repository;

    @Transactional(readOnly = true)
    public AmortizationSchedule findByIdAmortizationSchedule(Integer idAmortizationSchedule) {
        return repository.findByIdAmortizationSchedule(idAmortizationSchedule);
    }

    @Transactional
    public AmortizationSchedule saveOrUpdate(AmortizationSchedule amortizationSchedule) {
        return repository.save(amortizationSchedule);
    }

    @Transactional
    public void delete(AmortizationSchedule amortizationSchedule) {
        repository.delete(amortizationSchedule);
    }

    @Transactional(readOnly = true)
    public List<AmortizationSchedule> findAll() {
        return repository.findAll();
    }
}
