package org.softcaster.core.data;

import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("typeOfInterestDAO")
public class TypeOfInterestDAO {

    @Resource
    private TypeOfInterestRepository repository;

    @Transactional(readOnly = true)
    public TypeOfInterest findByIdTypeOfInterest(Integer idTypeOfInterest) {
        return repository.findByIdTypeOfInterest(idTypeOfInterest);
    }

    @Transactional
    public TypeOfInterest saveOrUpdate(TypeOfInterest typeOfInterest) {
        return repository.save(typeOfInterest);
    }

    @Transactional
    public void delete(TypeOfInterest typeOfInterest) {
        repository.delete(typeOfInterest);
    }

    @Transactional(readOnly = true)
    public List<TypeOfInterest> findAll() {
        return repository.findAll();
    }

    public TypeOfInterest findByCode(String code) {
        return repository.findByCode(code);
    }
}
