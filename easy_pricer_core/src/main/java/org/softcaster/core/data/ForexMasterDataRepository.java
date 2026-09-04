package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForexMasterDataRepository extends JpaRepository<ForexMasterData, Integer> {

    public ForexMasterData findByIdMasterData(Integer idMasterData);

    public ForexMasterData findByCode(String code);

    @EntityGraph(attributePaths = {"bcy", "ccy"})
    @Override
    public List<ForexMasterData> findAll(Sort sort);
}
