package org.softcaster.master_data_mgr.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.ParseException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.softcaster.commons.types.Date;
import org.softcaster.commons.utils.Converter;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.core.data.Currency;
import org.softcaster.core.data.IndexMasterData;
import org.softcaster.core.data.YieldCurveEntity;
import org.softcaster.master_data_mgr.MasterDataFacade;
import org.softcaster.master_data_mgr.models.beans.IndexBean;

/**
 * Indice azionario (SX5E): inserimento e modifica. Codice e valuta si
 * scelgono alla creazione; curva di sconto e dividend yield (continuo, in
 * percentuale) restano modificabili: il dividend yield si allinea a quello
 * implicito riportato dall'import della superficie di volatilita'.
 *
 * Dialog costruito in codice, senza .form.
 */
public class IndexDlg extends javax.swing.JDialog {

    private static final String ASSET_CLASS = "IDX";
    // l'indice non scade: data lontana, come per le coppie di cambi
    private static final Date FAR_MATURITY = new Date(2076, 12, 31);

    private IndexBean bean;
    private final MasterDataFacade masterDataFacade;
    private boolean isInsert = true;

    private final JTextField txtCode = new JTextField();
    private final JTextField txtDescription = new JTextField();
    private final JComboBox<Currency> cbCurrency = new JComboBox<>();
    private final JComboBox<YieldCurveEntity> cbCurve = new JComboBox<>();
    private final JTextField txtDividendYield = new JTextField();

    public IndexDlg(java.awt.Frame parent, boolean modal, IndexBean bean, MasterDataFacade masterDataFacade) {
        super(parent, modal);
        this.bean = bean;
        this.masterDataFacade = masterDataFacade;
        initComponents();
        postInit();
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Equity Index");

        // campi numerici: a destra, formattati all'uscita (come BondDlg)
        txtDividendYield.setHorizontalAlignment(JTextField.RIGHT);
        txtDividendYield.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                MDDialogHelper.textFieldDoubleFocusLost(txtDividendYield);
            }
        });
        getContentPane().setLayout(new BorderLayout(10, 10));

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        addField(fields, 0, 0, "Code", txtCode);
        addField(fields, 1, 0, "Description", txtDescription);
        addField(fields, 0, 1, "Currency", cbCurrency);
        addField(fields, 1, 1, "Discount Curve", cbCurve);
        addField(fields, 0, 2, "Dividend Yield % (cont.)", txtDividendYield);
        GridBagConstraints filler = new GridBagConstraints();
        filler.gridy = 6;
        filler.weighty = 1.0;
        fields.add(new JLabel(), filler);
        getContentPane().add(fields, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Save");
        btnSave.addActionListener(e -> save());
        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dispose());
        buttons.add(btnSave);
        buttons.add(btnCancel);
        getContentPane().add(buttons, BorderLayout.SOUTH);

        setBounds(0, 0, 606, 307);
    }

    private static void addField(JPanel panel, int column, int row, String label, JComponent field) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = column;
        c.gridy = row * 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 1.0;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(new JLabel(label), c);
        c = (GridBagConstraints) c.clone();
        c.gridy = row * 2 + 1;
        panel.add(field, c);
    }

    private void postInit() {
        List<Currency> currencies = masterDataFacade.getCurrencyDAO().findAll();
        cbCurrency.setModel(new DefaultComboBoxModel<>(currencies.toArray(Currency[]::new)));
        List<YieldCurveEntity> curves = masterDataFacade.getYieldCurveDAO().findAll();
        cbCurve.setModel(new DefaultComboBoxModel<>(curves.toArray(YieldCurveEntity[]::new)));

        if (bean != null) {
            isInsert = false;
            IndexMasterData imd = bean.getIndexMasterData();
            txtCode.setText(imd.getCode());
            txtCode.setEditable(false);
            txtDescription.setText(imd.getDescription());
            FutureProductHelper.selectById(cbCurrency, imd.getCurrency().getIdCurrency(), Currency::getIdCurrency);
            cbCurrency.setEnabled(false);
            cbCurve.setSelectedItem(masterDataFacade.getYieldCurveDAO().findByCode(imd.getIrc()));
            double q = imd.getDividendYield() == null ? 0. : imd.getDividendYield();
            txtDividendYield.setText(Converter.fromDouble(q * 100.));
        } else {
            txtDividendYield.setText(Converter.fromDouble(0.));
        }
    }

    /**
     * @return il messaggio d'errore, null se i dati sono corretti
     */
    private String validateIntegrity() {
        if (txtCode.getText().isBlank() || txtDescription.getText().isBlank()) {
            return "Please fill code and description.";
        }
        if (cbCurrency.getSelectedItem() == null || cbCurve.getSelectedItem() == null) {
            return "Please select currency and discount curve.";
        }
        if (isInsert && masterDataFacade.getMasterDataDAO().findByCode(txtCode.getText().trim()) != null) {
            return "Master data " + txtCode.getText().trim() + " already exists.";
        }
        try {
            dividendYield();
        } catch (ParseException | NumberFormatException ex) {
            return "Please check the dividend yield.";
        }
        return null;
    }

    private double dividendYield() throws ParseException {
        return txtDividendYield.getText().isBlank() ? 0. : Converter.toDouble(txtDividendYield.getText(), false) / 100.;
    }

    private void save() {
        String error = validateIntegrity();
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            IndexMasterData imd;
            if (isInsert) {
                imd = new IndexMasterData();
                imd.setCode(txtCode.getText().trim());
                imd.setAssetClass(masterDataFacade.findAssetClass(ASSET_CLASS));
                imd.setCurrency((Currency) cbCurrency.getSelectedItem());
                imd.setBusinessDays(0);
                imd.setMultiplier(1.);
                imd.setIssueDate(new Date().sqlDate());
                imd.setMaturityDate(FAR_MATURITY.sqlDate());
            } else {
                imd = bean.getIndexMasterData();
            }
            imd.setDescription(txtDescription.getText().trim());
            imd.setIrc(((YieldCurveEntity) cbCurve.getSelectedItem()).getCode());
            imd.setDividendYield(dividendYield());
            masterDataFacade.getIndexMasterDataDAO().saveOrUpdate(imd);
            bean = new IndexBean(imd);
            dispose();
        } catch (Exception ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            JOptionPane.showMessageDialog(this, "Error during save.\n See log file for more details.", "Save Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
