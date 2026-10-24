package org.softcaster.easy_pricer_mds_core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.List;
import org.softcaster.engine.cashflow.AmortizedCostPeriod;

/**
 * Risposta di pricing unica per tutti gli strumenti. Ogni calcolatore
 * valorizza solo i campi che calcola: gli altri restano null e non compaiono
 * nel JSON.
 *
 * Valori per 100 di nominale, tassi in decimale.
 *
 * @author softc
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PricingResponse implements Serializable {

    // -- bond
    public Double accruedInterest = null;
    public Double yieldToMaturity = null;
    public Double macaulayDuration = null;
    public Double modifiedDuration = null;
    public Double convexity = null;
    public Double presentValue = null;
    public Double yieldToMaturityPV = null;
    public Double dv01 = null;
    // floating rate bond
    public Double discountMargin = null;
    public Double shortBondYield = null;
    public Double marginDuration = null;
    public List<AmortizedCostPeriod> acpList = null;

    // -- forward / future
    public Double theoreticalPrice = null;
    // FX future: cambio forward - spot
    public Double forwardPoints = null;
    // bond future: tasso repo a termine fino alla consegna
    public Double termRepoRate = null;
    // bond future: analisi del CTD al prezzo a pronti
    public String ctd = null;
    public Double ctdYield = null;
    public Double ctdModifiedDuration = null;
    public Double ctdDv01 = null;
    // null se la request non ha il prezzo quotato del future
    public Double ctdNetBasis = null;
    public Double ctdImpliedRepo = null;
    // DV01 del future (DV01 del CTD / CF)
    public Double futureDv01 = null;
}
