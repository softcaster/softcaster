/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package org.softcaster.commons;

import org.softcaster.commons.types.Date;
import org.softcaster.commons.types.DateParser;
import org.softcaster.commons.utils.NumberUtils;

/**
 *
 * @author softc
 */
public class Foundation {

    public static void main(String[] args) {
        DateParser parser = new DateParser("300126");
        Date dt = new Date(parser.year(),parser.month(),parser.day());
        System.out.println(dt.toString());
        
        Date dt2 = new Date();
        System.out.println(dt2.toString());

        System.out.println(NumberUtils.isOdd(13));
        System.out.println(NumberUtils.isOdd(14));
    }
}
