package org.softcaster.core.data;

import java.util.Optional;
import org.softcaster.engine.enums.CurveRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssuerCurveLinkRepository extends JpaRepository<IssuerCurveLink, Integer> {

    Optional<IssuerCurveLink> findByIssuerAndCurrencyAndCurveRole(Integer issuer, Integer currency, CurveRole curveRole);
}
