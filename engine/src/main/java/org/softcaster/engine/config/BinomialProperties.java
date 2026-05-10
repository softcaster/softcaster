/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

// Nel file che include la libreria, in application.yml aggiungere
// engine:
//   binomial:
//      step: 1000
@ConfigurationProperties(prefix = "engine.binomial")
public class BinomialProperties {

    /**
     * Number of steps in the CRR tree
     */
    private int steps = 1000;

    /**
     * @return the steps
     */
    public int getSteps() {
        return steps;
    }

    /**
     * @param steps the steps to set
     */
    public void setSteps(int steps) {
        this.steps = steps;
    }

}
