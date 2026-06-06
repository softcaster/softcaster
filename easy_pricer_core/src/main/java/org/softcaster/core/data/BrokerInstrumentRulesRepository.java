package org.softcaster.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BrokerInstrumentRulesRepository extends JpaRepository<BrokerInstrumentRules, Integer> {

    public BrokerInstrumentRules findByBrokerRuleId(Integer brokerRuleId);
}
