/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_import.beans;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EexContractRow {

    private String productGroup;
    private String productId;

    private Integer expiryYear;
    private Integer expiryMonth;

    private LocalDate firstTradingDate;
    private LocalDate lastTradingDate;
    private LocalDate expiryDate;

    private LocalDate firstDeliveryDate;
    private LocalDate lastDeliveryDate;

    private BigDecimal contractSize;

    // getter/setter

    /**
     * @return the productGroup
     */
    public String getProductGroup() {
        return productGroup;
    }

    /**
     * @param productGroup the productGroup to set
     */
    public void setProductGroup(String productGroup) {
        this.productGroup = productGroup;
    }

    /**
     * @return the productId
     */
    public String getProductId() {
        return productId;
    }

    /**
     * @param productId the productId to set
     */
    public void setProductId(String productId) {
        this.productId = productId;
    }

    /**
     * @return the expiryYear
     */
    public Integer getExpiryYear() {
        return expiryYear;
    }

    /**
     * @param expiryYear the expiryYear to set
     */
    public void setExpiryYear(Integer expiryYear) {
        this.expiryYear = expiryYear;
    }

    /**
     * @return the expiryMonth
     */
    public Integer getExpiryMonth() {
        return expiryMonth;
    }

    /**
     * @param expiryMonth the expiryMonth to set
     */
    public void setExpiryMonth(Integer expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    /**
     * @return the firstTradingDate
     */
    public LocalDate getFirstTradingDate() {
        return firstTradingDate;
    }

    /**
     * @param firstTradingDate the firstTradingDate to set
     */
    public void setFirstTradingDate(LocalDate firstTradingDate) {
        this.firstTradingDate = firstTradingDate;
    }

    /**
     * @return the lastTradingDate
     */
    public LocalDate getLastTradingDate() {
        return lastTradingDate;
    }

    /**
     * @param lastTradingDate the lastTradingDate to set
     */
    public void setLastTradingDate(LocalDate lastTradingDate) {
        this.lastTradingDate = lastTradingDate;
    }

    /**
     * @return the expiryDate
     */
    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    /**
     * @param expiryDate the expiryDate to set
     */
    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    /**
     * @return the firstDeliveryDate
     */
    public LocalDate getFirstDeliveryDate() {
        return firstDeliveryDate;
    }

    /**
     * @param firstDeliveryDate the firstDeliveryDate to set
     */
    public void setFirstDeliveryDate(LocalDate firstDeliveryDate) {
        this.firstDeliveryDate = firstDeliveryDate;
    }

    /**
     * @return the lastDeliveryDate
     */
    public LocalDate getLastDeliveryDate() {
        return lastDeliveryDate;
    }

    /**
     * @param lastDeliveryDate the lastDeliveryDate to set
     */
    public void setLastDeliveryDate(LocalDate lastDeliveryDate) {
        this.lastDeliveryDate = lastDeliveryDate;
    }

    /**
     * @return the contractSize
     */
    public BigDecimal getContractSize() {
        return contractSize;
    }

    /**
     * @param contractSize the contractSize to set
     */
    public void setContractSize(BigDecimal contractSize) {
        this.contractSize = contractSize;
    }
}
