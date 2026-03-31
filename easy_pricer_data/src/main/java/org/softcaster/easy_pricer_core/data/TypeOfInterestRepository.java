package org.softcaster.easy_pricer_core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeOfInterestRepository extends JpaRepository<TypeOfInterest, Integer> {

    public TypeOfInterest findByIdTypeOfInterest(Integer idTypeOfInterest);

    public TypeOfInterest findByCode(String code);
}
