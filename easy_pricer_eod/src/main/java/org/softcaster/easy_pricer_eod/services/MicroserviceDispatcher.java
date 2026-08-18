/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_eod.services;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.softcaster.core.data.Descriptors;
import org.softcaster.core.data.DescriptorsDAO;
import org.softcaster.engine.enums.ServiceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MicroserviceDispatcher {

    @Autowired
    private DescriptorsDAO descriptorsDAO;

    private final ConcurrentHashMap<ServiceType, RestServiceDescriptor> descriptorsMap = new ConcurrentHashMap<>();

    public MicroserviceDispatcher() {
    }

    @PostConstruct
    public void init() {
        loadDescriptors();
    }

    public RestServiceDescriptor getDescriptor(ServiceType type) {
        if (!descriptorsMap.containsKey(type)) {
            return null;
        } else {
            return clone(descriptorsMap.get(type));
        }
    }

    private void loadService(Descriptors descriptor) {
        RestServiceDescriptor restDescriptor = new RestServiceDescriptor();
        restDescriptor.setServiceName(descriptor.getServiceType().getDescription());
        restDescriptor.setJarPath(descriptor.getJarPath());
        restDescriptor.setActiveProfile(descriptor.getActiveProfile());
        restDescriptor.setServiceInfo(null);
        restDescriptor.setPort(descriptor.getPort());
        descriptorsMap.put(descriptor.getServiceType(), restDescriptor);

    }

    private void loadDescriptors() {
        List<Descriptors> descriptors = descriptorsDAO.findAll();
        for (Descriptors descriptor : descriptors) {
            loadService(descriptor);
        }
    }

    private RestServiceDescriptor clone(RestServiceDescriptor descriptor) {
        RestServiceDescriptor copy = new RestServiceDescriptor();
        copy.setActiveProfile(descriptor.getActiveProfile());
        if (descriptor.getAdditionalArgs() != null) {
            copy.setAdditionalArgs(Arrays.asList(descriptor.getAdditionalArgs()));
        }
        copy.setJarPath(descriptor.getJarPath());
        copy.setServiceName(descriptor.getServiceName());
        copy.setPort(descriptor.getPort());
        copy.setServiceInfo(null);
        return copy;
    }
}
