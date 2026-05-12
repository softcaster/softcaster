/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider;

import java.util.ArrayList;
import java.util.List;
import org.softcaster.provider.bricks.Request;
import org.softcaster.provider.enums.Market;

/**
 *
 * @author ep
 */
public class ProviderInfo {

    private List<String> extraParameters = new ArrayList();
    // Link alle varie richieste specifiche
    private List<Request> requests = new ArrayList<>();

    public Request getRequest(Market market) {
        for (Request r : requests) {
            if (r.market() == market) {
                return r;
            }
        }

        return null;
    }

    /**
     * @return the requests
     */
    public List<Request> getRequests() {
        return requests;
    }

    /**
     * @param requests the requests to set
     */
    public void setRequests(List<Request> requests) {
        this.requests = requests;
    }

    /**
     * @return the extraParameters
     */
    public List<String> getExtraParameters() {
        return extraParameters;
    }

    /**
     * @param extraParameters the extraParameters to set
     */
    public void setExtraParameters(List<String> extraParameters) {
        this.extraParameters = extraParameters;
    }
}
