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
    
    
    private void addChfOis() {
        Link link = null;
        int id01 = 217715;
        List<Link> links = new ArrayList<>();
        
        link = new Link("7D", id01);
        links.add(link);
        id01 += 2;
        
        link = new Link("14D", id01);
        links.add(link);
        id01 += 2;
        
        link = new Link("21D", id01);
        links.add(link);
        id01 += 2;
        
        link = new Link("1M", id01);
        links.add(link);
        id01 += 2;

        link = new Link("2M", id01);
        links.add(link);
        id01 += 2;

        link = new Link("3M", id01);
        links.add(link);
        id01 += 2;

        link = new Link("3Y", id01);
        links.add(link);
        id01 += 2;

        link = new Link("4Y", id01);
        links.add(link);
        id01 += 2;

        link = new Link("5Y", id01);
        links.add(link);
        id01 += 2;

        link = new Link("6Y", id01);
        links.add(link);
        id01 += 2;

        link = new Link("7Y", id01);
        links.add(link);
        id01 += 2;

        link = new Link("8Y", id01);
        links.add(link);
        id01 += 2;

        link = new Link("9Y", id01);
        links.add(link);
        id01 += 2;

        link = new Link("10Y", id01);
        links.add(link);
        id01 += 2;

        link = new Link("12Y", id01);
        links.add(link);
        id01 += 2;

        link = new Link("15Y", id01);
        links.add(link);
        id01 += 2;

        link = new Link("20Y", id01);
        links.add(link);
        id01 += 2;

        link = new Link("25Y", id01);
        links.add(link);
        id01 += 2;

        link = new Link("30Y", id01);
        links.add(link);
        id01 += 2;

        link = new Link("40Y", id01);
        links.add(link);
        id01 += 2;
        
        link = new Link("50Y", id01);
        links.add(link);
        
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
