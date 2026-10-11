package org.softcaster.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CalibrationProfileItemRepository extends JpaRepository<CalibrationProfileItem, Integer> {

    public CalibrationProfileItem findByCalibrationProfileItemId(Integer calibrationProfileItemId);
}
