/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_acct.context;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ep
 */
public class JournalDsl {

    private final List<JournalLine> lines = new ArrayList<>();

    public void debit(String account, BigDecimal amount) {
        lines.add(new JournalLine(account, amount));
    }

    public void credit(String account, BigDecimal amount) {
        lines.add(new JournalLine(account, amount));
    }

    public List<JournalLine> build() {
        return List.copyOf(lines);
    }
}
