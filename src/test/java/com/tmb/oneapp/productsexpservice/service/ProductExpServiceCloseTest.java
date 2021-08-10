package com.tmb.oneapp.productsexpservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.kafka.service.KafkaProducerService;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.AccountRequestClient;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.activitylog.ActivityLogs;
import com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.response.FundAccountResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.request.AlternativeRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.model.request.fundfactsheet.FundFactSheetRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fundrule.FundRuleRequestBody;
import com.tmb.oneapp.productsexpservice.model.response.fundfactsheet.FundFactSheetResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundfactsheet.FundFactSheetValidationResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.FundPaymentDetailResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleBody;
import com.tmb.oneapp.productsexpservice.service.productexperience.alternative.AlternativeService;
import com.tmb.oneapp.productsexpservice.service.productexperience.customer.CustomerService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductExpServiceCloseTest {

    @Mock
    private AccountRequestClient accountRequestClient;

    @Mock
    private InvestmentRequestClient investmentRequestClient;

    @Mock
    private AlternativeService alternativeService;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private CustomerService customerService;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private ProductsExpService productsExpService;

    private FundRuleBody fundRuleBody = null;

    private final String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
    private final String crmId = "001100000000000000000000028365";

    @BeforeEach
    public void setUp() {

    }

    private Map<String, String> createHeader(String correlationId) {
        Map<String, String> invHeaderReqParameter = new HashMap<>();
        invHeaderReqParameter.put(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID, correlationId);
        invHeaderReqParameter.put(ProductsExpServiceConstant.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return invHeaderReqParameter;
    }

    @Test
    public void getFundFactSheetAndValidationOfShelf() {
        FundFactSheetRequestBody fundFactSheetRequestBody = new FundFactSheetRequestBody();
        fundFactSheetRequestBody.setFundCode("AAAAA");
        fundFactSheetRequestBody.setFundHouseCode("SCBAM");
        fundFactSheetRequestBody.setLanguage("en");
        fundFactSheetRequestBody.setCrmId("001100000000000000000012025950");
        fundFactSheetRequestBody.setProcessFlag("N");
        fundFactSheetRequestBody.setOrderType("1");

        FundRuleRequestBody fundRuleRequestBody = new FundRuleRequestBody();
        fundRuleRequestBody.setFundCode(fundFactSheetRequestBody.getFundCode());
        fundRuleRequestBody.setFundHouseCode(fundFactSheetRequestBody.getFundHouseCode());
        fundRuleRequestBody.setTranType(ProductsExpServiceConstant.FUND_RULE_TRANS_TYPE);

        String responseCustomerExp;
        TmbOneServiceResponse<FundRuleBody> responseEntity = new TmbOneServiceResponse<>();
        Map<String, String> headers = createHeader(correlationId);

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
            bypassAlternative();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FundFactSheetValidationResponse serviceRes = productsExpService.getFundFactSheetValidation(correlationId, crmId, fundFactSheetRequestBody);
        Assert.assertNotNull(serviceRes);
    }

    public void bypassAlternative(){
        TmbStatus tmbStatusSuccess = TmbStatusUtil.successStatus();
        when(alternativeService.validateCustomerRiskLevel(any(),any(),any())).thenReturn(tmbStatusSuccess);
        when(alternativeService.validateIdentityAssuranceLevel(any(),any())).thenReturn(tmbStatusSuccess);
    }



    @Test
    public void getFundFactSheetAndValidationOfBusinessClose() {
        FundFactSheetRequestBody fundFactSheetRequestBody = new FundFactSheetRequestBody();
        fundFactSheetRequestBody.setFundCode("ABSM");
        fundFactSheetRequestBody.setFundHouseCode("ABERDEEN");
        fundFactSheetRequestBody.setLanguage("en");
        fundFactSheetRequestBody.setCrmId("001100000000000000000012025950");
        fundFactSheetRequestBody.setProcessFlag("Y");
        fundFactSheetRequestBody.setOrderType("1");

        FundRuleRequestBody fundRuleRequestBody = new FundRuleRequestBody();
        fundRuleRequestBody.setFundCode(fundFactSheetRequestBody.getFundCode());
        fundRuleRequestBody.setFundHouseCode(fundFactSheetRequestBody.getFundHouseCode());
        fundRuleRequestBody.setTranType(ProductsExpServiceConstant.FUND_RULE_TRANS_TYPE);

        String responseCustomerExp = null;
        TmbOneServiceResponse<FundRuleBody> responseEntity = new TmbOneServiceResponse<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            fundRuleBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_rule_close.json").toFile(), FundRuleBody.class);
            responseEntity.setData(fundRuleBody);
            responseEntity.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundRuleService(any(), any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(responseEntity));
            when(accountRequestClient.callCustomerExpService(any(), anyString())).thenReturn(responseCustomerExp);
            mockGetFlatcaResponseFromCustomerSearch();
            bypassAlternative();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FundFactSheetValidationResponse serviceRes = productsExpService.getFundFactSheetValidation(correlationId, crmId, fundFactSheetRequestBody);
        Assert.assertNotNull(serviceRes);
    }

    @Test
    public void getFundFactSheetAndValidationOfCASADormant() {
        FundFactSheetRequestBody fundFactSheetRequestBody = new FundFactSheetRequestBody();
        fundFactSheetRequestBody.setFundCode("ABSM");
        fundFactSheetRequestBody.setFundHouseCode("ABERDEEN");
        fundFactSheetRequestBody.setLanguage("en");
        fundFactSheetRequestBody.setProcessFlag("N");
        fundFactSheetRequestBody.setOrderType("1");

        FundRuleRequestBody fundRuleRequestBody = new FundRuleRequestBody();
        fundRuleRequestBody.setFundCode(fundFactSheetRequestBody.getFundCode());
        fundRuleRequestBody.setFundHouseCode(fundFactSheetRequestBody.getFundHouseCode());
        fundRuleRequestBody.setTranType(ProductsExpServiceConstant.FUND_RULE_TRANS_TYPE);

        TmbOneServiceResponse<FundRuleBody> responseEntity = new TmbOneServiceResponse<>();
        String responseCustomerExp;
        Map<String, String> headers = createHeader(correlationId);

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

            mockGetFlatcaResponseFromCustomerSearch();
            bypassAlternative();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        boolean isBusClose = productsExpService.isCASADormant(correlationId, crmId);
        Assert.assertEquals(true, isBusClose);
        FundFactSheetValidationResponse serviceRes = productsExpService.getFundFactSheetValidation(correlationId, crmId, fundFactSheetRequestBody);
        Assert.assertNotNull(serviceRes);
    }

    private void mockGetFlatcaResponseFromCustomerSearch() {
        CustomerSearchResponse response = CustomerSearchResponse.builder().fatcaFlag("0").build();
        when(customerService.getCustomerInfo(any(), any())).thenReturn(response);
    }

    @Test
    public void getFundFactSheetAndValidationSuccess() {
        FundFactSheetRequestBody fundFactSheetRequestBody = new FundFactSheetRequestBody();
        fundFactSheetRequestBody.setFundCode("ABSM");
        fundFactSheetRequestBody.setFundHouseCode("ABERDEEN");
        fundFactSheetRequestBody.setLanguage("en");
        fundFactSheetRequestBody.setCrmId("001100000000000000000012025950");
        fundFactSheetRequestBody.setProcessFlag("N");
        fundFactSheetRequestBody.setOrderType("1");

        FundRuleRequestBody fundRuleRequestBody = new FundRuleRequestBody();
        fundRuleRequestBody.setFundCode(fundFactSheetRequestBody.getFundCode());
        fundRuleRequestBody.setFundHouseCode(fundFactSheetRequestBody.getFundHouseCode());
        fundRuleRequestBody.setTranType(ProductsExpServiceConstant.FUND_RULE_TRANS_TYPE);

        FundFactSheetRequestBody ffsRequest = new FundFactSheetRequestBody();
        ffsRequest.setLanguage("en");
        ffsRequest.setFundCode("ABSM");
        ffsRequest.setFundHouseCode("ABERDEEN");
        ffsRequest.setOrderType("1");
        ffsRequest.setProcessFlag("Y");

        TmbOneServiceResponse<FundRuleBody> responseEntity = new TmbOneServiceResponse<>();
        TmbOneServiceResponse<FundFactSheetResponse> responseFfs = new TmbOneServiceResponse<>();
        String responseCustomerExp;
        FundFactSheetResponse fundFactSheetResponse;
        Map<String, String> headers = createHeader(correlationId);

        try {
            ObjectMapper mapper = new ObjectMapper();
            fundRuleBody = mapper.readValue(Paths.get("src/test/resources/investment/fund_rule_payment.json").toFile(), FundRuleBody.class);
            responseCustomerExp = new String(Files.readAllBytes(Paths.get("src/test/resources/investment/cc_exp_service.json")), StandardCharsets.UTF_8);
            fundFactSheetResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund_factsheet.json").toFile(), FundFactSheetResponse.class);

            responseEntity.setData(fundRuleBody);
            responseEntity.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            responseFfs.setData(fundFactSheetResponse);
            responseFfs.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundRuleService(headers, fundRuleRequestBody)).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(responseEntity));
            when(accountRequestClient.callCustomerExpService(headers, "001100000000000000000012025950")).thenReturn(responseCustomerExp);
            when(investmentRequestClient.callInvestmentFundFactSheetService(headers, ffsRequest)).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(responseFfs));
            mockGetFlatcaResponseFromCustomerSearch();
            bypassAlternative();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FundFactSheetValidationResponse serviceRes = productsExpService.getFundFactSheetValidation(correlationId, crmId, fundFactSheetRequestBody);
        Assert.assertNotNull(serviceRes);
    }

    @Test
    public void getFundFactSheetAndValidationEmpty() {
        FundFactSheetRequestBody fundFactSheetRequestBody = new FundFactSheetRequestBody();
        fundFactSheetRequestBody.setFundCode("ABSM");
        fundFactSheetRequestBody.setFundHouseCode("ABERDEEN");
        fundFactSheetRequestBody.setLanguage("en");
        fundFactSheetRequestBody.setCrmId("001100000000000000000012025950");
        fundFactSheetRequestBody.setProcessFlag("N");
        fundFactSheetRequestBody.setOrderType("1");

        FundRuleRequestBody fundRuleRequestBody = new FundRuleRequestBody();
        fundRuleRequestBody.setFundCode(fundFactSheetRequestBody.getFundCode());
        fundRuleRequestBody.setFundHouseCode(fundFactSheetRequestBody.getFundHouseCode());
        fundRuleRequestBody.setTranType(ProductsExpServiceConstant.FUND_RULE_TRANS_TYPE);

        FundFactSheetRequestBody ffsRequest = new FundFactSheetRequestBody();
        ffsRequest.setLanguage("en");
        ffsRequest.setFundCode("ABSM");
        ffsRequest.setFundHouseCode("ABERDEEN");
        ffsRequest.setOrderType("1");
        ffsRequest.setProcessFlag("Y");

        String responseCustomerExp;
        TmbOneServiceResponse<FundRuleBody> responseEntity = new TmbOneServiceResponse<>();
        TmbOneServiceResponse<FundFactSheetResponse> responseFfs = new TmbOneServiceResponse<>();
        Map<String, String> headers = createHeader(correlationId);

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
            mockGetFlatcaResponseFromCustomerSearch();
            bypassAlternative();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FundFactSheetValidationResponse serviceRes = productsExpService.getFundFactSheetValidation(correlationId, crmId, fundFactSheetRequestBody);
        Assert.assertNotNull(serviceRes);
    }

    @Test
    public void testSaveActivityLogs() {
        FundFactSheetRequestBody fundFactSheetRequestBody = new FundFactSheetRequestBody();
        fundFactSheetRequestBody.setProcessFlag("N");
        fundFactSheetRequestBody.setLanguage("en");
        fundFactSheetRequestBody.setFundCode("TMONEY");
        fundFactSheetRequestBody.setCrmId("001100000000000000000012025950");
        fundFactSheetRequestBody.setUnitHolderNumber("PT000000000000587870");

        AlternativeRequest alternativeRequest = new AlternativeRequest();
        alternativeRequest.setFundCode(fundFactSheetRequestBody.getFundCode());
        alternativeRequest.setProcessFlag(fundFactSheetRequestBody.getProcessFlag());
        alternativeRequest.setUnitHolderNumber(fundFactSheetRequestBody.getUnitHolderNumber());
        alternativeRequest.setFundHouseCode(fundFactSheetRequestBody.getFundHouseCode());

        ActivityLogs activityLogs = productsExpService.constructActivityLogDataForBuyHoldingFund(correlationId,
                crmId,
                ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_STATUS_TRACKING,
                ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_STATUS_TRACKING, alternativeRequest);

        productsExpService.logActivity(activityLogs);
        Assert.assertNotNull(activityLogs);
    }

    @Test
    void testCreateLogWithException() throws Exception {
        FundFactSheetRequestBody fundFactSheetRequestBody = new FundFactSheetRequestBody();
        fundFactSheetRequestBody.setProcessFlag("N");
        fundFactSheetRequestBody.setLanguage("en");
        fundFactSheetRequestBody.setFundCode("TMONEY");
        fundFactSheetRequestBody.setCrmId("001100000000000000000012025950");
        fundFactSheetRequestBody.setUnitHolderNumber("PT000000000000587870");

        AlternativeRequest alternativeRequest = new AlternativeRequest();
        alternativeRequest.setFundCode(fundFactSheetRequestBody.getFundCode());
        alternativeRequest.setProcessFlag(fundFactSheetRequestBody.getProcessFlag());
        alternativeRequest.setUnitHolderNumber(fundFactSheetRequestBody.getUnitHolderNumber());
        alternativeRequest.setFundHouseCode(fundFactSheetRequestBody.getFundHouseCode());

        ActivityLogs activityLogs = productsExpService.constructActivityLogDataForBuyHoldingFund(correlationId,
                crmId,
                ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_STATUS_TRACKING,
                ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_STATUS_TRACKING, alternativeRequest);
        doNothing().when(kafkaProducerService).sendMessageAsync(anyString(), any());
        when(mapper.writeValueAsString(anyString())).thenThrow(MockitoException.class);

        productsExpService.logActivity(activityLogs);
        Assert.assertNotNull(activityLogs);
    }

    @Test
    public void testSaveActivityLogsNullUnit() {
        FundFactSheetRequestBody fundFactSheetRequestBody = new FundFactSheetRequestBody();
        fundFactSheetRequestBody.setProcessFlag("N");
        fundFactSheetRequestBody.setLanguage("en");
        fundFactSheetRequestBody.setFundCode("TMONEY");
        fundFactSheetRequestBody.setCrmId("001100000000000000000012025950");

        AlternativeRequest alternativeRequest = new AlternativeRequest();
        alternativeRequest.setFundCode(fundFactSheetRequestBody.getFundCode());
        alternativeRequest.setProcessFlag(fundFactSheetRequestBody.getProcessFlag());
        alternativeRequest.setUnitHolderNumber(fundFactSheetRequestBody.getUnitHolderNumber());
        alternativeRequest.setFundHouseCode(fundFactSheetRequestBody.getFundHouseCode());

        ActivityLogs activityLogs = productsExpService.constructActivityLogDataForBuyHoldingFund(correlationId,
                crmId,
                ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_FAILURE,
                ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_STATUS_TRACKING, alternativeRequest);

        productsExpService.logActivity(activityLogs);
        Assert.assertNotNull(activityLogs);
    }

    @Test
    public void validateTMBResponse() {
        FundAccountResponse fundAccountResponse = UtilMap.validateTMBResponse(null, null, null);
        Assert.assertNull(fundAccountResponse);
    }

    @Test
    public void mappingPaymentResponse() {
        UtilMap utilMap = new UtilMap();
        FundPaymentDetailResponse fundAccountRs = utilMap.mappingPaymentResponse(null, null, null, null);
        Assert.assertNull(fundAccountRs);
    }

    @Test
    public void isCASADormant() {
        boolean fundAccountRs = UtilMap.isCASADormant(null);
        Assert.assertTrue(fundAccountRs);
    }

    @Test
    public void convertAccountType() {
        String fundAccountRs = UtilMap.convertAccountType("AAAA");
        Assert.assertEquals("", fundAccountRs);
    }

    @Test
    public void isCASADormantException() {
        boolean fundAccountRs = UtilMap.isCASADormant("data not found");
        Assert.assertFalse(fundAccountRs);
    }

    @Test
    public void isBusinessCloseException() {
        boolean fundAccountRs = UtilMap.isBusinessClose("06:00", "08:00");
        Assert.assertFalse(fundAccountRs);
    }

    @Test
    public void addColonDateFormat() {
        String fundAccountRs = UtilMap.deleteColonDateFormat("06:00");
        Assert.assertEquals("0600", fundAccountRs);
    }

    @Test
    public void addColonDateFormatStart() {
        String fundAccountRs = UtilMap.deleteColonDateFormat("23:30");
        Assert.assertEquals("2330", fundAccountRs);
    }

    @Test
    public void addColonDateFormatFail() {
        String fundAccountRs = UtilMap.deleteColonDateFormat("");
        Assert.assertEquals("", fundAccountRs);
    }
}
