package org.softcaster.easy_pricer_core.data;

import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("daycountDAO")
public class DaycountDAO {

    @Resource
    private DaycountRepository repository;

    @Transactional(readOnly = true)
    public Daycount findByIdDaycount(Integer idDaycount) {
        return repository.findByIdDaycount(idDaycount);
    }

    @Transactional
    public Daycount saveOrUpdate(Daycount daycount) {
        return repository.save(daycount);
    }

    @Transactional
    public void delete(Daycount daycount) {
        repository.delete(daycount);
    }

    @Transactional(readOnly = true)
    public List<Daycount> findAll() {
        return repository.findAll();
    }

    public Daycount findByCode(String code) {
        return repository.findByCode(code);
    }
}
