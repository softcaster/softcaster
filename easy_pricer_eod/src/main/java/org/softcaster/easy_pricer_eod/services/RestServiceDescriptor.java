/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_eod.services;

import java.util.List;
import org.softcaster.easy_pricer_eod.ui.views.ServiceInfo;
import org.springframework.stereotype.Component;

@Component
public class RestServiceDescriptor implements MicroserviceDescriptor {

    private String serviceName = "";
    private String jarPath = "";
    private String activeProfile = "";
    private List<String> additionalArgs = null;
    private ServiceInfo serviceInfo = null;

    @Override
    public String getServiceName() {
        return serviceName;
    }

    @Override
    public String getJarPath() {
        return jarPath;
    }

    @Override
    public String getActiveProfile() {
        return activeProfile;
    }

    @Override
    public String[] getAdditionalArgs() {
        if (additionalArgs != null) {
            return (String[]) additionalArgs.toArray();
        } else {
            return null;
        }
    }

    /**
     * @param serviceName the serviceName to set
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * @param jarPath the jarPath to set
     */
    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    /**
     * @param activeProfile the activeProfile to set
     */
    public void setActiveProfile(String activeProfile) {
        this.activeProfile = activeProfile;
    }

    /**
     * @param additionalArgs the additionalArgs to set
     */
    public void setAdditionalArgs(List<String> additionalArgs) {
        this.additionalArgs = additionalArgs;
    }

    @Override
    public ServiceInfo getServiceInfo() {
        return serviceInfo;
    }

    /**
     * @param serviceInfo the serviceInfo to set
     */
    public void setServiceInfo(ServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
    }
}
