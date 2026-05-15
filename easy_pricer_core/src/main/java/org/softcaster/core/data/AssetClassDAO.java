package org.softcaster.core.data;

import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("assetClassDAO")
public class AssetClassDAO {

    @Resource
    private AssetClassRepository repository;

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
        return repository.findAll();
    }
}
