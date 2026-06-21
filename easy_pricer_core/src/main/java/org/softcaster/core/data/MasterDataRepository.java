package org.softcaster.core.data;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MasterDataRepository extends JpaRepository<MasterData, Integer> {

    public MasterData findByIdMasterData(Integer idMasterData);

    public MasterData findByCode(String code);

    // left join ottiene l'oggetto MasterData anche quando instrumentValuation non è presente
    @Query("""
    select md
    from MasterData md
    left join fetch md.instrumentValuations iv
    where md.idMasterData = :id
    """)           
    Optional<MasterData> findByIdWithInstrumentValuation(@Param("id") Integer id);
    
    @Query("""
    select md
    from MasterData md
    left join fetch md.instrumentValuations iv
    where md.code = :code
    """)
    Optional<MasterData> findByCodeWithInstrumentValuation(@Param("code") String code);

}
