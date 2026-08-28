package org.softcaster.core.data;

import java.util.List;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import org.softcaster.core.dto.YieldCurveDto;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("yieldCurveDAO")
public class YieldCurveDAO {

    @Resource
    private YieldCurveRepository repository;

    private final Sort sortByCode = Sort.by(Sort.Direction.ASC, "code");

    @Transactional(readOnly = true)
    public YieldCurve findByIdYieldCurve(Integer idYieldCurve) {
        return repository.findByIdYieldCurve(idYieldCurve);
    }

    @Transactional(readOnly = true)
    public YieldCurve findByCode(String code) {
        return repository.findByCode(code);
    }

    @Transactional
    public YieldCurve saveOrUpdate(YieldCurve yieldCurve) {
        return repository.save(yieldCurve);
    }

    @Transactional
    public void delete(YieldCurve yieldCurve) {
        repository.delete(yieldCurve);
    }

    @Transactional(readOnly = true)
    public List<YieldCurve> findAll() {
        return repository.findAll(sortByCode);
    }

    @Transactional(readOnly = true)
    public List<String> findNames() {
        return repository.findNames();
    }

    public List<YieldCurveDto> findAllDto() {
        List<YieldCurveDto> listDto = new ArrayList<>();
        List<YieldCurve> list = findAll();

        if (list != null && !list.isEmpty()) {
            YieldCurveDto dto;
            for (YieldCurve yc : list) {
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
