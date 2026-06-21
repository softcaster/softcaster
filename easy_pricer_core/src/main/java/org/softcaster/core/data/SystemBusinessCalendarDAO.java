package org.softcaster.core.data;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("systemBusinessCalendarDAO")
public class SystemBusinessCalendarDAO {

    @Resource
    private SystemBusinessCalendarRepository repository;

    @Transactional(readOnly = true)
    public SystemBusinessCalendar findBySbcId(Integer sbcId) {
        return repository.findBySbcId(sbcId);
    }

    @Transactional
    public SystemBusinessCalendar saveOrUpdate(SystemBusinessCalendar systemBusinessCalendar) {
        return repository.save(systemBusinessCalendar);
    }
}
