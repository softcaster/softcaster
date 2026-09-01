/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mds.core.test;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.hibernate.Hibernate;
import org.softcaster.core.data.Currency;
import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.FinancialTxnDAO;
import org.softcaster.core.data.ForexMasterData;
import org.softcaster.core.data.MasterData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;

@SpringBootApplication
// Scansiona i pacchetti della LIBRERIA per trovare @Service, @Component, ecc.
@ComponentScan(basePackages = {
    "org.softcaster.core.data", // Il pacchetto della libreria core
    "org.softcaster.engine", // Il pacchetto della libreria engine
    "org.softcaster.provider" // Il pacchetto della libreria provider
})
@EntityScan("org.softcaster.core.data")
@EnableJpaRepositories("org.softcaster.core.data")
public class TestBigDecimal implements CommandLineRunner {

    @Autowired
    private FinancialTxnDAO financialTxnDAO;

    public static void main(String[] args) {
        // Avvia l'applicazione tramite Spring Boot (NON fare "new DatabaseHelper()")
        SpringApplication.run(TestBigDecimal.class, args);
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Seleziono le transazioni fx-spot presenti su db
        List<org.softcaster.core.data.FinancialTxn> txnList = financialTxnDAO.findAll();
        for (FinancialTxn txn : txnList) {
            double amountCcy = txn.getQuantity() * txn.getFxRate();
            BigDecimal bdAmountCcy = getRoundedAmount(txn.getIdFinancialTxn(), BigDecimal.valueOf(amountCcy));
            System.out.println(bdAmountCcy);
        }
    }

    private BigDecimal getRoundedAmount(
            Integer txnId,
            BigDecimal amount
    ) {

        FinancialTxn fxTxn = financialTxnDAO.findByIdWithMasterData(txnId);

        if (fxTxn == null) {
            return BigDecimal.ZERO;
        }

        MasterData md = (MasterData) Hibernate.unproxy(fxTxn.getMasterData());

        if (md instanceof ForexMasterData fmd) {
            Currency ccy = fmd.getCcy();

            return amount.setScale(
                    ccy.getDecimalPlaces(),
                    RoundingMode.HALF_EVEN
            );
        }
        return BigDecimal.ZERO;
    }
}
