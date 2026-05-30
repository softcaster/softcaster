package org.softcaster.core.data.account;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FinancialStatementTypeRepository extends JpaRepository<FinancialStatementType, Integer> {

    public FinancialStatementType findByStatementTypeId(Integer statementTypeId);
}
