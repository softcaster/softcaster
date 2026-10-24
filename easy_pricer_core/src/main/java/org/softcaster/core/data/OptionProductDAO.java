package org.softcaster.core.data;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Testate delle opzioni (option_product).
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
    public List<OptionProduct> findAll() {
        return repository.findAllOrderByCode();
    }

    @Transactional
    public OptionProduct saveOrUpdate(OptionProduct product) {
        return repository.save(product);
    }

    @Transactional
    public void delete(OptionProduct product) {
        repository.delete(product);
    }
}
