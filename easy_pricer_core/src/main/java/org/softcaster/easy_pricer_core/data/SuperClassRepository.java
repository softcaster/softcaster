package org.softcaster.easy_pricer_core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SuperClassRepository extends JpaRepository<SuperClass, Integer> {

    public SuperClass findByIdSuperClass(Integer idSuperClass);
}
