package com.tmb.oneapp.productsexpservice.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.kafka.service.KafkaProducerService;
import com.tmb.common.model.*;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.AccountRequestClient;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerExpServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
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
import com.tmb.oneapp.productsexpservice.model.response.accdetail.FundAccountRs;
import com.tmb.oneapp.productsexpservice.model.response.fundfavorite.CustFavoriteFundData;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FfsRsAndValidation;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FundResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundholiday.FundHolidayBody;
import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundClassListInfo;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.FundPaymentDetailRs;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleBody;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleInfoList;
import com.tmb.oneapp.productsexpservice.model.response.investment.AccDetailBody;
import com.tmb.oneapp.productsexpservice.model.response.investment.DetailFund;
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

    InvestmentRequestClient investmentRequestClient;
    ProductsExpService productsExpService;
    AccountRequestClient accountRequestClient;
    KafkaProducerService kafkaProducerService;
    CommonServiceClient commonServiceClient;
    ProductExpAsynService productExpAsynService;
    CustomerExpServiceClient customerExpServiceClient;

    private final String success_code = "0000";
    private final String notfund_code = "0009";
    private AccDetailBody accDetailBody = null;
    private FundRuleBody fundRuleBody = null;
    private final String corrID = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
    private final String topicName = "activity";

    @BeforeEach
    public void setUp() {
        investmentRequestClient = mock(InvestmentRequestClient.class);
        accountRequestClient = mock(AccountRequestClient.class);
        productsExpService = mock(ProductsExpService.class);
        kafkaProducerService = mock(KafkaProducerService.class);
        commonServiceClient = mock(CommonServiceClient.class);
        productExpAsynService = mock(ProductExpAsynService.class);
        productsExpService = new ProductsExpService(investmentRequestClient, accountRequestClient, kafkaProducerService, commonServiceClient, productExpAsynService, topicName, customerExpServiceClient);

    }


    private void initAccDetailBody() {
        accDetailBody = new AccDetailBody();
        DetailFund detailFund = new DetailFund();
        detailFund.setFundHouseCode("TTTTT");
        detailFund.setFundHouseCode("EEEEE");
        accDetailBody.setDetailFund(detailFund);

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
        invHeaderReqParameter.put(ProductsExpServiceConstant.HEADER_CORRELATION_ID, correlationId);
        invHeaderReqParameter.put(ProductsExpServiceConstant.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return invHeaderReqParameter;
    }


    @Test
    public void testGetFundAccdetailAndFundRule() throws Exception {
        StatementResponse statementResponse = null;
        FundAccountRq fundAccountRq = new FundAccountRq();
        fundAccountRq.setFundHouseCode("ABCC");
        fundAccountRq.setTranType("2");
        fundAccountRq.setFundCode("ABCC");
        fundAccountRq.setServiceType("1");
        fundAccountRq.setUnitHolderNo("PT0000000000123");

        try {
            ObjectMapper mapper = new ObjectMapper();
            accDetailBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_account_detail.json").toFile(), AccDetailBody.class);
            fundRuleBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_rule.json").toFile(), FundRuleBody.class);
            statementResponse = mapper.readValue(Paths.get("src/test/resources/investment/investment_stmt.json").toFile(), StatementResponse.class);

            when(productExpAsynService.fetchFundAccDetail(any(), any())).thenReturn(CompletableFuture.completedFuture(accDetailBody));
            when(productExpAsynService.fetchFundRule(any(), any())).thenReturn(CompletableFuture.completedFuture(fundRuleBody));
            when(productExpAsynService.fetchStmtByPort(any(), any())).thenReturn(CompletableFuture.completedFuture(statementResponse));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        CompletableFuture<AccDetailBody> fetchFundAccDetail = productExpAsynService.fetchFundAccDetail(any(), any());
        CompletableFuture<FundRuleBody> fetchFundRule = productExpAsynService.fetchFundRule(any(), any());
        CompletableFuture<StatementResponse> fetchStmtByPort = productExpAsynService.fetchStmtByPort(any(), any());
        CompletableFuture.allOf(fetchFundAccDetail, fetchFundRule, fetchStmtByPort);

        AccDetailBody accDetailBody = fetchFundAccDetail.get();
        FundRuleBody fundRuleBody = fetchFundRule.get();
        StatementResponse statementRs = fetchStmtByPort.get();

        FundAccountRs fundAccountRs = UtilMap.validateTMBResponse(accDetailBody, fundRuleBody, statementRs);

        Assert.assertNotNull(fundAccountRs);
        Assert.assertNotNull(accDetailBody);
        Assert.assertNotNull(statementRs);
        FundAccountRs result = productsExpService.getFundAccountDetail(corrID, fundAccountRq);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetFundAccdetail() throws Exception {

        FundAccountRq fundAccountRequest = new FundAccountRq();
        fundAccountRequest.setFundCode("EEEEEE");
        fundAccountRequest.setServiceType("1");
        fundAccountRequest.setUnitHolderNo("PT000001111");
        fundAccountRequest.setFundHouseCode("TTTTTTT");

        ResponseEntity<TmbOneServiceResponse<AccDetailBody>> responseEntity = null;
        FundAccountRequestBody fundAccountRq = new FundAccountRequestBody();
        fundAccountRq.setUnitHolderNo("PT000000001");
        fundAccountRq.setServiceType("1");
        fundAccountRq.setFundCode("DDD");

        TmbOneServiceResponse<AccDetailBody> oneServiceResponse = new TmbOneServiceResponse<>();


        try {
            ObjectMapper mapper = new ObjectMapper();
            accDetailBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_account_detail.json").toFile(), AccDetailBody.class);

            oneServiceResponse.setData(accDetailBody);
            oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundAccDetailService(createHeader(corrID), fundAccountRq)).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        responseEntity = investmentRequestClient.callInvestmentFundAccDetailService(createHeader(corrID), fundAccountRq);
        Assert.assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCodeValue());
        Assert.assertEquals("FFFFF", responseEntity.getBody().getData().getDetailFund().getFundHouseCode());
        Assert.assertNotNull(responseEntity.getBody().getData().getDetailFund());
    }


    @Test
    public void testGetFundRule() throws Exception {

        ResponseEntity<TmbOneServiceResponse<AccDetailBody>> responseEntity = null;
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
    public void testGetFundAccdetailNull() throws Exception {
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


        TmbOneServiceResponse<AccDetailBody> oneServiceResponse = new TmbOneServiceResponse<>();
        TmbOneServiceResponse<FundRuleBody> oneServiceResponseBody = new TmbOneServiceResponse<>();
        TmbOneServiceResponse<StatementResponse> serviceResponseStmt = new TmbOneServiceResponse<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            statementResponse = mapper.readValue(Paths.get("src/test/resources/investment/investment_stmt.json").toFile(), StatementResponse.class);

            oneServiceResponse.setData(accDetailBody);
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
            when(investmentRequestClient.callInvestmentStmtByPortService(createHeader(corrID), orderStmtByPortRq)).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(serviceResponseStmt));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FundAccountRs result = productsExpService.getFundAccountDetail(corrID, fundAccountRequest);
        Assert.assertNull(result);
        UtilMap utilMap = new UtilMap();
        FundAccountDetail fundAccountDetailrs = utilMap.mappingResponse(accDetailBody, fundRuleBody, statementResponse);
        Assert.assertNotNull(fundAccountDetailrs);
    }


    @Test
    public void testGetFundAccdetailServiceNull() throws Exception {
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

        TmbOneServiceResponse<AccDetailBody> oneServiceResponse = new TmbOneServiceResponse<>();
        TmbOneServiceResponse<FundRuleBody> oneServiceResponseBody = new TmbOneServiceResponse<>();

        try {
            oneServiceResponse.setData(accDetailBody);
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

        FundAccountRs result = productsExpService.getFundAccountDetail(corrID, fundAccountRequest);
        Assert.assertNull(result);
    }

    @Test
    public void testgetFundPrePaymentDetail() throws Exception {
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


        String responseCustomerExp = null;
        String custExp = null;
        FundHolidayBody fundHolidayBody = null;
        FundRuleBody fundRuleBody = null;
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
    public void testgetFundPrePaymentDetailNotfound() throws Exception {
        FundPaymentDetailRq fundPaymentDetailRq = new FundPaymentDetailRq();
        fundPaymentDetailRq.setCrmId("001100000000000000000012025950");
        fundPaymentDetailRq.setFundCode("SCBTMF");
        fundPaymentDetailRq.setFundHouseCode("SCBAM");
        fundPaymentDetailRq.setTranType("1");

        String responseCustomerExp = null;

        ResponseEntity<TmbOneServiceResponse<FundRuleBody>> fundRuleEntity = null;
        ResponseEntity<TmbOneServiceResponse<FundHolidayBody>> hilodayEntity = null;
        ResponseEntity<TmbOneServiceResponse<List<CommonData>>> commonRs = null;
        String custExp = null;

        try {

            responseCustomerExp = null;

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
    public void isBusinessClose() throws Exception {

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
    public void isServiceClose() throws Exception {

        FundResponse fundResponse = new FundResponse();
        TmbOneServiceResponse<List<CommonData>> responseCommon = new TmbOneServiceResponse<>();
        ResponseEntity<TmbOneServiceResponse<List<CommonData>>> responseCommonRs = null;
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
    public void isServiceCloseAndStop() throws Exception {

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
    public void isServiceCloseWithException() throws Exception {
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
    public void testgetFundPrePaymentDetailNotfoundException() throws Exception {
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
    public void testGetFundAccdetailException() throws Exception {

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
        FundAccountRs result = productsExpService.getFundAccountDetail(corrID, fundAccountRequest);
        Assert.assertNull(result);
    }

    @Test
    public void getFundSummaryException() throws Exception {

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
    public void isBusinessCloseException() throws Exception {

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
    public void isCASADormantException() throws Exception {

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
    public void isCustIDExpired() throws Exception {

        FfsRequestBody fundAccountRequest = new FfsRequestBody();
        fundAccountRequest.setCrmId("001100000000000000000012025950");
        fundAccountRequest.setFundCode("SCBTMF");
        fundAccountRequest.setFundHouseCode("SCBAM");
        fundAccountRequest.setLanguage("en");
        fundAccountRequest.setProcessFlag("Y");
        fundAccountRequest.setOrderType("1");

        try {
        	CustGeneralProfileResponse fundHolidayBody = null;
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
    public void getFundFFSAndValidation() throws Exception {
        FfsRequestBody ffsRequestBody = new FfsRequestBody();
        ffsRequestBody.setFundCode("SCBTMF");
        ffsRequestBody.setFundHouseCode("SCBAM");
        ffsRequestBody.setLanguage("en");
        ffsRequestBody.setCrmId("001100000000000000000012025950");
        ffsRequestBody.setProcessFlag("Y");
        ffsRequestBody.setOrderType("1");


        TmbOneServiceResponse<FundRuleBody> responseEntity = new TmbOneServiceResponse<>();
        TmbOneServiceResponse<List<CommonData>> responseCommon = new TmbOneServiceResponse<>();
        String responseCustomerExp = null;
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FfsRsAndValidation serviceRes = productsExpService.getFundFFSAndValidation(corrID, ffsRequestBody);
        Assert.assertNotNull(serviceRes);
    }

    @Test
    public void getFundFFSAndValidationWithError() throws Exception {
        FfsRequestBody ffsRequestBody = new FfsRequestBody();
        ffsRequestBody.setFundCode("SCBTMF");
        ffsRequestBody.setFundHouseCode("SCBAM");
        ffsRequestBody.setLanguage("en");
        ffsRequestBody.setCrmId("001100000000000000000012025950");
        ffsRequestBody.setProcessFlag("Y");
        ffsRequestBody.setOrderType("1");


        TmbOneServiceResponse<FundRuleBody> responseEntity = new TmbOneServiceResponse<>();
        TmbOneServiceResponse<List<CommonData>> responseCommon = new TmbOneServiceResponse<>();
        String responseCustomerExp = null;
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FfsRsAndValidation serviceRes = productsExpService.getFundFFSAndValidation(corrID, ffsRequestBody);
        Assert.assertNotNull(serviceRes);
    }

    @Test
    public void validateAlternativeSellAndSwitch() throws Exception {
        AlternativeRq alternativeRq = new AlternativeRq();
        alternativeRq.setFundCode("SCBTMF");
        alternativeRq.setFundHouseCode("SCBAM");
        alternativeRq.setCrmId("001100000000000000000012025950");
        alternativeRq.setProcessFlag("Y");
        alternativeRq.setOrderType("1");


        TmbOneServiceResponse<FundRuleBody> responseEntity = new TmbOneServiceResponse<>();
        TmbOneServiceResponse<List<CommonData>> responseCommon = new TmbOneServiceResponse<>();
        TmbOneServiceResponse<SuitabilityInfo> responseResponseEntity = new TmbOneServiceResponse<>();
        String responseCustomerExp = null;
        SuitabilityInfo suitabilityInfo = null;
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FfsRequestBody ffsRequestBody = new FfsRequestBody();
        ffsRequestBody.setCrmId(alternativeRq.getCrmId());
        FundResponse fundResponse = new FundResponse();

        productsExpService.validateAlternativeSellAndSwitch(corrID, alternativeRq);
        fundResponse = productsExpService.validationAlternativeSellAndSwitchFlow(corrID, ffsRequestBody, fundResponse);
        Assert.assertNotNull(fundResponse);
    }

    @Test
    public void convertAccountType() throws Exception {
        String accType = UtilMap.convertAccountType(ProductsExpServiceConstant.ACC_TYPE_SDA);
        Assert.assertEquals(ProductsExpServiceConstant.ACC_TYPE_SAVING, accType);
        String accTypeTw = UtilMap.convertAccountType(ProductsExpServiceConstant.ACC_TYPE_DDA);
        Assert.assertEquals(ProductsExpServiceConstant.ACC_TYPE_CURRENT, accTypeTw);
    }


    @Test
    public void testCreateHeader() throws Exception {
        Map<String, Object> header = UtilMap.createHeader(corrID, 10, 1);
        Assert.assertNotNull(header);
    }

    @Test
    public void testisCASADormant() throws Exception {
        String responseCustomerExp = null;
        try {
            responseCustomerExp = new String(Files.readAllBytes(Paths.get("src/test/resources/investment/cc_exp_service.json")), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Assert.assertNotNull(responseCustomerExp);
    }

    @Test
    public void testinsertActivityLog() throws Exception {
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
    public void getFundFFSAndValidationOfShelf() throws Exception {
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
        String responseCustomerExp = null;
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
        FundClassListInfo fundAccount = null;
        FundSummaryResponse fundHolidayBody = null;
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
        List<FundClassListInfo> listFund = new ArrayList<>();
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
    public void getFundListWithException() throws Exception {

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


}


