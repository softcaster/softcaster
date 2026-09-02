/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.dto;

import java.time.LocalDate;

/**
 *
 * @author ep
 */
public class MasterDataDto extends GenericMasterDataDto {

    private String assetClass = "";
    private String currency = "";
    private double issuePrice;
    private double redempionPrice;
    private double interestRate;
    private LocalDate issueDate;
    private LocalDate maturityDate;
    
    @Override
    public String getClassIdentity() {
        return "MasterDataDto";
    }

    /**
     * @return the assetClass
     */
    public String getAssetClass() {
        return assetClass;
    }

    /**
     * @param assetClass the assetClass to set
     */
    public void setAssetClass(String assetClass) {
        this.assetClass = assetClass;
    }

    /**
     * @return the currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * @param currency the currency to set
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * @return the issuePrice
     */
    public double getIssuePrice() {
        return issuePrice;
    }

    /**
     * @param issuePrice the issuePrice to set
     */
    public void setIssuePrice(double issuePrice) {
        this.issuePrice = issuePrice;
    }

    /**
     * @return the redempionPrice
     */
    public double getRedempionPrice() {
        return redempionPrice;
    }

    /**
     * @param redempionPrice the redempionPrice to set
     */
    public void setRedempionPrice(double redempionPrice) {
        this.redempionPrice = redempionPrice;
    }

    /**
     * @return the interestRate
     */
    public double getInterestRate() {
        return interestRate;
    }

    /**
     * @param interestRate the interestRate to set
     */
    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    /**
     * @return the issueDate
     */
    public LocalDate getIssueDate() {
        return issueDate;
    }

    /**
     * @param issueDate the issueDate to set
     */
    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    /**
     * @return the maturityDate
     */
    public LocalDate getMaturityDate() {
        return maturityDate;
    }

    /**
     * @param maturityDate the maturityDate to set
     */
    public void setMaturityDate(LocalDate maturityDate) {
        this.maturityDate = maturityDate;
    }
    
}
