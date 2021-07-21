package com.tmb.oneapp.productsexpservice.service.productexperience.async;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.productexperience.aip.request.AipValidationRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.aip.response.AipValidationResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.client.request.RelationshipRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.client.response.RelationshipResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.account.purpose.response.AccountPurposeResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.account.redeem.request.AccountRedeemRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.account.redeem.response.AccountRedeemResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.dailynav.response.DailyNavBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.information.request.FundCodeRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.information.response.InformationBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.nickname.request.PortfolioNicknameRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.nickname.response.PortfolioNicknameResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.request.OpenPortfolioRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.response.OpenPortfolioResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.transaction.request.TransactionValidationRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.transaction.response.TransactionValidationResponseBody;
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

    /**
     * Method updateClientRelationship to update client relationship
     *
     * @return CompletableFuture<RelationshipResponseBody>
     */
    public CompletableFuture<RelationshipResponseBody> updateClientRelationship(Map<String, String> investmentRequestHeader, RelationshipRequest relationshipRequest) throws TMBCommonException {
        try {
            ResponseEntity<TmbOneServiceResponse<RelationshipResponseBody>> response = investmentRequestClient.updateClientRelationship(investmentRequestHeader, relationshipRequest);
            return CompletableFuture.completedFuture(response.getBody().getData());
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            throw getTmbCommonException();
        }
    }

    /**
     * Method openPortfolio to open portfolio
     *
     * @return CompletableFuture<OpenPortfolioResponseBody>
     */
    public CompletableFuture<OpenPortfolioResponseBody> openPortfolio(Map<String, String> investmentRequestHeader, OpenPortfolioRequest openPortfolioRequest) throws TMBCommonException {
        try {
            ResponseEntity<TmbOneServiceResponse<OpenPortfolioResponseBody>> response = investmentRequestClient.openPortfolio(investmentRequestHeader, openPortfolioRequest);
            return CompletableFuture.completedFuture(response.getBody().getData());
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            throw getTmbCommonException();
        }
    }

    /**
     * Method updatePortfolioNickname to create or update portfolio nickname
     *
     * @return CompletableFuture<PortfolioNicknameResponseBody>
     */
    public CompletableFuture<PortfolioNicknameResponseBody> updatePortfolioNickname(Map<String, String> investmentRequestHeader, PortfolioNicknameRequest portfolioNicknameRequest) throws TMBCommonException {
        try {
            ResponseEntity<TmbOneServiceResponse<PortfolioNicknameResponseBody>> response = investmentRequestClient.updatePortfolioNickname(investmentRequestHeader, portfolioNicknameRequest);
            return CompletableFuture.completedFuture(response.getBody().getData());
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            throw getTmbCommonException();
        }
    }

    /**
     * Method fetchTransactionValidation to get transaction validation
     *
     * @return CompletableFuture<TransactionValidationResponseBody>
     */
    public CompletableFuture<TransactionValidationResponseBody> fetchTransactionValidation(Map<String, String> investmentRequestHeader, String crmId, TransactionValidationRequest transactionValidationRequest) throws TMBCommonException {
        try {
            ResponseEntity<TmbOneServiceResponse<TransactionValidationResponseBody>> response = investmentRequestClient.getTransactionValidation(investmentRequestHeader, crmId, transactionValidationRequest);
            return CompletableFuture.completedFuture(response.getBody().getData());
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            throw getTmbCommonException();
        }
    }

    /**
     * Method fetchAipValidation to get aip validation
     *
     * @return CompletableFuture<TransactionValidationResponseBody>
     */
    public CompletableFuture<AipValidationResponseBody> fetchAipValidation(Map<String, String> investmentRequestHeader, AipValidationRequest aipValidationRequest) throws TMBCommonException {
        try {
            ResponseEntity<TmbOneServiceResponse<AipValidationResponseBody>> response = investmentRequestClient.getAipValidation(investmentRequestHeader, aipValidationRequest);
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
