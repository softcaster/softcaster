/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.market_data.services.CBonds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CurrencyLink {

    private final Map<String, List<Link>> mapLinks = new HashMap<>();

    public List<Link> getLinks(String key) {
        return mapLinks.get(key);
    }
    
    public CurrencyLink() {
        addChfOis();
        addUsdOis();
    }    
    
    /**
     * OIS CHF (Annual Money vs SARON). Id della pagina Cbonds, espliciti: il 40Y
     * (217775) ripete il valore del 30Y e non si carica.
     */
    private void addChfOis() {
        List<Link> links = new ArrayList<>();
        links.add(new Link("7D", 217715));
        links.add(new Link("1M", 217717));
        links.add(new Link("2M", 217719));
        links.add(new Link("3M", 217721));
        links.add(new Link("4M", 217723));
        links.add(new Link("5M", 217725));
        links.add(new Link("6M", 217727));
        links.add(new Link("7M", 217729));
        links.add(new Link("8M", 217731));
        links.add(new Link("9M", 217733));
        links.add(new Link("10M", 217735));
        links.add(new Link("11M", 217737));
        links.add(new Link("1Y", 217739));
        links.add(new Link("15M", 217741));
        links.add(new Link("18M", 217743));
        links.add(new Link("21M", 217745));
        links.add(new Link("2Y", 217747));
        links.add(new Link("3Y", 217749));
        links.add(new Link("4Y", 217751));
        links.add(new Link("5Y", 217753));
        links.add(new Link("6Y", 217755));
        links.add(new Link("7Y", 217757));
        links.add(new Link("8Y", 217759));
        links.add(new Link("9Y", 217761));
        links.add(new Link("10Y", 217763));
        links.add(new Link("12Y", 217765));
        links.add(new Link("15Y", 217767));
        links.add(new Link("20Y", 217769));
        links.add(new Link("25Y", 217771));
        links.add(new Link("30Y", 217773));
        mapLinks.put("CHF", links);
    }
    /**
     * OIS USD (Annual Money vs SOFR). Cbonds non pubblica tutti i tenor: tra il
     * 3M e il 3Y mancano (4M...2Y), la curva SOFR_OIS li completa con
     * TERMSOFR (yield_curve.fill_curve). Id espliciti per i buchi.
     */
    private void addUsdOis() {
        List<Link> links = new ArrayList<>();
        links.add(new Link("1W", 218471));
        links.add(new Link("2W", 218473));
        links.add(new Link("3W", 218475));
        links.add(new Link("1M", 218477));
        links.add(new Link("2M", 218479));
        links.add(new Link("3M", 218481));
        links.add(new Link("3Y", 218499));
        links.add(new Link("4Y", 218501));
        links.add(new Link("5Y", 218503));
        links.add(new Link("6Y", 218505));
        links.add(new Link("7Y", 218507));
        links.add(new Link("8Y", 218509));
        links.add(new Link("9Y", 218511));
        links.add(new Link("10Y", 218513));
        links.add(new Link("12Y", 218515));
        links.add(new Link("15Y", 218517));
        links.add(new Link("20Y", 218519));
        links.add(new Link("25Y", 218521));
        links.add(new Link("30Y", 218523));
        links.add(new Link("40Y", 218525));
        links.add(new Link("50Y", 218527));
        mapLinks.put("USD", links);
    }
}
