/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mtm.evaluators;

import java.time.LocalDate;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.SecurityMasterData;
import org.softcaster.easy_pricer_mds_core.calc.CashFlowInstrumentCalculator;
import org.softcaster.engine.dto.XRBInput;
import org.softcaster.engine.dto.XRBResult;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class EvaluatorHelper {

    private final CashFlowInstrumentCalculator calculator;

    // Iniezione tramite costruttore (best practice)
    public EvaluatorHelper(@Qualifier("cashFlowInstrumentCalculator") CashFlowInstrumentCalculator calculator) {
        this.calculator = calculator;
    }

    public XRBResult calculateXRB(MasterData masterData, double mktPrice, LocalDate officialDate) {
        if (masterData instanceof SecurityMasterData smd) {
            XRBInput bondInputData = calculator.getXRBInput(officialDate, mktPrice, smd);
            return calculator.fixedIncomeValuation(bondInputData);
        }
        
        return null;
    }
}
