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
        TmbOneServiceResponse<List<LoanOnlineInterestRate>> interestRateAll = getInterestRateAll();
        TmbOneServiceResponse<List<LoanOnlineRangeIncome>> rangeIncomeAll = getRangeIncomeAll();

        return parseResponse(disburstAccounts, interestRateAll.getData(), rangeIncomeAll.getData(), correlationId);

    }

    private LoanSubmissionResponse parseResponse(List<DepositAccount> loanCustomerResponse,
                                                 List<LoanOnlineInterestRate> interestRateAll,
                                                 List<LoanOnlineRangeIncome> rangeIncomeAll, String correlationId) throws TMBCommonException {
        LoanSubmissionResponse response = new LoanSubmissionResponse();
        List<LoanCustomerDisburstAccount> receiveAccountList = new ArrayList<>();
        List<LoanCustomerDisburstAccount> paymentAccountList = new ArrayList<>();
        List<RangeIncome> rangeIncomeList = new ArrayList<>();
        List<InterestRate> interestRateList = new ArrayList<>();

        for (var receiveAccount : loanCustomerResponse) {
            LoanCustomerDisburstAccount account = new LoanCustomerDisburstAccount();
            if (receiveAccount.getAllowReceiveLoanFund().equals("1") && receiveAccount.getAccountStatus().equals("ACTIVE") && receiveAccount.getRelationshipCode().equals("PRIIND")) {
                account.setAccountNo(receiveAccount.getAccountNumber());
                account.setAccountName(receiveAccount.getAccountName());
                account.setProductCode(receiveAccount.getProductCode());
                account.setProductNickName(receiveAccount.getProductNickname());
                account.setProductName(receiveAccount.getProductNameTh());
                receiveAccountList.add(account);
            }
        }

        for (var paymentAccount : loanCustomerResponse) {
            LoanCustomerDisburstAccount account = new LoanCustomerDisburstAccount();
            if (paymentAccount.getAllowPayLoanDirectDebit().equals("1") && paymentAccount.getAccountStatus().equals("ACTIVE") && paymentAccount.getRelationshipCode().equals("PRIIND")) {
                account.setAccountNo(paymentAccount.getAccountNumber());
                account.setAccountName(paymentAccount.getAccountName());
                account.setProductCode(paymentAccount.getProductCode());
                account.setProductNickName(paymentAccount.getProductNickname());
                account.setProductName(paymentAccount.getProductNameTh());
                paymentAccountList.add(account);
            }
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
        response.setReceiveAccounts(receiveAccountList);
        response.setPaymentAccounts(paymentAccountList);
        response.setAllowApplySoSmart(checkIsHasNoFixedAcc(correlationId, receiveAccountList));
        return response;
    }

    private boolean checkIsHasNoFixedAcc(String correlationId, List<LoanCustomerDisburstAccount> accList) throws TMBCommonException {
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


    private List<DepositAccount> getLoanCustomerDisburstAccount(String correlationId, String crmId) throws TMBCommonException {
        TmbOneServiceResponse<AccountSaving> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();

        try {
            ResponseEntity<TmbOneServiceResponse<AccountSaving>> accountSavingResponse = customerExpServiceClient.getCustomerAccountSaving(correlationId, crmId);
            oneTmbOneServiceResponse.setData(accountSavingResponse.getBody().getData());

            if (oneTmbOneServiceResponse.getData().getDepositAccountLists() == null) {
                throw new TMBCommonException(oneTmbOneServiceResponse.getStatus().getCode(),
                        ResponseCode.FAILED.getMessage(), ResponseCode.FAILED.getService(), HttpStatus.INTERNAL_SERVER_ERROR, null);
            }
            return oneTmbOneServiceResponse.getData().getDepositAccountLists();

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

