package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.LoanOnlineInterestRate;
import com.tmb.common.model.LoanOnlineRangeIncome;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerExpServiceClient;
import com.tmb.oneapp.productsexpservice.model.loan.*;
import com.tmb.oneapp.productsexpservice.model.response.loan.LoanCustomerDisburstAccount;
import com.tmb.oneapp.productsexpservice.model.response.loan.LoanCustomerResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.xml.rpc.ServiceException;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
public class LoanSubmissionCustomerService {
    private static final TMBLogger<LoanSubmissionCustomerService> logger = new TMBLogger<>(LoanSubmissionCustomerService.class);
    private final CommonServiceClient commonServiceClient;
    private final CustomerExpServiceClient customerExpServiceClient;
    private static final String RC01 = "RC01";

    public LoanSubmissionResponse getCustomerInfo(String correlationId, String crmId) throws ServiceException, RemoteException {
        LoanCustomerResponse response = new LoanCustomerResponse();
        List<LoanCustomerDisburstAccount> disburstAccounts = getLoanCustomerDisburstAccount(correlationId, crmId);
        response.setDisburstAccounts(disburstAccounts);
        TmbOneServiceResponse<List<LoanOnlineInterestRate>> interestRateAll = getInterestRateAll();
        TmbOneServiceResponse<List<LoanOnlineRangeIncome>> rangeIncomeAll = getRangeIncomeAll();

        return parseResponse(response, interestRateAll.getData(), rangeIncomeAll.getData());

    }

    private LoanSubmissionResponse parseResponse(LoanCustomerResponse loanCustomerResponse,
                                                 List<LoanOnlineInterestRate> interestRateAll,
                                                 List<LoanOnlineRangeIncome> rangeIncomeAll) {
        LoanSubmissionResponse response = new LoanSubmissionResponse();
        List<LoanCustomerDisburstAccount> accountList = new ArrayList<>();
        List<RangeIncome> rangeIncomeList = new ArrayList<>();
        List<InterestRate> interestRateList = new ArrayList<>();

        response.setTenure(BigDecimal.valueOf(36));
        response.setPayAmount(BigDecimal.valueOf(25));

        for (var itemFacility : loanCustomerResponse.getDisburstAccounts()) {
            LoanCustomerDisburstAccount account = new LoanCustomerDisburstAccount();
            account.setAccountNo(itemFacility.getAccountNo());
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


    private List<LoanCustomerDisburstAccount> getLoanCustomerDisburstAccount(String correlationId, String crmId) {

        List<LoanCustomerDisburstAccount> disburstAccounts = new ArrayList<>();

        TmbOneServiceResponse<AccountSaving> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();

        try {
            ResponseEntity<TmbOneServiceResponse<AccountSaving>> accountSavingResponse = customerExpServiceClient.getCustomerAccountSaving(correlationId, crmId);
            oneTmbOneServiceResponse.setData(accountSavingResponse.getBody().getData());

        } catch (NullPointerException e) {
            logger.error("get account saving fail: ", e);
            throw e;
        } catch (Exception ex) {
            logger.error("get account saving fail: ", ex);
            throw ex;
        }

        var accList = oneTmbOneServiceResponse.getData().getDepositAccountLists();
        accList.sort(Comparator.comparing(DepositAccount::getProductConfigSortOrder));

        for (DepositAccount acc : accList) {
            LoanCustomerDisburstAccount disburstAccount = new LoanCustomerDisburstAccount();
            disburstAccount.setAccountNo(acc.getAccountNumber());
            disburstAccount.setAccountName(acc.getProductNameTh());
            disburstAccounts.add(disburstAccount);
        }


        return disburstAccounts;
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

