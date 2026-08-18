package org.softcaster.core.data;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("descriptorsDAO")
public class DescriptorsDAO {

    private DescriptorsRepository repository;

    // Iniezione tramite costruttore (Best Practice per Spring)
    public DescriptorsDAO(DescriptorsRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Descriptors> findAll() {
        return repository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Descriptors findByDescriptorId(Integer descriptorId) {
        return repository.findByDescriptorId(descriptorId);
    }

    @Transactional
    public Descriptors saveOrUpdate(Descriptors descriptors) {
        return repository.save(descriptors);
    }

    @Transactional
    public void delete(Descriptors descriptors) {
        repository.delete(descriptors);
    }
}
