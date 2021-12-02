package com.tmb.oneapp.productsexpservice.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.CommonData;
import com.tmb.common.model.CustGeneralProfileResponse;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.*;
import com.tmb.oneapp.productsexpservice.model.ProductHoldingsResp;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.request.UnitHolder;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundSummaryBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.request.FundAccountRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fundrule.FundRuleRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.stmtrequest.OrderStmtByPortRequest;
import com.tmb.oneapp.productsexpservice.model.response.fundfavorite.CustomerFavoriteFundData;
import com.tmb.oneapp.productsexpservice.model.response.fundholiday.FundHolidayBody;
import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundClassListInfo;
import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundListBody;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleResponse;
import com.tmb.oneapp.productsexpservice.model.response.investment.AccountDetailResponse;
import com.tmb.oneapp.productsexpservice.model.response.stmtresponse.StatementResponse;
import com.tmb.oneapp.productsexpservice.model.response.suitability.SuitabilityInfo;
import com.tmb.oneapp.productsexpservice.service.productexperience.async.AbstactAsyncHandleBadRequest;
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
 * ProductExpAsyncService class this class use for async process
 */
@Service
public class ProductExpAsyncService extends AbstactAsyncHandleBadRequest {

    private static final TMBLogger<ProductExpAsyncService> logger = new TMBLogger<>(ProductExpAsyncService.class);

    private final InvestmentRequestClient investmentRequestClient;

    private final AccountRequestClient accountRequestClient;

    private final CustomerServiceClient customerServiceClient;

    private final CommonServiceClient commonServiceClient;

    @Autowired
    public ProductExpAsyncService(InvestmentRequestClient investmentRequestClient,
                                  AccountRequestClient accountRequestClient,
                                  CustomerServiceClient customerServiceClient,
                                  CommonServiceClient commonServiceClient) {

        this.investmentRequestClient = investmentRequestClient;
        this.customerServiceClient = customerServiceClient;
        this.accountRequestClient = accountRequestClient;
        this.commonServiceClient = commonServiceClient;
    }

