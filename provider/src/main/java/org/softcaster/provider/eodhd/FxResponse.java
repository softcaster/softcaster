/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.eodhd;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FxResponse {

    public String code;
    public int timestamp;
    public int gmtoffset;
    @JsonProperty("open")
    public double myopen;
    public double high;
    public double low;
    public double close;
    public int volume;
    public double previousClose;
    public double change;
    public double change_p;
}
