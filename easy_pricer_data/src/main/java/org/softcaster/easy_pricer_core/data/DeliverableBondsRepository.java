package org.softcaster.easy_pricer_core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliverableBondsRepository extends JpaRepository<DeliverableBonds, Integer> {

    public DeliverableBonds findByIdDeliverableBonds(Integer idDeliverableBonds);
}
