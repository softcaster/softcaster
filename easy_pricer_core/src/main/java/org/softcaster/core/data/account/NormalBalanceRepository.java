package org.softcaster.core.data.account;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NormalBalanceRepository extends JpaRepository<NormalBalance,Integer>{
	public NormalBalance findByBalanceId(Integer balanceId);
}
