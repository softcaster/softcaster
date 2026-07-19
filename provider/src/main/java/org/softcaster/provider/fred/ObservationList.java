/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.fred;

import java.util.ArrayList;

public class ObservationList {

    public String realtime_start;
    public String realtime_end;
    public String observation_start;
    public String observation_end;
    public String units;
    public int output_type;
    public String file_type;
    public String order_by;
    public String sort_order;
    public int count;
    public int offset;
    public int limit;
    public ArrayList<Observation> observations;
}
