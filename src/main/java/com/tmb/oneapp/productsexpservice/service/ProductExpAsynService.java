package com.tmb.oneapp.productsexpservice.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.CommonData;
import com.tmb.common.model.CustomerProfileResponseData;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.*;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.request.UnitHolder;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundSummaryResponse;
import com.tmb.oneapp.productsexpservice.model.request.accdetail.FundAccountRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fundrule.FundRuleRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.stmtrequest.OrderStmtByPortRq;
import com.tmb.oneapp.productsexpservice.model.response.fundfavorite.CustFavoriteFundData;
import com.tmb.oneapp.productsexpservice.model.response.fundholiday.FundHolidayBody;
import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundClassListInfo;
import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundListBody;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleBody;
import com.tmb.oneapp.productsexpservice.model.response.investment.AccDetailBody;
import com.tmb.oneapp.productsexpservice.model.response.stmtresponse.StatementResponse;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * ProductExpAsynService class this clasee use for asyn process
 */
@Service
public class ProductExpAsynService {
    private static final TMBLogger<ProductExpAsynService> logger = new TMBLogger<>(ProductExpAsynService.class);
    private final InvestmentRequestClient investmentRequestClient;
    private final AccountRequestClient accountRequestClient;
    private final CustomerServiceClient customerServiceClient;
    private final CommonServiceClient commonServiceClient;
    private final CacheServiceClient cacheServiceClient;


    @Autowired
    public ProductExpAsynService(InvestmentRequestClient investmentRequestClient,
                                 AccountRequestClient accountRequestClient,
                                 CustomerServiceClient customerServiceClient,
                                 CommonServiceClient commonServiceClient,
                                 CacheServiceClient cacheServiceClient) {

        this.investmentRequestClient = investmentRequestClient;
        this.customerServiceClient = customerServiceClient;
        this.accountRequestClient = accountRequestClient;
        this.commonServiceClient = commonServiceClient;
        this.cacheServiceClient = cacheServiceClient;
    }


    /**
     * Method fetchFundAccDetail get Fund account detail
     *
     * @param invHeaderReqParameter
     * @param fundAccountRequestBody
     * @return CompletableFuture<AccDetailBody>
     */
    @LogAround
    @Async
    public CompletableFuture<AccDetailBody> fetchFundAccDetail(Map<String, String> invHeaderReqParameter, FundAccountRequestBody fundAccountRequestBody) throws TMBCommonException {
        try {
            ResponseEntity<TmbOneServiceResponse<AccDetailBody>> response = investmentRequestClient
                    .callInvestmentFundAccDetailService(invHeaderReqParameter, fundAccountRequestBody);

            return CompletableFuture.completedFuture(response.getBody().getData());
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            throw new TMBCommonException(
                    ResponseCode.FAILED.getCode(),
                    ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(),
                    HttpStatus.OK,
                    null);
        }
    }


    /**
     * Method fetchFundRule get Fund rule
     *
     * @param invHeaderReqParameter
     * @param fundRuleRequestBody
     * @return CompletableFuture<FundRuleBody>
     */
    @LogAround
    @Async
    public CompletableFuture<FundRuleBody> fetchFundRule(Map<String, String> invHeaderReqParameter, FundRuleRequestBody fundRuleRequestBody) throws TMBCommonException {
        try {
            ResponseEntity<TmbOneServiceResponse<FundRuleBody>> responseEntity = investmentRequestClient
                    .callInvestmentFundRuleService(invHeaderReqParameter, fundRuleRequestBody);

            return CompletableFuture.completedFuture(responseEntity.getBody().getData());
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            throw new TMBCommonException(
                    ResponseCode.FAILED.getCode(),
                    ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(),
                    HttpStatus.OK,
                    null);
        }
    }


    /**
     * Method fetchStmtByPort get order statement
     *
     * @param invHeaderReqParameter
     * @param orderStmtByPortRq
     * @return CompletableFuture<StatementResponse>
     */
    @LogAround
    @Async
    public CompletableFuture<StatementResponse> fetchStmtByPort(Map<String, String> invHeaderReqParameter, OrderStmtByPortRq orderStmtByPortRq) throws TMBCommonException {
        try {
            ResponseEntity<TmbOneServiceResponse<StatementResponse>> responseStmt = investmentRequestClient
                    .callInvestmentStmtByPortService(invHeaderReqParameter, orderStmtByPortRq);

            return CompletableFuture.completedFuture(responseStmt.getBody().getData());
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            throw new TMBCommonException(
                    ResponseCode.FAILED.getCode(),
                    ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(),
                    HttpStatus.OK,
                    null);
        }
    }


    /**
     * Method fetchFundHoliday get fund holiday
     *
     * @param invHeaderReqParameter
     * @param fundCode
     * @return CompletableFuture<FundHolidayBody>
     */
    @LogAround
    @Async
    public CompletableFuture<FundHolidayBody> fetchFundHoliday(Map<String, String> invHeaderReqParameter, String fundCode) throws TMBCommonException {
        try {
            ResponseEntity<TmbOneServiceResponse<FundHolidayBody>> responseFundHoliday = investmentRequestClient.
                    callInvestmentFundHolidayService(invHeaderReqParameter, fundCode);

            return CompletableFuture.completedFuture(responseFundHoliday.getBody().getData());
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            throw new TMBCommonException(
                    ResponseCode.FAILED.getCode(),
                    ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(),
                    HttpStatus.OK,
                    null);
        }
    }

