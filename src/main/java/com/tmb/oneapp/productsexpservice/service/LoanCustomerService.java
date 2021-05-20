package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.legacy.rsl.common.ob.facility.Facility;
import com.tmb.common.model.legacy.rsl.ws.facility.response.ResponseFacility;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanCustomerRequest;
import com.tmb.oneapp.productsexpservice.model.response.loan.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class LoanCustomerService {

    private static final TMBLogger<LoanCustomerService> logger = new TMBLogger<>(LoanCustomerService.class);

    private final LoanSubmissionGetFacilityInfoClient getFacilityInfoClient;
    private final LoanSubmissionUpdateFacilityInfoClient updateFacilityInfoClient;
    private static final String FEATURE_TYPE_SB = "SB";
    private static final String FEATURE_TYPE_CT = "CT";

    public LoanCustomerResponse getCustomerProfile(LoanCustomerRequest request) {
        Facility facility = getFacility(request.getCaID());
        List<LoanCustomerFeature> features = getLoanCustomerFeatures(facility, request.getCaID());
        LoanCustomerResponse response = new LoanCustomerResponse();
        response.setFeatures(features);


        LoanCustomerDisburstAccount disburstAccount = new LoanCustomerDisburstAccount();
        response = mockResponse();
        return response;
    }

    public LoanCustomerSubmissionResponse saveCustomerSubmission(LoanCustomerSubmissionRequest request) {
        LoanCustomerSubmissionResponse response = mockCustomerResponse(request);
        return response;
    }


    private LoanCustomerResponse mockResponse() {
        LoanCustomerResponse response = new LoanCustomerResponse();

        List<LoanCustomerFeature> features = new ArrayList<>();

        LoanCustomerFeature feature = new LoanCustomerFeature();
        feature.setId(new BigDecimal(1));
        feature.setFeatureType("SB");
        feature.setAmountMin(new BigDecimal(200000));
        feature.setAmountMax(new BigDecimal(200000));
        features.add(feature);

        LoanCustomerFeature feature2 = new LoanCustomerFeature();
        feature2.setId(new BigDecimal(2));
        feature2.setFeatureType("CT");
        feature2.setAmountMin(new BigDecimal(300000));
        feature2.setAmountMax(new BigDecimal(500000));
        features.add(feature2);


        LoanCustomerInstallment installment = new LoanCustomerInstallment();
        installment.setId(1L);
        installment.setInstallment("60");

        LoanCustomerPricing pricing = new LoanCustomerPricing();
        pricing.setMonthFrom("1");
        pricing.setMonthTo("3");
        pricing.setRateVaraince(0.25);

        LoanCustomerPricing pricing2 = new LoanCustomerPricing();
        pricing2.setMonthFrom("4");
        pricing2.setMonthTo("5");
        pricing2.setRateVaraince(0.12);


        LoanCustomerPricing pricing3 = new LoanCustomerPricing();
        pricing3.setMonthFrom("4");
        pricing3.setMonthTo("5");
        pricing3.setRateVaraince(0.12);

        List<LoanCustomerPricing> pricings = new ArrayList<>();
        pricings.add(pricing);
        pricings.add(pricing2);
        pricings.add(pricing3);

        LoanCustomerInstallment installment2 = new LoanCustomerInstallment();
        installment2.setId(2L);
        installment2.setInstallment("30");

        List<LoanCustomerInstallment> installments= new ArrayList<>();
        installments.add(installment);
        installments.add(installment2);


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

        response.setInstallments(installments);
        response.setFeatures(features);
        response.setDisburstAccounts(disburstAccounts);
        response.setPricings(pricings);


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
        }catch (Exception e) {
            logger.error("searchFacilityInfoByCaID got exception:{}", e);
        }
        return facility;

    }

    private void updateFacility(Facility facility) {
        try {
            com.tmb.common.model.legacy.rsl.ws.facility.update.response.ResponseFacility updateFacilityResp = updateFacilityInfoClient.updateFacilityInfo(facility);
        }catch (Exception e) {
            logger.error("updateFacilityInfo got exception:{}", e);
        }
    }

    private List<LoanCustomerFeature> getLoanCustomerFeatures(Facility facility, Long caID) {
        List<LoanCustomerFeature> facilityFeatures = new ArrayList<>();

        Facility facilitySB = getFacilityFeature(facility, caID, FEATURE_TYPE_SB);
        LoanCustomerFeature facilityFeatureSB = parseLoanCustomerFeature(facilitySB);

        Facility facilityCT = getFacilityFeature(facility, caID, FEATURE_TYPE_CT);
        LoanCustomerFeature facilityFeatureCT = parseLoanCustomerFeature(facilityCT);

        facilityFeatures.add(facilityFeatureSB);
        facilityFeatures.add(facilityFeatureCT);

        return facilityFeatures;
    }

    private Facility getFacilityFeature(Facility facility, Long caID, String featureType) {
        facility.setFeatureType(featureType);
        updateFacility(facility);
        return getFacility(caID);
    }

    private LoanCustomerFeature parseLoanCustomerFeature(Facility facility) {
        LoanCustomerFeature  facilityFeature = new LoanCustomerFeature();
        facilityFeature.setId(facility.getId());
        facilityFeature.setFeatureType(facility.getFeatureType());
        facilityFeature.setAmountMin(facility.getFeature().getRequestAmount());
        facilityFeature.setAmountMax(facility.getAmountFinance());
        return facilityFeature;
    }




}
