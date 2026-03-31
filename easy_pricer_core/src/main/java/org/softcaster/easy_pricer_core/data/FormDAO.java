package org.softcaster.easy_pricer_core.data;

import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("formDAO")
public class FormDAO {

    @Resource
    private FormRepository repository;

    @Transactional(readOnly = true)
    public Form findByIdForm(Integer idForm) {
        return repository.findByIdForm(idForm);
    }

    @Transactional
    public Form saveOrUpdate(Form form) {
        return repository.save(form);
    }

    @Transactional
    public void delete(Form form) {
        repository.delete(form);
    }

    @Transactional(readOnly = true)
    public List<Form> findAll() {
        return repository.findAll();
    }

    public Form findByCode(String code) {
        return repository.findByCode(code);
    }
}
