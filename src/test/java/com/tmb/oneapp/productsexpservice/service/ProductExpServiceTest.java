package com.tmb.oneapp.productsexpservice.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.kafka.service.KafkaProducerService;
import com.tmb.common.model.*;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.dto.fund.fundallocation.SuggestAllocationDTO;
import com.tmb.oneapp.productsexpservice.feignclients.*;
import com.tmb.oneapp.productsexpservice.model.activitylog.ActivityLogs;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundSummaryBody;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundSummaryResponse;
import com.tmb.oneapp.productsexpservice.model.request.accdetail.FundAccountRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.accdetail.FundAccountRq;
import com.tmb.oneapp.productsexpservice.model.request.alternative.AlternativeRq;
import com.tmb.oneapp.productsexpservice.model.request.cache.CacheModel;
import com.tmb.oneapp.productsexpservice.model.request.fundffs.FfsRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fundlist.FundListRq;
import com.tmb.oneapp.productsexpservice.model.request.fundpayment.FundPaymentDetailRq;
import com.tmb.oneapp.productsexpservice.model.request.fundrule.FundRuleRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fundsummary.FundSummaryRq;
import com.tmb.oneapp.productsexpservice.model.request.stmtrequest.OrderStmtByPortRq;
import com.tmb.oneapp.productsexpservice.model.response.accdetail.FundAccountDetail;
import com.tmb.oneapp.productsexpservice.model.response.accdetail.FundAccountResponse;
import com.tmb.oneapp.productsexpservice.model.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.model.response.fund.fundallocation.FundAllocationResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundfavorite.CustFavoriteFundData;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FfsRsAndValidation;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FundResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundholiday.FundHolidayBody;
import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundClassListInfo;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.FundPaymentDetailRs;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleBody;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleInfoList;
import com.tmb.oneapp.productsexpservice.model.response.investment.AccountDetailBody;
import com.tmb.oneapp.productsexpservice.model.response.investment.FundDetail;
import com.tmb.oneapp.productsexpservice.model.response.investment.Order;
import com.tmb.oneapp.productsexpservice.model.response.investment.OrderToBeProcess;
import com.tmb.oneapp.productsexpservice.model.response.stmtresponse.StatementResponse;
import com.tmb.oneapp.productsexpservice.model.response.suitability.SuitabilityInfo;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.exceptions.base.MockitoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProductExpServiceTest {

    private InvestmentRequestClient investmentRequestClient;

    private ProductsExpService productsExpService;

    private AccountRequestClient accountRequestClient;

    private KafkaProducerService kafkaProducerService;

    private CommonServiceClient commonServiceClient;

    private ProductExpAsynService productExpAsynService;

    private CustomerExpServiceClient customerExpServiceClient;

    private CustomerServiceClient customerServiceClient;

    private final String corrID = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";

    private AccountDetailBody accountDetailBody = null;

    private FundRuleBody fundRuleBody = null;

    @BeforeEach
    public void setUp() {
        investmentRequestClient = mock(InvestmentRequestClient.class);
        accountRequestClient = mock(AccountRequestClient.class);
        productsExpService = mock(ProductsExpService.class);
        kafkaProducerService = mock(KafkaProducerService.class);
        commonServiceClient = mock(CommonServiceClient.class);
        productExpAsynService = mock(ProductExpAsynService.class);
        customerServiceClient = mock(CustomerServiceClient.class);
        productsExpService = new ProductsExpService(investmentRequestClient,
                accountRequestClient,
                kafkaProducerService,
                commonServiceClient,
                productExpAsynService,
                customerExpServiceClient,
                customerServiceClient);
    }

    private void initAccDetailBody() {
        accountDetailBody = new AccountDetailBody();
        FundDetail fundDetail = new FundDetail();
        fundDetail.setFundHouseCode("TTTTT");
        fundDetail.setFundHouseCode("EEEEE");
        accountDetailBody.setFundDetail(fundDetail);

        OrderToBeProcess orderToBeProcess = new OrderToBeProcess();
        List<Order> orders = new ArrayList<>();
        Order order = new Order();
        order.setAmount("200");
        order.setOrderDate("20201212");
        orders.add(order);
        orderToBeProcess.setOrder(orders);
    }

    private void initFundRuleBody() {
        fundRuleBody = new FundRuleBody();
        List<FundRuleInfoList> fundRuleInfoList = new ArrayList<>();
        FundRuleInfoList list = new FundRuleInfoList();
        list.setFundCode("TTTTTT");
        list.setProcessFlag("N");
        fundRuleInfoList.add(list);
        fundRuleBody.setFundRuleInfoList(fundRuleInfoList);
    }

    private Map<String, String> createHeader(String correlationId) {
        Map<String, String> invHeaderReqParameter = new HashMap<>();
        invHeaderReqParameter.put(ProductsExpServiceConstant.X_CORRELATION_ID, correlationId);
        invHeaderReqParameter.put(ProductsExpServiceConstant.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return invHeaderReqParameter;
    }

    @Test
    public void testGetFundAccountDetailAndFundRule() throws Exception {
        StatementResponse statementResponse = null;
        FundAccountRq fundAccountRq = new FundAccountRq();
        fundAccountRq.setFundHouseCode("ABCC");
        fundAccountRq.setTranType("2");
        fundAccountRq.setFundCode("ABCC");
        fundAccountRq.setServiceType("1");
        fundAccountRq.setUnitHolderNo("PT0000000000123");

        try {
            ObjectMapper mapper = new ObjectMapper();
            this.accountDetailBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_account_detail.json").toFile(), AccountDetailBody.class);
            fundRuleBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_rule.json").toFile(), FundRuleBody.class);
            statementResponse = mapper.readValue(Paths.get("src/test/resources/investment/investment_stmt.json").toFile(), StatementResponse.class);

            when(productExpAsynService.fetchFundAccountDetail(any(), any())).thenReturn(CompletableFuture.completedFuture(this.accountDetailBody));
            when(productExpAsynService.fetchFundRule(any(), any())).thenReturn(CompletableFuture.completedFuture(fundRuleBody));
            when(productExpAsynService.fetchStatementByPort(any(), any())).thenReturn(CompletableFuture.completedFuture(statementResponse));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        CompletableFuture<AccountDetailBody> fetchFundAccDetail = productExpAsynService.fetchFundAccountDetail(any(), any());
        CompletableFuture<FundRuleBody> fetchFundRule = productExpAsynService.fetchFundRule(any(), any());
        CompletableFuture<StatementResponse> fetchStmtByPort = productExpAsynService.fetchStatementByPort(any(), any());
        CompletableFuture.allOf(fetchFundAccDetail, fetchFundRule, fetchStmtByPort);

        AccountDetailBody accountDetailBody = fetchFundAccDetail.get();
        FundRuleBody fundRuleBody = fetchFundRule.get();
        StatementResponse statementRs = fetchStmtByPort.get();

        FundAccountResponse fundAccountResponse = UtilMap.validateTMBResponse(accountDetailBody, fundRuleBody, statementRs);

        Assert.assertNotNull(fundAccountResponse);
        Assert.assertNotNull(accountDetailBody);
        Assert.assertNotNull(statementRs);
        FundAccountResponse result = productsExpService.getFundAccountDetail(corrID, fundAccountRq);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetFundAccountDetail() {
        FundAccountRq fundAccountRequest = new FundAccountRq();
        fundAccountRequest.setFundCode("EEEEEE");
        fundAccountRequest.setServiceType("1");
        fundAccountRequest.setUnitHolderNo("PT000001111");
        fundAccountRequest.setFundHouseCode("TTTTTTT");

        ResponseEntity<TmbOneServiceResponse<AccountDetailBody>> responseEntity = null;
        FundAccountRequestBody fundAccountRq = new FundAccountRequestBody();
        fundAccountRq.setUnitHolderNo("PT000000001");
        fundAccountRq.setServiceType("1");
        fundAccountRq.setFundCode("DDD");

        TmbOneServiceResponse<AccountDetailBody> oneServiceResponse = new TmbOneServiceResponse<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            accountDetailBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_account_detail.json").toFile(), AccountDetailBody.class);

            oneServiceResponse.setData(accountDetailBody);
            oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundAccDetailService(createHeader(corrID), fundAccountRq)).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        responseEntity = investmentRequestClient.callInvestmentFundAccDetailService(createHeader(corrID), fundAccountRq);
        Assert.assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCodeValue());
        Assert.assertEquals("FFFFF", responseEntity.getBody().getData().getFundDetail().getFundHouseCode());
        Assert.assertNotNull(responseEntity.getBody().getData().getFundDetail());
    }

    @Test
    public void testGetFundRule() {
        ResponseEntity<TmbOneServiceResponse<AccountDetailBody>> responseEntity = null;
        FundAccountRequestBody fundAccountRq = new FundAccountRequestBody();
        fundAccountRq.setUnitHolderNo("PT000000001");
        fundAccountRq.setServiceType("1");
        fundAccountRq.setFundCode("DDD");

        FundRuleRequestBody fundRuleRequestBody = new FundRuleRequestBody();
        fundRuleRequestBody.setTranType("2");
        fundRuleRequestBody.setFundHouseCode("TTTTT");
        fundRuleRequestBody.setFundCode("EEEEE");

        TmbOneServiceResponse<FundRuleBody> oneServiceResponseBody = new TmbOneServiceResponse<>();
        ResponseEntity<TmbOneServiceResponse<FundRuleBody>> fundRuleResponseEntity = null;

        try {
            ObjectMapper mapper = new ObjectMapper();

            fundRuleBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_rule.json").toFile(), FundRuleBody.class);

            oneServiceResponseBody.setData(fundRuleBody);
            oneServiceResponseBody.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundRuleService(createHeader(corrID), fundRuleRequestBody)).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponseBody));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        fundRuleResponseEntity = investmentRequestClient.callInvestmentFundRuleService(createHeader(corrID), fundRuleRequestBody);
        Assert.assertEquals(HttpStatus.OK, fundRuleResponseEntity.getStatusCode());
        Assert.assertEquals("TESEQDSSFX", fundRuleResponseEntity.getBody().getData().getFundRuleInfoList().get(0).getFundCode());
        Assert.assertEquals("TFUND", fundRuleResponseEntity.getBody().getData().getFundRuleInfoList().get(0).getFundHouseCode());
        Assert.assertEquals("20200413", fundRuleResponseEntity.getBody().getData().getFundRuleInfoList().get(0).getTranStartDate());
        Assert.assertEquals("3", fundRuleResponseEntity.getBody().getData().getFundRuleInfoList().get(0).getOrderType());
        Assert.assertEquals("3", fundRuleResponseEntity.getBody().getData().getFundRuleInfoList().get(0).getAllotType());
        Assert.assertEquals("06", fundRuleResponseEntity.getBody().getData().getFundRuleInfoList().get(0).getRiskRate());
    }

    @Test
    public void testGetFundAccountDetailNull() {
        initAccDetailBody();
        initFundRuleBody();
        FundAccountRq fundAccountRequest = new FundAccountRq();
        fundAccountRequest.setFundCode("EEEEEE");
        fundAccountRequest.setServiceType("1");
        fundAccountRequest.setUnitHolderNo("PT000001111");
        fundAccountRequest.setFundHouseCode("TTTTTTT");

        FundAccountRequestBody fundAccountRq = new FundAccountRequestBody();
        fundAccountRq.setUnitHolderNo("PT000000000000138924");
        fundAccountRq.setServiceType("2");
        fundAccountRq.setFundCode("DDD");

        FundRuleRequestBody fundRuleRequestBody = new FundRuleRequestBody();
        fundRuleRequestBody.setTranType("1");
        fundRuleRequestBody.setFundHouseCode("TFUND");
        fundRuleRequestBody.setFundCode("TMB50");

        OrderStmtByPortRq orderStmtByPortRq = new OrderStmtByPortRq();
        orderStmtByPortRq.setPortfolioNumber("PT0000000032534");
        orderStmtByPortRq.setRowEnd("5");
        orderStmtByPortRq.setRowStart("1");
        orderStmtByPortRq.setFundCode("EEEE");

        StatementResponse statementResponse = null;

        TmbOneServiceResponse<AccountDetailBody> oneServiceResponse = new TmbOneServiceResponse<>();
        TmbOneServiceResponse<FundRuleBody> oneServiceResponseBody = new TmbOneServiceResponse<>();
        TmbOneServiceResponse<StatementResponse> serviceResponseStmt = new TmbOneServiceResponse<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            statementResponse = mapper.readValue(Paths.get("src/test/resources/investment/investment_stmt.json").toFile(), StatementResponse.class);

            oneServiceResponse.setData(accountDetailBody);
            oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            oneServiceResponseBody.setData(fundRuleBody);
            oneServiceResponseBody.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            serviceResponseStmt.setData(statementResponse);
            serviceResponseStmt.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundAccDetailService(createHeader(corrID), fundAccountRq)).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse));
            when(investmentRequestClient.callInvestmentFundRuleService(createHeader(corrID), fundRuleRequestBody)).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponseBody));
            when(investmentRequestClient.callInvestmentStatementByPortService(createHeader(corrID), orderStmtByPortRq)).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(serviceResponseStmt));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FundAccountResponse result = productsExpService.getFundAccountDetail(corrID, fundAccountRequest);
        Assert.assertNull(result);
        UtilMap utilMap = new UtilMap();
        FundAccountDetail fundAccountDetailrs = utilMap.mappingResponse(accountDetailBody, fundRuleBody, statementResponse);
        Assert.assertNotNull(fundAccountDetailrs);
    }

    @Test
    public void testGetFundAccountDetailServiceNull() {
        initAccDetailBody();
        initFundRuleBody();
        FundAccountRq fundAccountRequest = new FundAccountRq();
        fundAccountRequest.setFundCode("EEEEEE");
        fundAccountRequest.setServiceType("1");
        fundAccountRequest.setUnitHolderNo("PT000001111");
        fundAccountRequest.setFundHouseCode("TTTTTTT");

        FundAccountRequestBody fundAccountRq = new FundAccountRequestBody();
        fundAccountRq.setUnitHolderNo("PT000000000000138924");
        fundAccountRq.setServiceType("2");
        fundAccountRq.setFundCode("DDD");

        FundRuleRequestBody fundRuleRequestBody = new FundRuleRequestBody();
        fundRuleRequestBody.setTranType("1");
        fundRuleRequestBody.setFundHouseCode("TFUND");
        fundRuleRequestBody.setFundCode("TMB50");

        TmbOneServiceResponse<AccountDetailBody> oneServiceResponse = new TmbOneServiceResponse<>();
        TmbOneServiceResponse<FundRuleBody> oneServiceResponseBody = new TmbOneServiceResponse<>();

        try {
            oneServiceResponse.setData(accountDetailBody);
            oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            oneServiceResponseBody.setData(fundRuleBody);
            oneServiceResponseBody.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(productsExpService.getFundAccountDetail(corrID, fundAccountRequest)).thenReturn(null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FundAccountResponse result = productsExpService.getFundAccountDetail(corrID, fundAccountRequest);
        Assert.assertNull(result);
    }

    @Test
    public void testGetFundPrePaymentDetail() throws Exception {
        FundPaymentDetailRq fundPaymentDetailRq = new FundPaymentDetailRq();
        fundPaymentDetailRq.setCrmId("001100000000000000000012025950");
        fundPaymentDetailRq.setFundCode("SCBTMF");
        fundPaymentDetailRq.setFundHouseCode("SCBAM");
        fundPaymentDetailRq.setTranType("1");

        List<String> eligibleAcc = Arrays.asList("200",
                "205",
                "212",
                "212",
                "219",
                "221",
                "225",
                "207",
                "208",
                "251",
                "252",
                "253",
                "255",
                "101",
                "107",
                "108",
                "109",
                "151",
                "152",
                "153",
                "154",
                "155",
                "171",
                "172",
                "173");

        String responseCustomerExp;
        FundHolidayBody fundHolidayBody;
        FundRuleBody fundRuleBody;
        CommonData commonData = new CommonData();
        List<CommonData> commonDataList = new ArrayList<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            fundRuleBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_rule_payment.json").toFile(), FundRuleBody.class);
            fundHolidayBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_holiday.json").toFile(), FundHolidayBody.class);

            responseCustomerExp = new String(Files.readAllBytes(Paths.get("src/test/resources/investment/cc_exp_service.json")), StandardCharsets.UTF_8);

            commonData.setEligibleAccountCodeBuy(eligibleAcc);
            commonDataList.add(commonData);

            when(productExpAsynService.fetchFundRule(any(), any())).thenReturn(CompletableFuture.completedFuture(fundRuleBody));
            when(productExpAsynService.fetchFundHoliday(any(), anyString())).thenReturn(CompletableFuture.completedFuture(fundHolidayBody));
            when(productExpAsynService.fetchCustomerExp(any(), any())).thenReturn(CompletableFuture.completedFuture(responseCustomerExp));
            when(productExpAsynService.fetchCommonConfigByModule(anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(commonDataList));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        UtilMap utilMap = new UtilMap();

        CompletableFuture<FundRuleBody> fetchFundRule = productExpAsynService.fetchFundRule(any(), any());
        CompletableFuture<FundHolidayBody> fetchFundHoliday = productExpAsynService.fetchFundHoliday(any(), anyString());
        CompletableFuture<String> fetchCustomerExp = productExpAsynService.fetchCustomerExp(any(), anyString());
        CompletableFuture<List<CommonData>> fetchCommonConfigByModule = productExpAsynService.fetchCommonConfigByModule(anyString(), anyString());

        CompletableFuture.allOf(fetchFundRule, fetchFundHoliday, fetchCustomerExp, fetchCommonConfigByModule);
        FundRuleBody fundRuleBodyCom = fetchFundRule.get();
        FundHolidayBody fundHolidayBodyCom = fetchFundHoliday.get();
        String customerExp = fetchCustomerExp.get();
        List<CommonData> commonDataListCom = fetchCommonConfigByModule.get();

        Assert.assertNotNull(customerExp);
        FundPaymentDetailRs response = utilMap.mappingPaymentResponse(fundRuleBodyCom, fundHolidayBodyCom, commonDataListCom, customerExp);
        Assert.assertNotNull(response);

        FundPaymentDetailRs serviceRes = productsExpService.getFundPrePaymentDetail(corrID, fundPaymentDetailRq);
        Assert.assertNotNull(serviceRes);
    }

    @Test
    public void testGetFundPrePaymentDetailNotFound() throws Exception {
        FundPaymentDetailRq fundPaymentDetailRq = new FundPaymentDetailRq();
        fundPaymentDetailRq.setCrmId("001100000000000000000012025950");
        fundPaymentDetailRq.setFundCode("SCBTMF");
        fundPaymentDetailRq.setFundHouseCode("SCBAM");
        fundPaymentDetailRq.setTranType("1");

        String custExp = null;

        try {
            when(productExpAsynService.fetchFundRule(any(), any())).thenReturn(CompletableFuture.completedFuture(null));
            when(productExpAsynService.fetchFundHoliday(any(), anyString())).thenReturn(CompletableFuture.completedFuture(null));
            when(productExpAsynService.fetchCustomerExp(any(), any())).thenReturn(CompletableFuture.completedFuture(null));
            when(productExpAsynService.fetchCommonConfigByModule(anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(null));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        CompletableFuture<FundRuleBody> fetchFundRule = productExpAsynService.fetchFundRule(any(), any());
        CompletableFuture<FundHolidayBody> fetchFundHoliday = productExpAsynService.fetchFundHoliday(any(), anyString());
        CompletableFuture<String> fetchCustomerExp = productExpAsynService.fetchCustomerExp(any(), anyString());
        CompletableFuture<List<CommonData>> fetchCommonConfigByModule = productExpAsynService.fetchCommonConfigByModule(anyString(), anyString());

        CompletableFuture.allOf(fetchFundRule, fetchFundHoliday, fetchCustomerExp, fetchCommonConfigByModule);
        FundRuleBody fundRuleBodyCom = fetchFundRule.get();
        FundHolidayBody fundHolidayBodyCom = fetchFundHoliday.get();
        String customerExp = fetchCustomerExp.get();
        List<CommonData> commonDataListCom = fetchCommonConfigByModule.get();
        UtilMap utilMap = new UtilMap();
        Assert.assertNull(custExp);
        FundPaymentDetailRs response = utilMap.mappingPaymentResponse(fundRuleBodyCom, fundHolidayBodyCom, commonDataListCom, customerExp);
        Assert.assertNull(response);
    }

    @Test
    public void isBusinessClose() {
        FfsRequestBody fundAccountRequest = new FfsRequestBody();
        fundAccountRequest.setCrmId("001100000000000000000012025950");
        fundAccountRequest.setFundCode("SCBTMF");
        fundAccountRequest.setFundHouseCode("SCBAM");
        fundAccountRequest.setLanguage("en");
        fundAccountRequest.setProcessFlag("Y");
        fundAccountRequest.setOrderType("1");
        TmbOneServiceResponse<FundRuleBody> responseEntity = new TmbOneServiceResponse<>();

        try {
            ObjectMapper mapper = new ObjectMapper();

            fundRuleBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_rule_payment.json").toFile(), FundRuleBody.class);

            responseEntity.setData(fundRuleBody);
            responseEntity.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundRuleService(any(), any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(responseEntity));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        boolean getFundSummary = productsExpService.isBusinessClose(corrID, fundAccountRequest);
        Assert.assertFalse(getFundSummary);
    }

    @Test
    public void isServiceClose() {
        FundResponse fundResponse = new FundResponse();
        TmbOneServiceResponse<List<CommonData>> responseCommon = new TmbOneServiceResponse<>();
        ResponseEntity<TmbOneServiceResponse<List<CommonData>>> responseCommonRs;
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

        responseCommonRs = commonServiceClient.getCommonConfigByModule(anyString(), anyString());
        fundResponse = productsExpService.isServiceHour(corrID, fundResponse);
        Assert.assertNotNull(responseCommonRs);
        Assert.assertNotNull(fundResponse);
    }

    @Test
    public void isServiceCloseAndStop() {
        FundResponse fundResponse = new FundResponse();
        TmbOneServiceResponse<List<CommonData>> responseCommon = new TmbOneServiceResponse<>();
        ResponseEntity<TmbOneServiceResponse<List<CommonData>>> responseCommonRs = null;
        CommonData commonData = new CommonData();
        CommonTime commonTime = new CommonTime();
        List<CommonData> commonDataList = new ArrayList<>();

        try {
            commonTime.setStart("09:30");
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

        responseCommonRs = commonServiceClient.getCommonConfigByModule(anyString(), anyString());
        fundResponse = productsExpService.isServiceHour(corrID, fundResponse);
        Assert.assertNotNull(responseCommonRs);
        Assert.assertNotNull(fundResponse);
    }

    @Test
    public void isServiceCloseWithException() {
        FundResponse fundResponse = new FundResponse();
        try {
            when(commonServiceClient.getCommonConfigByModule(anyString(), anyString())).thenThrow(MockitoException.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        fundResponse = productsExpService.isServiceHour(corrID, fundResponse);
        Assert.assertNotNull(fundResponse);
    }

    @Test
    public void testgetFundPrePaymentDetailNotfoundException() {
        FundPaymentDetailRq fundPaymentDetailRq = new FundPaymentDetailRq();
        fundPaymentDetailRq.setCrmId("001100000000000000000012025950");
        fundPaymentDetailRq.setFundCode("SCBTMF");
        fundPaymentDetailRq.setFundHouseCode("SCBAM");
        fundPaymentDetailRq.setTranType("1");

        try {
            when(accountRequestClient.callCustomerExpService(any(), anyString())).thenThrow(MockitoException.class);
            when(investmentRequestClient.callInvestmentFundHolidayService(any(), any())).thenThrow(MockitoException.class);
            when(investmentRequestClient.callInvestmentFundRuleService(any(), any())).thenThrow(MockitoException.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FundPaymentDetailRs serviceRes = productsExpService.getFundPrePaymentDetail(corrID, fundPaymentDetailRq);
        Assert.assertNull(serviceRes);
    }

    @Test
    public void testGetFundAccountDetailException() {
        FundAccountRq fundAccountRequest = new FundAccountRq();
        fundAccountRequest.setFundCode("EEEEEE");
        fundAccountRequest.setServiceType("1");
        fundAccountRequest.setUnitHolderNo("PT000001111");
        fundAccountRequest.setFundHouseCode("TTTTTTT");

        try {
            when(investmentRequestClient.callInvestmentFundRuleService(any(), any())).thenThrow(MockitoException.class);
            when(investmentRequestClient.callInvestmentFundAccDetailService(any(), any())).thenThrow(MockitoException.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FundAccountResponse result = productsExpService.getFundAccountDetail(corrID, fundAccountRequest);
        Assert.assertNull(result);
    }

    @Test
    public void getFundSummaryException() {
        FundSummaryRq fundAccountRequest = new FundSummaryRq();
        fundAccountRequest.setCrmId("001100000000000000000012025950");

        try {
            when(accountRequestClient.getPortList(any(), any())).thenThrow(MockitoException.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FundSummaryBody getFundSummary = productsExpService.getFundSummary(corrID, fundAccountRequest);
        Assert.assertNull(getFundSummary);
    }

    @Test
    public void isBusinessCloseException() {
        FfsRequestBody fundAccountRequest = new FfsRequestBody();
        fundAccountRequest.setCrmId("001100000000000000000012025950");
        fundAccountRequest.setFundCode("SCBTMF");
        fundAccountRequest.setFundHouseCode("SCBAM");
        fundAccountRequest.setLanguage("en");
        fundAccountRequest.setProcessFlag("Y");
        fundAccountRequest.setOrderType("1");

        try {
            when(investmentRequestClient.callInvestmentFundRuleService(any(), any())).thenThrow(MockitoException.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        boolean getFundSummary = productsExpService.isBusinessClose(corrID, fundAccountRequest);
        Assert.assertTrue(getFundSummary);
    }

    @Test
    public void isCASADormantException() {
        FfsRequestBody fundAccountRequest = new FfsRequestBody();
        fundAccountRequest.setCrmId("001100000000000000000012025950");
        fundAccountRequest.setFundCode("SCBTMF");
        fundAccountRequest.setFundHouseCode("SCBAM");
        fundAccountRequest.setLanguage("en");
        fundAccountRequest.setProcessFlag("Y");
        fundAccountRequest.setOrderType("1");

        try {
            when(accountRequestClient.callCustomerExpService(any(), any())).thenThrow(MockitoException.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        boolean getFundSummary = productsExpService.isCASADormant(corrID, fundAccountRequest);
        Assert.assertTrue(getFundSummary);
    }

    @Test
    public void isCustIdExpired() {
        FfsRequestBody fundAccountRequest = new FfsRequestBody();
        fundAccountRequest.setCrmId("001100000000000000000012025950");
        fundAccountRequest.setFundCode("SCBTMF");
        fundAccountRequest.setFundHouseCode("SCBAM");
        fundAccountRequest.setLanguage("en");
        fundAccountRequest.setProcessFlag("Y");
        fundAccountRequest.setOrderType("1");

        try {
            CustGeneralProfileResponse fundHolidayBody;
            ObjectMapper mapper = new ObjectMapper();
            fundHolidayBody = mapper.readValue(Paths.get("src/test/resources/investment/customers_profile.json").toFile(), CustGeneralProfileResponse.class);

            when(productExpAsynService.fetchCustomerProfile(anyString())).thenReturn(CompletableFuture.completedFuture(fundHolidayBody));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        boolean getFundSummary = productsExpService.isCustIDExpired(fundAccountRequest);
        Assert.assertFalse(getFundSummary);
    }

    @Test
    public void getFundFFSAndValidation() {
        FfsRequestBody ffsRequestBody = new FfsRequestBody();
        ffsRequestBody.setFundCode("SCBTMF");
        ffsRequestBody.setFundHouseCode("SCBAM");
        ffsRequestBody.setLanguage("en");
        ffsRequestBody.setCrmId("001100000000000000000012025950");
        ffsRequestBody.setProcessFlag("Y");
        ffsRequestBody.setOrderType("1");

        TmbOneServiceResponse<FundRuleBody> responseEntity = new TmbOneServiceResponse<>();
        TmbOneServiceResponse<List<CommonData>> responseCommon = new TmbOneServiceResponse<>();
        String responseCustomerExp;
        List<CommonData> commonDataList = new ArrayList<>();

        try {
            ObjectMapper mapper = new ObjectMapper();

            fundRuleBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_rule_payment.json").toFile(), FundRuleBody.class);
            responseCustomerExp = new String(Files.readAllBytes(Paths.get("src/test/resources/investment/cc_exp_service.json")), StandardCharsets.UTF_8);

            responseEntity.setData(fundRuleBody);
            responseEntity.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            CommonTime commonTime = new CommonTime();
            commonTime.setStart("06:00");
            commonTime.setEnd("23:00");
            CommonData commonData = new CommonData();
            commonData.setNoneServiceHour(commonTime);
            commonDataList.add(commonData);

            responseCommon.setData(commonDataList);
            responseCommon.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundRuleService(any(), any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(responseEntity));
            when(accountRequestClient.callCustomerExpService(any(), anyString())).thenReturn(responseCustomerExp);
            when(commonServiceClient.getCommonConfigByModule(anyString(), anyString())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(responseCommon));
            mockGetFlatcaResponseFromCustomerSearch();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FfsRsAndValidation serviceRes = productsExpService.getFundFFSAndValidation(corrID, ffsRequestBody);
        Assert.assertNotNull(serviceRes);
    }

    @Test
    public void getFundFFSAndValidationWithError() {
        FfsRequestBody ffsRequestBody = new FfsRequestBody();
        ffsRequestBody.setFundCode("SCBTMF");
        ffsRequestBody.setFundHouseCode("SCBAM");
        ffsRequestBody.setLanguage("en");
        ffsRequestBody.setCrmId("001100000000000000000012025950");
        ffsRequestBody.setProcessFlag("Y");
        ffsRequestBody.setOrderType("1");

        TmbOneServiceResponse<FundRuleBody> responseEntity = new TmbOneServiceResponse<>();
        TmbOneServiceResponse<List<CommonData>> responseCommon = new TmbOneServiceResponse<>();
        String responseCustomerExp;
        List<CommonData> commonDataList = new ArrayList<>();

        try {
            ObjectMapper mapper = new ObjectMapper();

            fundRuleBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_rule_payment.json").toFile(), FundRuleBody.class);
            responseCustomerExp = new String(Files.readAllBytes(Paths.get("src/test/resources/investment/cc_exp_service.json")), StandardCharsets.UTF_8);

            responseEntity.setData(fundRuleBody);
            responseEntity.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            CommonTime commonTime = new CommonTime();
            commonTime.setStart("06:00");
            commonTime.setEnd("23:00");
            CommonData commonData = new CommonData();
            commonData.setNoneServiceHour(commonTime);
            commonDataList.add(commonData);

            responseCommon.setData(commonDataList);
            responseCommon.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundRuleService(any(), any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(responseEntity));
            when(accountRequestClient.callCustomerExpService(any(), anyString())).thenReturn(responseCustomerExp);
            when(commonServiceClient.getCommonConfigByModule(anyString(), anyString())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(responseCommon));
            mockGetFlatcaResponseFromCustomerSearch();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FfsRsAndValidation serviceRes = productsExpService.getFundFFSAndValidation(corrID, ffsRequestBody);
        Assert.assertNotNull(serviceRes);
    }

    @Test
    public void validateAlternativeSellAndSwitch() {
        AlternativeRq alternativeRq = new AlternativeRq();
        alternativeRq.setFundCode("SCBTMF");
        alternativeRq.setFundHouseCode("SCBAM");
        alternativeRq.setCrmId("001100000000000000000012025950");
        alternativeRq.setProcessFlag("Y");
        alternativeRq.setOrderType("1");

        TmbOneServiceResponse<FundRuleBody> responseEntity = new TmbOneServiceResponse<>();
        TmbOneServiceResponse<List<CommonData>> responseCommon = new TmbOneServiceResponse<>();
        TmbOneServiceResponse<SuitabilityInfo> responseResponseEntity = new TmbOneServiceResponse<>();
        String responseCustomerExp;
        SuitabilityInfo suitabilityInfo;
        List<CommonData> commonDataList = new ArrayList<>();

        try {
            ObjectMapper mapper = new ObjectMapper();

            fundRuleBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_rule_payment.json").toFile(), FundRuleBody.class);
            responseCustomerExp = new String(Files.readAllBytes(Paths.get("src/test/resources/investment/cc_exp_service.json")), StandardCharsets.UTF_8);
            suitabilityInfo = mapper.readValue(Paths.get("src/test/resources/investment/suitability.json").toFile(), SuitabilityInfo.class);

            responseEntity.setData(fundRuleBody);
            responseEntity.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            responseResponseEntity.setData(suitabilityInfo);
            responseResponseEntity.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            CommonTime commonTime = new CommonTime();
            commonTime.setStart("06:00");
            commonTime.setEnd("23:00");
            CommonData commonData = new CommonData();
            commonData.setNoneServiceHour(commonTime);
            commonDataList.add(commonData);

            responseCommon.setData(commonDataList);
            responseCommon.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundRuleService(any(), any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(responseEntity));
            when(accountRequestClient.callCustomerExpService(any(), anyString())).thenReturn(responseCustomerExp);
            when(commonServiceClient.getCommonConfigByModule(anyString(), anyString())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(responseCommon));
            when(investmentRequestClient.callInvestmentFundSuitabilityService(any(), any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(responseResponseEntity));
            mockGetFlatcaResponseFromCustomerSearch();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FfsRequestBody ffsRequestBody = new FfsRequestBody();
        ffsRequestBody.setCrmId(alternativeRq.getCrmId());
        FundResponse fundResponse = new FundResponse();

        productsExpService.validateAlternativeSellAndSwitch(corrID, alternativeRq);
        String flatcaFlag = "0";
        fundResponse = productsExpService.validationAlternativeSellAndSwitchFlow(corrID, ffsRequestBody, fundResponse, flatcaFlag);
        Assert.assertNotNull(fundResponse);
    }

    @Test
    public void convertAccountType() {
        String accType = UtilMap.convertAccountType(ProductsExpServiceConstant.ACC_TYPE_SDA);
        Assert.assertEquals(ProductsExpServiceConstant.ACC_TYPE_SAVING, accType);
        String accTypeTw = UtilMap.convertAccountType(ProductsExpServiceConstant.ACC_TYPE_DDA);
        Assert.assertEquals(ProductsExpServiceConstant.ACC_TYPE_CURRENT, accTypeTw);
    }

    @Test
    public void testCreateHeader() {
        Map<String, Object> header = UtilMap.createHeader(corrID, 10, 1);
        Assert.assertNotNull(header);
    }

    @Test
    public void testisCASADormant() {
        String responseCustomerExp = null;
        try {
            responseCustomerExp = new String(Files.readAllBytes(Paths.get("src/test/resources/investment/cc_exp_service.json")), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Assert.assertNotNull(responseCustomerExp);
    }

    @Test
    public void testInsertActivityLog() {
        FfsRequestBody ffsRequestBody = new FfsRequestBody();
        ffsRequestBody.setProcessFlag("N");
        ffsRequestBody.setLanguage("en");
        ffsRequestBody.setFundCode("TMONEY");
        ffsRequestBody.setCrmId("001100000000000000000012025950");

        AlternativeRq alternativeRq = new AlternativeRq();
        alternativeRq.setCrmId(ffsRequestBody.getCrmId());
        alternativeRq.setFundCode(ffsRequestBody.getFundCode());
        alternativeRq.setProcessFlag(ffsRequestBody.getProcessFlag());
        alternativeRq.setUnitHolderNo(ffsRequestBody.getUnitHolderNo());
        alternativeRq.setFundHouseCode(ffsRequestBody.getFundHouseCode());

        ActivityLogs activityLogs = productsExpService.constructActivityLogDataForBuyHoldingFund(corrID,
                ProductsExpServiceConstant.ACTIVITY_LOG_FAILURE,
                ProductsExpServiceConstant.ACTIVITY_TYPE_INVESTMENT_STATUS_TRACKING, alternativeRq);

        Assert.assertNotNull(activityLogs);
    }

    @Test
    public void getFundFFSAndValidationOfShelf() {
        FfsRequestBody ffsRequestBody = new FfsRequestBody();
        ffsRequestBody.setFundCode("AAAAA");
        ffsRequestBody.setFundHouseCode("SCBAM");
        ffsRequestBody.setLanguage("en");
        ffsRequestBody.setCrmId("001100000000000000000012025950");
        ffsRequestBody.setProcessFlag("N");
        ffsRequestBody.setOrderType("1");

        FundRuleRequestBody fundRuleRequestBody = new FundRuleRequestBody();
        fundRuleRequestBody.setFundCode(ffsRequestBody.getFundCode());
        fundRuleRequestBody.setFundHouseCode(ffsRequestBody.getFundHouseCode());
        fundRuleRequestBody.setTranType(ProductsExpServiceConstant.FUND_RULE_TRANS_TYPE);

        TmbOneServiceResponse<FundRuleBody> responseEntity = new TmbOneServiceResponse<>();
        String responseCustomerExp;
        Map<String, String> headers = createHeader(corrID);

        try {
            ObjectMapper mapper = new ObjectMapper();

            fundRuleBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_rule_payment.json").toFile(), FundRuleBody.class);
            responseCustomerExp = new String(Files.readAllBytes(Paths.get("src/test/resources/investment/cc_exp_service.json")), StandardCharsets.UTF_8);

            responseEntity.setData(fundRuleBody);
            responseEntity.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundRuleService(headers, fundRuleRequestBody)).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(responseEntity));
            when(accountRequestClient.callCustomerExpService(headers, "001100000000000000000012025950")).thenReturn(responseCustomerExp);
            mockGetFlatcaResponseFromCustomerSearch();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        boolean isBusClose = productsExpService.isBusinessClose(corrID, ffsRequestBody);
        Assert.assertEquals(false, isBusClose);
        boolean isCASADormant = productsExpService.isCASADormant(corrID, ffsRequestBody);
        Assert.assertEquals(false, isCASADormant);
        FfsRsAndValidation serviceRes = productsExpService.getFundFFSAndValidation(corrID, ffsRequestBody);
        Assert.assertNotNull(serviceRes);
    }

    @Test
    public void getFundList() throws Exception {
        List<FundClassListInfo> fundAccountRs = new ArrayList<>();
        FundClassListInfo fundAccount;
        FundSummaryResponse fundHolidayBody;
        List<CustFavoriteFundData> favoriteFundData = new ArrayList<>();
        CustFavoriteFundData favoriteFundData1 = new CustFavoriteFundData();

        try {
            ObjectMapper mapper = new ObjectMapper();
            fundAccount = mapper.readValue(Paths.get("src/test/resources/investment/fund_list.json").toFile(), FundClassListInfo.class);
            fundHolidayBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_summary_data.json").toFile(), FundSummaryResponse.class);

            fundAccountRs.add(fundAccount);

            favoriteFundData1.setFundCode("AAAA");
            favoriteFundData1.setIsFavorite("N");
            favoriteFundData1.setId("1");
            favoriteFundData1.setCustId("100000023333");

            favoriteFundData.add(favoriteFundData1);

            when(productExpAsynService.fetchFundListInfo(any(), anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(fundAccountRs));
            when(productExpAsynService.fetchFundSummary(any(), any())).thenReturn(CompletableFuture.completedFuture(fundHolidayBody));
            when(productExpAsynService.fetchFundFavorite(any(), anyString())).thenReturn(CompletableFuture.completedFuture(favoriteFundData));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        List<FundClassListInfo> listFund;
        CompletableFuture<List<FundClassListInfo>> fetchFundListInfo =
                productExpAsynService.fetchFundListInfo(any(), anyString(), anyString());
        CompletableFuture<FundSummaryResponse> fetchFundSummary = productExpAsynService.fetchFundSummary(any(), any());
        CompletableFuture<List<CustFavoriteFundData>> fetchFundFavorite = productExpAsynService.fetchFundFavorite(any(), anyString());
        CompletableFuture.allOf(fetchFundListInfo, fetchFundSummary, fetchFundFavorite);

        listFund = fetchFundListInfo.get();
        FundSummaryResponse fundSummaryResponse = fetchFundSummary.get();
        List<CustFavoriteFundData> custFavoriteFundDataList = fetchFundFavorite.get();
        listFund = UtilMap.mappingFollowingFlag(listFund, custFavoriteFundDataList);
        listFund = UtilMap.mappingBoughtFlag(listFund, fundSummaryResponse);

        CacheModel cacheModel = UtilMap.mappingCache("teeeeeeee", "abc");
        Assert.assertNotNull(cacheModel);

        List<String> unitStr = new ArrayList<>();
        unitStr.add("PT0000001111111");
        FundListRq fundListRq = new FundListRq();
        fundListRq.setCrmId("12343455555");
        fundListRq.setUnitHolderNo(unitStr);

        Assert.assertNotNull(listFund);
        List<FundClassListInfo> result = productsExpService.getFundList(corrID, fundListRq);
        Assert.assertNotNull(result);
    }

    @Test
    public void getFundListWithException() {
        try {
            when(productExpAsynService.fetchFundListInfo(any(), anyString(), anyString())).thenReturn(null);
            when(productExpAsynService.fetchFundSummary(any(), any())).thenReturn(null);
            when(productExpAsynService.fetchFundFavorite(any(), anyString())).thenReturn(null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        List<String> unitStr = new ArrayList<>();
        unitStr.add("PT0000001111111");
        FundListRq fundListRq = new FundListRq();
        fundListRq.setCrmId("12343455555");
        fundListRq.setUnitHolderNo(unitStr);

        List<FundClassListInfo> result = productsExpService.getFundList(corrID, fundListRq);
        Assert.assertNotNull(result);
    }

    @Test
    public void should_return_suggest_allocation_dto_when_get_suggest_allocation_given_correlationId_and_crmId() throws Exception {
        String crmId = "00000018592884";
        ObjectMapper mapper = new ObjectMapper();
        String portListReturn = "{\"status\":{\"code\":\"0000\",\"message\":\"success\",\"service\":\"accounts-service\",\"description\":\"success\"},\"data\":{\"saving_accounts\":[{\"appl_code\":\"60\",\"acct_ctrl1\":\"0011\",\"acct_ctrl2\":\"0001\",\"acct_ctrl3\":\"0110\",\"acct_ctrl4\":\"0200\",\"acct_nbr\":\"00001102416367\",\"account_name\":\"MIBITSIE01 LMIB1\",\"product_group_code\":\"SDA\",\"product_code\":\"0225\",\"owner_type\":\"P\",\"relationship_code\":\"PRIIND\",\"account_status\":\"0\",\"current_balance\":1.0335840775E9,\"balance_currency\":\"THB\"},{\"appl_code\":\"60\",\"acct_ctrl1\":\"0011\",\"acct_ctrl2\":\"0001\",\"acct_ctrl3\":\"0110\",\"acct_ctrl4\":\"0200\",\"acct_nbr\":\"00001102416458\",\"account_name\":\"นาย MIBITSIE01 LMIB1\",\"product_group_code\":\"SDA\",\"product_code\":\"0221\",\"owner_type\":\"P\",\"relationship_code\":\"PRIIND\",\"account_status\":\"0\",\"current_balance\":922963.66,\"balance_currency\":\"THB\"},{\"appl_code\":\"60\",\"acct_ctrl1\":\"0011\",\"acct_ctrl2\":\"0001\",\"acct_ctrl3\":\"0110\",\"acct_ctrl4\":\"0200\",\"acct_nbr\":\"00001102416524\",\"account_name\":\"นาย MIBITSIE01 LMIB1\",\"product_group_code\":\"SDA\",\"product_code\":\"0211\",\"owner_type\":\"P\",\"relationship_code\":\"PRIIND\",\"account_status\":\"0\",\"current_balance\":5000.0,\"balance_currency\":\"THB\"},{\"appl_code\":\"60\",\"acct_ctrl1\":\"0011\",\"acct_ctrl2\":\"0001\",\"acct_ctrl3\":\"0110\",\"acct_ctrl4\":\"0300\",\"acct_nbr\":\"00001103318497\",\"account_name\":\"นาย MIBITSIE01 LMIB1\",\"product_group_code\":\"CDA\",\"product_code\":\"0664\",\"owner_type\":\"P\",\"relationship_code\":\"PRIIND\",\"account_status\":\"0\",\"current_balance\":10000.0,\"balance_currency\":\"THB\"}],\"current_accounts\":[],\"loan_accounts\":[],\"trade_finance_accounts\":[],\"treasury_accounts\":[],\"debit_card_accounts\":[],\"merchant_accounts\":[],\"foreign_exchange_accounts\":[],\"mutual_fund_accounts\":[{\"appl_code\":\"97\",\"acct_ctrl1\":\"0011\",\"acct_ctrl2\":\"0000\",\"acct_ctrl3\":\"0000\",\"acct_ctrl4\":\"0000\",\"acct_nbr\":\"PT000000000001829798\",\"product_group_code\":\"MF\",\"product_group_code_ec\":\"0000\",\"product_code\":\"\",\"relationship_code\":\"PRIIND\",\"xps_account_status\":\"BLANK\"},{\"appl_code\":\"97\",\"acct_ctrl1\":\"0011\",\"acct_ctrl2\":\"0000\",\"acct_ctrl3\":\"0000\",\"acct_ctrl4\":\"0000\",\"acct_nbr\":\"PT000000000001829800\",\"product_group_code\":\"MF\",\"product_group_code_ec\":\"0000\",\"product_code\":\"\",\"relationship_code\":\"PRIIND\",\"xps_account_status\":\"BLANK\"}],\"bancassurance_accounts\":[],\"other_accounts\":[]}}";
        when(accountRequestClient.getPortList(any(), anyString())).thenReturn(portListReturn);
        FundSummaryBody fundSummaryBody = mapper.readValue(Paths.get("src/test/resources/investment/fund/invest_fundsummary_for_suggestallocation_data.json").toFile(), FundSummaryBody.class);
        when(productExpAsynService.fetchFundSummary(any(), any())).thenReturn(CompletableFuture.completedFuture(FundSummaryResponse.builder().body(fundSummaryBody).build()));
        when(productExpAsynService.suitabilityInquiry(any(), anyString())).thenReturn(CompletableFuture.completedFuture(SuitabilityInfo.builder().suitabilityScore("2").build()));
        FundAllocationResponse fundAllocationResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund/suggest_allocation.json").toFile(), FundAllocationResponse.class);
        TmbOneServiceResponse<FundAllocationResponse> response = new TmbOneServiceResponse<>();
        response.setData(fundAllocationResponse);
        when(investmentRequestClient.callInvestmentFundAllocation(any(), any()))
                .thenReturn(ResponseEntity.ok(response));
        SuggestAllocationDTO suggestAllocationDTOMock = mapper.readValue(Paths.get("src/test/resources/investment/fund/suggest_allocation_dto.json").toFile(), SuggestAllocationDTO.class);
        SuggestAllocationDTO suggestAllocationDTO = productsExpService.getSuggestAllocation(corrID, crmId);
        Assert.assertNotNull(suggestAllocationDTO);
        Assert.assertEquals(suggestAllocationDTOMock, suggestAllocationDTO);
    }

    @Test
    public void should_return_null_when_get_suggest_allocation_given_correlationId_and_crmId() {
        String crmId = "00000018592884";
        when(accountRequestClient.getPortList(any(), anyString())).thenThrow(RuntimeException.class);
        SuggestAllocationDTO suggestAllocationDTO = productsExpService.getSuggestAllocation(corrID, crmId);
        Assert.assertNull(suggestAllocationDTO);
    }

    private void mockGetFlatcaResponseFromCustomerSearch() {
        Map<String, String> response = new HashMap<>();
        response.put(ProductsExpServiceConstant.FATCA_FLAG, "0");
        TmbOneServiceResponse<List<CustomerSearchResponse>> customerSearchResponse = new TmbOneServiceResponse<>();
        customerSearchResponse.setData(List.of(CustomerSearchResponse.builder().fatcaFlag("0").build()));
        ResponseEntity<TmbOneServiceResponse<List<CustomerSearchResponse>>> mockResponse = new ResponseEntity<>(customerSearchResponse, HttpStatus.OK);
        when(customerServiceClient.customerSearch(any(), any(), any())).thenReturn(mockResponse);
    }
}