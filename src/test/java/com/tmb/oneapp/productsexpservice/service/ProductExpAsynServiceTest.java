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
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundSummaryResponse;
import com.tmb.oneapp.productsexpservice.model.request.fund.FundCodeRequestBody;
import com.tmb.oneapp.productsexpservice.model.response.fund.dailynav.DailyNavBody;
import com.tmb.oneapp.productsexpservice.model.response.fund.dailynav.DailyNavResponse;
import com.tmb.oneapp.productsexpservice.model.response.fund.information.InformationBody;
import com.tmb.oneapp.productsexpservice.model.response.fund.information.InformationResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundfavorite.CustFavoriteFundData;
import com.tmb.oneapp.productsexpservice.model.response.fundholiday.FundHolidayBody;
import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundClassListInfo;
import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundListBody;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleBody;
import com.tmb.oneapp.productsexpservice.model.response.investment.AccDetailBody;
import com.tmb.oneapp.productsexpservice.model.response.stmtresponse.StatementResponse;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.exceptions.base.MockitoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class ProductExpAsynServiceTest {
    InvestmentRequestClient investmentRequestClient;
    AccountRequestClient accountRequestClient;
    CustomerServiceClient customerServiceClient;
    CommonServiceClient commonServiceClient;
    ProductExpAsynService productExpAsynService;
    CacheServiceClient cacheServiceClient;

    private AccDetailBody accDetailBody = null;
    private FundRuleBody fundRuleBody = null;

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
        productExpAsynService = new ProductExpAsynService(investmentRequestClient, accountRequestClient, customerServiceClient, commonServiceClient, cacheServiceClient);
    }

    @Test
    public void fetchFundAccDetail() throws Exception {
        TmbOneServiceResponse<AccDetailBody> oneServiceResponse = new TmbOneServiceResponse<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            accDetailBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_account_detail.json").toFile(), AccDetailBody.class);

            oneServiceResponse.setData(accDetailBody);
            oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundAccDetailService(any(), any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        CompletableFuture<AccDetailBody> response = productExpAsynService.fetchFundAccDetail(any(), any());
        Assert.assertNotNull(response);
    }

    @Test
    public void fetchFundAccDetailWithException() throws Exception {
        try {
            when(investmentRequestClient.callInvestmentFundAccDetailService(any(), any())).thenThrow(MockitoException.class);
            CompletableFuture<AccDetailBody> response = productExpAsynService.fetchFundAccDetail(any(), any());
            Assert.assertNotNull(response);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Test
    public void fetchFundRule() throws Exception {

        TmbOneServiceResponse<FundRuleBody> oneServiceResponseBody = new TmbOneServiceResponse<>();

        try {
            ObjectMapper mapper = new ObjectMapper();

            fundRuleBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_rule.json").toFile(), FundRuleBody.class);

            oneServiceResponseBody.setData(fundRuleBody);
            oneServiceResponseBody.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundRuleService(any(), any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponseBody));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        CompletableFuture<FundRuleBody> response = productExpAsynService.fetchFundRule(any(), any());
        Assert.assertNotNull(response);

    }

    @Test
    public void fetchFundRuleWithException() throws Exception {
        try {
            when(investmentRequestClient.callInvestmentFundRuleService(any(), any())).thenThrow(MockitoException.class);
            CompletableFuture<FundRuleBody> response = productExpAsynService.fetchFundRule(any(), any());
            Assert.assertNotNull(response);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Test
    public void fetchStmtByPort() throws Exception {
        try {
            StatementResponse statementResponse = null;
            TmbOneServiceResponse<StatementResponse> serviceResponseStmt = new TmbOneServiceResponse<>();

            ObjectMapper mapper = new ObjectMapper();
            statementResponse = mapper.readValue(Paths.get("src/test/resources/investment/investment_stmt.json").toFile(), StatementResponse.class);

            serviceResponseStmt.setData(statementResponse);
            serviceResponseStmt.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentStmtByPortService(any(), any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(serviceResponseStmt));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        CompletableFuture<StatementResponse> response = productExpAsynService.fetchStmtByPort(any(), any());
        Assert.assertNotNull(response);
    }

    @Test
    public void fetchStmtByPortWithException() throws Exception {
        try {
            when(investmentRequestClient.callInvestmentStmtByPortService(any(), any())).thenThrow(MockitoException.class);
            CompletableFuture<StatementResponse> response = productExpAsynService.fetchStmtByPort(any(), any());
            Assert.assertNotNull(response);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Test
    public void fetchFundHoliday() throws Exception {
        try {
            FundHolidayBody fundHolidayBody = null;
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
        CompletableFuture<FundHolidayBody> response = productExpAsynService.fetchFundHoliday(any(), any());
        Assert.assertNotNull(response);
    }

    @Test
    public void fetchFundHolidayWithException() throws Exception {
        try {
            when(investmentRequestClient.callInvestmentFundHolidayService(any(), any())).thenThrow(MockitoException.class);
            CompletableFuture<FundHolidayBody> response = productExpAsynService.fetchFundHoliday(any(), any());
            Assert.assertNotNull(response);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Test
    public void fetchCustomerExp() throws Exception {
        try {
            String responseCustomerExp = null;
            responseCustomerExp = new String(Files.readAllBytes(Paths.get("src/test/resources/investment/cc_exp_service.json")), StandardCharsets.UTF_8);
            when(accountRequestClient.callCustomerExpService(any(), anyString())).thenReturn(responseCustomerExp);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        CompletableFuture<String> response = productExpAsynService.fetchCustomerExp(any(), any());
        Assert.assertNotNull(response);
    }

    @Test
    public void fetchCustomerExpWithException() throws Exception {
        try {
            when(accountRequestClient.callCustomerExpService(any(), any())).thenThrow(MockitoException.class);
            CompletableFuture<String> response = productExpAsynService.fetchCustomerExp(any(), any());
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

        CompletableFuture<List<CommonData>> response = productExpAsynService.fetchCommonConfigByModule(anyString(), anyString());
        Assert.assertNotNull(response);

    }

    @Test
    public void getCommonConfigByModuleWithException() throws Exception {
        try {
            when(commonServiceClient.getCommonConfigByModule(anyString(), anyString())).thenThrow(MockitoException.class);
            CompletableFuture<List<CommonData>> response = productExpAsynService.fetchCommonConfigByModule(any(), any());
            Assert.assertNotNull(response);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Test
    public void fetchCustomerProfile() throws Exception {
        try {
        	CustGeneralProfileResponse fundHolidayBody = null;
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
        CompletableFuture<CustGeneralProfileResponse> response = productExpAsynService.fetchCustomerProfile( anyString());
        Assert.assertNotNull(response);
    }


    @Test
    public void fetchCustomerProfileWithException() throws Exception {
        try {
            when(customerServiceClient.getCustomerProfile( anyString())).thenThrow(MockitoException.class);
            CompletableFuture<CustGeneralProfileResponse> response = productExpAsynService.fetchCustomerProfile( anyString());
            Assert.assertNotNull(response);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    @Test
    public void fetchFundListInfoWithException() throws Exception {
        try {
            when(cacheServiceClient.getCacheByKey(anyString(), anyString())).thenThrow(MockitoException.class);
            CompletableFuture<List<FundClassListInfo>> response = productExpAsynService.fetchFundListInfo(any(), anyString(), anyString());
            Assert.assertNotNull(response);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    @Test
    public void fetchFundSummary() throws Exception {
        try {
            FundSummaryResponse fundHolidayBody = null;
            TmbOneServiceResponse<FundSummaryResponse> serviceResponseStmt = new TmbOneServiceResponse<>();

            ObjectMapper mapper = new ObjectMapper();
            fundHolidayBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_summary_data.json").toFile(), FundSummaryResponse.class);

            serviceResponseStmt.setData(fundHolidayBody);
            serviceResponseStmt.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundSummaryService(any(), any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(serviceResponseStmt));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        CompletableFuture<FundSummaryResponse> response = productExpAsynService.fetchFundSummary(any(), any());
        Assert.assertNotNull(response);
    }

    @Test
    public void fetchFundSummaryWithException() throws Exception {
        try {
            when(investmentRequestClient.callInvestmentFundSummaryService(any(), any())).thenThrow(MockitoException.class);
            CompletableFuture<FundSummaryResponse> response = productExpAsynService.fetchFundSummary(any(), any());
            Assert.assertNotNull(response);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    @Test
    public void fetchFundFavoriteWitchException() throws Exception {
        try {
            when(investmentRequestClient.callInvestmentFundFavoriteService(any(), anyString())).thenThrow(MockitoException.class);
            CompletableFuture<List<CustFavoriteFundData>> response = productExpAsynService.fetchFundFavorite(any(), anyString());
            Assert.assertNotNull(response);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    @Test
    public void fetchFundFavorite() throws Exception {
        try {
            List<CustFavoriteFundData> favoriteFundData = new ArrayList<>();
            CustFavoriteFundData fundHolidayBody = new CustFavoriteFundData();
            TmbOneServiceResponse<List<CustFavoriteFundData>> serviceResponseStmt = new TmbOneServiceResponse<>();

            fundHolidayBody.setFundCode("AAAA");
            fundHolidayBody.setIsFavorite("N");
            fundHolidayBody.setId("1");
            fundHolidayBody.setCustId("100000023333");

            favoriteFundData.add(fundHolidayBody);


            serviceResponseStmt.setData(favoriteFundData);
            serviceResponseStmt.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundFavoriteService(any(), anyString())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(serviceResponseStmt));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        CompletableFuture<List<CustFavoriteFundData>> response = productExpAsynService.fetchFundFavorite(any(), anyString());
        Assert.assertNotNull(response);
    }


    @Test
    void testGetListCompletableFuture() throws JsonProcessingException {
        Map<String, String> invHeaderReqParameter = new HashMap<>();
        invHeaderReqParameter.put("test", "test");
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        String key = "test";
        ObjectMapper mapper = new ObjectMapper();
        Object value = true;
        mapper.writeValueAsString(value);
        TmbOneServiceResponse<String> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        tmbOneServiceResponse.setData("test");
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
        CompletableFuture<List<FundClassListInfo>> listCompletableFuture = productExpAsynService.getListCompletableFuture(invHeaderReqParameter, correlationId, key, mapper);
        assertNotEquals(100, listCompletableFuture.getNumberOfDependents());
    }

    @Test
    public void testGetFundClassListInfos() throws Exception {
        Map<String, String> invHeaderReqParameter = new HashMap<>();
        invHeaderReqParameter.put("test", "test");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        fundRuleBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_rule_payment.json").toFile(), FundRuleBody.class);
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
        productExpAsynService.getFundClassListInfos(mapper, cacheResponse);


        assertNotNull(response);
    }

    @Test
    void should_return_information_body_when_call_fetch_fund_information_given_header_and_fund_code_request_body() throws TMBCommonException, IOException, ExecutionException, InterruptedException {
        //Given
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> investmentRequestHeader = Map.of("test", "test");
        InformationResponse informationResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund/fund_information.json").toFile(),
                InformationResponse.class);
        TmbOneServiceResponse<InformationBody> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        tmbOneServiceResponse.setData(informationResponse.getData());
        TmbStatus tmbStatus = new TmbStatus();
        tmbStatus.setService("products-exp-async-service");
        tmbOneServiceResponse.setStatus(tmbStatus);
        ResponseEntity<TmbOneServiceResponse<InformationBody>> response = new ResponseEntity<>(tmbOneServiceResponse, HttpStatus.OK);
        FundCodeRequestBody fundCodeRequestBody = FundCodeRequestBody.builder()
                .code("TMBCOF")
                .build();
        when(investmentRequestClient.callInvestmentFundInformationService(investmentRequestHeader, fundCodeRequestBody)).thenReturn(response);

        //When
        CompletableFuture<InformationBody> actual = productExpAsynService.fetchFundInformation(investmentRequestHeader, fundCodeRequestBody);

        //Then
        CompletableFuture<InformationBody> expected = CompletableFuture.completedFuture(informationResponse.getData());
        assertEquals(expected.get(), actual.get());
    }

    @Test
    void should_return_daily_nav_body_when_call_fetch_fund_daily_nav_given_header_and_fund_code_request_body() throws TMBCommonException, IOException, ExecutionException, InterruptedException {
        //Given
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> investmentRequestHeader = Map.of("test", "test");
        DailyNavResponse dailyNavResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund/fund_daily_nav.json").toFile(),
                DailyNavResponse.class);
        TmbOneServiceResponse<DailyNavBody> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        tmbOneServiceResponse.setData(dailyNavResponse.getData());
        TmbStatus tmbStatus = new TmbStatus();
        tmbStatus.setService("products-exp-async-service");
        tmbOneServiceResponse.setStatus(tmbStatus);
        ResponseEntity<TmbOneServiceResponse<DailyNavBody>> response = new ResponseEntity<>(tmbOneServiceResponse, HttpStatus.OK);
        FundCodeRequestBody fundCodeRequestBody = FundCodeRequestBody.builder()
                .code("TMBCOF")
                .build();
        when(investmentRequestClient.callInvestmentFundDailyNavService(investmentRequestHeader, fundCodeRequestBody)).thenReturn(response);

        //When
        CompletableFuture<DailyNavBody> actual = productExpAsynService.fetchFundDailyNav(investmentRequestHeader, fundCodeRequestBody);

        //Then
        CompletableFuture<DailyNavBody> expected = CompletableFuture.completedFuture(dailyNavResponse.getData());
        assertEquals(expected.get(), actual.get());
    }
}
