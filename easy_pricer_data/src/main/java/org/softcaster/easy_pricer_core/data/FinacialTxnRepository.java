package org.softcaster.easy_pricer_core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FinacialTxnRepository extends JpaRepository<FinacialTxn, Integer> {

    public FinacialTxn findByIdFinacialTxn(Integer idFinacialTxn);
}
