/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.bondblox;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;

public class GraphData {

    public ArrayList<ArrayList<Object>> price;
    @JsonProperty("yield")
    public ArrayList<ArrayList<Object>> myyield;
    public int selectedZoom;
}
