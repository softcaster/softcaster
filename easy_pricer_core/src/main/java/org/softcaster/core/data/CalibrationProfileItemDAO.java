package org.softcaster.core.data;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("calibrationProfileItemDAO")
public class CalibrationProfileItemDAO {

    private CalibrationProfileItemRepository repository;

    protected CalibrationProfileItemDAO(CalibrationProfileItemRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public CalibrationProfileItem findByCalibrationProfileItemId(Integer calibrationProfileItemId) {
        return repository.findByCalibrationProfileItemId(calibrationProfileItemId);
    }
}
