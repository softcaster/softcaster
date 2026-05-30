package org.softcaster.core.data.account;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GlAccountRepository extends JpaRepository<GlAccount, Integer> {

    public GlAccount findByAccountId(Integer accountId);
}
