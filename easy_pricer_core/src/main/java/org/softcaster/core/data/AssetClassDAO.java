package org.softcaster.core.data;

import java.util.List;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import org.softcaster.core.dto.AssetClassDto;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("assetClassDAO")
public class AssetClassDAO {

    @Resource
    private AssetClassRepository repository;
    
    private final Sort sortByCode = Sort.by(Sort.Direction.ASC, "code");

    @Transactional(readOnly = true)
    public AssetClass findByIdAssetClass(Integer idAssetClass) {
        return repository.findByIdAssetClass(idAssetClass);
    }

    @Transactional(readOnly = true)
    public AssetClass findByCode(String code) {
        return repository.findByCode(code);
    }

    @Transactional
    public AssetClass saveOrUpdate(AssetClass assetClass) {
        return repository.save(assetClass);
    }

    @Transactional
    public  void delete(AssetClass assetClass) {
        repository.delete(assetClass);
    }

    @Transactional(readOnly = true)
    public List<AssetClass> findAll() {
        return repository.findAll(sortByCode);
    }

    public List<AssetClassDto> findAllDto() {
        List<AssetClass> list = findAll();
        List<AssetClassDto> dtoList = null;

        if (list != null && !list.isEmpty()) {
            dtoList = new ArrayList<>();
            AssetClassDto dto;
            for (AssetClass ac : list) {
                dto = new AssetClassDto();
                dto.setGenericMasterDataId(ac.getIdAssetClass());
                dto.setCode(ac.getCode());
                dto.setDescription(ac.getDescription());
                dtoList.add(dto);
            }
        }

        return dtoList;
    }
}
