package org.softcaster.core.data;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Testate delle opzioni (option_product e rami future/spot).
 */
@Component
@Transactional(readOnly = true)
public class OptionProductDAO {

    private final OptionProductRepository repository;

    public OptionProductDAO(OptionProductRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Transactional(readOnly = true)
    public Optional<OptionProduct> findByCode(String code) {
        return repository.findByCode(code);
    }

    @Transactional(readOnly = true)
    public <T extends OptionProduct> Optional<T> findByCode(String code, Class<T> type) {
        return repository.findByCode(code)
                .map(Hibernate::unproxy)
                .filter(type::isInstance)
                .map(type::cast);
    }

    @Transactional(readOnly = true)
    public List<OptionProduct> findAll() {
        return repository.findAllOrderByCode();
    }

    @Transactional(readOnly = true)
    public <T extends OptionProduct> List<T> findAll(Class<T> type) {
        return repository.findAllOrderByCode().stream()
                .map(Hibernate::unproxy)
                .filter(type::isInstance)
                .map(type::cast)
                .toList();
    }

    @Transactional
    public <T extends OptionProduct> T saveOrUpdate(T product) {
        return repository.save(product);
    }

    @Transactional
    public void delete(OptionProduct product) {
        repository.delete(product);
    }
}
