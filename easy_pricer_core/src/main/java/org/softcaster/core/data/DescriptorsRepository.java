package org.softcaster.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DescriptorsRepository extends JpaRepository<Descriptors, Integer> {

    public Descriptors findByDescriptorId(Integer descriptorId);
}
