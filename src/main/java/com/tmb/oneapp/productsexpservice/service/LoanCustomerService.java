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
import org.springframework.stereotype.Service;

import javax.xml.rpc.ServiceException;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private static final BigDecimal PRICING_MONTH_FROM = new BigDecimal(1);
    private static final BigDecimal PRICING_MONTH_TO = new BigDecimal(3);
    private static final BigDecimal RATE_VARAINCE = new BigDecimal(1.73);
    private static final BigDecimal PRICING_MONTH_FROM_4 = new BigDecimal(4);
    private static final BigDecimal PRICING_MONTH_TO_5 = new BigDecimal(5);
    private static final BigDecimal RATE_VARAINCE_1 = new BigDecimal(12.00);
    private static final BigDecimal PRICING_MONTH_FROM_6 = new BigDecimal(6);
    private static final BigDecimal PRICING_MONTH_TO_12 = new BigDecimal(12);
    private static final BigDecimal RATE_VARAINCE_2 = new BigDecimal(23.00);
    private static final BigDecimal LIMIT_AMOUNT = new BigDecimal(500000);
    private static final BigDecimal AMOUNT_MIN = new BigDecimal(20000);


    public LoanCustomerResponse getCustomerProfile(LoanCustomerRequest request) throws ServiceException, RemoteException {
        Facility facility = getFacility(request.getCaID());
        LoanCustomerResponse response = parseLoanCustomerResponse(facility, request.getCaID());

        return response;
    }

    public LoanCustomerSubmissionResponse saveCustomerSubmission(LoanCustomerSubmissionRequest request) {
        Facility facility = getFacility(request.getCaID());
        saveFacility(request, facility);
        LoanCustomerSubmissionResponse response = parseSaveFacilityResponse(request, facility);
        return response;
    }

    private void saveFacility(LoanCustomerSubmissionRequest request, Facility facility) {
        facility.setFeatureType(request.getFeatureType());
        if (request.getFeatureType().equals(FEATURE_TYPE_C)) {
            facility.getFeature().setRequestAmount(request.getRequestAmount());
        }

        facility.setDisburstAccountName(request.getDisburstAccountName());
        facility.setDisburstAccountNo(request.getDisburstAccountNo());
        facility.setDisburstBankName(request.getBankName());
    }

    private LoanCustomerSubmissionResponse parseSaveFacilityResponse(LoanCustomerSubmissionRequest request, Facility facility) {
        LoanCustomerSubmissionResponse response = new LoanCustomerSubmissionResponse();
        response.setInstallment(request.getInstallment());
        response.setRequestAmount(request.getRequestAmount());
        LoanCustomerDisburstAccount disburstAccount = new LoanCustomerDisburstAccount();
        disburstAccount.setAccountNo(request.getDisburstAccountNo());
        disburstAccount.setAccountName(request.getDisburstAccountName());
        disburstAccount.setBankName(request.getBankName());
        response.setDisburstAccount(disburstAccount);
        response.setLimitAmount(facility.getLimitApplied());

        return response;

    }

    private LoanCustomerSubmissionResponse mockCustomerResponse(LoanCustomerSubmissionRequest loanCustomerSubmissionRequest) {
        LoanCustomerSubmissionResponse response = new LoanCustomerSubmissionResponse();
        response.setLimitAmount(new BigDecimal(300000));
        response.setRequestAmount(loanCustomerSubmissionRequest.getRequestAmount());
        LoanCustomerDisburstAccount disburstAccount2 = new LoanCustomerDisburstAccount();
        disburstAccount2.setAccountNo("123-4-56789-0");
        disburstAccount2.setAccountName("บัญชี โนฟิกช์");
        disburstAccount2.setBankName("ทีทีบี");
        response.setDisburstAccount(disburstAccount2);
        response.setInstallment("60");
        return response;
    }

    private Facility getFacility(Long caID) {
        Facility facility = null;
        try {
            ResponseFacility getFacilityResp = getFacilityInfoClient.searchFacilityInfoByCaID(caID);
            facility = getFacilityResp.getBody().getFacilities()[0];
        } catch (Exception e) {
            logger.error("searchFacilityInfoByCaID got exception:{}", e);
        }
        return facility;

    }

    private void updateFacility(Facility facility) {
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
            pricing.setRateVaraince(RATE_VARAINCE);

            LoanCustomerPricing pricing1 = new LoanCustomerPricing();
            pricing1.setMonthFrom(PRICING_MONTH_FROM_4);
            pricing1.setMonthTo(PRICING_MONTH_TO_5);
            pricing1.setRateVaraince(RATE_VARAINCE_1);

            LoanCustomerPricing pricing2 = new LoanCustomerPricing();
            pricing2.setMonthFrom(PRICING_MONTH_FROM_6);
            pricing2.setMonthTo(PRICING_MONTH_TO_12);
            pricing2.setRateVaraince(RATE_VARAINCE_2);
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

        return response;
    }

    private Facility getFacilityFeature(Facility facility, Long caID, String featureType) {
        facility.setFeatureType(featureType);
        updateFacility(facility);
        if (!featureType.equals(facility.getFeatureType())) {
            logger.error("invalid feature type");
        }
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
