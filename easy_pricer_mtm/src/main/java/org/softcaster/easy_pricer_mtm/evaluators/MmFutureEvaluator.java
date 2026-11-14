package org.softcaster.easy_pricer_mtm.evaluators;

import org.springframework.stereotype.Component;

/**
 * Future money market: valutati alla quotazione di mercato, come i future su
 * commodity (nessuna curva per il prezzo teorico).
 */
@Component("MFU")
public class MmFutureEvaluator extends CmdFutureEvaluator {
}
