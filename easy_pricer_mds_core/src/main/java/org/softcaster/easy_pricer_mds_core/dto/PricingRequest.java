package org.softcaster.easy_pricer_mds_core.dto;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Richiesta di pricing unica per tutti gli strumenti (bond, bond future, FX
 * future): l'endpoint REST sceglie il calcolatore, ogni calcolatore legge i
 * campi che gli servono e ignora gli altri.
 *
 * I campi numerici facoltativi sono Double: null = non indicato.
 *
 * @author softc
 */
public class PricingRequest implements Serializable {

    // -- comuni
    public String isin = "";
    // bond: prezzo secco; bond future: prezzo quotato del future (null o <= 0 = solo prezzo teorico);
    // FX future: cambio a pronti
    public Double referencePrice = null;
    // data di riferimento, nel JSON "yyyy-MM-dd"
    public LocalDate referenceDate = null;

    // -- bond
    public String yieldCurve = "";
    public boolean fullCalc = false;
    public Double referenceRate = null; // valido per FRB

    // -- forward / future
    // bond future: tasso repo (decimale, ACT/360) se domesticRCurve non e' indicata
    public Double domesticRate = null;
    public String domesticRCurve = "";
    public Double foreignRate = null;
    public String foreignRCurve = "";
    public LocalDate maturityDate = null;
}
