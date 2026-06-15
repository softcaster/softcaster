/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_acct.context;

import java.util.ArrayList;
import java.util.List;
import org.softcaster.engine.enums.NormalBalance;

/**
 *
 * @author ep
 */
public class JournalDsl {

    private final List<JournalLine> lines = new ArrayList<>();

    public void debit(String account, double amount, int currency) {
        lines.add(new JournalLine(account, amount, currency, NormalBalance.DEBIT));
    }

    public void credit(String account, double amount, int currency) {
        lines.add(new JournalLine(account, amount, currency, NormalBalance.CREDIT));
    }

    public List<JournalLine> build() {
        return List.copyOf(lines);
    }
}
