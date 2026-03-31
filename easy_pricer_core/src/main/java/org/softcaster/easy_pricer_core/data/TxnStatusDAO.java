package org.softcaster.easy_pricer_core.data;

import java.util.List;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("txnStatusDAO")
public class TxnStatusDAO {

    @Resource
    private TxnStatusRepository repository;

    private final Sort sortByCode = Sort.by(Sort.Direction.ASC, "code");

    @Transactional(readOnly = true)
    public TxnStatus findByIdTxnStatus(Integer idTxnStatus) {
        return repository.findByIdTxnStatus(idTxnStatus);
    }

    @Transactional(readOnly = true)
    public List<TxnStatus> findAll() {
        return repository.findAll(sortByCode);
    }

    @Transactional
    public TxnStatus saveOrUpdate(TxnStatus txnStatus) {
        return repository.save(txnStatus);
    }

    @Transactional
    public void delete(TxnStatus txnStatus) {
        repository.delete(txnStatus);
    }

}
