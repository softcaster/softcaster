/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.marketdataprovider;

/**
 *
 * @author ep
 */
public class UrlNode {

    private MARKETS urlId;
    private String urlAddress;

    public UrlNode(MARKETS id, String address) {
        urlId = id;
        urlAddress = address;
    }

    /**
     * @return the urlId
     */
    public MARKETS getUrlId() {
        return urlId;
    }

    /**
     * @param urlId the urlId to set
     */
    public void setUrlId(MARKETS urlId) {
        this.urlId = urlId;
    }

    /**
     * @return the urlAddress
     */
    public String getUrlAddress() {
        return urlAddress;
    }

    /**
     * @param urlAddress the urlAddress to set
     */
    public void setUrlAddress(String urlAddress) {
        this.urlAddress = urlAddress;
    }

}