    /**
     * Method fetchCustomerExp get customer account
     *
     * @param invHeaderReqParameter
     * @param crmID
     * @return CompletableFuture<String>
     */
    @LogAround
    @Async
    public CompletableFuture<String> fetchCustomerExp(Map<String, String> invHeaderReqParameter, String crmID) throws TMBCommonException {
        try {
            String responseFundHoliday = accountRequestClient.callCustomerExpService(invHeaderReqParameter, crmID);
            return CompletableFuture.completedFuture(responseFundHoliday);
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            throw new TMBCommonException(
                    ResponseCode.FAILED.getCode(),
                    ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(),
                    HttpStatus.OK,
                    null);
        }
    }

    /**
     * Method fetchCommonConfigByModule get common config
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
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            throw new TMBCommonException(
                    ResponseCode.FAILED.getCode(),
                    ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(),
                    HttpStatus.OK,
                    null);
        }
    }

    /**
     * Method fetchCustomerProfile get customer profile
     *
     * @param invHeaderReqParameter
     * @param crmID
     * @return CompletableFuture<CustomerProfileResponseData>
     */
    @LogAround
    @Async
    public CompletableFuture<CustomerProfileResponseData> fetchCustomerProfile(Map<String, String> invHeaderReqParameter, String crmID) throws TMBCommonException {
        try {
            ResponseEntity<TmbOneServiceResponse<CustomerProfileResponseData>> responseResponseEntity = customerServiceClient.
                    getCustomerProfile(invHeaderReqParameter, crmID);
            return CompletableFuture.completedFuture(responseResponseEntity.getBody().getData());
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            throw new TMBCommonException(
                    ResponseCode.FAILED.getCode(),
                    ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(),
                    HttpStatus.OK,
                    null);
        }
    }


    /**
     * Method fetchFundListInfo get fundlist info
     *
     * @param invHeaderReqParameter
     * @param correlationId
     * @param key
     * @return CompletableFuture<List < FundClassList>>
     */
    @LogAround
    @Async
    public CompletableFuture<List<FundClassListInfo>> fetchFundListInfo(Map<String, String> invHeaderReqParameter, String correlationId, String key) throws TMBCommonException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return getListCompletableFuture(invHeaderReqParameter, correlationId, key, mapper);
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            throw new TMBCommonException(
                    ResponseCode.FAILED.getCode(),
                    ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(),
                    HttpStatus.OK,
                    null);
        }
    }

    /**
     * @param invHeaderReqParameter
     * @param correlationId
     * @param key
     * @param mapper
     * @return
     * @throws JsonProcessingException
     */
    CompletableFuture<List<FundClassListInfo>> getListCompletableFuture(Map<String, String> invHeaderReqParameter, String correlationId, String key, ObjectMapper mapper) throws JsonProcessingException {
        List<FundClassListInfo> fundClassLists;
        ResponseEntity<TmbOneServiceResponse<String>> responseCache = cacheServiceClient.getCacheByKey(correlationId, key);
        if (!ProductsExpServiceConstant.SUCCESS_CODE.equals(responseCache.getBody().getStatus().getCode())) {
            ResponseEntity<TmbOneServiceResponse<FundListBody>> responseResponseEntity =
                    investmentRequestClient.callInvestmentFundListInfoService(invHeaderReqParameter);
            fundClassLists = responseResponseEntity.getBody().getData().getFundClassList();
            String fundClassStr = mapper.writeValueAsString(fundClassLists);
            cacheServiceClient.putCacheByKey(invHeaderReqParameter, UtilMap.mappingCache(fundClassStr, key));
        } else {
            fundClassLists = getFundClassListInfos(mapper, responseCache);
        }
        return CompletableFuture.completedFuture(fundClassLists);
    }

    List<FundClassListInfo> getFundClassListInfos(ObjectMapper mapper, ResponseEntity<TmbOneServiceResponse<String>> responseCache) throws JsonProcessingException {
        List<FundClassListInfo> fundClassLists;
        String fundStr = responseCache.getBody().getData();
        TypeFactory typeFactory = mapper.getTypeFactory();
        fundClassLists = mapper.readValue(fundStr, typeFactory.constructCollectionType(List.class, FundClassListInfo.class));
        return fundClassLists;
    }


    /**
     * Method fetchFundSummary get fund summary
     *
     * @param invHeaderReqParameter
     * @param unitHolder
     * @return CompletableFuture<FundSummaryResponse>
     */
    @LogAround
    @Async
    public CompletableFuture<FundSummaryResponse> fetchFundSummary(Map<String, String> invHeaderReqParameter, UnitHolder unitHolder) throws TMBCommonException {
        try {
            ResponseEntity<TmbOneServiceResponse<FundSummaryResponse>> responseResponseEntity =
                    investmentRequestClient.callInvestmentFundSummaryService(invHeaderReqParameter, unitHolder);
            return CompletableFuture.completedFuture(responseResponseEntity.getBody().getData());
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            throw new TMBCommonException(
                    ResponseCode.FAILED.getCode(),
                    ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(),
                    HttpStatus.OK,
                    null);
        }
    }


    /**
     * Method fetchFundFavorite get fund favorite
     *
     * @param invHeaderReqParameter
     * @param crmId
     * @return CompletableFuture<List < CustFavoriteFundData>>
     */
    @LogAround
    @Async
    public CompletableFuture<List<CustFavoriteFundData>> fetchFundFavorite(Map<String, String> invHeaderReqParameter, String crmId) throws TMBCommonException {
        try {
            ResponseEntity<TmbOneServiceResponse<List<CustFavoriteFundData>>> responseResponseEntity =
                    investmentRequestClient.callInvestmentFundFavoriteService(invHeaderReqParameter, crmId);
            return CompletableFuture.completedFuture(responseResponseEntity.getBody().getData());
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            throw new TMBCommonException(
                    ResponseCode.FAILED.getCode(),
                    ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(),
                    HttpStatus.OK,
                    null);
        }
    }


}
