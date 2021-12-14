package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.CommonData;
import com.tmb.common.model.LoanOnlineInterestRate;
import com.tmb.common.model.LoanOnlineRangeIncome;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerExpServiceClient;
import com.tmb.oneapp.productsexpservice.model.loan.*;
import com.tmb.oneapp.productsexpservice.model.response.loan.LoanCustomerDisburstAccount;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class LoanSubmissionCustomerService {
    private static final TMBLogger<LoanSubmissionCustomerService> logger = new TMBLogger<>(LoanSubmissionCustomerService.class);
    private final CommonServiceClient commonServiceClient;
    private final CustomerExpServiceClient customerExpServiceClient;
    private static final String RC01 = "RC01";

    public LoanSubmissionResponse getCustomerInfo(String correlationId, String crmId) throws TMBCommonException {
        List<DepositAccount> disburstAccounts = getLoanCustomerDisburstAccount(correlationId, crmId);
        return parseResponse(disburstAccounts);

    }

    private LoanSubmissionResponse parseResponse(List<DepositAccount> loanCustomerResponse) {
        LoanSubmissionResponse response = new LoanSubmissionResponse();
        response.setRangeIncomeList(prepareRangeIncomes());
        response.setInterestRateList(prepareInterestRates());
        response.setReceiveAccounts(prepareAccounts(loanCustomerResponse, false));
        response.setPaymentAccounts(prepareAccounts(loanCustomerResponse, true));
        response.setAllowApplySoSmart(true);
        return response;
    }

    private List<InterestRate> prepareInterestRates() {
        TmbOneServiceResponse<List<LoanOnlineInterestRate>> interestRateAll = getInterestRateAll();
        List<InterestRate> interestRateList = new ArrayList<>();
        for (var itemInterestRate : interestRateAll.getData()) {
            InterestRate interestRate = new InterestRate();
            if (Objects.nonNull(itemInterestRate.getInterestRate())) {
                interestRate.setInterestRate(itemInterestRate.getInterestRate());
            }
            interestRate.setProductCode(itemInterestRate.getProductCode());
            interestRate.setMaxAmount(itemInterestRate.getRangeIncomeMax());
            interestRate.setMinAmount(itemInterestRate.getRangeIncomeMin());
            interestRate.setStatusWorking(itemInterestRate.getEmploymentStatus());
            interestRateList.add(interestRate);
        }
        return interestRateList;
    }

    private List<RangeIncome> prepareRangeIncomes() {
        TmbOneServiceResponse<List<LoanOnlineRangeIncome>> rangeIncomeAll = getRangeIncomeAll();
        List<RangeIncome> rangeIncomeList = new ArrayList<>();
        for (var itemRangeIncome : rangeIncomeAll.getData()) {
            RangeIncome rangeIncome = new RangeIncome();
            rangeIncome.setProductCode(itemRangeIncome.getProductCode());
            rangeIncome.setMaxAmount(itemRangeIncome.getRangeIncomeMaz());
            rangeIncome.setMinAmount(itemRangeIncome.getRangeIncomeMin());
            rangeIncome.setStatusWorking(itemRangeIncome.getEmploymentStatus());
            rangeIncome.setProductNameEng(itemRangeIncome.getProductNameEng());
            rangeIncome.setProductNameTh(itemRangeIncome.getProductNameTh());
            if (!itemRangeIncome.getProductCode().equals(RC01) && Objects.nonNull(itemRangeIncome.getRevenueMultiple())) {
                rangeIncome.setMaxLimit(itemRangeIncome.getMaxLimit());
                rangeIncome.setRevenueMultiple(itemRangeIncome.getRevenueMultiple());
            }
            rangeIncomeList.add(rangeIncome);
        }
        return rangeIncomeList;
    }

    private List<LoanCustomerDisburstAccount> prepareAccounts(List<DepositAccount> depositAccounts, boolean isPaymentAccount) {
        List<LoanCustomerDisburstAccount> accounts = new ArrayList<>();
        for (var depositAccount : depositAccounts) {
            if (depositAccount.getAccountStatus().equals("ACTIVE") && depositAccount.getRelationshipCode().equals("PRIIND")) {
                if (!isPaymentAccount && depositAccount.getAllowReceiveLoanFund().equals("1")) {
                    accounts.add(mapAccount(depositAccount));
                } else if (isPaymentAccount && depositAccount.getAllowPayLoanDirectDebit().equals("1")) {
                    accounts.add(mapAccount(depositAccount));
                }
            }
        }
        return accounts;
    }

    private LoanCustomerDisburstAccount mapAccount(DepositAccount depositAccount) {
        LoanCustomerDisburstAccount account = new LoanCustomerDisburstAccount();
        account.setAccountNo(depositAccount.getAccountNumber());
        account.setAccountName(depositAccount.getAccountName());
        account.setProductCode(depositAccount.getProductCode());
        account.setProductNickName(depositAccount.getProductNickname());
        account.setProductName(depositAccount.getProductNameTh());
        return account;
    }

    private List<DepositAccount> getLoanCustomerDisburstAccount(String correlationId, String crmId) throws TMBCommonException {
        try {
            ResponseEntity<TmbOneServiceResponse<AccountSaving>> accountSavingResponse = customerExpServiceClient.getCustomerAccountSaving(correlationId, crmId);
            if (accountSavingResponse.getBody().getStatus().getCode().equals("0000")) {
                TmbOneServiceResponse<AccountSaving> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();
                oneTmbOneServiceResponse.setData(accountSavingResponse.getBody().getData());
                return oneTmbOneServiceResponse.getData().getDepositAccountLists();
            }
            throw new TMBCommonException(accountSavingResponse.getBody().getStatus().getCode(),
                    ResponseCode.FAILED.getMessage(), ResponseCode.FAILED.getService(), HttpStatus.INTERNAL_SERVER_ERROR, null);

        } catch (Exception e) {
            logger.error("get account saving fail: ", e);
            throw e;
        }
    }

    public TmbOneServiceResponse<List<LoanOnlineInterestRate>> getInterestRateAll() {
        TmbOneServiceResponse<List<LoanOnlineInterestRate>> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();
        ResponseEntity<TmbOneServiceResponse<List<LoanOnlineInterestRate>>> nodeTextResponse = commonServiceClient.getInterestRateAll();
        oneTmbOneServiceResponse.setData(nodeTextResponse.getBody().getData());
        return oneTmbOneServiceResponse;

    }

    public TmbOneServiceResponse<List<LoanOnlineRangeIncome>> getRangeIncomeAll() {
        TmbOneServiceResponse<List<LoanOnlineRangeIncome>> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();
        ResponseEntity<TmbOneServiceResponse<List<LoanOnlineRangeIncome>>> nodeTextResponse = commonServiceClient.getRangeIncomeAll();
        oneTmbOneServiceResponse.setData(nodeTextResponse.getBody().getData());
        return oneTmbOneServiceResponse;

    }

    //use for check permission to apply so smart , can set in field AllowApplySoSmart
    private boolean checkIsHasNoFixedAcc(String correlationId, List<DepositAccount> accList) throws TMBCommonException {
        List<String> noFixedCodes = getCodeNoFixedAccCodes(correlationId);
        if (Objects.isNull(noFixedCodes)) {
            return false;
        }
        for (var acc : accList) {
            for (var noFixedCode : noFixedCodes) {
                if (acc.getProductCode().contains(noFixedCode)) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<String> getCodeNoFixedAccCodes(String correlationId) throws TMBCommonException {
        ResponseEntity<TmbOneServiceResponse<List<CommonData>>> nodeTextResponse = commonServiceClient.getCommonConfigByModule(correlationId, ProductsExpServiceConstant.LENDING_MODULE);
        if (Objects.isNull(nodeTextResponse.getBody())) {
            throw new TMBCommonException(nodeTextResponse.getBody().getStatus().getCode(),
                    ResponseCode.FAILED.getMessage(), ResponseCode.FAILED.getService(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
        if (Objects.nonNull(nodeTextResponse.getBody().getData())) {
            return nodeTextResponse.getBody().getData().get(0).getNofixedAccount();
        }
        return Collections.emptyList();
    }
}

