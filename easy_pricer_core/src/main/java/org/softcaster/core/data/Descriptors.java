package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;
import org.softcaster.core.data.converters.ServiceTypeConverter;
import org.softcaster.engine.enums.ServiceType;

@Entity
@Table(name = "descriptors")
@SuppressWarnings("PersistenceUnitPresent")

public class Descriptors implements Serializable {

    @Id
    @SequenceGenerator(name = "descriptors_seq", sequenceName = "descriptors_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "descriptors_seq")
    @Column(name = "descriptor_id")
    private Integer descriptorId;

    @Convert(converter = ServiceTypeConverter.class)
    @Column(name = "service_type")
    private ServiceType serviceType;

    @Column(name = "jar_path")
    private String jarPath;

    @Column(name = "active_profile")
    private String activeProfile;

    @Column(name = "port")
    private String port;

    public Integer getDescriptorId() {
        return descriptorId;
    }

    public void setDescriptorId(Integer descriptorId) {
        this.descriptorId = descriptorId;
    }

    public String getJarPath() {
        return jarPath;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    public String getActiveProfile() {
        return activeProfile;
    }

    public void setActiveProfile(String activeProfile) {
        this.activeProfile = activeProfile;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Descriptors that)) {
            return false;
        }

        return descriptorId != null
                && descriptorId.equals(that.descriptorId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * @return the serviceType
     */
    public ServiceType getServiceType() {
        return serviceType;
    }

    /**
     * @param serviceType the serviceType to set
     */
    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }
}
