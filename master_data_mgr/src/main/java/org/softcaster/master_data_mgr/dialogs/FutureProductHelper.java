package org.softcaster.master_data_mgr.dialogs;

import java.util.Objects;
import java.util.function.Function;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;
import org.softcaster.commons.utils.Converter;
import org.softcaster.core.data.FutureMasterData;
import org.softcaster.core.data.FutureProduct;
import org.softcaster.core.data.FutureProductDAO;

/**
 * Testata (FutureProduct) nelle finestre delle scadenze dei future.
 *
 * Le testate si inseriscono da Future Products; la scadenza sceglie la sua
 * dalla combo Product e ne mostra i dati di prodotto in sola lettura.
 */
final class FutureProductHelper {

    private FutureProductHelper() {
    }

    /**
     * Combo con le testate del tipo della finestra, senza selezione.
     */
    static <P extends FutureProduct> void setUpProductCombo(JComboBox<P> combo, FutureProductDAO dao, Class<P> type) {
        DefaultComboBoxModel<P> model = new DefaultComboBoxModel<>();
        dao.findAll(type).forEach(model::addElement);
        combo.setModel(model);
        combo.setSelectedIndex(-1);
    }

    /**
     * Seleziona per id: le testate (e i loro sottostanti LAZY) caricate dalla
     * combo sono istanze diverse da quelle della scadenza. Leggere l'id di un
     * proxy non lo inizializza.
     */
    static <T> void selectById(JComboBox<T> combo, Object id, Function<T, Object> idOf) {
        combo.setSelectedIndex(-1);
        if (id == null) {
            return;
        }
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (Objects.equals(id, idOf.apply(combo.getItemAt(i)))) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    /**
     * Dati di prodotto in sola lettura: si modificano da Future Products.
     */
    static void lock(JComponent... components) {
        for (JComponent c : components) {
            if (c instanceof JTextComponent text) {
                text.setEditable(false);
            } else {
                c.setEnabled(false);
            }
        }
    }

    static String format(Double value) {
        return value == null ? "" : Converter.fromDouble(value);
    }

    /**
     * Valori della testata copiati nella scadenza (master_data).
     */
    static void applyProduct(FutureMasterData expiry, FutureProduct product) {
        expiry.setCurrency(product.getCurrency());
        expiry.setAssetClass(product.getAssetClass());
        expiry.setBusinessDays(product.getBusinessDays());
        expiry.setMultiplier(product.getMultiplier());
    }
}
