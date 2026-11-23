package org.softcaster.easy_pricer_mtm.evaluators;

import org.springframework.stereotype.Component;

/**
 * MtM dei future su indice (FESX): come i future su commodity, prezzo
 * teorico = prezzo di mercato della scadenza (quotazione Eurex), P&L non
 * realizzato su prezzo x multiplier.
 */
@Component("IXF")
public class IndexFutureEvaluator extends CmdFutureEvaluator {
}
