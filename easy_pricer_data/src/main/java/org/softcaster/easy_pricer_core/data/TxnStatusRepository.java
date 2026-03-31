package org.softcaster.easy_pricer_core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TxnStatusRepository extends JpaRepository<TxnStatus, Integer> {

    public TxnStatus findByIdTxnStatus(Integer idTxnStatus);
}
