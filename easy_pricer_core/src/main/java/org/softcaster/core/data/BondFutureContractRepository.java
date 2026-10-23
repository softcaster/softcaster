package org.softcaster.core.data;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Accesso alle scadenze dei bond future (listed_contract con contract_type =
 * 'BOND_FUTURE'), con testata, calendario e paniere.
 */
public interface BondFutureContractRepository extends JpaRepository<BondFutureContract, Integer> {

    @EntityGraph("BondFutureContract.withProduct")
    Optional<BondFutureContract> findByCode(String code);

    @EntityGraph("BondFutureContract.withProduct")
    Optional<BondFutureContract> findByIsin(String isin);
}
