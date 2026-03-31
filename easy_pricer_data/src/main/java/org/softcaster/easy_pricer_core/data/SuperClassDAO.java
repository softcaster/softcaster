package org.softcaster.easy_pricer_core.data;

import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("superClassDAO")
    public class SuperClassDAO {

    @Resource
    private SuperClassRepository repository;

    @Transactional(readOnly = true)
    public SuperClass findByIdSuperClass(Integer idSuperClass) {
        return repository.findByIdSuperClass(idSuperClass);
    }

    @Transactional
    public SuperClass saveOrUpdate(SuperClass superClass) {
        return repository.save(superClass);
    }

    @Transactional
    public void delete(SuperClass superClass) {
        repository.delete(superClass);
    }

    @Transactional(readOnly = true)
    public List<SuperClass> findAll() {
        return repository.findAll();
    }
}
