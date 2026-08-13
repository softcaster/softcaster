/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_eod.services;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import org.softcaster.commons.xml.ParamsMgr;
import org.softcaster.engine.enums.ServiceType;
import org.springframework.stereotype.Service;

@Service
public class MicroserviceDispatcher {

    private final ConcurrentHashMap<ServiceType, RestServiceDescriptor> descriptorsMap = new ConcurrentHashMap<>();

    public MicroserviceDispatcher() {
        loadDescriptors();
    }

    public RestServiceDescriptor getDescriptor(ServiceType type) {
        if (!descriptorsMap.containsKey(type)) {
            return null;
        } else {
            return clone(descriptorsMap.get(type));
        }
    }

    private void loadService(ServiceType type) {
        ParamsMgr paramsMgr = ParamsMgr.getInstance();
        String[] params = paramsMgr.getParamValue(type.getCode()).split(";");
        RestServiceDescriptor descriptor = new RestServiceDescriptor();
        descriptor.setServiceName(type.getCode());
        descriptor.setJarPath(params[0]);
        descriptor.setActiveProfile(params[1]);
        descriptor.setServiceInfo(null);
        descriptorsMap.put(type, descriptor);
    }

    private void loadDescriptors() {
        loadService(ServiceType.TSRV);
        loadService(ServiceType.PSRV);
        loadService(ServiceType.MSRV);
        loadService(ServiceType.ASRV);
        loadService(ServiceType.LSRV);
    }

    private RestServiceDescriptor clone(RestServiceDescriptor descriptor) {
        RestServiceDescriptor copy = new RestServiceDescriptor();
        copy.setActiveProfile(descriptor.getActiveProfile());
        if (descriptor.getAdditionalArgs() != null) {
            copy.setAdditionalArgs(Arrays.asList(descriptor.getAdditionalArgs()));
        }
        copy.setJarPath(descriptor.getJarPath());
        copy.setServiceName(descriptor.getServiceName());
        copy.setServiceInfo(null);
        return copy;
    }
}
