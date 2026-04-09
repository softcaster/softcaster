package org.softcaster.easy_pricer_core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AmortizationScheduleRepository extends JpaRepository<AmortizationSchedule, Integer> {

    public AmortizationSchedule findByIdAmortizationSchedule(Integer idAmortizationSchedule);
    
    public AmortizationSchedule findByCode(String code);

}
