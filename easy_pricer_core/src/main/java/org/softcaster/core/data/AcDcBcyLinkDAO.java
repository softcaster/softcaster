/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.data;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.softcaster.engine.enums.CurveRole;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * DAO per i collegamenti (asset class, valuta, ruolo) -> curva.
 *
 * Letture in transazione read-only; scritture in transazione propria.
 * saveOrUpdate lavora sulla chiave naturale: se il collegamento esiste gia'
 * aggiorna la curva, altrimenti lo crea.
 */
@Component
@Transactional(readOnly = true)
public class AcDcBcyLinkDAO {

    private final AcDcBcyLinkRepository repository;

    public AcDcBcyLinkDAO(AcDcBcyLinkRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    // ------------------------------------------------------------------------
    // -- Letture
    // ------------------------------------------------------------------------
    @Transactional(readOnly = true)
    public Optional<AcDcBcyLink> find(Integer assetClass, Integer currency, CurveRole role) {
        return repository.findByAssetClassAndCurrencyAndCurveRole(assetClass, currency, role);
    }

    /**
     * Come find, ma il collegamento e' obbligatorio: se manca lancia
     * un'eccezione che dice esattamente quale configurazione aggiungere.
     * @param assetClass
     * @param currency
     * @param role
     * @return 
     */
    @Transactional(readOnly = true)
    public AcDcBcyLink get(Integer assetClass, Integer currency, CurveRole role) {
        return find(assetClass, currency, role).orElseThrow(() -> new IllegalStateException(
                "Nessuna curva " + role + " configurata per asset_class=" + assetClass + ", currency=" + currency));
    }

    @Transactional(readOnly = true)
    public List<AcDcBcyLink> findByAssetClassAndCurrency(Integer assetClass, Integer currency) {
        return repository.findByAssetClassAndCurrency(assetClass, currency);
    }

    /**
     * Collegamenti che usano una curva: utile prima di cancellarla.
     * @param curveId
     * @param spreadCurve
     * @return 
     */
    @Transactional(readOnly = true)
    public List<AcDcBcyLink> findByCurve(Integer curveId, boolean spreadCurve) {
        return spreadCurve ? repository.findBySpreadCurve(curveId) : repository.findByYieldCurve(curveId);
    }

    @Transactional(readOnly = true)
    public List<AcDcBcyLink> findAll() {
        return repository.findAll();
    }

    // ------------------------------------------------------------------------
    // -- Scritture
    // ------------------------------------------------------------------------
    /**
     * Crea o aggiorna il collegamento identificato da (asset class, valuta,
     * ruolo). Della entity passata contano la chiave naturale e la curva; l'id
     * viene ignorato se il collegamento esiste gia'.
     * @param link
     * @return 
     */
    @Transactional
    public AcDcBcyLink saveOrUpdate(AcDcBcyLink link) {
        Objects.requireNonNull(link, "link");
        AcDcBcyLink target = find(link.getAssetClass(), link.getCurrency(), link.getCurveRole())
                .orElse(link);
        if (target != link) {
            if (link.isSpreadCurve()) {
                target.linkSpreadCurve(link.getSpreadCurve());
            } else {
                target.linkYieldCurve(link.getYieldCurve());
            }
        }
        return repository.save(target);
    }

    @Transactional
    public void delete(Integer assetClass, Integer currency, CurveRole role) {
        find(assetClass, currency, role).ifPresent(repository::delete);
    }
}
