package org.softcaster.easy_pricer_core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanMasterDataRepository extends JpaRepository<LoanMasterData, Integer> {

    public LoanMasterData findByIdMasterData(Integer idMasterData);
}
