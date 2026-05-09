/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale; // Import fondamentale
import org.softcaster.engine.cashflow.CashFlow;

public class CashFlowExporter {

    public static void toCsv(List<CashFlow> flows, String filePath, String sep) throws IOException {
        // Excel italiano vuole il ";" come separatore di colonna e la "," per i decimali
        Locale currentLocale = Locale.getDefault();

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Header
            writer.println(String.join(sep,
                    "AccrualStart", "AccrualEnd", "PaymentDate",
                    "Principal", "Interest", "Total", "Balance"));

            for (CashFlow cf : flows) {
                // Usiamo String.format passando il Locale corrente
                String line = String.format(currentLocale,
                        "%s%s%s%s%s%s%.2f%s%.2f%s%.2f%s%.2f%s",
                        cf.accrualStart(), sep,
                        cf.accrualEnd(), sep,
                        cf.paymentDate(), sep,
                        cf.principal(), sep,
                        cf.interest(), sep,
                        cf.getTotalAmount(), sep,
                        cf.outstandingBalance(), sep
                );
                writer.println(line);
            }
        }
    }
}
