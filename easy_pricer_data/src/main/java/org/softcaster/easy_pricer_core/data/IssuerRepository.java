package org.softcaster.easy_pricer_core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IssuerRepository extends JpaRepository<Issuer,Integer>{
	public Issuer findByIdIssuer(Integer idIssuer);
}
