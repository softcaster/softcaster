package org.softcaster.easy_pricer_core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FormRepository extends JpaRepository<Form, Integer> {

    public Form findByIdForm(Integer idForm);

    public Form findByCode(String code);
}
