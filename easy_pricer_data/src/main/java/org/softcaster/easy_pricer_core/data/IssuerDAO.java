package org.softcaster.easy_pricer_core.data;

import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("issuerDAO")
public class IssuerDAO {

    @Resource
    private IssuerRepository repository;

    @Transactional(readOnly = true)
    public Issuer findByIdIssuer(Integer idIssuer) {
        return repository.findByIdIssuer(idIssuer);
    }

    @Transactional
    public Issuer saveOrUpdate(Issuer issuer) {
        return repository.save(issuer);
    }

    @Transactional
    public void delete(Issuer issuer) {
        repository.delete(issuer);
    }

    @Transactional(readOnly = true)
    public List<Issuer> findAll() {
        return repository.findAll();
    }
}
