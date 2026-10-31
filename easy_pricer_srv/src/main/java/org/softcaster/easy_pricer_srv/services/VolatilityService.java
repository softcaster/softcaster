package org.softcaster.easy_pricer_srv.services;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.MasterDataDAO;
import org.softcaster.core.data.VolatilityPoint;
import org.softcaster.core.data.VolatilitySurfaceDAO;
import org.softcaster.core.data.VolatilitySurfaceEntity;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.easy_pricer_mds_core.calc.VolatilityProvider;
import org.softcaster.easy_pricer_srv.dto.VolatilitySurfaceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Superfici di volatilita': elenco, punti a una data, caricamento dei punti
 * di una data di rilevazione, volatilita' interpolata.
 */
@Service
public class VolatilityService {

    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private VolatilitySurfaceDAO volatilitySurfaceDAO;
    @Autowired
    private MasterDataDAO masterDataDAO;
    @Autowired
    private VolatilityProvider volatilityProvider;
    @Autowired
    @Qualifier("marketDataService")
    private MarketDataService marketDataService;

    /**
     * Superfici con la data dell'ultima rilevazione (senza punti).
     */
    public List<VolatilitySurfaceDto> findAll() {
        LocalDate any = LocalDate.of(9999, 12, 31);
        return volatilitySurfaceDAO.findAll().stream()
                .map(s -> new VolatilitySurfaceDto(s.getCode(), s.getDescription(), s.getUnderlying().getCode(),
                        volatilitySurfaceDAO.findLastReferenceDate(s.getIdVolatilitySurface(), any).orElse(null),
                        List.of()))
                .toList();
    }

    /**
     * Punti dell'ultima rilevazione non successiva a date (default data
     * ufficiale).
     */
    public VolatilitySurfaceDto find(String code, LocalDate date) {
        VolatilitySurfaceEntity s = surface(code);
        LocalDate d = date != null ? date : marketDataService.getOfficialDate();
        List<VolatilityPoint> points = volatilitySurfaceDAO.findLatestPoints(s.getIdVolatilitySurface(), d);
        return new VolatilitySurfaceDto(s.getCode(), s.getDescription(), s.getUnderlying().getCode(),
                points.isEmpty() ? null : points.get(0).getReferenceDate(),
                points.stream().map(p -> new VolatilitySurfaceDto.Point(p.getExpiryDate(), p.getStrike(),
                        p.getVolatility(), p.getSource())).toList());
    }

    /**
     * Crea la superficie se manca e sostituisce i punti della data di
     * rilevazione.
     */
    public VolatilitySurfaceDto upload(String code, VolatilitySurfaceDto dto) {
        if (dto.referenceDate() == null) {
            throw new IllegalArgumentException("Missing referenceDate");
        }
        if (dto.points() == null || dto.points().isEmpty()) {
            throw new IllegalArgumentException("No points to load");
        }
        transactionTemplate.executeWithoutResult(status -> {
            VolatilitySurfaceEntity s = volatilitySurfaceDAO.findByCode(code).orElseGet(() -> create(code, dto));
            List<VolatilityPoint> points = dto.points().stream()
                    .map(p -> point(s.getIdVolatilitySurface(), dto.referenceDate(), p))
                    .toList();
            volatilitySurfaceDAO.replacePoints(s.getIdVolatilitySurface(), dto.referenceDate(), points);
        });
        return find(code, dto.referenceDate());
    }

    /**
     * Volatilita' interpolata dalla superficie alla data (default data
     * ufficiale), come la vedono i calcolatori.
     */
    public double volatility(String code, LocalDate date, LocalDate expiry, double strike) {
        VolatilitySurfaceEntity s = surface(code);
        LocalDate d = date != null ? date : marketDataService.getOfficialDate();
        return volatilityProvider.surface(s.getUnderlying().getIdMasterData(), d)
                .map(data -> data.surface().volatility(expiry, strike))
                .orElseThrow(() -> new NoSuchElementException("No usable points for " + code + " at " + d));
    }

    private VolatilitySurfaceEntity create(String code, VolatilitySurfaceDto dto) {
        if (dto.underlyingCode() == null || dto.underlyingCode().isBlank()) {
            throw new IllegalArgumentException("New surface " + code + ": missing underlyingCode");
        }
        MasterData underlying = masterDataDAO.findByCode(dto.underlyingCode());
        if (underlying == null) {
            throw new NoSuchElementException("Underlying not found: " + dto.underlyingCode());
        }
        VolatilitySurfaceEntity s = new VolatilitySurfaceEntity();
        s.setCode(code);
        s.setDescription(dto.description() != null ? dto.description() : "Volatility " + underlying.getCode());
        s.setUnderlying(underlying);
        return volatilitySurfaceDAO.saveOrUpdate(s);
    }

    private static VolatilityPoint point(Integer surfaceId, LocalDate referenceDate, VolatilitySurfaceDto.Point p) {
        if (p.expiryDate() == null || p.volatility() == null) {
            throw new IllegalArgumentException("Point without expiry or volatility");
        }
        if (!p.expiryDate().isAfter(referenceDate)) {
            throw new IllegalArgumentException("Expiry " + p.expiryDate() + " is not after the reference date");
        }
        if (p.volatility() <= 0 || p.volatility() >= 5) {
            throw new IllegalArgumentException("Volatility " + p.volatility() + " out of range (decimal: 0.08 = 8%)");
        }
        return new VolatilityPoint(surfaceId, referenceDate, p.expiryDate(), p.strike(), p.volatility(), p.source());
    }

    private VolatilitySurfaceEntity surface(String code) {
        return volatilitySurfaceDAO.findByCode(code)
                .orElseThrow(() -> new NoSuchElementException("Volatility surface not found: " + code));
    }
}
