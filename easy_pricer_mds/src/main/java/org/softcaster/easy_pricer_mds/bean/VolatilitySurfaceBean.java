package org.softcaster.easy_pricer_mds.bean;

import java.time.LocalDate;
import org.softcaster.commons.ui.model.IFndtModel;
import org.softcaster.core.data.VolatilitySurfaceEntity;

/**
 * Riga del batch delle volatilita': una superficie (volatility_surface) con
 * la data dell'ultima rilevazione e i simboli CME da cui si carica
 * (CME_VOL_SURFACES in conf/params.xml), vuoti se la superficie non e' configurata.
 */
public class VolatilitySurfaceBean implements IFndtModel {

    private final VolatilitySurfaceEntity bean;
    private final String underlyingCode;
    private final LocalDate lastReferenceDate;
    private final String optionSymbol;
    private final String futureSymbol;
    private final double strikeScale;

    public VolatilitySurfaceBean(VolatilitySurfaceEntity bean, String underlyingCode, LocalDate lastReferenceDate,
            String optionSymbol, String futureSymbol, double strikeScale) {
        this.bean = bean;
        this.underlyingCode = underlyingCode;
        this.lastReferenceDate = lastReferenceDate;
        this.optionSymbol = optionSymbol;
        this.futureSymbol = futureSymbol;
        this.strikeScale = strikeScale;
    }

    @Override
    public Object getValueAt(int columnIndex) {
        return switch (columnIndex) {
            case 0 ->
                bean.getDescription();
            case 1 ->
                bean.getCode();
            case 2 ->
                underlyingCode;
            case 3 ->
                lastReferenceDate;
            case 4 ->
                bean.getMaxAgeDays();
            case 5 ->
                isConfigured() ? optionSymbol + " / " + futureSymbol : "";
            default ->
                null;
        };
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{"Description", "Code", "Underlying", "Last Update", "Max Age (days)", "CME Option / Future"};
    }

    /**
     * Superficie con i simboli CME (CME_VOL_SURFACES): servono solo ai file di
     * settlement per strike.
     */
    public boolean isConfigured() {
        return optionSymbol != null && !optionSymbol.isBlank() && futureSymbol != null && !futureSymbol.isBlank();
    }

    public VolatilitySurfaceEntity getBean() {
        return bean;
    }

    public String getOptionSymbol() {
        return optionSymbol;
    }

    public String getFutureSymbol() {
        return futureSymbol;
    }

    public double getStrikeScale() {
        return strikeScale;
    }
}
