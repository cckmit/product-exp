package com.tmb.oneapp.productsexpservice.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.kafka.service.KafkaProducerService;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.AccountRequestClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.activitylog.ActivityLogs;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundSummaryBody;
import com.tmb.oneapp.productsexpservice.model.request.accdetail.FundAccountRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.accdetail.FundAccountRq;
import com.tmb.oneapp.productsexpservice.model.request.fundffs.FfsRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fundpayment.FundPaymentDetailRq;
import com.tmb.oneapp.productsexpservice.model.request.fundrule.FundRuleRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fundsummary.FundSummaryRq;
import com.tmb.oneapp.productsexpservice.model.request.stmtrequest.OrderStmtByPortRq;
import com.tmb.oneapp.productsexpservice.model.response.accdetail.FundAccountDetail;
import com.tmb.oneapp.productsexpservice.model.response.accdetail.FundAccountRs;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FfsRsAndValidation;
import com.tmb.oneapp.productsexpservice.model.response.fundholiday.FundHolidayBody;
import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundListPage;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.FundPaymentDetailRs;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleBody;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleInfoList;
import com.tmb.oneapp.productsexpservice.model.response.investment.AccDetailBody;
import com.tmb.oneapp.productsexpservice.model.response.investment.DetailFund;
import com.tmb.oneapp.productsexpservice.model.response.investment.Order;
import com.tmb.oneapp.productsexpservice.model.response.investment.OrderToBeProcess;
import com.tmb.oneapp.productsexpservice.model.response.stmtresponse.StatementResponse;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.apache.kafka.common.protocol.types.Field;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.mockito.Mockito.*;

public class ProductExpServiceTest {

    InvestmentRequestClient investmentRequestClient;
    ProductsExpService productsExpService;
    AccountRequestClient accountRequestClient;
    KafkaProducerService kafkaProducerService;
    CustomerServiceClient customerServiceClient;

    private final String success_code = "0000";
    private final String notfund_code = "0009";
    private AccDetailBody accDetailBody = null;
    private FundRuleBody fundRuleBody = null;
    private final String corrID = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
    private final String investmentStartTime = "08:00";
    private final String investmentEndTime = "08:30";
    private final String  topicName = "activity";

    @BeforeEach
    public void setUp() {
        investmentRequestClient = mock(InvestmentRequestClient.class);
        accountRequestClient = mock(AccountRequestClient.class);
        productsExpService = mock(ProductsExpService.class);
        kafkaProducerService = mock(KafkaProducerService.class);
        customerServiceClient = mock(CustomerServiceClient.class);
        productsExpService = new ProductsExpService(investmentRequestClient,accountRequestClient,kafkaProducerService, customerServiceClient,
                 investmentStartTime, investmentEndTime, topicName);

    }


    private void initAccDetailBody(){
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
        accDetailBody.setOrderToBeProcess(orderToBeProcess);

    }

    private void initFundRuleBody(){
        fundRuleBody = new FundRuleBody();
        List<FundRuleInfoList> fundRuleInfoList = new ArrayList<>();
        FundRuleInfoList list = new FundRuleInfoList();
        list.setFundCode("TTTTTT");
        list.setProcessFlag("N");
        fundRuleInfoList.add(list);
        fundRuleBody.setFundRuleInfoList(fundRuleInfoList);
    }

