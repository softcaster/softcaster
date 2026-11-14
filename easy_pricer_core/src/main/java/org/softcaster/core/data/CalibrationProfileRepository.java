package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CalibrationProfileRepository extends JpaRepository<CalibrationProfile, Integer> {

    @Query("""
        SELECT cp
        FROM CalibrationProfile cp
        WHERE cp.calibrationProfileId = :id
        """)
    @EntityGraph("CalibrationProfile.fullGraph")
    public CalibrationProfile findByCalibrationProfileId(@Param("id") Integer calibrationProfileId);

    @Query("""
        SELECT cp
        FROM CalibrationProfile cp
        WHERE cp.code = :code
        """)
    @EntityGraph("CalibrationProfile.fullGraph")
    public CalibrationProfile findByCalibrationProfileCode(@Param("code") String code);

    @Override
    @EntityGraph(attributePaths = "currency")
    List<CalibrationProfile> findAll(Sort sort);

    @Query(value = """
        SELECT code
        FROM spread_curve
        WHERE calibration_profile = :profileCode
        """, nativeQuery = true)
    public List<String> findCurvesFromCalibrationProfileCode(@Param("profileCode") Integer profileCode);
}
