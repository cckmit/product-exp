package com.tmb.oneapp.productsexpservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.kafka.service.KafkaProducerService;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.AccountRequestClient;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerExpServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.activitylog.ActivityLogs;
import com.tmb.oneapp.productsexpservice.model.request.alternative.AlternativeRq;
import com.tmb.oneapp.productsexpservice.model.request.fundffs.FfsRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fundrule.FundRuleRequestBody;
import com.tmb.oneapp.productsexpservice.model.response.accdetail.FundAccountRs;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FfsResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FfsRsAndValidation;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.FundPaymentDetailRs;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleBody;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.exceptions.base.MockitoException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProductExpServiceCloseTest {

    InvestmentRequestClient investmentRequestClient;
    ProductsExpService productsExpService;
    AccountRequestClient accountRequestClient;
    KafkaProducerService kafkaProducerService;
    CommonServiceClient commonServiceClient;
    ProductExpAsynService productExpAsynService;
    CustomerExpServiceClient customerExpServiceClient;

    private FundRuleBody fundRuleBody = null;
    private final String corrID = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
    private final String  topicName = "activity";

    @BeforeEach
    public void setUp() {
        investmentRequestClient = mock(InvestmentRequestClient.class);
        accountRequestClient = mock(AccountRequestClient.class);
        productsExpService = mock(ProductsExpService.class);
        kafkaProducerService = mock(KafkaProducerService.class);
        commonServiceClient = mock(CommonServiceClient.class);
        productExpAsynService =  mock(ProductExpAsynService.class);
        productsExpService = new ProductsExpService(investmentRequestClient,accountRequestClient,kafkaProducerService, commonServiceClient,
                productExpAsynService, topicName,customerExpServiceClient);

    }

    private Map<String, String> createHeader(String correlationId){
        Map<String, String> invHeaderReqParameter = new HashMap<>();
        invHeaderReqParameter.put(ProductsExpServiceConstant.HEADER_CORRELATION_ID, correlationId);
        invHeaderReqParameter.put(ProductsExpServiceConstant.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return invHeaderReqParameter;
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
        String responseCustomerExp = null;
        Map<String, String> headers = createHeader(corrID);
        Map<String, Object> invHeaderReqParameter = UtilMap.createHeader(corrID, 139, 0);
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
        FfsRsAndValidation serviceRes = productsExpService.getFundFFSAndValidation(corrID, ffsRequestBody);
        Assert.assertNotNull(serviceRes);
    }

    @Test
    public void getFundFFSAndValidationBusinesClose()  {
        FfsRequestBody ffsRequestBody = new FfsRequestBody();
        ffsRequestBody.setFundCode("ABSM");
        ffsRequestBody.setFundHouseCode("ABERDEEN");
        ffsRequestBody.setLanguage("en");
        ffsRequestBody.setCrmId("001100000000000000000012025950");
        ffsRequestBody.setProcessFlag("Y");
        ffsRequestBody.setOrderType("1");

        FundRuleRequestBody fundRuleRequestBody = new FundRuleRequestBody();
        fundRuleRequestBody.setFundCode(ffsRequestBody.getFundCode());
        fundRuleRequestBody.setFundHouseCode(ffsRequestBody.getFundHouseCode());
        fundRuleRequestBody.setTranType(ProductsExpServiceConstant.FUND_RULE_TRANS_TYPE);

        TmbOneServiceResponse<FundRuleBody> responseEntity = new TmbOneServiceResponse<>();
        String responseCustomerExp = null;
        try {
            ObjectMapper mapper = new ObjectMapper();

            fundRuleBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_rule_close.json").toFile(), FundRuleBody.class);

            responseEntity.setData(fundRuleBody);
            responseEntity.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));


            when(investmentRequestClient.callInvestmentFundRuleService(any(), any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(responseEntity));
            when(accountRequestClient.callCustomerExpService(any(), anyString())).thenReturn(responseCustomerExp);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        boolean isBusClose = productsExpService.isBusinessClose(corrID, ffsRequestBody);
        Assert.assertEquals(true, isBusClose);
        FfsRsAndValidation serviceRes = productsExpService.getFundFFSAndValidation(corrID, ffsRequestBody);
        Assert.assertNotNull(serviceRes);
    }

    @Test
    public void getFundFFSAndValidationCASADormant() throws Exception {
        FfsRequestBody ffsRequestBody = new FfsRequestBody();
        ffsRequestBody.setFundCode("ABSM");
        ffsRequestBody.setFundHouseCode("ABERDEEN");
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
        Map<String, Object> invHeaderReqParameter = UtilMap.createHeader(corrID, 139, 0);
        try {
            ObjectMapper mapper = new ObjectMapper();


            fundRuleBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_rule_payment.json").toFile(), FundRuleBody.class);
            responseCustomerExp = new String(Files.readAllBytes(Paths.get("src/test/resources/investment/account_dormant.json")), StandardCharsets.UTF_8);

            responseEntity.setData(fundRuleBody);
            responseEntity.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));



            when(investmentRequestClient.callInvestmentFundRuleService(headers, fundRuleRequestBody)).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(responseEntity));
            when(accountRequestClient.callCustomerExpService(headers, "001100000000000000000012025950")).thenReturn(responseCustomerExp);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        boolean isBusClose = productsExpService.isCASADormant(corrID, ffsRequestBody);
        Assert.assertEquals(true, isBusClose);
        FfsRsAndValidation serviceRes = productsExpService.getFundFFSAndValidation(corrID, ffsRequestBody);
        Assert.assertNotNull(serviceRes);
    }

    @Test
    public void getFundFFSAndValidationSuccess()  {
        FfsRequestBody ffsRequestBody = new FfsRequestBody();
        ffsRequestBody.setFundCode("ABSM");
        ffsRequestBody.setFundHouseCode("ABERDEEN");
        ffsRequestBody.setLanguage("en");
        ffsRequestBody.setCrmId("001100000000000000000012025950");
        ffsRequestBody.setProcessFlag("N");
        ffsRequestBody.setOrderType("1");

        FundRuleRequestBody fundRuleRequestBody = new FundRuleRequestBody();
        fundRuleRequestBody.setFundCode(ffsRequestBody.getFundCode());
        fundRuleRequestBody.setFundHouseCode(ffsRequestBody.getFundHouseCode());
        fundRuleRequestBody.setTranType(ProductsExpServiceConstant.FUND_RULE_TRANS_TYPE);

        FfsRequestBody ffsRequest = new FfsRequestBody();
        ffsRequest.setLanguage("en");
        ffsRequest.setFundCode("ABSM");
        ffsRequest.setFundHouseCode("ABERDEEN");
        ffsRequest.setOrderType("1");
        ffsRequest.setProcessFlag("Y");



        TmbOneServiceResponse<FundRuleBody> responseEntity = new TmbOneServiceResponse<>();
        TmbOneServiceResponse<FfsResponse> responseFfs = new TmbOneServiceResponse<>();
        String responseCustomerExp = null;
        Map<String, String> headers = createHeader(corrID);
        Map<String, Object> invHeaderReqParameter = UtilMap.createHeader(corrID, 139, 0);
        FfsResponse ffsResponse = null;
        try {
            ObjectMapper mapper = new ObjectMapper();

            fundRuleBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_rule_payment.json").toFile(), FundRuleBody.class);
            responseCustomerExp = new String(Files.readAllBytes(Paths.get("src/test/resources/investment/cc_exp_service.json")), StandardCharsets.UTF_8);
            ffsResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund_factsheet.json").toFile(), FfsResponse.class);

            responseEntity.setData(fundRuleBody);
            responseEntity.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));


            responseFfs.setData(ffsResponse);
            responseFfs.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundRuleService(headers, fundRuleRequestBody)).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(responseEntity));
            when(accountRequestClient.callCustomerExpService(headers, "001100000000000000000012025950")).thenReturn(responseCustomerExp);
            when(investmentRequestClient.callInvestmentFundFactSheetService(headers, ffsRequest)).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(responseFfs));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FfsRsAndValidation serviceRes = productsExpService.getFundFFSAndValidation(corrID, ffsRequestBody);
        Assert.assertNotNull(serviceRes);
    }

    @Test
    public void getFundFFSAndValidationEMpty() throws Exception {
        FfsRequestBody ffsRequestBody = new FfsRequestBody();
        ffsRequestBody.setFundCode("ABSM");
        ffsRequestBody.setFundHouseCode("ABERDEEN");
        ffsRequestBody.setLanguage("en");
        ffsRequestBody.setCrmId("001100000000000000000012025950");
        ffsRequestBody.setProcessFlag("N");
        ffsRequestBody.setOrderType("1");

        FundRuleRequestBody fundRuleRequestBody = new FundRuleRequestBody();
        fundRuleRequestBody.setFundCode(ffsRequestBody.getFundCode());
        fundRuleRequestBody.setFundHouseCode(ffsRequestBody.getFundHouseCode());
        fundRuleRequestBody.setTranType(ProductsExpServiceConstant.FUND_RULE_TRANS_TYPE);

        FfsRequestBody ffsRequest = new FfsRequestBody();
        ffsRequest.setLanguage("en");
        ffsRequest.setFundCode("ABSM");
        ffsRequest.setFundHouseCode("ABERDEEN");
        ffsRequest.setOrderType("1");
        ffsRequest.setProcessFlag("Y");


        TmbOneServiceResponse<FundRuleBody> responseEntity = new TmbOneServiceResponse<>();
        TmbOneServiceResponse<FfsResponse> responseFfs = new TmbOneServiceResponse<>();
        String responseCustomerExp = null;
        Map<String, String> headers = createHeader(corrID);
        Map<String, Object> invHeaderReqParameter = UtilMap.createHeader(corrID, 139, 0);

        try {
            ObjectMapper mapper = new ObjectMapper();

            fundRuleBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_rule_payment.json").toFile(), FundRuleBody.class);
            responseCustomerExp = new String(Files.readAllBytes(Paths.get("src/test/resources/investment/cc_exp_service.json")), StandardCharsets.UTF_8);

            responseEntity.setData(fundRuleBody);
            responseEntity.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));



            responseFfs.setData(null);
            responseFfs.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundRuleService(headers, fundRuleRequestBody)).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(responseEntity));
            when(accountRequestClient.callCustomerExpService(headers, "001100000000000000000012025950")).thenReturn(responseCustomerExp);
            when(investmentRequestClient.callInvestmentFundFactSheetService(headers, ffsRequest)).thenThrow(MockitoException.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FfsRsAndValidation serviceRes = productsExpService.getFundFFSAndValidation(corrID, ffsRequestBody);
        Assert.assertNotNull(serviceRes);
    }

    @Test
    public void testSaveActivityLogs() throws Exception {
        FfsRequestBody ffsRequestBody = new FfsRequestBody();
        ffsRequestBody.setProcessFlag("N");
        ffsRequestBody.setLanguage("en");
        ffsRequestBody.setFundCode("TMONEY");
        ffsRequestBody.setCrmId("001100000000000000000012025950");
        ffsRequestBody.setUnitHolderNo("PT000000000000587870");

        AlternativeRq alternativeRq = new AlternativeRq();
        alternativeRq.setCrmId(ffsRequestBody.getCrmId());
        alternativeRq.setFundCode(ffsRequestBody.getFundCode());
        alternativeRq.setProcessFlag(ffsRequestBody.getProcessFlag());
        alternativeRq.setUnitHolderNo(ffsRequestBody.getUnitHolderNo());
        alternativeRq.setFundHouseCode(ffsRequestBody.getFundHouseCode());

        ActivityLogs activityLogs = productsExpService.constructActivityLogDataForBuyHoldingFund(corrID,
                ProductsExpServiceConstant.ACTIVITY_TYPE_INVESTMENT_STATUS_TRACKING,
                ProductsExpServiceConstant.ACTIVITY_TYPE_INVESTMENT_STATUS_TRACKING, alternativeRq );

        productsExpService.logactivity(activityLogs);
        Assert.assertNotNull(activityLogs);

    }

    @Test
    public void testSaveActivityLogsNullUnit() throws Exception {
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

        productsExpService.logactivity(activityLogs);
        Assert.assertNotNull(activityLogs);

    }

    @Test
    public void validateTMBResponse() throws Exception {
        UtilMap utilMap = new UtilMap();
        FundAccountRs fundAccountRs = utilMap.validateTMBResponse(null, null, null);
        Assert.assertNull(fundAccountRs);
    }

    @Test
    public void mappingPaymentResponse() throws Exception {
        UtilMap utilMap = new UtilMap();
        FundPaymentDetailRs fundAccountRs = utilMap.mappingPaymentResponse(null, null, null, null);
        Assert.assertNull(fundAccountRs);
    }

    @Test
    public void isCASADormant() throws Exception {
        UtilMap utilMap = new UtilMap();
        boolean  fundAccountRs = utilMap.isCASADormant(null);
        Assert.assertTrue(fundAccountRs);
    }

    @Test
    public void convertAccountType() throws Exception {
        UtilMap utilMap = new UtilMap();
        String fundAccountRs = utilMap.convertAccountType("AAAA");
        Assert.assertEquals("",fundAccountRs);
    }

    @Test
    public void isCASADormantException() throws Exception {
        UtilMap utilMap = new UtilMap();
        boolean  fundAccountRs = utilMap.isCASADormant("data not found");
        Assert.assertFalse(fundAccountRs);
    }


    @Test
    public void isBusinessCloseException() throws Exception {
        UtilMap utilMap = new UtilMap();
        boolean  fundAccountRs = utilMap.isBusinessClose("yyy", "xxx");
        Assert.assertFalse(fundAccountRs);
    }

    @Test
    public void addColonDateFormat() throws Exception {
        UtilMap utilMap = new UtilMap();
        String  fundAccountRs = utilMap.deleteColonDateFormat("06:00");
        Assert.assertEquals("0600",fundAccountRs);
    }

    @Test
    public void addColonDateFormatStart() throws Exception {
        UtilMap utilMap = new UtilMap();
        String  fundAccountRs = utilMap.deleteColonDateFormat("23:30");
        Assert.assertEquals("2330",fundAccountRs);
    }

    @Test
    public void addColonDateFormatFail() throws Exception {
        UtilMap utilMap = new UtilMap();
        String  fundAccountRs = utilMap.deleteColonDateFormat("");
        Assert.assertEquals("",fundAccountRs);
    }


}
