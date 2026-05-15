package org.softcaster.core.data;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "txn_status")
@SuppressWarnings("PersistenceUnitPresent")

public class TxnStatus implements Serializable {

    @Id
    @SequenceGenerator(name = "txn_status_seq", sequenceName = "txn_status_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "txn_status_seq")
    @Column(name = "id_txn_status", columnDefinition = "INTEGER")
    private Integer idTxnStatus;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    public Integer getIdTxnStatus() {
        return idTxnStatus;
    }

    public void setIdTxnStatus(Integer idTxnStatus) {
        this.idTxnStatus = idTxnStatus;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getIdTxnStatus() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        TxnStatus that = (TxnStatus) obj;
        return getIdTxnStatus().equals(that.getIdTxnStatus());
    }

    @Override
    public int hashCode() {
        return getIdTxnStatus() == null ? 0 : idTxnStatus.hashCode();
    }
}
