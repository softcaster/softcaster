package org.softcaster.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IssuerRepository extends JpaRepository<Issuer, Integer> {

    public Issuer findByIdIssuer(Integer idIssuer);

    public Issuer findByShortIssuerName(String shortIssuerName);
}
