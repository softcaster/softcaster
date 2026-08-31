package org.softcaster.core.data.account;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GlAccountSlotsRepository extends JpaRepository<GlAccountSlots, Integer> {

    public GlAccountSlots findByAccountSlotId(Integer accountSlotId);
    public GlAccountSlots findByAccountAndCurrency(Integer account, Integer currency);
}
