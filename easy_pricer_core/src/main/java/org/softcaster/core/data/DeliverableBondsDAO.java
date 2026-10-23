package org.softcaster.core.data;

import java.util.List;
import jakarta.annotation.Resource;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("deliverableBondsDAO")
public class DeliverableBondsDAO {

    @Resource
    private DeliverableBondsRepository repository;

    @Transactional(readOnly = true)
    public DeliverableBonds findByIdDeliverableBonds(Integer idDeliverableBonds) {
        return repository.findByIdDeliverableBonds(idDeliverableBonds);
    }

    @Transactional(readOnly = true)
    public Optional<DeliverableBonds> findByListedContractAndIsin(Integer listedContract, String isin) {
        return repository.findByListedContractAndIsin(listedContract, isin);
    }
    
    @Transactional(readOnly = true)
    public List<DeliverableBonds> findAll() {
        return repository.findAll();
    }

    @Transactional
    public DeliverableBonds saveOrUpdate(DeliverableBonds deliverableBonds) {
        return repository.save(deliverableBonds);
    }

    @Transactional
    public void delete(DeliverableBonds deliverableBonds) {
        repository.delete(deliverableBonds);
    }

}