    private Map<String, String> createHeader(String correlationId){
        Map<String, String> invHeaderReqParameter = new HashMap<>();
        invHeaderReqParameter.put(ProductsExpServiceConstant.HEADER_CORRELATION_ID, correlationId);
        invHeaderReqParameter.put(ProductsExpServiceConstant.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return invHeaderReqParameter;
    }


    @Test
    public void testGetFundAccdetailAndFundRule() throws Exception {

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


        FundRuleRequestBody fundRuleRequestBody = new FundRuleRequestBody();
        fundRuleRequestBody.setTranType("2");
        fundRuleRequestBody.setFundHouseCode("TTTTT");
        fundRuleRequestBody.setFundCode("EEEEE");

        OrderStmtByPortRq orderStmtByPortRq = new OrderStmtByPortRq();
        orderStmtByPortRq.setPortfolioNumber("PT0000000032534");
        orderStmtByPortRq.setRowEnd("5");
        orderStmtByPortRq.setRowStart("1");
        orderStmtByPortRq.setFundCode("EEEE");

        TmbOneServiceResponse<AccDetailBody> oneServiceResponse = new TmbOneServiceResponse<>();
        TmbOneServiceResponse<FundRuleBody> oneServiceResponseBody = new TmbOneServiceResponse<>();
        TmbOneServiceResponse<StatementResponse> serviceResponseStmt = new TmbOneServiceResponse<>();
        ResponseEntity<TmbOneServiceResponse<FundRuleBody>> responseResponseEntity = null;
        ResponseEntity<TmbOneServiceResponse<StatementResponse>> responseResponseEntity1 = null;
        StatementResponse statementResponse = null;

        try {
            ObjectMapper mapper = new ObjectMapper();
            accDetailBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_account_detail.json").toFile(), AccDetailBody.class);
            oneServiceResponse.setData(accDetailBody);
            oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            fundRuleBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_rule.json").toFile(), FundRuleBody.class);

            statementResponse = mapper.readValue(Paths.get("src/test/resources/investment/investment_stmt.json").toFile(), StatementResponse.class);

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
        responseResponseEntity = investmentRequestClient.callInvestmentFundRuleService(createHeader(corrID), fundRuleRequestBody);
        responseEntity = investmentRequestClient.callInvestmentFundAccDetailService(createHeader(corrID), fundAccountRq);
        responseResponseEntity1 = investmentRequestClient.callInvestmentStmtByPortService(createHeader(corrID),orderStmtByPortRq);
        UtilMap map = new UtilMap();
        FundAccountRs rs = map.validateTMBResponse(responseEntity, responseResponseEntity, responseResponseEntity1);
        Assert.assertNotNull(responseResponseEntity);
        Assert.assertNotNull(responseEntity);
        FundAccountRs result = productsExpService.getFundAccountDetail(corrID, fundAccountRequest);
        Assert.assertNull(result);
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
        Assert.assertEquals(HttpStatus.OK.value(),responseEntity.getStatusCodeValue());
        Assert.assertEquals("FFFFF",responseEntity.getBody().getData().getDetailFund().getFundHouseCode());
        Assert.assertEquals(2,responseEntity.getBody().getData().getOrderToBeProcess().getOrder()
                .size());
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
        Assert.assertEquals(HttpStatus.OK,fundRuleResponseEntity.getStatusCode());
        Assert.assertEquals("TESEQDSSFX",fundRuleResponseEntity.getBody().getData().getFundRuleInfoList().get(0).getFundCode());
        Assert.assertEquals("TFUND",fundRuleResponseEntity.getBody().getData().getFundRuleInfoList().get(0).getFundHouseCode());
        Assert.assertEquals("20200413",fundRuleResponseEntity.getBody().getData().getFundRuleInfoList().get(0).getTranStartDate());
        Assert.assertEquals("3",fundRuleResponseEntity.getBody().getData().getFundRuleInfoList().get(0).getOrderType());
        Assert.assertEquals("3",fundRuleResponseEntity.getBody().getData().getFundRuleInfoList().get(0).getAllotType());
        Assert.assertEquals("06",fundRuleResponseEntity.getBody().getData().getFundRuleInfoList().get(0).getRiskRate());
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

            serviceResponseStmt.setData(null);
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

        TmbOneServiceResponse<FundRuleBody> responseEntity = new TmbOneServiceResponse<>();
        TmbOneServiceResponse<FundHolidayBody> responseFundHoliday = new TmbOneServiceResponse<>();
        String responseCustomerExp = null;

        ResponseEntity<TmbOneServiceResponse<FundRuleBody>> fundRuleEntity = null;
        ResponseEntity<TmbOneServiceResponse<FundHolidayBody>> hilodayEntity = null;
        String custExp = null;

        FundHolidayBody fundHolidayBody = null;
        FundRuleBody fundRuleBody = null;
        FundPaymentDetailRs fundPaymentDetailRs = null;


        try {
            ObjectMapper mapper = new ObjectMapper();
            fundRuleBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_rule_payment.json").toFile(), FundRuleBody.class);
            fundHolidayBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_holiday.json").toFile(), FundHolidayBody.class);

            responseCustomerExp = new String(Files.readAllBytes(Paths.get("src/test/resources/investment/cc_exp_service.json")), StandardCharsets.UTF_8);

            responseEntity.setData(fundRuleBody);
            responseEntity.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            responseFundHoliday.setData(fundHolidayBody);
            responseFundHoliday.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(accountRequestClient.callCustomerExpService(any(), anyString())).thenReturn(responseCustomerExp);
            when(investmentRequestClient.callInvestmentFundHolidayService(any(), any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(responseFundHoliday));
            when(investmentRequestClient.callInvestmentFundRuleService(any(), any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(responseEntity));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        UtilMap utilMap = new UtilMap();
        custExp = accountRequestClient.callCustomerExpService(any(), anyString());
        fundRuleEntity = investmentRequestClient.callInvestmentFundRuleService(any(), any());
        hilodayEntity = investmentRequestClient.callInvestmentFundHolidayService(any(), any());

        Assert.assertEquals(HttpStatus.OK, fundRuleEntity.getStatusCode());
        Assert.assertEquals(HttpStatus.OK, hilodayEntity.getStatusCode());
        Assert.assertNotNull(custExp);
        FundPaymentDetailRs response = utilMap.mappingPaymentResponse(fundRuleEntity, hilodayEntity, custExp);
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
        String custExp = null;

        try {

            responseCustomerExp = null;

            when(accountRequestClient.callCustomerExpService(any(), anyString())).thenReturn(responseCustomerExp);
            when(investmentRequestClient.callInvestmentFundHolidayService(any(), any())).thenReturn(null);
            when(investmentRequestClient.callInvestmentFundRuleService(any(), any())).thenReturn(null);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        UtilMap utilMap = new UtilMap();
        custExp = accountRequestClient.callCustomerExpService(any(), anyString());
        fundRuleEntity = investmentRequestClient.callInvestmentFundRuleService(any(), any());
        hilodayEntity = investmentRequestClient.callInvestmentFundHolidayService(any(), any());

        Assert.assertNull(custExp);
        FundPaymentDetailRs response = utilMap.mappingPaymentResponse(fundRuleEntity, hilodayEntity, custExp);
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

        FundSummaryRq fundAccountRequest = new FundSummaryRq  ();
        fundAccountRequest.setCrmId("001100000000000000000012025950");

        try {
            when(accountRequestClient.getPortList(any(),any())).thenThrow(MockitoException.class);
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
    public void isServiceCloseException() throws Exception {

        FfsRequestBody fundAccountRequest = new FfsRequestBody();
        fundAccountRequest.setCrmId("001100000000000000000012025950");
        fundAccountRequest.setFundCode("SCBTMF");
        fundAccountRequest.setFundHouseCode("SCBAM");
        fundAccountRequest.setLanguage("en");
        fundAccountRequest.setProcessFlag("Y");
        fundAccountRequest.setOrderType("1");

        try {
            when(investmentRequestClient.callInvestmentFundListInfoService(any())).thenThrow(MockitoException.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        boolean getFundSummary = productsExpService.isOfShelfFund(corrID, fundAccountRequest);
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
    public void getFundFFSAndValidation() throws Exception {
        FfsRequestBody ffsRequestBody = new FfsRequestBody();
        ffsRequestBody.setFundCode("SCBTMF");
        ffsRequestBody.setFundHouseCode("SCBAM");
        ffsRequestBody.setLanguage("en");
        ffsRequestBody.setCrmId("001100000000000000000012025950");
        ffsRequestBody.setProcessFlag("Y");
        ffsRequestBody.setOrderType("1");


        TmbOneServiceResponse<FundRuleBody> responseEntity = new TmbOneServiceResponse<>();
        TmbOneServiceResponse<FundListPage> responseList = new TmbOneServiceResponse<>();
        String responseCustomerExp = null;
        FundListPage fundListPage = null;
        try {
            ObjectMapper mapper = new ObjectMapper();

            fundRuleBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_rule_payment.json").toFile(), FundRuleBody.class);
            fundListPage = mapper.readValue(Paths.get("src/test/resources/investment/fund_list_info.json").toFile(), FundListPage.class);
            responseCustomerExp = new String(Files.readAllBytes(Paths.get("src/test/resources/investment/cc_exp_service.json")), StandardCharsets.UTF_8);

            responseEntity.setData(fundRuleBody);
            responseEntity.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            responseList.setData(fundListPage);
            responseList.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));


            when(investmentRequestClient.callInvestmentFundRuleService(any(), any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(responseEntity));
            when(investmentRequestClient.callInvestmentFundListInfoService(any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(responseList));
            when(accountRequestClient.callCustomerExpService(any(), anyString())).thenReturn(responseCustomerExp);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FfsRsAndValidation serviceRes = productsExpService.getFundFFSAndValidation(corrID, ffsRequestBody);
        Assert.assertNotNull(serviceRes);
    }

    @Test
    public void convertAccountType() throws Exception {
        String accType = UtilMap.convertAccountType(ProductsExpServiceConstant.ACC_TYPE_SDA);
        Assert.assertEquals(ProductsExpServiceConstant.ACC_TYPE_SAVING, accType);
        String accTypeTw = UtilMap.convertAccountType(ProductsExpServiceConstant.ACC_TYPE_DDA);
        Assert.assertEquals(ProductsExpServiceConstant.ACC_TYPE_CURRENT, accTypeTw);
    }

    @Test
    public void testisServiceClose() throws Exception {
        boolean isClose = UtilMap.isOfShelfCheck(null,null);
        Assert.assertTrue(isClose);
    }

    @Test
    public void testisBusinessClose() throws Exception {
        boolean isClose = UtilMap.isBusinessClose("08:01","23:00");
        Assert.assertFalse(isClose);
    }

    @Test
    public void testCreateHeader() throws Exception {
        Map<String, Object> header = UtilMap.createHeader(corrID, 10,1);
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

        ActivityLogs activityLogs = productsExpService.constructActivityLogDataForBuyHoldingFund(corrID,
                ProductsExpServiceConstant.FAILED_MESSAGE, ProductsExpServiceConstant.ACTIVITY_LOG_FAILURE,
                ProductsExpServiceConstant.ACTIVITY_TYPE_INVESTMENT_STATUS_TRACKING, ffsRequestBody);

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
        TmbOneServiceResponse<FundListPage> responseList = new TmbOneServiceResponse<>();
        String responseCustomerExp = null;
        Map<String, String> headers = createHeader(corrID);
        Map<String, Object> invHeaderReqParameter = UtilMap.createHeader(corrID, 139, 0);
        FundListPage fundListPage = null;
        try {
            ObjectMapper mapper = new ObjectMapper();

            fundListPage = mapper.readValue(Paths.get("src/test/resources/investment/fund_list_info.json").toFile(), FundListPage.class);
            fundRuleBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_rule_payment.json").toFile(), FundRuleBody.class);
            responseCustomerExp = new String(Files.readAllBytes(Paths.get("src/test/resources/investment/cc_exp_service.json")), StandardCharsets.UTF_8);

            responseEntity.setData(fundRuleBody);
            responseEntity.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            responseList.setData(fundListPage);
            responseList.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundRuleService(headers, fundRuleRequestBody)).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(responseEntity));
            when(accountRequestClient.callCustomerExpService(headers, "001100000000000000000012025950")).thenReturn(responseCustomerExp);
            when(investmentRequestClient.callInvestmentFundListInfoService(invHeaderReqParameter)).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(responseList));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        boolean isBusClose = productsExpService.isBusinessClose(corrID, ffsRequestBody);
        Assert.assertEquals(false, isBusClose);
        boolean isCASADormant = productsExpService.isCASADormant(corrID, ffsRequestBody);
        Assert.assertEquals(false, isCASADormant);
        boolean isServiceClose = productsExpService.isOfShelfFund(corrID, ffsRequestBody);
        Assert.assertEquals(true, isServiceClose);
        FfsRsAndValidation serviceRes = productsExpService.getFundFFSAndValidation(corrID, ffsRequestBody);
        Assert.assertNotNull(serviceRes);
    }


}


