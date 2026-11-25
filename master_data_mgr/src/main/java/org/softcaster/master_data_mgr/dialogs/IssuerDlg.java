package org.softcaster.master_data_mgr.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.core.data.Country;
import org.softcaster.core.data.Issuer;
import org.softcaster.master_data_mgr.MasterDataFacade;
import org.softcaster.master_data_mgr.models.beans.IssuerBean;

/**
 * Emittente (stato o banca): nome breve, nome esteso e paese. Serve ai titoli
 * e alle testate dei covered warrant.
 *
 * Dialog costruito in codice, senza .form.
 */
public class IssuerDlg extends javax.swing.JDialog {

    // lunghezza di issuer.short_issuer_name
    private static final int SHORT_NAME_LENGTH = 25;

    private final IssuerBean bean;
    private final MasterDataFacade masterDataFacade;

    private final JTextField txtShortName = new JTextField();
    private final JTextField txtLongName = new JTextField();
    private final JComboBox<Country> cbCountry = new JComboBox<>();

    public IssuerDlg(java.awt.Frame parent, boolean modal, IssuerBean bean, MasterDataFacade masterDataFacade) {
        super(parent, modal);
        this.bean = bean;
        this.masterDataFacade = masterDataFacade;
        initComponents();
        postInit();
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Issuer");
        getContentPane().setLayout(new BorderLayout(10, 10));

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        addField(fields, 0, 0, "Short Name", txtShortName);
        addField(fields, 1, 0, "Long Name", txtLongName);
        addField(fields, 0, 1, "Country", cbCountry);
        GridBagConstraints filler = new GridBagConstraints();
        filler.gridy = 4;
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

        setBounds(0, 0, 606, 250);
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
        List<Country> countries = masterDataFacade.getCountryDAO().findAll();
        cbCountry.setModel(new DefaultComboBoxModel<>(countries.toArray(Country[]::new)));
        cbCountry.setSelectedIndex(-1);
        if (bean != null) {
            Issuer issuer = bean.getIssuer();
            txtShortName.setText(issuer.getShortIssuerName());
            txtLongName.setText(issuer.getLongIssuerName());
            if (issuer.getCountry() != null) {
                Integer id = issuer.getCountry().getIdCountry();
                for (int i = 0; i < cbCountry.getItemCount(); i++) {
                    if (Objects.equals(cbCountry.getItemAt(i).getIdCountry(), id)) {
                        cbCountry.setSelectedIndex(i);
                    }
                }
            }
        }
    }

    /**
     * @return il messaggio d'errore, null se i dati sono corretti
     */
    private String validateIntegrity() {
        String shortName = txtShortName.getText().trim();
        if (shortName.isEmpty() || txtLongName.getText().isBlank()) {
            return "Please fill short and long name.";
        }
        if (shortName.length() > SHORT_NAME_LENGTH) {
            return "The short name can have at most " + SHORT_NAME_LENGTH + " characters.";
        }
        if (cbCountry.getSelectedItem() == null) {
            return "Please select the country.";
        }
        Issuer existing = masterDataFacade.getIssuerDAO().findByShortIssuerName(shortName);
        if (existing != null && (bean == null || !existing.getIdIssuer().equals(bean.getIssuer().getIdIssuer()))) {
            return "Issuer " + shortName + " already exists.";
        }
        return null;
    }

    private void save() {
        String error = validateIntegrity();
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Issuer issuer = bean == null ? new Issuer() : bean.getIssuer();
            issuer.setShortIssuerName(txtShortName.getText().trim());
            issuer.setLongIssuerName(txtLongName.getText().trim());
            issuer.setCountry((Country) cbCountry.getSelectedItem());
            masterDataFacade.getIssuerDAO().saveOrUpdate(issuer);
            dispose();
        } catch (Exception ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            JOptionPane.showMessageDialog(this, "Error during save.\n See log file for more details.", "Save Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
