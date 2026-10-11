package org.softcaster.core.data;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedEntityGraphs;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "calibration_profile")
@SuppressWarnings("PersistenceUnitPresent")
@NamedEntityGraphs({
    @NamedEntityGraph(
            name = "CalibrationProfile.fullGraph",
            attributeNodes = {
                @NamedAttributeNode("currency"),
                @NamedAttributeNode("items")}
    )
})
public class CalibrationProfile implements Serializable {

    @Id
    @SequenceGenerator(name = "calibration_profile_seq", sequenceName = "calibration_profile_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "calibration_profile_seq")
    @Column(name = "calibration_profile_id")
    private Integer calibrationProfileId;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "currency", nullable = false)
    private Currency currency;

    @Column(name = "last_update")
    private LocalDate lastUpdate;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumn(name = "calibration_profile", nullable = false) // FK in child table calibration_profile_item
    private List<CalibrationProfileItem> items = new ArrayList<>();

    /**
     * @return the calibrationProfileId
     */
    public Integer getCalibrationProfileId() {
        return calibrationProfileId;
    }

    /**
     * @param calibrationProfileId the calibrationProfileId to set
     */
    public void setCalibrationProfileId(Integer calibrationProfileId) {
        this.calibrationProfileId = calibrationProfileId;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the currency
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * @param currency the currency to set
     */
    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    /**
     * @return the lastUpdate
     */
    public LocalDate getLastUpdate() {
        return lastUpdate;
    }

    /**
     * @param lastUpdate the lastUpdate to set
     */
    public void setLastUpdate(LocalDate lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * @return the items
     */
    public List<CalibrationProfileItem> getItems() {
        return items;
    }

    /**
     * @param items the items to set
     */
    public void setItems(List<CalibrationProfileItem> items) {
        this.items = items;
    }

    public void addItem(CalibrationProfileItem newItem) {
        if (items == null || newItem == null) {
            return;
        }

        // L'indexOf usa internamente il metodo equals() che abbiamo definito
        int index = items.indexOf(newItem);

        if (index != -1) {
            // Item esistente: lo recupera dalla lista e aggiorna solo bid ed ask
            CalibrationProfileItem itemToUpdate = items.get(index);
            itemToUpdate.setBid(newItem.getBid());
            itemToUpdate.setAsk(newItem.getAsk());
        } else {
            // Item nuovo: lo aggiunge alla lista
            items.add(newItem);
        }
    }
}
