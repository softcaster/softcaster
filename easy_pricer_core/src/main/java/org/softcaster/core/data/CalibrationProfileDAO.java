package org.softcaster.core.data;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("calibrationProfileDAO")
public class CalibrationProfileDAO {

    private CalibrationProfileRepository repository;

    protected CalibrationProfileDAO(CalibrationProfileRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public CalibrationProfile findByCalibrationProfileId(Integer calibrationProfileId) {
        return repository.findByCalibrationProfileId(calibrationProfileId);
    }

    public CalibrationProfile findByCalibrationProfileCode(String code){
        return repository.findByCalibrationProfileCode(code);
    }
    
    @Transactional
    public CalibrationProfile saveOrUpdate(CalibrationProfile calibrationProfile) {
        return repository.save(calibrationProfile);
    }

    @Transactional
    public void delete(CalibrationProfile calibrationProfile) {
        repository.delete(calibrationProfile);
    }

}
