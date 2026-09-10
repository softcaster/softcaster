package org.softcaster.core.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.softcaster.core.dto.SecurityMasterDataDto;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class SecurityMasterDataDAO extends AbstractMasterDataDAO<SecurityMasterData, SecurityMasterDataRepository> {

    public SecurityMasterDataDAO(SecurityMasterDataRepository repository) {
        super(repository);
    }

    @Transactional(readOnly = true)
    public Optional<SecurityMasterData> findByIsin(String isin) {
        return repository.findByIsin(isin);
    }    

    @Transactional(readOnly = true)
    public List<SecurityMasterDataDto> findAllDto() {
        List<SecurityMasterDataDto> listDto = null;
        List<SecurityMasterData> list = findAllByAssetClass("XRB");
        
        if(list != null && !list.isEmpty()) {
            listDto = new ArrayList<>();
            SecurityMasterDataDto smdDto;
            for(SecurityMasterData smd: list) {
                smdDto = new SecurityMasterDataDto();
                smdDto.setGenericMasterDataId(smd.getIdMasterData());
                smdDto.setCode(smd.getIsin());
                smdDto.setDescription(smd.getDescription());
                smdDto.setAssetClass(smd.getAssetClass().getCode());
                smdDto.setCurrency(smd.getCurrency().getIsoCode());
                smdDto.setIssueDate(smd.getIssueDate().toLocalDate());
                smdDto.setShortIssuerName(smd.getIssuer().getShortIssuerName());
                smdDto.setLongIssuerName(smd.getIssuer().getLongIssuerName());
                smdDto.setFirstCouponPaymentDate(smd.getFirstCouponPaymentDate().toLocalDate());
                smdDto.setFirstCouponRate(smd.getFirstCouponRate());
                smdDto.setInterestRate(smd.getInterestRate());
                smdDto.setMaturityDate(smd.getMaturityDate().toLocalDate());
                smdDto.setIssuePrice(smd.getIssuePrice());
                smdDto.setRedempionPrice(smd.getRedempionPrice());
                smdDto.setCountry(smd.getIssuer().getCountry().getAlfa3Code());
                smdDto.setFrequency(smd.getFrequency().getCode());
                listDto.add(smdDto);
            }
        }
        
        return listDto;
    }

    @Transactional(readOnly = true)
    public Optional<List<SecurityMasterData>> findByCurrency(String currencyCode) {
        return repository.findByCurrency(currencyCode);
    }
}
