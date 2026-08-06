/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.yahoo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;

/**
 *
 * @author ep
 */
public class Quote {

    @JsonProperty("open")
    public ArrayList<Double> myopen;
    public ArrayList<Integer> volume;
    public ArrayList<Double> high;
    public ArrayList<Double> close;
    public ArrayList<Double> low;
}
