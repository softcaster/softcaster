package org.softcaster.core.data.account;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountMappingRepository extends JpaRepository<AccountMapping, Integer> {

    public AccountMapping findByAccountMappingId(Integer accountMappingId);

    @Query(value = 
            """
                SELECT g.code FROM account_mapping am
                JOIN gl_accounts g ON am.gl_account = g.account_id
                WHERE am.mapping_key = :key AND am.currency = :ccy
            """, nativeQuery = true)
    public String findAccount(@Param("key") String mappingKey, @Param("ccy") int currencyId);
    
    @Query("SELECT am FROM AccountMapping am " +
           "LEFT JOIN FETCH am.currency " +
           "LEFT JOIN FETCH am.glAccount")
    public List<AccountMapping> findAllWithAssociations();
}
