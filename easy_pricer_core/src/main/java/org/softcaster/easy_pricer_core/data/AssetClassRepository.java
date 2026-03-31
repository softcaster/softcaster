package org.softcaster.easy_pricer_core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetClassRepository extends JpaRepository<AssetClass,Integer>{
	public AssetClass findByIdAssetClass(Integer idAssetClass);
}
