package org.softcaster.core.data;

import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("brokerInstrumentRulesDAO")
public class BrokerInstrumentRulesDAO {

    @Resource
    private BrokerInstrumentRulesRepository repository;

    @Transactional(readOnly = true)
    public BrokerInstrumentRules findByBrokerRuleId(Integer brokerRuleId) {
        return repository.findByBrokerRuleId(brokerRuleId);
    }

    @Transactional
    public BrokerInstrumentRules saveOrUpdate(BrokerInstrumentRules brokerInstrumentRules) {
        return repository.save(brokerInstrumentRules);
    }

    @Transactional
    public void delete(BrokerInstrumentRules brokerInstrumentRules) {
        repository.delete(brokerInstrumentRules);
    }
    
    @Transactional(readOnly = true)
    public List<BrokerInstrumentRules> findAll() {
        return repository.findAll();
    }
}
