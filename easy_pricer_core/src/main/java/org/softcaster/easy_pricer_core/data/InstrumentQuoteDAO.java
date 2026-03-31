package org.softcaster.easy_pricer_core.data;

import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("instrumentQuoteDAO")
public class InstrumentQuoteDAO {

    @Resource
    private InstrumentQuoteRepository repository;

    private final Sort sortByCode = Sort.by(Sort.Direction.ASC, "code");

    @Transactional(readOnly = true)
    public List<InstrumentQuote> findAll() {
        return repository.findAll(sortByCode);
    }

    @Transactional(readOnly = true)
    public InstrumentQuote findByIdInstrumentQuote(Integer idInstrumentQuote) {
        return repository.findByIdInstrumentQuote(idInstrumentQuote);
    }

    @Transactional
    public InstrumentQuote saveOrUpdate(InstrumentQuote instrumentQuote) {
        return repository.save(instrumentQuote);
    }

    @Transactional
    public void delete(InstrumentQuote instrumentQuote) {
        repository.delete(instrumentQuote);
    }

}
