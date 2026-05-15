package org.softcaster.core.data;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("calendarDAO")
public class CalendarDAO {

    @Autowired
    private ApplicationContext appContext;

    @Resource
    private CalendarRepository repository;
    
    private final Sort sortByCode = Sort.by(Sort.Direction.ASC, "code");

    @Transactional(readOnly = true)
    public Calendar findByIdCalendar(Integer idCalendar) {
        return repository.findByIdCalendar(idCalendar);
    }

    @Transactional
    public Calendar saveOrUpdate(Calendar calendar) {
        return repository.save(calendar);
    }

    @Transactional
    public void delete(Calendar calendar) {
        repository.delete(calendar);
    }

    @Transactional(readOnly = true)
    public List<Calendar> findAll() {
        return repository.findAll(sortByCode);
    }

    @Transactional(readOnly = true)
    public List<Holiday> findHolidays(Integer idCalendar) {
        try {
            EntityManagerFactory emf = (EntityManagerFactory) appContext.getBean("entityManagerFactory", jakarta.persistence.EntityManagerFactory.class);

            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            JPAQueryFactory queryFactory = new JPAQueryFactory(em);

            QHoliday holiday = QHoliday.holiday;

            return queryFactory.selectFrom(holiday).where(holiday.calendar.eq(idCalendar)).fetch();
        } catch (BeansException e) {
            e.getLocalizedMessage();
            return null;
        }
    }
    
    @Transactional(readOnly = true)
    public List<Holiday> findHolidaysByIdCalendar(Integer idCalendar) {
        return repository.findHolidaysByIdCalendar(idCalendar);
    }

    @Transactional(readOnly = true)
    public Calendar findByCode(String code) {
        return repository.findByCode(code);
    }
}
