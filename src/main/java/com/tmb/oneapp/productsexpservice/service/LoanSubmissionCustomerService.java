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

        RangeIncome rangeIncome = new RangeIncome();
        InterestRate interestRate = new InterestRate();
        LoanCustomerDisburstAccount account = new LoanCustomerDisburstAccount();

        response.setTenure(facilityInfo[0].getTenure());
        response.setPayAmount(BigDecimal.valueOf(25));

        for (var itemFacility : facilityInfo) {
            account.setAccountNo(itemFacility.getDisburstAccountNo());
            account.setAccountName(itemFacility.getAccountName());
            accountList.add(account);
        }

        for (var itemRangeIncome : rangeIncomeAll) {
            rangeIncome.setProductCode(itemRangeIncome.getProductCode());
            rangeIncome.setMaxAmount(itemRangeIncome.getRangeIncomeMaz());
            rangeIncome.setMinAmount(itemRangeIncome.getRangeIncomeMin());
            rangeIncome.setStatusWorking(itemRangeIncome.getEmploymentStatus());
            rangeIncome.setProductNameEng(itemRangeIncome.getProductNameEng());
            rangeIncome.setProductNameTh(itemRangeIncome.getProductNameTh());
            if (!itemRangeIncome.getProductCode().equals(RC01)) {
                rangeIncome.setMaxLimit(itemRangeIncome.getMaxLimit());
                rangeIncome.setRevenueMultiple(itemRangeIncome.getRevenueMultiple());
            }
            rangeIncomeList.add(rangeIncome);
        }

        for (var itemInterestRate : interestRateAll) {
            interestRate.setInterestRate(itemInterestRate.getInterestRate());
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
        } catch (Exception e) {
            throw e;
        }
    }

    public TmbOneServiceResponse<List<LoanOnlineInterestRate>> getInterestRateAll() {
        TmbOneServiceResponse<List<LoanOnlineInterestRate>> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();

        try {
            ResponseEntity<TmbOneServiceResponse<List<LoanOnlineInterestRate>>> nodeTextResponse = commonServiceClient.getInterestRateAll();
            oneTmbOneServiceResponse.setData(nodeTextResponse.getBody().getData());
            return oneTmbOneServiceResponse;

        } catch (Exception e) {
            throw e;
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