    /**
     * Method fetchFundAccDetail to get Fund account detail
     *
     * @param header
     * @param fundAccountRequestBody
     * @return CompletableFuture<AccDetailBody>
     */
    @LogAround
    @Async
    public CompletableFuture<AccountDetailResponse> fetchFundAccountDetail(Map<String, String> header, FundAccountRequestBody fundAccountRequestBody) throws TMBCommonException {
        try {
            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT,"accountDetail", ProductsExpServiceConstant.LOGGING_REQUEST), UtilMap.convertObjectToStringJson(fundAccountRequestBody));
            ResponseEntity<TmbOneServiceResponse<AccountDetailResponse>> response = investmentRequestClient
                    .callInvestmentFundAccountDetailService(header, fundAccountRequestBody);
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
     * Method fetchFundRule to get Fund rule
     *
     * @param header
     * @param fundRuleRequestBody
     * @return CompletableFuture<FundRuleBody>
     */
    @LogAround
    @Async
    public CompletableFuture<FundRuleResponse> fetchFundRule(Map<String, String> header, FundRuleRequestBody fundRuleRequestBody) throws TMBCommonException {
        try {
            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT,"fetchFundRule", ProductsExpServiceConstant.LOGGING_REQUEST), UtilMap.convertObjectToStringJson(fundRuleRequestBody));
            ResponseEntity<TmbOneServiceResponse<FundRuleResponse>> responseEntity = investmentRequestClient
                    .callInvestmentFundRuleService(header, fundRuleRequestBody);
            return CompletableFuture.completedFuture(responseEntity.getBody().getData());
        } catch (FeignException feignException) {
            handleFeignException(feignException);
            throw getTmbCommonException();
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, e);
            throw getTmbCommonException();
        }
    }

    /**
     * Method fetchStmtByPort to get order statement
     *
     * @param header
     * @param orderStmtByPortRequest
     * @return CompletableFuture<StatementResponse>
     */
    @LogAround
    @Async
    public CompletableFuture<StatementResponse> fetchStatementByPort(Map<String, String> header, OrderStmtByPortRequest orderStmtByPortRequest) throws TMBCommonException {
        try {
            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT,"statement", ProductsExpServiceConstant.LOGGING_REQUEST), UtilMap.convertObjectToStringJson(orderStmtByPortRequest));
            ResponseEntity<TmbOneServiceResponse<StatementResponse>> responseStmt = investmentRequestClient
                    .callInvestmentStatementByPortService(header, orderStmtByPortRequest);
            return CompletableFuture.completedFuture(responseStmt.getBody().getData());
        } catch (FeignException feignException) {
            handleFeignException(feignException);
            throw getTmbCommonException();
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, e);
            throw getTmbCommonException();
        }
    }

    /**
     * Method fetchFundHoliday to get fund holiday
     *
     * @param invHeaderReqParameter
     * @param fundCode
     * @return CompletableFuture<FundHolidayBody>
     */
    @LogAround
    @Async
    public CompletableFuture<FundHolidayBody> fetchFundHoliday(Map<String, String> invHeaderReqParameter, String fundCode) throws TMBCommonException {
        try {
            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT,"fundHoliday", ProductsExpServiceConstant.LOGGING_REQUEST), fundCode);
            ResponseEntity<TmbOneServiceResponse<FundHolidayBody>> responseFundHoliday = investmentRequestClient.
                    callInvestmentFundHolidayService(invHeaderReqParameter, fundCode);
            return CompletableFuture.completedFuture(responseFundHoliday.getBody().getData());
        } catch (FeignException feignException) {
            handleFeignException(feignException);
            throw getTmbCommonException();
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, e);
            throw getTmbCommonException();
        }
    }

    /**
     * Method fetchCustomerExp to get customer account
     *
     * @param headerParameter
     * @param crmId
     * @return CompletableFuture<String>
     */
    @LogAround
    @Async
    public CompletableFuture<String> getAccountList(Map<String, String> headerParameter, String crmId) throws TMBCommonException {
        try {
            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_ACCOUNT,"accountList", ProductsExpServiceConstant.LOGGING_REQUEST), crmId);
            String responseFundHoliday = accountRequestClient.getAccountList(headerParameter, crmId);
            return CompletableFuture.completedFuture(responseFundHoliday);
        } catch (FeignException feignException) {
            handleFeignException(feignException);
            throw getTmbCommonException();
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, e);
            throw getTmbCommonException();
        }
    }

    /**
     * Method fetchCommonConfigByModule to get common config
     *
     * @param correlationId
     * @param module
     * @return CompletableFuture<CommonData>
     */
    @LogAround
    @Async
    public CompletableFuture<List<CommonData>> fetchCommonConfigByModule(String correlationId, String module) throws TMBCommonException {
        try {
            ResponseEntity<TmbOneServiceResponse<List<CommonData>>> responseCommon = commonServiceClient.
                    getCommonConfigByModule(correlationId, module);
            return CompletableFuture.completedFuture(responseCommon.getBody().getData());
        } catch (FeignException feignException) {
            handleFeignException(feignException);
            throw getTmbCommonException();
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, e);
            throw getTmbCommonException();
        }
    }

    /**
     * Method fetchCustomerProfile to get customer profile
     *
     * @param crmId
     * @return CompletableFuture<CustomerProfileResponseData>
     */
    @LogAround
    @Async
    public CompletableFuture<CustGeneralProfileResponse> fetchCustomerProfile(String crmId) throws TMBCommonException {
        try {
            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_CUSTOMER,"getCustomerProfile", ProductsExpServiceConstant.LOGGING_REQUEST), crmId);
            ResponseEntity<TmbOneServiceResponse<CustGeneralProfileResponse>> responseResponseEntity = customerServiceClient.
                    getCustomerProfile(crmId);
            return CompletableFuture.completedFuture(responseResponseEntity.getBody().getData());
        } catch (FeignException feignException) {
            handleFeignException(feignException);
            throw getTmbCommonException();
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, e);
            throw getTmbCommonException();
        }
    }

    /**
     * Method fetchFundListInfo to get fund list info
     *
     * @param invHeaderReqParameter
     * @param correlationId
     * @param key
     * @return CompletableFuture<List < FundClassListInfo>>
     */
    @LogAround
    @Async
    public CompletableFuture<List<FundClassListInfo>> fetchFundListInfo(Map<String, String> invHeaderReqParameter, String correlationId, String key) throws TMBCommonException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return getListCompletableFuture(invHeaderReqParameter, correlationId, key, mapper);
        } catch (FeignException feignException) {
            handleFeignException(feignException);
            throw getTmbCommonException();
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, e);
            throw getTmbCommonException();
        }
    }

    /**
     * Method fetchFundSummary to get fund summary
     *
     * @param invHeaderReqParameter
     * @param unitHolder
     * @return CompletableFuture<FundSummaryResponse>
     */
    @LogAround
    @Async
    public CompletableFuture<FundSummaryBody> fetchFundSummary(Map<String, String> invHeaderReqParameter, UnitHolder unitHolder) throws TMBCommonException {
        try {
            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT,"fundSummary", ProductsExpServiceConstant.LOGGING_REQUEST), unitHolder);
            ResponseEntity<TmbOneServiceResponse<FundSummaryBody>> responseResponseEntity =
                    investmentRequestClient.callInvestmentFundSummaryService(invHeaderReqParameter, unitHolder);
            return CompletableFuture.completedFuture(responseResponseEntity.getBody().getData());
        } catch (FeignException feignException) {
            handleFeignException(feignException);
            throw getTmbCommonException();
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, e);
            throw getTmbCommonException();
        }
    }

    /**
     * Method fetchFundFavorite to get fund favorite
     *
     * @param headerParameter
     * @param crmId
     * @return CompletableFuture<List < CustomerFavoriteFundData>>
     */
    @LogAround
    @Async
    public CompletableFuture<List<CustomerFavoriteFundData>> fetchFundFavorite(Map<String, String> headerParameter, String crmId) throws TMBCommonException {
        try {
            headerParameter.put(ProductsExpServiceConstant.HEADER_X_CRM_ID, crmId);
            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT,"fundFavorite", ProductsExpServiceConstant.LOGGING_REQUEST), crmId);
            ResponseEntity<TmbOneServiceResponse<List<CustomerFavoriteFundData>>> responseResponseEntity =
                    investmentRequestClient.callInvestmentFundFavoriteService(headerParameter);
            return CompletableFuture.completedFuture(responseResponseEntity.getBody().getData());
        } catch (FeignException feignException) {
            handleFeignException(feignException);
            throw getTmbCommonException();
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, e);
            throw getTmbCommonException();
        }
    }

    /**
     * Method fetchFundFavorite to get fund favorite
     *
     * @param investmentHeaderRequest
     * @param crmId
     * @return CompletableFuture<SuitabilityInfo>
     */
    @LogAround
    @Async
    public CompletableFuture<SuitabilityInfo> fetchSuitabilityInquiry(Map<String, String> investmentHeaderRequest, String crmId) throws TMBCommonException {
        try {
            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT,"fetchSuitabilityInquiry", ProductsExpServiceConstant.LOGGING_REQUEST),  UtilMap.halfCrmIdFormat(crmId));
            ResponseEntity<TmbOneServiceResponse<SuitabilityInfo>> responseResponseEntity =
                    investmentRequestClient.callInvestmentFundSuitabilityService(investmentHeaderRequest, UtilMap.halfCrmIdFormat(crmId));
            return CompletableFuture.completedFuture(responseResponseEntity.getBody().getData());
        } catch (FeignException feignException) {
            handleFeignException(feignException);
            throw getTmbCommonException();
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, e);
            throw getTmbCommonException();
        }
    }

    public CompletableFuture<List<FundClassListInfo>> getListCompletableFuture(Map<String, String> invHeaderReqParameter, String correlationId, String key, ObjectMapper mapper) throws JsonProcessingException {

        logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT,"fundList", ProductsExpServiceConstant.LOGGING_REQUEST), "");
        ResponseEntity<TmbOneServiceResponse<FundListBody>> responseResponseEntity = investmentRequestClient.callInvestmentFundListInfoService(invHeaderReqParameter);
        logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_INVESTMENT,"fundList", ProductsExpServiceConstant.LOGGING_RESPONSE), UtilMap.convertObjectToStringJson(responseResponseEntity.getBody().getData()));
        List<FundClassListInfo>  fundClassLists = responseResponseEntity.getBody().getData().getFundClassList();
        return CompletableFuture.completedFuture(fundClassLists);
    }


    /**
     * Method fetchProductHoldingService to get holding account details.
     *
     * @param headerParameter
     * @param crmId
     * @return CompletableFuture<ProductHoldingsResp>
     */
    @LogAround
    @Async
    public CompletableFuture<ProductHoldingsResp> fetchProductHoldingService(Map<String, String> headerParameter, String crmId) throws TMBCommonException {
        try {
            logger.info(UtilMap.mfLoggingMessage(ProductsExpServiceConstant.SYSTEM_ACCOUNT,"getProductHoldings", ProductsExpServiceConstant.LOGGING_REQUEST), crmId);
            ResponseEntity<TmbOneServiceResponse<ProductHoldingsResp>> result = accountRequestClient.getProductHoldingService(headerParameter, crmId);
            return CompletableFuture.completedFuture(result.getBody().getData());
        } catch (FeignException feignException) {
            handleFeignException(feignException);
            throw getTmbCommonException();
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, e);
            throw getTmbCommonException();
        }
    }


}