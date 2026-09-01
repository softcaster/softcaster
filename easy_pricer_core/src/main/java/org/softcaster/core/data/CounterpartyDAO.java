package org.softcaster.core.data;

import java.util.List;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import org.softcaster.core.dto.CounterpartyDto;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("counterpartyDAO")
public class CounterpartyDAO {

    @Resource
    private CounterpartyRepository repository;

    private final Sort sortByCode = Sort.by(Sort.Direction.ASC, "code");

    @Transactional(readOnly = true)
    public Counterparty findByIdCounterparty(Integer idCounterparty) {
        return repository.findByIdCounterparty(idCounterparty);
    }

    @Transactional(readOnly = true)
    public Counterparty findByCode(String code) {
        return repository.findByCode(code);
    }

    @Transactional(readOnly = true)
    public List<Counterparty> findAll() {
        //return repository.findAll(sortByCode);
        return repository.findAllWithRoles(sortByCode);
    }

    public List<CounterpartyDto> findAllDto() {
        List<Counterparty> list = findAll();
        List<CounterpartyDto> dtoList = null;

        if (list != null && !list.isEmpty()) {
            dtoList = new ArrayList<>();
            CounterpartyDto dto;
            for (Counterparty ctp : list) {
                dto = new CounterpartyDto();
                dto.setGenericMasterDataId(ctp.getIdCounterparty());
                dto.setCode(ctp.getCode());
                dto.setDescription(ctp.getDescription());
                dtoList.add(dto);
            }
        }

        return dtoList;
    }

    @Transactional
    public Counterparty saveOrUpdate(Counterparty counterparty) {
        return repository.save(counterparty);
    }

    @Transactional
    public void delete(Counterparty counterparty) {
        repository.delete(counterparty);
    }

}
