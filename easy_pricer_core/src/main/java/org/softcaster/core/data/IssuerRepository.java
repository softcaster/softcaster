package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssuerRepository extends JpaRepository<Issuer, Integer> {

    public Issuer findByIdIssuer(Integer idIssuer);

    public Issuer findByShortIssuerName(String shortIssuerName);
    
    @EntityGraph(attributePaths = {"country"})
    @Override
    public List<Issuer> findAll(Sort sort);
}
