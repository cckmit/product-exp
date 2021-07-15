package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.model.LoanOnlineInterestRate;
import com.tmb.common.model.LoanOnlineRangeIncome;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.legacy.rsl.common.ob.facility.Facility;
import com.tmb.common.model.legacy.rsl.ws.facility.response.ResponseFacility;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionGetFacilityInfoClient;
import com.tmb.oneapp.productsexpservice.model.loan.InterestRate;
import com.tmb.oneapp.productsexpservice.model.loan.LoanSubmissionResponse;
import com.tmb.oneapp.productsexpservice.model.loan.RangeIncome;
import com.tmb.oneapp.productsexpservice.model.response.loan.LoanCustomerDisburstAccount;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.xml.rpc.ServiceException;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class LoanSubmissionCustomerService {

    private final LoanSubmissionGetFacilityInfoClient getFacilityInfoClient;
    private final CommonServiceClient commonServiceClient;
    private static final String RC01 = "RC01";

    public LoanSubmissionResponse getCustomerInfo(Long caId) throws ServiceException, RemoteException {
        Facility[] facilityInfo = getFacility(caId);
        TmbOneServiceResponse<List<LoanOnlineInterestRate>> interestRateAll = getInterestRateAll();
        TmbOneServiceResponse<List<LoanOnlineRangeIncome>> rangeIncomeAll = getRangeIncomeAll();

        return parseResponse(facilityInfo, interestRateAll.getData(), rangeIncomeAll.getData());

    }

    private LoanSubmissionResponse parseResponse(Facility[] facilityInfo,
                                                 List<LoanOnlineInterestRate> interestRateAll,
                                                 List<LoanOnlineRangeIncome> rangeIncomeAll) {
        LoanSubmissionResponse response = new LoanSubmissionResponse();
        List<LoanCustomerDisburstAccount> accountList = new ArrayList<>();
        List<RangeIncome> rangeIncomeList = new ArrayList<>();
        List<InterestRate> interestRateList = new ArrayList<>();

        response.setTenure(BigDecimal.valueOf(36));
        response.setPayAmount(BigDecimal.valueOf(25));

        for (var itemFacility : facilityInfo) {
            LoanCustomerDisburstAccount account = new LoanCustomerDisburstAccount();
            account.setAccountNo(itemFacility.getDisburstAccountNo());
            account.setAccountName(itemFacility.getAccountName());
            accountList.add(account);
        }

        for (var itemRangeIncome : rangeIncomeAll) {
            RangeIncome rangeIncome = new RangeIncome();
            rangeIncome.setProductCode(itemRangeIncome.getProductCode());
            rangeIncome.setMaxAmount(itemRangeIncome.getRangeIncomeMaz());
            rangeIncome.setMinAmount(itemRangeIncome.getRangeIncomeMin());
            rangeIncome.setStatusWorking(itemRangeIncome.getEmploymentStatus());
            rangeIncome.setProductNameEng(itemRangeIncome.getProductNameEng());
            rangeIncome.setProductNameTh(itemRangeIncome.getProductNameTh());
            if (!itemRangeIncome.getProductCode().equals(RC01) && itemRangeIncome.getRevenueMultiple() != null) {
                rangeIncome.setMaxLimit(itemRangeIncome.getMaxLimit());
                rangeIncome.setRevenueMultiple(itemRangeIncome.getRevenueMultiple());
            }
            rangeIncomeList.add(rangeIncome);
        }

        for (var itemInterestRate : interestRateAll) {
            InterestRate interestRate = new InterestRate();
            if (itemInterestRate.getInterestRate() != null) {
                interestRate.setInterestRate(itemInterestRate.getInterestRate());
            }

            interestRate.setProductCode(itemInterestRate.getProductCode());
            interestRate.setMaxAmount(itemInterestRate.getRangeIncomeMax());
            interestRate.setMinAmount(itemInterestRate.getRangeIncomeMin());
            interestRate.setStatusWorking(itemInterestRate.getEmploymentStatus());
            interestRateList.add(interestRate);
        }

        response.setRangeIncomeList(rangeIncomeList);
        response.setInterestRateList(interestRateList);
        response.setAccounts(accountList);
        return response;
    }


    private Facility[] getFacility(Long caID) throws ServiceException, RemoteException {
        try {
            ResponseFacility getFacilityResp = getFacilityInfoClient.searchFacilityInfoByCaID(caID);
            return getFacilityResp.getBody().getFacilities();
        } catch (Exception exception) {
            throw exception;
        }
    }

    public TmbOneServiceResponse<List<LoanOnlineInterestRate>> getInterestRateAll() {
        TmbOneServiceResponse<List<LoanOnlineInterestRate>> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();

        try {
            ResponseEntity<TmbOneServiceResponse<List<LoanOnlineInterestRate>>> nodeTextResponse = commonServiceClient.getInterestRateAll();
            oneTmbOneServiceResponse.setData(nodeTextResponse.getBody().getData());
            return oneTmbOneServiceResponse;

        } catch (Exception ex) {
            throw ex;
        }

    }

    public TmbOneServiceResponse<List<LoanOnlineRangeIncome>> getRangeIncomeAll() {
        TmbOneServiceResponse<List<LoanOnlineRangeIncome>> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();

        try {
            ResponseEntity<TmbOneServiceResponse<List<LoanOnlineRangeIncome>>> nodeTextResponse = commonServiceClient.getRangeIncomeAll();
            oneTmbOneServiceResponse.setData(nodeTextResponse.getBody().getData());
            return oneTmbOneServiceResponse;

        } catch (Exception e) {
            throw e;
        }
    }
}

