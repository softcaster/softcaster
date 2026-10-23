package org.softcaster.core.data;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * DAO per le testate dei future (MBTP, M6E).
 */
@Component
@Transactional(readOnly = true)
public class FutureProductDAO {

    private final FutureProductRepository repository;

    public FutureProductDAO(FutureProductRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Transactional(readOnly = true)
    public Optional<FutureProduct> findByCode(String code) {
        return repository.findByCode(code);
    }

    /**
     * Testata con il tipo atteso, es. findByCode("MBTP", BondFutureProduct.class).
     *
     * @param <T>
     * @param code
     * @param type
     * @return vuoto se la testata non esiste o e' di un altro tipo
     */
    @Transactional(readOnly = true)
    public <T extends FutureProduct> Optional<T> findByCode(String code, Class<T> type) {
        return repository.findByCode(code)
                .map(Hibernate::unproxy)
                .filter(type::isInstance)
                .map(type::cast);
    }

    @Transactional(readOnly = true)
    public List<FutureProduct> findAll() {
        return repository.findAllOrderByCode();
    }

    /**
     * Testate di un tipo, es. per le liste di scelta delle finestre di
     * anagrafica.
     *
     * @param <T>
     * @param type
     * @return
     */
    @Transactional(readOnly = true)
    public <T extends FutureProduct> List<T> findAll(Class<T> type) {
        return repository.findAllOrderByCode().stream()
                .map(Hibernate::unproxy)
                .filter(type::isInstance)
                .map(type::cast)
                .toList();
    }

    @Transactional
    public <T extends FutureProduct> T saveOrUpdate(T product) {
        return repository.save(product);
    }

    @Transactional
    public void delete(FutureProduct product) {
        repository.delete(product);
    }
}
