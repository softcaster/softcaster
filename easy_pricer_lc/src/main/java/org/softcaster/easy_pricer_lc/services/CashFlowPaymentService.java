package org.softcaster.easy_pricer_lc.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.core.data.CashFlowItem;
import org.softcaster.core.data.CashFlowMasterData;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.core.data.PositionDetailDAO;
import org.softcaster.core.data.PositionMasterData;
import org.softcaster.core.data.PositionMasterDataDAO;
import org.softcaster.engine.enums.CashFlowStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Stato PAID delle cedole e rate pagate nella chiusura (step COUPON): flussi
 * che scadono in (last_official_date, official_date] degli strumenti a flussi
 * (titoli, mutui) con una posizione, anche appena chiusa da un rimborso.
 */
@Service
public class CashFlowPaymentService {

    private static final Logger log = LoggerFactory.getLogger(CashFlowPaymentService.class);

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private PositionMasterDataDAO positionMasterDataDAO;
    @Autowired
    private PositionDetailDAO positionDetailDAO;
    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * @return flussi passati a PAID
     */
    public int markPaid(LocalDate from, LocalDate to) {
        Set<Integer> masterDataIds = new TreeSet<>();
        for (PositionMasterData position : positionMasterDataDAO.findAll()) {
            for (PositionDetail detail : positionDetailDAO.findByPositionMasterData(position)) {
                masterDataIds.add(detail.getMasterData());
            }
        }
        int paid = 0;
        for (Integer id : masterDataIds) {
            Integer count = transactionTemplate.execute(status -> {
                MasterData masterData = (MasterData) Hibernate.unproxy(entityManager.find(MasterData.class, id));
                if (!(masterData instanceof CashFlowMasterData cfmd)) {
                    return 0;
                }
                int n = 0;
                for (CashFlowItem item : cfmd.getCashFlows()) {
                    LocalDate end = item.getEnddate().toLocalDate();
                    if (end.isAfter(from) && !end.isAfter(to) && item.getCashFlowStatus() != CashFlowStatus.PAID) {
                        item.setCashFlowStatus(CashFlowStatus.PAID);
                        n++;
                    }
                }
                if (n > 0) {
                    log.info("{}: {} cash flows paid in ({}, {}]", masterData.getCode(), n, from, to);
                }
                return n;
            });
            paid += count == null ? 0 : count;
        }
        return paid;
    }
}
