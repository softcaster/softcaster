package org.softcaster.easy_pricer_core.data;

import java.util.List;
import jakarta.annotation.Resource;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("positionDetailDAO")
public class PositionDetailDAO {

    @Resource
    private PositionDetailRepository repository;

    private final Sort sortByCode = Sort.by(Sort.Direction.ASC, "code");

    @Transactional(readOnly = true)
    public PositionDetail findByIdPositionDetail(Integer idPositionDetail) {
        return repository.findByIdPositionDetail(idPositionDetail);
    }

    @Transactional(readOnly = true)
    public Optional<PositionDetail> findByPositionMdAndMasterDataAndCounterparty(Integer positionMd,
            Integer masterData,
            Integer counterparty) {
        return repository.findByPositionMdAndMasterDataAndCounterparty(positionMd,
                masterData,
                counterparty);
    }

    @Transactional(readOnly = true)
    public List<PositionDetail> findAll() {
        return repository.findAll(sortByCode);
    }

    @Transactional
    public PositionDetail saveOrUpdate(PositionDetail positionDetail) {
        return repository.save(positionDetail);
    }

    @Transactional
    public void delete(PositionDetail positionDetail) {
        repository.delete(positionDetail);
    }

}
