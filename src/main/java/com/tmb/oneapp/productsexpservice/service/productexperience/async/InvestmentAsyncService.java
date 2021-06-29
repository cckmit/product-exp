package com.tmb.oneapp.productsexpservice.service.productexperience.async;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.customer.account.purpose.response.AccountPurposeResponseBody;
import com.tmb.oneapp.productsexpservice.model.customer.account.redeem.request.AccountRedeemRequest;
import com.tmb.oneapp.productsexpservice.model.customer.account.redeem.response.AccountRedeemResponseBody;
import com.tmb.oneapp.productsexpservice.model.fund.dailynav.response.DailyNavBody;
import com.tmb.oneapp.productsexpservice.model.fund.information.request.FundCodeRequestBody;
import com.tmb.oneapp.productsexpservice.model.fund.information.response.InformationBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * InvestmentAsyncService class will call the apis that related with investment api by asyn process
 */
@Service
public class InvestmentAsyncService {

    private static final TMBLogger<InvestmentAsyncService> logger = new TMBLogger<>(InvestmentAsyncService.class);

    private InvestmentRequestClient investmentRequestClient;

    @Autowired
    public InvestmentAsyncService(InvestmentRequestClient investmentRequestClient) {
        this.investmentRequestClient = investmentRequestClient;
    }

    /**
     * Method fetchFundInformation to get fund information
     *
     * @param fundCodeRequestBody
     * @return CompletableFuture<Information>
     */
    @LogAround
    @Async
    public CompletableFuture<InformationBody> fetchFundInformation(Map<String, String> investmentRequestHeader, FundCodeRequestBody fundCodeRequestBody) throws TMBCommonException {
        try {
            ResponseEntity<TmbOneServiceResponse<InformationBody>> response = investmentRequestClient.getFundInformation(investmentRequestHeader, fundCodeRequestBody);
            return CompletableFuture.completedFuture(response.getBody().getData());
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            throw getTmbCommonException();
        }
    }

    /**
     * Method fetchFundDailyNav to get fund daily nav
     *
     * @param fundCodeRequestBody
     * @return CompletableFuture<DailyNav>
     */
    @LogAround
    @Async
    public CompletableFuture<DailyNavBody> fetchFundDailyNav(Map<String, String> investmentRequestHeader, FundCodeRequestBody fundCodeRequestBody) throws TMBCommonException {
        try {
            ResponseEntity<TmbOneServiceResponse<DailyNavBody>> response = investmentRequestClient.getFundDailyNav(investmentRequestHeader, fundCodeRequestBody);
            return CompletableFuture.completedFuture(response.getBody().getData());
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            throw getTmbCommonException();
        }
    }

    /**
     * Method fetchAccountPurpose to get customer account purpose
     *
     * @return CompletableFuture<AccountPurposeBody>
     */
    @LogAround
    @Async
    public CompletableFuture<AccountPurposeResponseBody> fetchAccountPurpose(Map<String, String> investmentRequestHeader) throws TMBCommonException {
        try {
            ResponseEntity<TmbOneServiceResponse<AccountPurposeResponseBody>> response = investmentRequestClient.getCustomerAccountPurpose(investmentRequestHeader);
            return CompletableFuture.completedFuture(response.getBody().getData());
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            throw getTmbCommonException();
        }
    }

    /**
     * Method fetchFundDailyNav to get customer account redeem
     *
     * @return CompletableFuture<AccountRedeemBody>
     */
    public CompletableFuture<AccountRedeemResponseBody> fetchAccountRedeem(Map<String, String> investmentRequestHeader, AccountRedeemRequest accountRedeemRequest) throws TMBCommonException {
        try {
            ResponseEntity<TmbOneServiceResponse<AccountRedeemResponseBody>> response = investmentRequestClient.getCustomerAccountRedeem(investmentRequestHeader, accountRedeemRequest);
            return CompletableFuture.completedFuture(response.getBody().getData());
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            throw getTmbCommonException();
        }
    }

    private TMBCommonException getTmbCommonException() {
        return new TMBCommonException(
                ResponseCode.FAILED.getCode(),
                ResponseCode.FAILED.getMessage(),
                ResponseCode.FAILED.getService(),
                HttpStatus.OK,
                null);
    }
}
