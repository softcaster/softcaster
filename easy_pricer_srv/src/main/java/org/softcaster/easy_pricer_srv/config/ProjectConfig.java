package org.softcaster.easy_pricer_srv.config;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ph.alephzero.finance.products.forward.BondForward;
import ph.alephzero.finance.products.forward.ForexForward;

/**
 *
 * @author ep
 */
@Configuration
public class ProjectConfig {

    @Bean(name = "bondForward") // Il nome qui deve corrispondere al @Qualifier
    public BondForward bondForward() {
        return new BondForward();
    }

    @Bean(name = "forexForward")
    public ForexForward forexForward() {
        return new ForexForward();
    }
 
}
