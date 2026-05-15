package org.softcaster.core.data;

import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("holidayDAO")
public class HolidayDAO {

    @Resource
    private HolidayRepository repository;

    @Transactional(readOnly = true)
    public Holiday findByIdHoliday(Integer idHoliday) {
        return repository.findByIdHoliday(idHoliday);
    }

    @Transactional
    public Holiday saveOrUpdate(Holiday holiday) {
        return repository.save(holiday);
    }

    @Transactional
    public void delete(Holiday holiday) {
        repository.delete(holiday);
    }

    @Transactional(readOnly = true)
    public List<Holiday> findAll() {
        return repository.findAll();
    }
}
