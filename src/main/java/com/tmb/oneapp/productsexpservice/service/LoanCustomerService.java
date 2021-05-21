package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.legacy.rsl.common.ob.dropdown.CommonCodeEntry;
import com.tmb.common.model.legacy.rsl.common.ob.facility.Facility;
import com.tmb.common.model.legacy.rsl.ws.dropdown.response.ResponseDropdown;
import com.tmb.common.model.legacy.rsl.ws.facility.response.ResponseFacility;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionGetDropdownListClient;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionGetFacilityInfoClient;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionUpdateFacilityInfoClient;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanCustomerRequest;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanCustomerSubmissionRequest;
import com.tmb.oneapp.productsexpservice.model.response.loan.*;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import javax.xml.rpc.ServiceException;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class LoanCustomerService {

    private static final TMBLogger<LoanCustomerService> logger = new TMBLogger<>(LoanCustomerService.class);

    private final LoanSubmissionGetFacilityInfoClient getFacilityInfoClient;
    private final LoanSubmissionUpdateFacilityInfoClient updateFacilityInfoClient;
    private final LoanSubmissionGetDropdownListClient getDropdownListClient;
    private static final String DROPDOWN_TENURE = "TENURE";
    private static final String FEATURE_TYPE_S = "S";
    private static final String FEATURE_TYPE_C = "C";
    private static final Double VAT = 0.07;
    private static final Double CHARGE = 0.1;
    private static final Double INTEREST = 0.16;
    private static final BigDecimal PRICING_MONTH_FROM = BigDecimal.valueOf(1);
    private static final BigDecimal PRICING_MONTH_TO = BigDecimal.valueOf(3);
    private static final BigDecimal RATE_VARIANCE = BigDecimal.valueOf(1.73);
    private static final BigDecimal PRICING_MONTH_FROM_4 = BigDecimal.valueOf(4);
    private static final BigDecimal PRICING_MONTH_TO_5 = BigDecimal.valueOf(5);
    private static final BigDecimal RATE_VARIANCE_1 = BigDecimal.valueOf(12.00);
    private static final BigDecimal PRICING_MONTH_FROM_6 = BigDecimal.valueOf(6);
    private static final BigDecimal PRICING_MONTH_TO_12 = BigDecimal.valueOf(12);
    private static final BigDecimal RATE_VARIANCE_2 = BigDecimal.valueOf(23.00);
    private static final BigDecimal LIMIT_AMOUNT = BigDecimal.valueOf(500000);
    private static final BigDecimal AMOUNT_MIN = BigDecimal.valueOf(20000);


    public LoanCustomerResponse getCustomerProfile(LoanCustomerRequest request) throws ServiceException, RemoteException {
        Facility facility = getFacility(request.getCaID());
        LoanCustomerResponse response = parseLoanCustomerResponse(facility, request.getCaID());

        return response;
    }

    public LoanCustomerSubmissionResponse saveCustomerSubmission(LoanCustomerSubmissionRequest request) throws ServiceException, RemoteException {
        Facility facility = getFacility(request.getCaID());
        saveFacility(request, facility);
        LoanCustomerSubmissionResponse response = parseSaveFacilityResponse(request, facility);
        return response;
    }

    private void saveFacility(@NonNull LoanCustomerSubmissionRequest request, @NonNull Facility facility) {
        facility.setFeatureType(request.getFeatureType());
        if (request.getFeatureType().equals(FEATURE_TYPE_C)) {
            facility.getFeature().setRequestAmount(request.getRequestAmount());
        }
        facility.getFeature().setTenure(request.getTenure());

        facility.setDisburstAccountName(request.getDisburstAccountName());
        facility.setDisburstAccountNo(request.getDisburstAccountNo());
        facility.setDisburstBankName(request.getBankName());
    }

    private LoanCustomerSubmissionResponse parseSaveFacilityResponse(LoanCustomerSubmissionRequest request, Facility facility) {
        LoanCustomerSubmissionResponse response = new LoanCustomerSubmissionResponse();
        response.setTenure(request.getTenure());
        response.setRequestAmount(request.getRequestAmount());
        LoanCustomerDisburstAccount disburstAccount = new LoanCustomerDisburstAccount();
        disburstAccount.setAccountNo(request.getDisburstAccountNo());
        disburstAccount.setAccountName(request.getDisburstAccountName());
        disburstAccount.setBankName(request.getBankName());
        response.setDisburstAccount(disburstAccount);
        response.setLimitAmount(facility.getLimitApplied());

        return response;

    }

    private Facility getFacility(Long caID) throws ServiceException, RemoteException {
        try {
            ResponseFacility getFacilityResp = getFacilityInfoClient.searchFacilityInfoByCaID(caID);
            Facility facility = getFacilityResp.getBody().getFacilities()[0];
            return facility;
        } catch (Exception e) {
            logger.error("searchFacilityInfoByCaID got exception:{}", e);
            throw e;
        }

    }

    private void updateFacility(@NonNull Facility facility) {
        try {
            com.tmb.common.model.legacy.rsl.ws.facility.update.response.ResponseFacility updateFacilityResp = updateFacilityInfoClient.updateFacilityInfo(facility);
        } catch (Exception e) {
            logger.error("updateFacilityInfo got exception:{}", e);
        }
    }

    private List<LoanCustomerFeature> getLoanCustomerFeatures(Facility facilityS, Facility facilityC) {
        List<LoanCustomerFeature> facilityFeatures = new ArrayList<>();

        facilityFeatures.add(parseLoanCustomerFeature(facilityS));
        facilityFeatures.add(parseLoanCustomerFeature(facilityC));

        return facilityFeatures;
    }

    private List<LoanCustomerPricing> getLoanCustomerPricings(Facility facility) {
        List<LoanCustomerPricing> pricings = new ArrayList<>();
        if (facility.getFeatureType().equals(FEATURE_TYPE_C)) {

            LoanCustomerPricing pricing = new LoanCustomerPricing();

            pricing.setMonthFrom(PRICING_MONTH_FROM);
            pricing.setMonthTo(PRICING_MONTH_TO);
            pricing.setRateVaraince(RATE_VARIANCE);

            LoanCustomerPricing pricing1 = new LoanCustomerPricing();
            pricing1.setMonthFrom(PRICING_MONTH_FROM_4);
            pricing1.setMonthTo(PRICING_MONTH_TO_5);
            pricing1.setRateVaraince(RATE_VARIANCE_1);

            LoanCustomerPricing pricing2 = new LoanCustomerPricing();
            pricing2.setMonthFrom(PRICING_MONTH_FROM_6);
            pricing2.setMonthTo(PRICING_MONTH_TO_12);
            pricing2.setRateVaraince(RATE_VARIANCE_2);
            pricings.add(pricing);
            pricings.add(pricing1);
            pricings.add(pricing2);
        }

        return pricings;
    }

    private List<LoanCustomerInstallment> getLoanCustomerInstallment(Facility facility, String categoryCode) throws ServiceException, RemoteException {
        List<LoanCustomerInstallment> installments = new ArrayList<>();
        if (facility.getFeatureType().equals(FEATURE_TYPE_C)) {
            CommonCodeEntry[] entries = getDropdownList(DROPDOWN_TENURE);
            for (CommonCodeEntry e : entries) {
                LoanCustomerInstallment installment = new LoanCustomerInstallment();
                installment.setInstallment(e.getEntryCode());
                installment.setId(e.getEntryID());
                installments.add(installment);
            }
        }

        return installments;
    }

    private List<LoanCustomerDisburstAccount> getLoanCustomerDisburstAccount(Facility facility) {

        List<LoanCustomerDisburstAccount> disburstAccounts = new ArrayList<>();

        LoanCustomerDisburstAccount disburstAccount = new LoanCustomerDisburstAccount();
        disburstAccount.setAccountNo("123-4-56789-2");
        disburstAccount.setAccountName("บัญชี ออลล์ ฟรี");
        disburstAccount.setBankName("ทีทีบี");
        disburstAccounts.add(disburstAccount);

        LoanCustomerDisburstAccount disburstAccount2 = new LoanCustomerDisburstAccount();
        disburstAccount2.setAccountNo("123-4-56789-0");
        disburstAccount2.setAccountName("บัญชี โนฟิกช์");
        disburstAccount2.setBankName("ทีทีบี");
        disburstAccounts.add(disburstAccount2);

        return disburstAccounts;
    }


    private AnnualInterest getAnnualInterest() {
        AnnualInterest annualInterest = new AnnualInterest();

        annualInterest.setInterest(INTEREST);
        annualInterest.setVat(VAT);
        annualInterest.setCharge(CHARGE);
        return annualInterest;
    }

    private LoanCustomerResponse parseLoanCustomerResponse(Facility facility, Long caID) throws ServiceException, RemoteException {
        LoanCustomerResponse response = new LoanCustomerResponse();

        List<LoanCustomerDisburstAccount> disburstAccounts = getLoanCustomerDisburstAccount(facility);
        response.setDisburstAccounts(disburstAccounts);

        Facility facilityS = getFacilityFeature(facility, caID, FEATURE_TYPE_S);

        AnnualInterest annualInterest = getAnnualInterest();
        response.setAnnualInterest(annualInterest);

        Facility facilityC = getFacilityFeature(facility, caID, FEATURE_TYPE_C);

        List<LoanCustomerPricing> pricings = getLoanCustomerPricings(facilityC);
        response.setPricings(pricings);

        List<LoanCustomerInstallment> installments = getLoanCustomerInstallment(facilityC, DROPDOWN_TENURE);
        response.setInstallments(installments);

        List<LoanCustomerFeature> features = getLoanCustomerFeatures(facilityS, facilityC);
        response.setFeatures(features);

        updateFacility(facility);

        return response;
    }

    private Facility getFacilityFeature(Facility f, Long caID, String featureType) throws ServiceException, RemoteException {
        Facility facility = f;
        facility.getFeature().setDisbAcctName("0");
        facility.getFeature().setDisbAcctNo("0");
        facility.getFeature().setDisbBankCode("0");
        facility.getFeature().setRequestAmount(BigDecimal.ZERO);
        facility.getFeature().setTenure(1L);
        facility.setFeatureType(featureType);
        updateFacility(facility);
        return getFacility(caID);
    }

    private LoanCustomerFeature parseLoanCustomerFeature(Facility facility) {
        LoanCustomerFeature facilityFeature = new LoanCustomerFeature();
        facilityFeature.setId(facility.getId());
        facilityFeature.setFeatureType(facility.getFeatureType());
        facilityFeature.setAmountMin(AMOUNT_MIN);
        facilityFeature.setAmountMax(facility.getLimitApplied());
        facilityFeature.setLimitAmount(LIMIT_AMOUNT);
        return facilityFeature;
    }

    private CommonCodeEntry[] getDropdownList(String categoryCode) throws ServiceException, RemoteException {
        ResponseDropdown getDropdownListResp = getDropdownListClient.getDropdownList(categoryCode);
        return getDropdownListResp.getBody().getCommonCodeEntries();
    }


}
