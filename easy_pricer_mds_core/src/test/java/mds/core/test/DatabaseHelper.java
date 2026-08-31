package mds.core.test;

import jakarta.transaction.Transactional;
import java.util.List;
import org.softcaster.core.data.CurrencyDAO;
import org.softcaster.core.data.account.GlAccount;
import org.softcaster.core.data.account.GlAccountDAO;
import org.softcaster.core.data.account.GlAccountSlots;
import org.softcaster.core.data.account.GlAccountSlotsDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
@SpringBootApplication
// Scansiona i pacchetti della LIBRERIA per trovare @Service, @Component, ecc.
@ComponentScan(basePackages = {
    "org.softcaster.core.data", // Il pacchetto della libreria core
    "org.softcaster.engine", // Il pacchetto della libreria engine
    "org.softcaster.provider" // Il pacchetto della libreria provider
})
@EntityScan("org.softcaster.core.data")
@EnableJpaRepositories("org.softcaster.core.data")
public class DatabaseHelper implements CommandLineRunner {

    @Autowired
    private CurrencyDAO currencyDAO;
    @Autowired
    private GlAccountDAO glAccountDAO;
    @Autowired
    private GlAccountSlotsDAO glAccountSlotsDAO;

    private void updateGlAccountSlot(GlAccount account, List<org.softcaster.core.data.Currency> currencies) {
        if (!account.isPostable()) {
            return;
        }
        GlAccountSlots slot = null;
        for (org.softcaster.core.data.Currency currency : currencies) {
            slot = glAccountSlotsDAO.findByAccountAndCurrency(account.getAccountId(), currency.getIdCurrency());
            if (slot == null) {
                slot = new GlAccountSlots();
                slot.setAccount(null);
                slot.setAccount(account.getAccountId());
                slot.setCurrency(currency.getIdCurrency());
                account.getSlots().add(slot);
            }
        }
        glAccountDAO.saveOrUpdate(account);
    }
    
    public static void main(String[] args) {
        // Avvia l'applicazione tramite Spring Boot (NON fare "new DatabaseHelper()")
        SpringApplication.run(DatabaseHelper.class, args);
    }
    
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Seleziono le divise presenti nel database
        List<org.softcaster.core.data.Currency> currencies = currencyDAO.findAll();
        // Seleziono i conti presenti su database
        List<GlAccount> accounts = glAccountDAO.findAll();

        for (GlAccount account : accounts) {
            updateGlAccountSlot(account, currencies);
        }
    }

}
