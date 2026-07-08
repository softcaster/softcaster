/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.eex;

public class RequestParams {

    private String shortCode;
    private String area;
    private String product;
    private String commodity;
    private String pricing;
    private String maturity;

    public RequestParams(String symbol) {
        parse(symbol);
    }

    private void parse(String symbol) {
        String[] tokens = symbol.split("@");
        shortCode = tokens[0];
        area = tokens[1];
        product = tokens[2];
        commodity = tokens[3];
        pricing = tokens[4];
        maturity = tokens[5];
    }

    public String getUrl() {

        String url = "/pub/market-data/price-ticker?shortCode=" + shortCode + "&area=" + area + "&product=" + product 
                + "&commodity=" + commodity + "&pricing=" + pricing + "&maturity=" + maturity;
        return url;
    }

    public String getCode() {
        return shortCode+maturity;
    }
}
