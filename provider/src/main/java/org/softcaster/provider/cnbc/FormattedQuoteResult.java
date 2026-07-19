/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.cnbc;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;

/**
 *
 * @author softc
 */
public class FormattedQuoteResult {

    @JsonProperty("FormattedQuote")
    public ArrayList<FormattedQuote> formattedQuote;
}
