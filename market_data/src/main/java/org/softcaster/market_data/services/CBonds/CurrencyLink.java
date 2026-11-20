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
    }
    
    
    private void addChfOis() {
        Link link = null;
        int id01 = 217715;
        List<Link> links = new ArrayList<>();
        link = new Link("7D", id01);
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

        link = new Link("4M", id01);
        links.add(link);
        id01 += 2;

        link = new Link("5M", id01);
        links.add(link);
        id01 += 2;

        link = new Link("6M", id01);
        links.add(link);
        id01 += 2;

        link = new Link("7M", id01);
        links.add(link);
        id01 += 2;

        link = new Link("8M", id01);
        links.add(link);
        id01 += 2;

        link = new Link("9M", id01);
        links.add(link);
        id01 += 2;

        link = new Link("10M", id01);
        links.add(link);
        id01 += 2;

        link = new Link("11M", id01);
        links.add(link);
        id01 += 2;

        link = new Link("1Y", id01);
        links.add(link);
        id01 += 2;

        link = new Link("15M", id01);
        links.add(link);
        id01 += 2;

        link = new Link("18M", id01);
        links.add(link);
        id01 += 2;

        link = new Link("21M", id01);
        links.add(link);
        id01 += 2;

        link = new Link("2Y", id01);
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
        // il 40Y (id01 + 2) ripete il valore del 30Y: non e' una quotazione, non si carica

        mapLinks.put("CHF", links);
    }
    
    
}
