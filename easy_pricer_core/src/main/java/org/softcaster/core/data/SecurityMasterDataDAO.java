package org.softcaster.core.data;

import java.util.List;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import org.softcaster.core.dto.SecurityMasterDataDto;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("securityMasterDataDAO")
public class SecurityMasterDataDAO {

    @Resource
    private SecurityMasterDataRepository repository;
    @Resource
    private InstrumentQuoteRepository quoteRepository;

    private final Sort sortByMaturity = Sort.by(Sort.Direction.ASC, "maturityDate");

    @Transactional(readOnly = true)
    public SecurityMasterData findByIdMasterData(Integer idMasterData) {
        return repository.findByIdMasterData(idMasterData);
    }

    @Transactional
    public SecurityMasterData saveOrUpdate(SecurityMasterData securityMasterData) {
        return repository.save(securityMasterData);
    }

    @Transactional
    public void delete(SecurityMasterData securityMasterData) {
        if (securityMasterData != null && securityMasterData.getIdMasterData() != null) {
            quoteRepository.deleteInstrumentQuotes(securityMasterData.getIdMasterData());
            quoteRepository.deleteInstrumentQuoteHist(securityMasterData.getIdMasterData());
            repository.delete(securityMasterData);
        }
    }

    @Transactional(readOnly = true)
    public List<SecurityMasterData> findAll() {
        return repository.findAll(sortByMaturity);
    }

    @Transactional(readOnly = true)
    public List<SecurityMasterData> findAllByAssetClass(String code) {
        return repository.findAllByAssetClass(code);
    }

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
    public SecurityMasterData findByIsin(String isin) {
        return repository.findByIsin(isin);
    }

    @Transactional(readOnly = true)
    public List<SecurityMasterData> findByCurrency(String currencyCode) {
        return repository.findByCurrencyIsoCode(currencyCode);
    }

    @Transactional(readOnly = true)
    public List<SecurityMasterData> findByDescriptionContaining(String issueDescriptionFragment) {
        return repository.findByDescriptionContaining(issueDescriptionFragment);
    }

    @Transactional
    public SecurityMasterData saveAndFlush(SecurityMasterData securityMasterData) {
        return repository.saveAndFlush(securityMasterData);
    }

    @Transactional
    public void deleteCashFlowItems(Integer idMasterData) {
        repository.deleteCashFlowsByMasterDataId(idMasterData);
    }
}
