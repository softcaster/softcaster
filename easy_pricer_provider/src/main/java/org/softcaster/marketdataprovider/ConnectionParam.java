/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.marketdataprovider;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ep
 */
public class ConnectionParam {
    // Link alla pagina madre
    public String baseUrl;
    // Link alla richiesta effettiva
    public String url;
    // Determina quale url usare
    public boolean useBaseUrl = true;
    // Mercato a cui si riferisce la richiesta
    public MARKETS market;
    // Data corrente
    public org.softcaster.commons.types.Date today = new org.softcaster.commons.types.Date();
    // Eventuali parametri extra
    public List<String> extraParams = new ArrayList<>();
}
