/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.dto;

/**
 *
 * @author ep
 */
public abstract class  GenericMasterDataDto {
    
    private Integer genericMasterDataId;
    private String code;
    private String description;

    /**
     * @return the genericMasterDataId
     */
    public Integer getGenericMasterDataId() {
        return genericMasterDataId;
    }

    /**
     * @param genericMasterDataId the genericMasterDataId to set
     */
    public void setGenericMasterDataId(Integer genericMasterDataId) {
        this.genericMasterDataId = genericMasterDataId;
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
    
    public abstract String getClassIdentity();
}
