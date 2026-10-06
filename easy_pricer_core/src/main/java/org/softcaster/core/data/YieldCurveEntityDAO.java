package org.softcaster.core.data;

import java.util.List;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import org.softcaster.core.dto.YieldCurveDto;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("yieldCurveEntityDAO")
public class YieldCurveEntityDAO {

    @Resource
    private YieldCurveEntityRepository repository;

    private final Sort sortByCode = Sort.by(Sort.Direction.ASC, "code");

    @Transactional(readOnly = true)
    public YieldCurveEntity findByIdYieldCurve(Integer idYieldCurve) {
        return repository.findByIdYieldCurve(idYieldCurve);
    }

    @Transactional(readOnly = true)
    public YieldCurveEntity findByCode(String code) {
        return repository.findByCode(code);
    }

    @Transactional
    public YieldCurveEntity saveOrUpdate(YieldCurveEntity yieldCurve) {
        return repository.save(yieldCurve);
    }

    @Transactional
    public void delete(YieldCurveEntity yieldCurve) {
        repository.delete(yieldCurve);
    }

    @Transactional(readOnly = true)
    public List<YieldCurveEntity> findAll() {
        return repository.findAll(sortByCode);
    }

    @Transactional(readOnly = true)
    public List<String> findNames() {
        return repository.findNames();
    }

    public List<YieldCurveDto> findAllDto() {
        List<YieldCurveDto> listDto = new ArrayList<>();
        List<YieldCurveEntity> list = findAll();

        if (list != null && !list.isEmpty()) {
            YieldCurveDto dto;
            for (YieldCurveEntity yc : list) {
                dto = new YieldCurveDto();
                dto.setYieldCurveId(yc.getIdYieldCurve());
                dto.setCode(yc.getCode());
                dto.setDescription(yc.getDescription());
                listDto.add(dto);
            }
        }
        return listDto;
    }
}
