package com.tmb.oneapp.productsexpservice.service;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.*;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.*;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundSummaryBody;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundSummaryResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundfavorite.CustomerFavoriteFundData;
import com.tmb.oneapp.productsexpservice.model.response.fundholiday.FundHolidayBody;
import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundClassListInfo;
import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundListBody;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleResponse;
import com.tmb.oneapp.productsexpservice.model.response.investment.AccountDetailResponse;
import com.tmb.oneapp.productsexpservice.model.response.stmtresponse.StatementResponse;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.exceptions.base.MockitoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class ProductExpAsyncServiceTest {

    private InvestmentRequestClient investmentRequestClient;

    private AccountRequestClient accountRequestClient;

    private CustomerServiceClient customerServiceClient;

    private CommonServiceClient commonServiceClient;

    private ProductExpAsyncService productExpAsyncService;

    private CacheServiceClient cacheServiceClient;

    private AccountDetailResponse accountDetailResponse = null;

    private FundRuleResponse fundRuleResponse = null;

    @JsonProperty("Project")
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    List<FundClassListInfo> fundClassLists = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        investmentRequestClient = mock(InvestmentRequestClient.class);
        accountRequestClient = mock(AccountRequestClient.class);
        commonServiceClient = mock(CommonServiceClient.class);
        customerServiceClient = mock(CustomerServiceClient.class);
        cacheServiceClient = mock(CacheServiceClient.class);
        productExpAsyncService = new ProductExpAsyncService(investmentRequestClient, accountRequestClient, customerServiceClient, commonServiceClient, cacheServiceClient);
    }

    @Test
    public void fetchFundAccDetail() throws Exception {
        TmbOneServiceResponse<AccountDetailResponse> oneServiceResponse = new TmbOneServiceResponse<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            accountDetailResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund_account_detail.json").toFile(), AccountDetailResponse.class);

            oneServiceResponse.setData(accountDetailResponse);
            oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundAccountDetailService(any(), any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        CompletableFuture<AccountDetailResponse> response = productExpAsyncService.fetchFundAccountDetail(any(), any());
        Assert.assertNotNull(response);
    }

    @Test
    public void fetchFundAccDetailWithException() {
        try {
            when(investmentRequestClient.callInvestmentFundAccountDetailService(any(), any())).thenThrow(MockitoException.class);
            CompletableFuture<AccountDetailResponse> response = productExpAsyncService.fetchFundAccountDetail(any(), any());
            Assert.assertNotNull(response);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void fetchFundRule() throws Exception {
        TmbOneServiceResponse<FundRuleResponse> oneServiceResponseBody = new TmbOneServiceResponse<>();

        try {
            ObjectMapper mapper = new ObjectMapper();

            fundRuleResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund_rule.json").toFile(), FundRuleResponse.class);

            oneServiceResponseBody.setData(fundRuleResponse);
            oneServiceResponseBody.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundRuleService(any(), any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponseBody));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        CompletableFuture<FundRuleResponse> response = productExpAsyncService.fetchFundRule(any(), any());
        Assert.assertNotNull(response);
    }

    @Test
    public void fetchFundRuleWithException() {
        try {
            when(investmentRequestClient.callInvestmentFundRuleService(any(), any())).thenThrow(MockitoException.class);
            CompletableFuture<FundRuleResponse> response = productExpAsyncService.fetchFundRule(any(), any());
            Assert.assertNotNull(response);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void fetchStmtByPort() throws Exception {
        try {
            StatementResponse statementResponse;
            TmbOneServiceResponse<StatementResponse> serviceResponseStmt = new TmbOneServiceResponse<>();

            ObjectMapper mapper = new ObjectMapper();
            statementResponse = mapper.readValue(Paths.get("src/test/resources/investment/investment_stmt.json").toFile(), StatementResponse.class);

            serviceResponseStmt.setData(statementResponse);
            serviceResponseStmt.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentStatementByPortService(any(), any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(serviceResponseStmt));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        CompletableFuture<StatementResponse> response = productExpAsyncService.fetchStatementByPort(any(), any());
        Assert.assertNotNull(response);
    }

    @Test
    public void fetchStmtByPortWithException() {
        try {
            when(investmentRequestClient.callInvestmentStatementByPortService(any(), any())).thenThrow(MockitoException.class);
            CompletableFuture<StatementResponse> response = productExpAsyncService.fetchStatementByPort(any(), any());
            Assert.assertNotNull(response);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void fetchFundHoliday() throws Exception {
        try {
            FundHolidayBody fundHolidayBody;
            TmbOneServiceResponse<FundHolidayBody> serviceResponseStmt = new TmbOneServiceResponse<>();

            ObjectMapper mapper = new ObjectMapper();
            fundHolidayBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_holiday.json").toFile(), FundHolidayBody.class);

            serviceResponseStmt.setData(fundHolidayBody);
            serviceResponseStmt.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundHolidayService(any(), any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(serviceResponseStmt));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        CompletableFuture<FundHolidayBody> response = productExpAsyncService.fetchFundHoliday(any(), any());
        Assert.assertNotNull(response);
    }

    @Test
    public void fetchFundHolidayWithException() {
        try {
            when(investmentRequestClient.callInvestmentFundHolidayService(any(), any())).thenThrow(MockitoException.class);
            CompletableFuture<FundHolidayBody> response = productExpAsyncService.fetchFundHoliday(any(), any());
            Assert.assertNotNull(response);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void fetchCustomerExp() throws Exception {
        try {
            String responseCustomerExp;
            responseCustomerExp = new String(Files.readAllBytes(Paths.get("src/test/resources/investment/cc_exp_service.json")), StandardCharsets.UTF_8);
            when(accountRequestClient.getAccountList(any(), anyString())).thenReturn(responseCustomerExp);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        CompletableFuture<String> response = productExpAsyncService.fetchCustomerExp(any(), any());
        Assert.assertNotNull(response);
    }

    @Test
    public void fetchCustomerExpWithException() {
        try {
            when(accountRequestClient.getAccountList(any(), any())).thenThrow(MockitoException.class);
            CompletableFuture<String> response = productExpAsyncService.fetchCustomerExp(any(), any());
            Assert.assertNotNull(response);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void getCommonConfigByModule() throws Exception {
        TmbOneServiceResponse<List<CommonData>> responseCommon = new TmbOneServiceResponse<>();
        CommonData commonData = new CommonData();
        CommonTime commonTime = new CommonTime();
        List<CommonData> commonDataList = new ArrayList<>();

        try {
            commonTime.setStart("06:00");
            commonTime.setEnd("23:00");
            commonData.setNoneServiceHour(commonTime);
            commonDataList.add(commonData);

            responseCommon.setData(commonDataList);
            responseCommon.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(commonServiceClient.getCommonConfigByModule(anyString(), anyString())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(responseCommon));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        CompletableFuture<List<CommonData>> response = productExpAsyncService.fetchCommonConfigByModule(anyString(), anyString());
        Assert.assertNotNull(response);
    }

    @Test
    public void getCommonConfigByModuleWithException() {
        try {
            when(commonServiceClient.getCommonConfigByModule(anyString(), anyString())).thenThrow(MockitoException.class);
            CompletableFuture<List<CommonData>> response = productExpAsyncService.fetchCommonConfigByModule(any(), any());
            Assert.assertNotNull(response);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void fetchCustomerProfile() throws Exception {
        try {
            CustGeneralProfileResponse fundHolidayBody;
            TmbOneServiceResponse<CustGeneralProfileResponse> serviceResponseStmt = new TmbOneServiceResponse<>();

            ObjectMapper mapper = new ObjectMapper();
            fundHolidayBody = mapper.readValue(Paths.get("src/test/resources/investment/customers_profile.json").toFile(), CustGeneralProfileResponse.class);

            serviceResponseStmt.setData(fundHolidayBody);
            serviceResponseStmt.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(customerServiceClient.getCustomerProfile(anyString())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(serviceResponseStmt));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        CompletableFuture<CustGeneralProfileResponse> response = productExpAsyncService.fetchCustomerProfile(anyString());
        Assert.assertNotNull(response);
    }

    @Test
    public void fetchCustomerProfileWithException() {
        try {
            when(customerServiceClient.getCustomerProfile(anyString())).thenThrow(MockitoException.class);
            CompletableFuture<CustGeneralProfileResponse> response = productExpAsyncService.fetchCustomerProfile(anyString());
            Assert.assertNotNull(response);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void fetchFundListInfoWithException() {
        try {
            when(cacheServiceClient.getCacheByKey(anyString(), anyString())).thenThrow(MockitoException.class);
            CompletableFuture<List<FundClassListInfo>> response = productExpAsyncService.fetchFundListInfo(any(), anyString(), anyString());
            Assert.assertNotNull(response);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void fetchFundSummary() throws Exception {
        try {
            FundSummaryBody fundSummaryBody = null;
            TmbOneServiceResponse<FundSummaryBody> serviceResponseStmt = new TmbOneServiceResponse<>();

            ObjectMapper mapper = new ObjectMapper();
            fundSummaryBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_summary_data.json").toFile(), FundSummaryBody.class);

            serviceResponseStmt.setData(fundSummaryBody);
            serviceResponseStmt.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundSummaryService(any(), any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(serviceResponseStmt));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        CompletableFuture<FundSummaryBody> response = productExpAsyncService.fetchFundSummary(any(), any());
        Assert.assertNotNull(response);
    }

    @Test
    public void fetchFundSummaryWithException() {
        try {
            when(investmentRequestClient.callInvestmentFundSummaryService(any(), any())).thenThrow(MockitoException.class);
            CompletableFuture<FundSummaryBody> response = productExpAsyncService.fetchFundSummary(any(), any());
            Assert.assertNotNull(response);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void fetchFundFavoriteWitchException() {
        try {
            Map<String, String> invHeaderReqParameter = new HashMap<>();
            when(investmentRequestClient.callInvestmentFundFavoriteService(any())).thenThrow(MockitoException.class);
            CompletableFuture<List<CustomerFavoriteFundData>> response = productExpAsyncService.fetchFundFavorite(invHeaderReqParameter, "100000023333");
            Assert.assertNotNull(response);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void fetchFundFavorite() {
        try {
            Map<String, String> invHeaderReqParameter = new HashMap<>();
            List<CustomerFavoriteFundData> favoriteFundData = new ArrayList<>();
            CustomerFavoriteFundData fundHolidayBody = new CustomerFavoriteFundData();
            TmbOneServiceResponse<List<CustomerFavoriteFundData>> serviceResponseStmt = new TmbOneServiceResponse<>();

            fundHolidayBody.setFundCode("AAAA");
            fundHolidayBody.setIsFavorite("N");
            fundHolidayBody.setId("1");
            fundHolidayBody.setCustomerId("100000023333");

            favoriteFundData.add(fundHolidayBody);

            serviceResponseStmt.setData(favoriteFundData);
            serviceResponseStmt.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundFavoriteService(any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(serviceResponseStmt));

            CompletableFuture<List<CustomerFavoriteFundData>> response = productExpAsyncService.fetchFundFavorite(invHeaderReqParameter, "100000023333");
            Assert.assertNotNull(response);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    void testGetListCompletableFuture() throws JsonProcessingException {
        String key = "test";
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        Map<String, String> invHeaderReqParameter = new HashMap<>();
        invHeaderReqParameter.put("test", "test");

        ObjectMapper mapper = new ObjectMapper();
        Object value = true;
        mapper.writeValueAsString(value);

        TmbStatus tmbStatus = new TmbStatus();
        tmbStatus.setService("products-exp-async-service");

        TmbOneServiceResponse<String> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        tmbOneServiceResponse.setData("test");
        tmbOneServiceResponse.setStatus(tmbStatus);

        ResponseEntity<TmbOneServiceResponse<String>> response = new ResponseEntity<>(tmbOneServiceResponse, HttpStatus.OK);
        when(cacheServiceClient.getCacheByKey(any(), any())).thenReturn(response);

        FundClassListInfo fundClass = new FundClassListInfo();
        fundClass.setFundClassCode("1234");
        fundClass.setAllotType("test");

        List<FundClassListInfo> fundClassLists = new ArrayList<>();
        fundClassLists.add(fundClass);

        FundListBody data = new FundListBody();
        data.setFundClassList(fundClassLists);

        TmbOneServiceResponse<FundListBody> investmentResponse = new TmbOneServiceResponse<>();
        investmentResponse.setStatus(tmbStatus);
        investmentResponse.setData(data);
        ResponseEntity<TmbOneServiceResponse<FundListBody>> resp = new ResponseEntity<>(investmentResponse, HttpStatus.OK);
        when(investmentRequestClient.callInvestmentFundListInfoService(any())).thenReturn(resp);

        ResponseEntity<TmbOneServiceResponse<String>> cacheResponse = new ResponseEntity<>(tmbOneServiceResponse, HttpStatus.OK);
        when(cacheServiceClient.putCacheByKey(any(), any())).thenReturn(cacheResponse);

        CompletableFuture<List<FundClassListInfo>> listCompletableFuture = productExpAsyncService.getListCompletableFuture(invHeaderReqParameter, correlationId, key, mapper);

        assertNotEquals(100, listCompletableFuture.getNumberOfDependents());
    }

    @Test
    public void testGetFundClassListInfos() throws Exception {
        Map<String, String> invHeaderReqParameter = new HashMap<>();
        invHeaderReqParameter.put("test", "test");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        fundRuleResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund_rule_payment.json").toFile(), FundRuleResponse.class);
        String responseCustomerExp = new String(Files.readAllBytes(Paths.get("src/test/resources/investment/cc_exp_service.json")), StandardCharsets.UTF_8);
        TmbOneServiceResponse<String> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        tmbOneServiceResponse.setData(responseCustomerExp);
        TmbStatus tmbStatus = new TmbStatus();
        tmbStatus.setService("products-exp-async-service");
        tmbOneServiceResponse.setStatus(tmbStatus);
        ResponseEntity<TmbOneServiceResponse<String>> response = new ResponseEntity<>(tmbOneServiceResponse, HttpStatus.OK);
        when(cacheServiceClient.getCacheByKey(any(), any())).thenReturn(response);
        List<FundClassListInfo> fundClassLists = new ArrayList<>();
        FundClassListInfo fundClass = new FundClassListInfo();
        fundClass.setFundClassCode("1234");
        fundClass.setAllotType("test");
        fundClassLists.add(fundClass);
        TmbOneServiceResponse<FundListBody> investmentResponse = new TmbOneServiceResponse<>();
        investmentResponse.setStatus(tmbStatus);
        FundListBody data = new FundListBody();
        data.setFundClassList(fundClassLists);
        investmentResponse.setData(data);
        ResponseEntity<TmbOneServiceResponse<FundListBody>> resp = new ResponseEntity<>(investmentResponse, HttpStatus.OK);
        when(investmentRequestClient.callInvestmentFundListInfoService(any())).thenReturn(resp);
        ResponseEntity<TmbOneServiceResponse<String>> cacheResponse = new ResponseEntity<>(tmbOneServiceResponse, HttpStatus.OK);
        when(cacheServiceClient.putCacheByKey(any(), any())).thenReturn(cacheResponse);
        productExpAsyncService.getFundClassListInfoList(mapper, cacheResponse);

        assertNotNull(response);
    }

    @Test
    void should_throw_common_exception_when_call_fetchFundAccountDetail() {
        //Given
        String errorCode = "2000009";
        String errorMessage = "Bad Request";
        when(investmentRequestClient.callInvestmentFundAccountDetailService(any(), any())).thenThrow(mockFeignExceptionBadRequest(errorCode, errorMessage));

        //When
        try {
            productExpAsyncService.fetchFundAccountDetail(any(), any());
        } catch (TMBCommonException ex) {
            // Then
            assertEquals(errorCode, ex.getErrorCode());
            assertEquals(errorMessage, ex.getErrorMessage());
        }

    }

    @Test
    void should_throw_common_exception_when_call_fetchFundRule() {
        //Given
        String errorCode = "2000009";
        String errorMessage = "Bad Request";
        when(investmentRequestClient.callInvestmentFundRuleService(any(), any())).thenThrow(mockFeignExceptionBadRequest(errorCode, errorMessage));

        //When
        try {
            productExpAsyncService.fetchFundRule(any(), any());
        } catch (TMBCommonException ex) {
            // Then
            assertEquals(errorCode, ex.getErrorCode());
            assertEquals(errorMessage, ex.getErrorMessage());
        }

    }

    @Test
    void should_throw_common_exception_when_call_fetchStatementByPort() {
        //Given
        String errorCode = "2000009";
        String errorMessage = "Bad Request";
        when(investmentRequestClient.callInvestmentStatementByPortService(any(), any())).thenThrow(mockFeignExceptionBadRequest(errorCode, errorMessage));

        //When
        try {
            productExpAsyncService.fetchStatementByPort(any(), any());
        } catch (TMBCommonException ex) {
            // Then
            assertEquals(errorCode, ex.getErrorCode());
            assertEquals(errorMessage, ex.getErrorMessage());
        }

    }

    @Test
    void should_throw_common_exception_when_call_fetchFundHoliday() {
        //Given
        String errorCode = "2000009";
        String errorMessage = "Bad Request";
        when(investmentRequestClient.callInvestmentFundHolidayService(any(), any())).thenThrow(mockFeignExceptionBadRequest(errorCode, errorMessage));

        //When
        try {
            productExpAsyncService.fetchFundHoliday(any(), any());
        } catch (TMBCommonException ex) {
            // Then
            assertEquals(errorCode, ex.getErrorCode());
            assertEquals(errorMessage, ex.getErrorMessage());
        }

    }

    @Test
    void should_throw_common_exception_when_call_fetchCustomerExp() {
        //Given
        String errorCode = "2000009";
        String errorMessage = "Bad Request";
        when(accountRequestClient.getAccountList(any(), any())).thenThrow(mockFeignExceptionBadRequest(errorCode, errorMessage));

        //When
        try {
            productExpAsyncService.fetchCustomerExp(any(), any());
        } catch (TMBCommonException ex) {
            // Then
            assertEquals(errorCode, ex.getErrorCode());
            assertEquals(errorMessage, ex.getErrorMessage());
        }

    }

    @Test
    void should_throw_common_exception_when_call_fetchCommonConfigByModule() {
        //Given
        String errorCode = "2000009";
        String errorMessage = "Bad Request";
        when(commonServiceClient.
                getCommonConfigByModule(any(), any())).thenThrow(mockFeignExceptionBadRequest(errorCode, errorMessage));

        //When
        try {
            productExpAsyncService.fetchCommonConfigByModule(any(), any());
        } catch (TMBCommonException ex) {
            // Then
            assertEquals(errorCode, ex.getErrorCode());
            assertEquals(errorMessage, ex.getErrorMessage());
        }

    }

    @Test
    void should_throw_common_exception_when_call_fetchCustomerProfile() {
        //Given
        String errorCode = "2000009";
        String errorMessage = "Bad Request";
        when(customerServiceClient.getCustomerProfile(any())).thenThrow(mockFeignExceptionBadRequest(errorCode, errorMessage));

        //When
        try {
            productExpAsyncService.fetchCustomerProfile(any());
        } catch (TMBCommonException ex) {
            // Then
            assertEquals(errorCode, ex.getErrorCode());
            assertEquals(errorMessage, ex.getErrorMessage());
        }

    }

    @Test
    void should_throw_common_exception_when_call_fetchFundSummary() {
        //Given
        String errorCode = "2000009";
        String errorMessage = "Bad Request";
        when(investmentRequestClient.callInvestmentFundSummaryService(any(), any())).thenThrow(mockFeignExceptionBadRequest(errorCode, errorMessage));

        //When
        try {
            productExpAsyncService.fetchFundSummary(any(), any());
        } catch (TMBCommonException ex) {
            // Then
            assertEquals(errorCode, ex.getErrorCode());
            assertEquals(errorMessage, ex.getErrorMessage());
        }

    }

    @Test
    void should_throw_common_exception_when_call_fetchFundFavorite() {
        //Given
        String errorCode = "2000009";
        String errorMessage = "Bad Request";
        when(investmentRequestClient.callInvestmentFundFavoriteService(any())).thenThrow(mockFeignExceptionBadRequest(errorCode, errorMessage));

        //When
        try {
            Map<String, String> investmentHeaderRequest = UtilMap.createHeader("2323232323");
            productExpAsyncService.fetchFundFavorite(investmentHeaderRequest, "1111111111111");
        } catch (TMBCommonException ex) {
            // Then
            assertEquals(errorCode, ex.getErrorCode());
            assertEquals(errorMessage, ex.getErrorMessage());
        }

    }

    @Test
    void should_throw_common_exception_when_call_fetchSuitabilityInquiry() {
        //Given
        String errorCode = "2000009";
        String errorMessage = "Bad Request";
        when(investmentRequestClient.callInvestmentFundSuitabilityService(any(),any())).thenThrow(mockFeignExceptionBadRequest(errorCode, errorMessage));

        //When
        try {
            Map<String, String> investmentHeaderRequest = UtilMap.createHeader("2323232323");
            productExpAsyncService.fetchSuitabilityInquiry(investmentHeaderRequest, "111111111111111");
        } catch (TMBCommonException ex) {
            // Then
            assertEquals(errorCode, ex.getErrorCode());
            assertEquals(errorMessage, ex.getErrorMessage());
        }

    }

    private FeignException mockFeignExceptionBadRequest(String errorCode, String errorMessage) {
        Request.Body body = Request.Body.create("".getBytes(StandardCharsets.UTF_8));
        RequestTemplate template = new RequestTemplate();
        Map<String, Collection<String>> headers = new HashMap<>();
        String errorBody = "{\n" +
                "    \"status\": {\n" +
                "        \"code\": \"" + errorCode + "\",\n" +
                "        \"message\": \"" + errorMessage + "\",\n" +
                "        \"service\": null,\n" +
                "        \"description\": \"Please enter PIN\"\n" +
                "    },\n" +
                "    \"data\": null\n" +
                "}";
        Request request = Request.create(Request.HttpMethod.POST, "http://localhost", headers, body, template);
        FeignException.BadRequest e = new FeignException.BadRequest("", request, errorBody.getBytes(StandardCharsets.UTF_8));
        return e;
    }

}