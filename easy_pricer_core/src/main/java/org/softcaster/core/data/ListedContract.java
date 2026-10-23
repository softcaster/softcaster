package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;
import org.softcaster.engine.enums.ContractType;

/**
 * Contratto quotato NEGOZIABILE (6EZ6, call 1,135 dic-2026): dettaglio di una
 * testata in master_data (6E, EUU, MBTP).
 *
 * Strategia SINGLE_TABLE sulla tabella listed_contract, discriminatore
 * intero contract_type (FK verso la lookup contract_type, enum ContractType):
 * una sottoclasse concreta per ogni valore, con
 * {@code @DiscriminatorValue(ContractType.XXX_ID)}.
 *
 * Il collegamento alla testata (colonna master_data) e' mappato nelle
 * sottoclassi, ognuna con il tipo di testata corretto (FutureMasterData,
 * OptionMasterData).
 */
@Entity
@Table(name = "listed_contract")
@SuppressWarnings("PersistenceUnitPresent")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "contract_type", discriminatorType = DiscriminatorType.INTEGER)
public abstract class ListedContract implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "listed_contract_seq", sequenceName = "listed_contract_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "listed_contract_seq")
    @Column(name = "id_listed_contract")
    private Integer idListedContract;

    /**
     * Codice univoco del contratto (es. 6EZ6).
     */
    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "isin")
    private String isin;

    /**
     * Testata del contratto.
     */
    public abstract MasterData getMasterData();

    /**
     * Tipo del contratto, corrispondente al discriminatore della sottoclasse.
     *
     * @return
     */
    public abstract ContractType getContractType();

    public Integer getIdListedContract() {
        return idListedContract;
    }

    public void setIdListedContract(Integer idListedContract) {
        this.idListedContract = idListedContract;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getIdListedContract() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ListedContract that = (ListedContract) obj;
        return getIdListedContract().equals(that.getIdListedContract());
    }

    @Override
    public int hashCode() {
        return getIdListedContract() == null ? 0 : idListedContract.hashCode();
    }

    @Override
    public String toString() {
        return code;
    }
}
