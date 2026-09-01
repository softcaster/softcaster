package org.softcaster.core.data;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "country")
@SuppressWarnings("PersistenceUnitPresent")

public class Country implements Serializable {

    @Id
    @SequenceGenerator(name = "country_seq", sequenceName = "country_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "country_seq")
    @Column(name = "id_country", columnDefinition = "INTEGER")
    private Integer idCountry;

    @Column(name = "country_name")
    private String countryName;

    @Column(name = "official_state_name")
    private String officialStateName;

    @Column(name = "alfa_2_code")
    private String alfa2Code;

    @Column(name = "alfa_3_code")
    private String alfa3Code;

    @Column(name = "country_numeric_code")
    private Short countryNumericCode;

    @Column(name = "sovereign")
    private String sovereign;

    @Column(name = "subdivision_code_links")
    private String subdivisionCodeLinks;

    @Column(name = "internet_cc_tld")
    private String internetCcTld;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency", nullable = true)
    private Currency currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar", nullable = true)
    private Calendar calendar;

    public Integer getIdCountry() {
        return idCountry;
    }

    public void setIdCountry(Integer idCountry) {
        this.idCountry = idCountry;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getOfficialStateName() {
        return officialStateName;
    }

    public void setOfficialStateName(String officialStateName) {
        this.officialStateName = officialStateName;
    }

    public String getAlfa2Code() {
        return alfa2Code;
    }

    public void setAlfa2Code(String alfa2Code) {
        this.alfa2Code = alfa2Code;
    }

    public String getAlfa3Code() {
        return alfa3Code;
    }

    public void setAlfa3Code(String alfa3Code) {
        this.alfa3Code = alfa3Code;
    }

    public Short getCountryNumericCode() {
        return countryNumericCode;
    }

    public void setCountryNumericCode(Short countryNumericCode) {
        this.countryNumericCode = countryNumericCode;
    }

    public String getSovereign() {
        return sovereign;
    }

    public void setSovereign(String sovereign) {
        this.sovereign = sovereign;
    }

    public String getSubdivisionCodeLinks() {
        return subdivisionCodeLinks;
    }

    public void setSubdivisionCodeLinks(String subdivisionCodeLinks) {
        this.subdivisionCodeLinks = subdivisionCodeLinks;
    }

    public String getInternetCcTld() {
        return internetCcTld;
    }

    public void setInternetCcTld(String internetCcTld) {
        this.internetCcTld = internetCcTld;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getIdCountry() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Country that = (Country) obj;
        return getIdCountry().equals(that.getIdCountry());
    }

    @Override
    public int hashCode() {
        return getIdCountry() == null ? 0 : idCountry.hashCode();
    }

     @Override
    public String toString() {
        return alfa3Code;
    }
    
    /**
     * @return the calendar
     */
    public Calendar getCalendar() {
        return calendar;
    }

    /**
     * @param calendar the calendar to set
     */
    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }
}
