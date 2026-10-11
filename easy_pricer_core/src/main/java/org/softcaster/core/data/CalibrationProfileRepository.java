package org.softcaster.core.data;

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
}
