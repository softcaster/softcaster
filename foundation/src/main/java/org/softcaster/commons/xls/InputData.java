/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.commons.xls;

import java.util.List;

/**
 *
 * @author ep
 */
public class InputData {

    private List<Body> body;
    private InputHeader header;

    /**
     * @return the body
     */
    public List<Body> getBody() {
        return body;
    }

    /**
     * @param body the body to set
     */
    public void setBody(List<Body> body) {
        this.body = body;
    }

    /**
     * @return the header
     */
    public InputHeader getHeader() {
        return header;
    }

    /**
     * @param header the header to set
     */
    public void setHeader(InputHeader header) {
        this.header = header;
    }

}
