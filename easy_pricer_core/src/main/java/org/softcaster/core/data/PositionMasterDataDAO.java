package org.softcaster.core.data;

import java.util.List;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import org.softcaster.core.dto.PositionMasterDataDto;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("positionMasterDataDAO")
public class PositionMasterDataDAO {

    @Resource
    private PositionMasterDataRepository repository;

    private final Sort sortByCode = Sort.by(Sort.Direction.ASC, "code");

    @Transactional(readOnly = true)
    public PositionMasterData findByIdPosition(Integer idPosition) {
        return repository.findByIdPosition(idPosition);
    }

    @Transactional(readOnly = true)
    public PositionMasterData findByCode(String code) {
        return repository.findByCode(code);
    }

    @Transactional(readOnly = true)
    public List<PositionMasterData> findAll() {
        return repository.findAll(sortByCode);
    }

    public List<PositionMasterDataDto> findAllDto() {
        List<PositionMasterData> list = findAll();
        List<PositionMasterDataDto> dtoList = null;

        if (list != null && !list.isEmpty()) {
            dtoList = new ArrayList<>();
            PositionMasterDataDto dto;
            for (PositionMasterData pmd : list) {
                dto = new PositionMasterDataDto();
                dto.setGenericMasterDataId(pmd.getIdPosition());
                dto.setCode(pmd.getCode());
                dto.setDescription(pmd.getDescription());
                dtoList.add(dto);
            }
        }

        return dtoList;
    }

    @Transactional
    public PositionMasterData saveOrUpdate(PositionMasterData positionMasterData) {
        return repository.save(positionMasterData);
    }

    @Transactional
    public void delete(PositionMasterData positionMasterData) {
        repository.delete(positionMasterData);
    }

}
