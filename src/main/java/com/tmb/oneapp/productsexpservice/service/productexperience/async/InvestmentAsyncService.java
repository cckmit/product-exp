package com.tmb.oneapp.productsexpservice.service.productexperience.async;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.productexperience.client.request.RelationshipRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.client.response.RelationshipResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.account.purpose.response.AccountPurposeResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.account.redeem.response.AccountRedeemResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.occupation.request.OccupationRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.occupation.response.OccupationInquiryResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.occupation.response.OccupationResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.dailynav.response.DailyNavBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.firsttrade.request.FirstTradeRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.firsttrade.response.FirstTradeResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.information.request.FundCodeRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.information.response.InformationBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.request.OpenPortfolioRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.response.OpenPortfolioResponseBody;
import com.tmb.oneapp.productsexpservice.model.response.PtesDetail;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * InvestmentAsyncService class will call the apis that related with investment api by asyn process
 */
@Service
public class InvestmentAsyncService extends AbstactAsyncHandleBadRequest {

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
            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT,"getFundInfo", "request"),  UtilMap.convertObjectToStringJson(fundCodeRequestBody));
            ResponseEntity<TmbOneServiceResponse<InformationBody>> response = investmentRequestClient.getFundInformation(investmentRequestHeader, fundCodeRequestBody);
            return CompletableFuture.completedFuture(response.getBody().getData());
        } catch (FeignException feignException) {
            handleFeignException(feignException);
            throw getTmbCommonException();
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, e);
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
            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT,"getFundDailyNav", "request"),  UtilMap.convertObjectToStringJson(fundCodeRequestBody));
            ResponseEntity<TmbOneServiceResponse<DailyNavBody>> response = investmentRequestClient.getFundDailyNav(investmentRequestHeader, fundCodeRequestBody);
            return CompletableFuture.completedFuture(response.getBody().getData());
        } catch (FeignException feignException) {
            handleFeignException(feignException);
            throw getTmbCommonException();
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, e);
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
            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT,"fetchAccountPurpose", "request"),  UtilMap.convertObjectToStringJson(investmentRequestHeader));
            ResponseEntity<TmbOneServiceResponse<AccountPurposeResponseBody>> response = investmentRequestClient.getCustomerAccountPurpose(investmentRequestHeader);
            return CompletableFuture.completedFuture(response.getBody().getData());
        } catch (FeignException feignException) {
            handleFeignException(feignException);
            throw getTmbCommonException();
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, e);
            throw getTmbCommonException();
        }
    }

    /**
     * Method fetchFundDailyNav to get customer account redeem
     *
     * @return CompletableFuture<AccountRedeemBody>
     */
    public CompletableFuture<AccountRedeemResponseBody> fetchAccountRedeem(Map<String, String> investmentRequestHeader, String crmId) throws TMBCommonException {
        try {
            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT,"fetchAccountRedeem", "request"),  UtilMap.halfCrmIdFormat(crmId));
            ResponseEntity<TmbOneServiceResponse<AccountRedeemResponseBody>> response = investmentRequestClient.getCustomerAccountRedeem(investmentRequestHeader, crmId);
            return CompletableFuture.completedFuture(response.getBody().getData());
        } catch (FeignException feignException) {
            handleFeignException(feignException);
            throw getTmbCommonException();
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, e);
            throw getTmbCommonException();
        }
    }

    /**
     * Method fetchCustomerOccupationInquiry to get customer occupation requiry
     *
     * @return CompletableFuture<OccupationInquiryResponseBody>
     */
    public CompletableFuture<OccupationInquiryResponseBody> fetchOccupationInquiry(Map<String, String> investmentRequestHeader, String crmId) throws TMBCommonException {
        try {
            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT,"fetchOccupationInquiry", "request"),  crmId);
            ResponseEntity<TmbOneServiceResponse<OccupationInquiryResponseBody>> response = investmentRequestClient.getCustomerOccupationInquiry(investmentRequestHeader, crmId);
            return CompletableFuture.completedFuture(response.getBody().getData());
        } catch (FeignException feignException) {
            handleFeignException(feignException);
            throw getTmbCommonException();
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, e);
            throw getTmbCommonException();
        }
    }

    /**
     * Method get first trade for buy flow
     *
     * @return CompletableFuture<FirstTradeResponseBody>
     */
    public CompletableFuture<FirstTradeResponseBody> getFirstTrade(Map<String, String> investmentRequestHeader, FirstTradeRequestBody firstTradeRequestBody) throws TMBCommonException {
        try {
            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT,"fisrtTrade", "request"),  UtilMap.convertObjectToStringJson(firstTradeRequestBody));
            ResponseEntity<TmbOneServiceResponse<FirstTradeResponseBody>> response = investmentRequestClient.getFirstTrade(investmentRequestHeader, firstTradeRequestBody);
            return CompletableFuture.completedFuture(response.getBody().getData());
        } catch (FeignException feignException) {
            handleFeignException(feignException);
            throw getTmbCommonException();
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, e);
            throw getTmbCommonException();
        }
    }

    /**
     * Method updateClientRelationship to update client relationship
     *
     * @return CompletableFuture<RelationshipResponseBody>
     */
    public CompletableFuture<RelationshipResponseBody> updateClientRelationship(Map<String, String> investmentRequestHeader, String crmId, RelationshipRequest relationshipRequest) throws TMBCommonException {
        try {
            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT,"clientRelationship", "request"), UtilMap.convertObjectToStringJson(relationshipRequest));
            ResponseEntity<TmbOneServiceResponse<RelationshipResponseBody>> response = investmentRequestClient.updateClientRelationship(investmentRequestHeader, crmId, relationshipRequest);
            return CompletableFuture.completedFuture(response.getBody().getData());
        } catch (FeignException feignException) {
            handleFeignException(feignException);
            throw getTmbCommonException();
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, e);
            throw getTmbCommonException();
        }
    }

    /**
     * Method openPortfolio to open portfolio
     *
     * @return CompletableFuture<OpenPortfolioResponseBody>
     */
    public CompletableFuture<OpenPortfolioResponseBody> openPortfolio(Map<String, String> investmentRequestHeader, String crmId, OpenPortfolioRequest openPortfolioRequest) throws TMBCommonException {
        try {
            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT,"openPortfolio", "request"), UtilMap.convertObjectToStringJson(openPortfolioRequest));
            ResponseEntity<TmbOneServiceResponse<OpenPortfolioResponseBody>> response = investmentRequestClient.openPortfolio(investmentRequestHeader, crmId, openPortfolioRequest);
            return CompletableFuture.completedFuture(response.getBody().getData());
        } catch (FeignException feignException) {
            handleFeignException(feignException);
            throw getTmbCommonException();
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, e);
            throw getTmbCommonException();
        }
    }

    /**
     * Method updateOccupation to update customer occupation
     *
     * @return CompletableFuture<OccupationResponseBody>
     */
    public CompletableFuture<OccupationResponseBody> updateOccupation(Map<String, String> investmentRequestHeader, String crmId, OccupationRequest occupationRequest) throws TMBCommonException {
        try {
            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT,"updateOccupation", "request"), UtilMap.convertObjectToStringJson(occupationRequest));
            ResponseEntity<TmbOneServiceResponse<OccupationResponseBody>> response = investmentRequestClient.updateOccupation(investmentRequestHeader, crmId, occupationRequest);
            return CompletableFuture.completedFuture(response.getBody().getData());
        } catch (FeignException feignException) {
            handleFeignException(feignException);
            throw getTmbCommonException();
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, e);
            throw getTmbCommonException();
        }
    }

}
