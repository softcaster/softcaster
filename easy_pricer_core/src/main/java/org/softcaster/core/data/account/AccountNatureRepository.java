package org.softcaster.core.data.account;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountNatureRepository extends JpaRepository<AccountNature, Integer> {

    public AccountNature findByNatureId(Integer natureId);
}
