package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.legacy.rsl.common.ob.dropdown.CommonCodeEntry;
import com.tmb.common.model.legacy.rsl.common.ob.facility.Facility;
import com.tmb.common.model.legacy.rsl.common.ob.pricing.Pricing;
import com.tmb.common.model.legacy.rsl.ws.dropdown.response.ResponseDropdown;
import com.tmb.common.model.legacy.rsl.ws.facility.response.ResponseFacility;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerExpServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionGetDropdownListClient;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionGetFacilityInfoClient;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionUpdateFacilityInfoClient;
import com.tmb.oneapp.productsexpservice.model.loan.AccountSaving;
import com.tmb.oneapp.productsexpservice.model.loan.DepositAccount;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanCustomerRequest;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanCustomerSubmissionRequest;
import com.tmb.oneapp.productsexpservice.model.response.loan.*;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final CustomerExpServiceClient customerExpServiceClient;
    private static final String FEATURE_TYPE_S = "S";
    private static final String FEATURE_TYPE_C = "C";
    private static final String DROPDOWN_TENURE = "TENURE";

    private static final Double VAT = 0.07;
    private static final Double CHARGE = 0.1;
    private static final BigDecimal AMOUNT_MIN = BigDecimal.valueOf(5000);

    public LoanCustomerResponse getCustomerProfile(String correlationId, LoanCustomerRequest request, String crmID) throws ServiceException, TMBCommonException, RemoteException {
        Facility facility = getFacility(request.getCaId());
        return parseLoanCustomerResponse(correlationId, facility, request.getCaId(), crmID);
    }

    public LoanCustomerSubmissionResponse saveCustomerSubmission(LoanCustomerSubmissionRequest request) throws ServiceException, TMBCommonException, RemoteException {
        Facility facility = getFacility(request.getCaID());
        saveFacility(request, facility);
        return parseSaveFacilityResponse(request, facility);
    }

    private void saveFacility(@NonNull LoanCustomerSubmissionRequest request, @NonNull Facility facility) throws ServiceException, TMBCommonException, RemoteException {
        facility.setFeatureType(request.getFeatureType());
        if (request.getFeatureType().equals(FEATURE_TYPE_S)) {
            facility.getFeature().setRequestAmount(request.getRequestAmount());
        }
        facility.getFeature().setTenure(request.getTenure());

        facility.setDisburstAccountName(request.getDisburstAccountName());
        facility.setDisburstAccountNo(request.getDisburstAccountNo());
        updateFacility(facility);
    }

    private LoanCustomerSubmissionResponse parseSaveFacilityResponse(LoanCustomerSubmissionRequest request, Facility facility) {
        LoanCustomerSubmissionResponse response = new LoanCustomerSubmissionResponse();
        response.setTenure(request.getTenure());
        response.setRequestAmount(request.getRequestAmount());
        LoanCustomerDisburstAccount disburstAccount = new LoanCustomerDisburstAccount();
        disburstAccount.setAccountNo(request.getDisburstAccountNo());
        disburstAccount.setAccountName(request.getDisburstAccountName());
        response.setDisburstAccount(disburstAccount);
        response.setLimitAmount(facility.getLimitApplied());

        return response;

    }

    private Facility getFacility(Long caID) throws ServiceException, TMBCommonException, RemoteException {
        try {
            ResponseFacility getFacilityResp = getFacilityInfoClient.searchFacilityInfoByCaID(caID);
            if (getFacilityResp.getHeader().getResponseCode().equals("MSG_000")) {
                return getFacilityResp.getBody().getFacilities()[0];
            } else {
                throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                        ResponseCode.FAILED.getMessage(),
                        ResponseCode.FAILED.getService(), HttpStatus.BAD_REQUEST, null);
            }

        } catch (Exception e) {
            logger.error("searchFacilityInfoByCaID got exception:{}", e);
            throw e;
        }
    }

    private void updateFacility(@NonNull Facility facility) throws ServiceException, TMBCommonException, RemoteException {
        try {
            com.tmb.common.model.legacy.rsl.ws.facility.update.response.ResponseFacility responseFacility = updateFacilityInfoClient.updateFacilityInfo(facility);

            if (!responseFacility.getHeader().getResponseCode().equals("MSG_000")) {
                throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                        ResponseCode.FAILED.getMessage(),
                        ResponseCode.FAILED.getService(), HttpStatus.BAD_REQUEST, null);
            }
        } catch (Exception e) {
            logger.error("updateFacilityInfo got exception:{}", e);
            throw e;
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
        if (facility.getFeatureType().equals(FEATURE_TYPE_S)) {
            for (var item : facility.getPricings()) {
                LoanCustomerPricing pricing = new LoanCustomerPricing();
                pricing.setMonthFrom(item.getMonthFrom());
                pricing.setMonthTo(item.getMonthTo());
                pricing.setYearFrom(item.getYearFrom());
                pricing.setYearTo(item.getYearTo());
                pricing.setRate(String.valueOf(item.getCalculatedRate().floatValue() * 100));
                pricing.setRateVariance(BigDecimal.valueOf(item.getRateVaraince().floatValue() * 100));
                pricings.add(pricing);
            }
        }

        return pricings;
    }

    private List<LoanCustomerTenure> getLoanCustomerTenure(Facility facility) throws ServiceException, RemoteException {
        List<LoanCustomerTenure> installments = new ArrayList<>();
        if (facility.getFeatureType().equals(FEATURE_TYPE_S)) {
            CommonCodeEntry[] entries = getDropdownList(DROPDOWN_TENURE);
            for (CommonCodeEntry e : entries) {
                LoanCustomerTenure installment = new LoanCustomerTenure();
                installment.setInstallment(e.getEntryCode());
                installment.setId(e.getEntryID());
                installments.add(installment);
            }
        }

        return installments;
    }

    private List<DepositAccount> getLoanCustomerDisburstAccount(String correlationId, String crmId) {

        TmbOneServiceResponse<AccountSaving> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();

        try {
            ResponseEntity<TmbOneServiceResponse<AccountSaving>> accountSavingResponse = customerExpServiceClient.getCustomerAccountSaving(correlationId, crmId);
            oneTmbOneServiceResponse.setData(accountSavingResponse.getBody().getData());
            return oneTmbOneServiceResponse.getData().getDepositAccountLists();
        } catch (NullPointerException e) {
            logger.error("get account saving fail: ", e);
            throw e;
        } catch (Exception ex) {
            logger.error("get account saving fail: ", ex);
            throw ex;
        }
    }


    private AnnualInterest getAnnualInterest(Facility facility) {
        AnnualInterest annualInterest = new AnnualInterest();
        if (facility.getPricings() != null) {
            for (Pricing q : facility.getPricings()) {
                if (q.getPricingType().equals("C")) {
                    annualInterest.setInterest(q.getCalculatedRate().doubleValue() * 100);
                    break;
                }
            }
        }
        annualInterest.setVat(VAT);
        annualInterest.setCharge(CHARGE);
        return annualInterest;
    }

    private LoanCustomerResponse parseLoanCustomerResponse(String correlationId, Facility facility, Long caID, String crmId) throws ServiceException, TMBCommonException, RemoteException {
        LoanCustomerResponse response = new LoanCustomerResponse();
        List<LoanCustomerDisburstAccount> receiveAccountList = new ArrayList<>();
        List<LoanCustomerDisburstAccount> paymentAccountList = new ArrayList<>();
        List<DepositAccount> depositAccounts = getLoanCustomerDisburstAccount(correlationId, crmId);
        if (depositAccounts != null) {
            for (var receiveAccount : depositAccounts) {
                LoanCustomerDisburstAccount account = new LoanCustomerDisburstAccount();
                if (receiveAccount.getAllowReceiveLoanFund().equals("1") && receiveAccount.getAccountStatus().equals("ACTIVE") && receiveAccount.getRelationshipCode().equals("PRIIND"))
                    account.setAccountNo(receiveAccount.getAccountNumber());
                account.setAccountName(receiveAccount.getAccountName());
                receiveAccountList.add(account);
            }

            for (var paymentAccount : depositAccounts) {
                LoanCustomerDisburstAccount account = new LoanCustomerDisburstAccount();
                if (paymentAccount.getAllowPayLoanDirectDebit().equals("1") && paymentAccount.getAccountStatus().equals("ACTIVE") && paymentAccount.getRelationshipCode().equals("PRIIND")) {
                    account.setAccountNo(paymentAccount.getAccountNumber());
                    account.setAccountName(paymentAccount.getAccountName());
                    paymentAccountList.add(account);
                }
            }
        }

        response.setReceiveAccounts(receiveAccountList);
        response.setPaymentAccounts(paymentAccountList);

        Facility facilityC = getFacilityFeature(facility, caID, FEATURE_TYPE_C);

        Facility facilityS = getFacilityFeature(facility, caID, FEATURE_TYPE_S);

        List<LoanCustomerPricing> pricings = getLoanCustomerPricings(facilityS);
        response.setPricings(pricings);

        AnnualInterest annualInterest = getAnnualInterest(facility);
        response.setAnnualInterest(annualInterest);

        List<LoanCustomerTenure> installments = getLoanCustomerTenure(facilityS);
        response.setInstallments(installments);

        List<LoanCustomerFeature> features = getLoanCustomerFeatures(facilityS, facilityC);
        response.setFeatures(features);

        updateFacility(facility);

        return response;
    }

    private Facility getFacilityFeature(Facility f, Long caID, String featureType) throws ServiceException, TMBCommonException, RemoteException {
        Facility facility = f;
        facility.getFeature().setDisbAcctName(f.getFeature().getDisbAcctName());
        facility.getFeature().setDisbAcctNo(f.getFeature().getDisbAcctNo());
        facility.getFeature().setDisbBankCode(f.getFeature().getDisbBankCode());
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
        facilityFeature.setLimitAmount(facility.getLimitApplied());
        return facilityFeature;
    }

    private CommonCodeEntry[] getDropdownList(String categoryCode) throws ServiceException, RemoteException {
        ResponseDropdown getDropdownListResp = getDropdownListClient.getDropdownList(categoryCode);
        return getDropdownListResp.getBody().getCommonCodeEntries();
    }


}
