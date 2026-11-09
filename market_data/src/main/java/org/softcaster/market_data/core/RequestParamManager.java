/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.market_data.core;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author softc
 */
public class RequestParamManager {

    private String beanName = null;
    private final List<String> requestParams = new ArrayList<>();

    public void add(String param) {
        requestParams.add(param);
    }

    public String get(int pos) {
        return requestParams.get(pos);
    }

    public void clear() {
        requestParams.clear();
    }
    /**
     * @return the beanName
     */
    public String getBeanName() {
        return beanName;
    }

    /**
     * @param beanName the beanName to set
     */
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }
}
