package org.softcaster.core.data;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaQuery;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class MasterDataDAO extends AbstractMasterDataDAO<MasterData, MasterDataRepository> {

    @PersistenceContext
    private EntityManager entityManager;

    public MasterDataDAO(MasterDataRepository repository) {
        super(repository);
    }

    /**
     * Anagrafiche di un tipo (sottoclassi comprese), gia' del tipo concreto.
     * Da usare al posto di findAll() + instanceof: in findAll() un'anagrafica
     * referenziata da un'altra puo' arrivare come proxy di MasterData, che non
     * supera instanceof e fuori sessione non si puo' sempre "sproxare".
     */
    @Transactional(readOnly = true)
    public <T extends MasterData> List<T> findAllOfType(Class<T> type) {
        CriteriaQuery<T> query = entityManager.getCriteriaBuilder().createQuery(type);
        query.select(query.from(type));
        return entityManager.createQuery(query).getResultList();
    }
}
