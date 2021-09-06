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
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.buy.request.AlternativeBuyRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.response.servicehour.ValidateServiceHourResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.model.request.fundfactsheet.FundFactSheetRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fundrule.FundRuleRequestBody;
import com.tmb.oneapp.productsexpservice.model.response.fundfactsheet.FundFactSheetResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundfactsheet.FundFactSheetValidationResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.FundPaymentDetailResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleResponse;
import com.tmb.oneapp.productsexpservice.service.productexperience.alternative.AlternativeService;
import com.tmb.oneapp.productsexpservice.service.productexperience.customer.CustomerService;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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

    private FundRuleResponse fundRuleResponse = null;

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
        TmbOneServiceResponse<FundRuleResponse> responseEntity = new TmbOneServiceResponse<>();
        Map<String, String> headers = createHeader(correlationId);

        try {
            ObjectMapper mapper = new ObjectMapper();
            fundRuleResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund_rule_payment.json").toFile(), FundRuleResponse.class);
            responseCustomerExp = new String(Files.readAllBytes(Paths.get("src/test/resources/investment/cc_exp_service.json")), StandardCharsets.UTF_8);

            responseEntity.setData(fundRuleResponse);
            responseEntity.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundRuleService(headers, fundRuleRequestBody)).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(responseEntity));
            when(accountRequestClient.getAccountList(headers, "001100000000000000000012025950")).thenReturn(responseCustomerExp);
            mockGetFatcaResponseFromCustomerSearch();
            mockSuccessAllAlternative();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FundFactSheetValidationResponse serviceRes = productsExpService.validateAlternativeBuyFlow(correlationId, crmId, fundFactSheetRequestBody);
        Assert.assertNotNull(serviceRes);
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
        TmbOneServiceResponse<FundRuleResponse> responseEntity = new TmbOneServiceResponse<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            fundRuleResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund_rule_close.json").toFile(), FundRuleResponse.class);
            responseEntity.setData(fundRuleResponse);
            responseEntity.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundRuleService(any(), any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(responseEntity));
            when(accountRequestClient.getAccountList(any(), anyString())).thenReturn(responseCustomerExp);
            mockGetFatcaResponseFromCustomerSearch();
            mockSuccessAllAlternative();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FundFactSheetValidationResponse serviceRes = productsExpService.validateAlternativeBuyFlow(correlationId, crmId, fundFactSheetRequestBody);
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

        TmbOneServiceResponse<FundRuleResponse> responseEntity = new TmbOneServiceResponse<>();
        String responseCustomerExp;
        Map<String, String> headers = createHeader(correlationId);

        try {
            ObjectMapper mapper = new ObjectMapper();
            fundRuleResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund_rule_payment.json").toFile(), FundRuleResponse.class);
            responseCustomerExp = new String(Files.readAllBytes(Paths.get("src/test/resources/investment/account_dormant.json")), StandardCharsets.UTF_8);

            responseEntity.setData(fundRuleResponse);
            responseEntity.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundRuleService(headers, fundRuleRequestBody)).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(responseEntity));
            when(accountRequestClient.getAccountList(headers, "001100000000000000000012025950")).thenReturn(responseCustomerExp);
            mockSuccessAllAlternative();
            mockGetFatcaResponseFromCustomerSearch();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        boolean isBusClose = productsExpService.isCASADormant(correlationId, crmId);
        Assert.assertEquals(true, isBusClose);
        FundFactSheetValidationResponse serviceRes = productsExpService.validateAlternativeBuyFlow(correlationId, crmId, fundFactSheetRequestBody);
        Assert.assertNotNull(serviceRes);
    }

    private void mockGetFatcaResponseFromCustomerSearch() {
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

        TmbOneServiceResponse<FundRuleResponse> responseEntity = new TmbOneServiceResponse<>();
        TmbOneServiceResponse<FundFactSheetResponse> responseFfs = new TmbOneServiceResponse<>();
        String responseCustomerExp;
        FundFactSheetResponse fundFactSheetResponse;
        Map<String, String> headers = createHeader(correlationId);

        try {
            ObjectMapper mapper = new ObjectMapper();
            fundRuleResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund_rule_payment.json").toFile(), FundRuleResponse.class);
            responseCustomerExp = new String(Files.readAllBytes(Paths.get("src/test/resources/investment/cc_exp_service.json")), StandardCharsets.UTF_8);
            fundFactSheetResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund_factsheet.json").toFile(), FundFactSheetResponse.class);

            responseEntity.setData(fundRuleResponse);
            responseEntity.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            responseFfs.setData(fundFactSheetResponse);
            responseFfs.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundRuleService(headers, fundRuleRequestBody)).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(responseEntity));
            when(accountRequestClient.getAccountList(headers, "001100000000000000000012025950")).thenReturn(responseCustomerExp);
            when(investmentRequestClient.callInvestmentFundFactSheetService(headers, ffsRequest)).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(responseFfs));
            mockGetFatcaResponseFromCustomerSearch();
            mockSuccessAllAlternative();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FundFactSheetValidationResponse serviceRes = productsExpService.validateAlternativeBuyFlow(correlationId, crmId, fundFactSheetRequestBody);
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
        TmbOneServiceResponse<FundRuleResponse> responseEntity = new TmbOneServiceResponse<>();
        TmbOneServiceResponse<FundFactSheetResponse> responseFfs = new TmbOneServiceResponse<>();
        Map<String, String> headers = createHeader(correlationId);

        try {
            ObjectMapper mapper = new ObjectMapper();
            fundRuleResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund_rule_payment.json").toFile(), FundRuleResponse.class);
            responseCustomerExp = new String(Files.readAllBytes(Paths.get("src/test/resources/investment/cc_exp_service.json")), StandardCharsets.UTF_8);

            responseEntity.setData(fundRuleResponse);
            responseEntity.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            responseFfs.setData(null);
            responseFfs.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundRuleService(headers, fundRuleRequestBody)).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(responseEntity));
            when(accountRequestClient.getAccountList(headers, "001100000000000000000012025950")).thenReturn(responseCustomerExp);
            when(investmentRequestClient.callInvestmentFundFactSheetService(headers, ffsRequest)).thenThrow(MockitoException.class);
            mockGetFatcaResponseFromCustomerSearch();
            mockSuccessAllAlternative();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FundFactSheetValidationResponse serviceRes = productsExpService.validateAlternativeBuyFlow(correlationId, crmId, fundFactSheetRequestBody);
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

        AlternativeBuyRequest alternativeBuyRequest = new AlternativeBuyRequest();
        alternativeBuyRequest.setFundCode(fundFactSheetRequestBody.getFundCode());
        alternativeBuyRequest.setProcessFlag(fundFactSheetRequestBody.getProcessFlag());
        alternativeBuyRequest.setUnitHolderNumber(fundFactSheetRequestBody.getUnitHolderNumber());
        alternativeBuyRequest.setFundHouseCode(fundFactSheetRequestBody.getFundHouseCode());

        ActivityLogs activityLogs = productsExpService.constructActivityLogDataForBuyHoldingFund(correlationId,
                crmId,
                ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_STATUS_TRACKING,
                ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_STATUS_TRACKING, alternativeBuyRequest);

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

        AlternativeBuyRequest alternativeBuyRequest = new AlternativeBuyRequest();
        alternativeBuyRequest.setFundCode(fundFactSheetRequestBody.getFundCode());
        alternativeBuyRequest.setProcessFlag(fundFactSheetRequestBody.getProcessFlag());
        alternativeBuyRequest.setUnitHolderNumber(fundFactSheetRequestBody.getUnitHolderNumber());
        alternativeBuyRequest.setFundHouseCode(fundFactSheetRequestBody.getFundHouseCode());

        ActivityLogs activityLogs = productsExpService.constructActivityLogDataForBuyHoldingFund(correlationId,
                crmId,
                ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_STATUS_TRACKING,
                ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_STATUS_TRACKING, alternativeBuyRequest);
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

        AlternativeBuyRequest alternativeBuyRequest = new AlternativeBuyRequest();
        alternativeBuyRequest.setFundCode(fundFactSheetRequestBody.getFundCode());
        alternativeBuyRequest.setProcessFlag(fundFactSheetRequestBody.getProcessFlag());
        alternativeBuyRequest.setUnitHolderNumber(fundFactSheetRequestBody.getUnitHolderNumber());
        alternativeBuyRequest.setFundHouseCode(fundFactSheetRequestBody.getFundHouseCode());

        ActivityLogs activityLogs = productsExpService.constructActivityLogDataForBuyHoldingFund(correlationId,
                crmId,
                ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_FAILURE,
                ProductsExpServiceConstant.ACTIVITY_LOG_INVESTMENT_STATUS_TRACKING, alternativeBuyRequest);

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

    private TmbStatus mockTmbStatusError(String code, String message, String desc) {
        TmbStatus tmbStatus = new TmbStatus();
        tmbStatus.setCode(code);
        tmbStatus.setDescription(desc);
        tmbStatus.setMessage(message);
        return tmbStatus;
    }

    private ValidateServiceHourResponse mockTmbStatusWithTimeSuccess(String code, String message, String desc) {
        ValidateServiceHourResponse validateServiceHourResponse = new ValidateServiceHourResponse();
        validateServiceHourResponse.setCode(code);
        validateServiceHourResponse.setDescription(desc);
        validateServiceHourResponse.setMessage(message);
        return validateServiceHourResponse;
    }

    private void mockSuccessAllAlternative() {
        when(alternativeService.validateServiceHour(any(), any())).thenReturn(mockTmbStatusWithTimeSuccess(ProductsExpServiceConstant.SUCCESS_CODE, null, null));
        when(alternativeService.validateDateNotOverTwentyYearOld(any(), any())).thenReturn(mockTmbStatusError(ProductsExpServiceConstant.SUCCESS_CODE, null, null));
        when(alternativeService.validateCasaAccountActiveOnce(any(), any())).thenReturn(mockTmbStatusError(ProductsExpServiceConstant.SUCCESS_CODE, null, null));
        when(alternativeService.validateFatcaFlagNotValid(any(), any())).thenReturn(mockTmbStatusError(ProductsExpServiceConstant.SUCCESS_CODE, null, null));
        when(alternativeService.validateKycAndIdCardExpire(any(), any(), any())).thenReturn(mockTmbStatusError(ProductsExpServiceConstant.SUCCESS_CODE, null, null));
        when(alternativeService.validateIdentityAssuranceLevel(any(), any())).thenReturn(mockTmbStatusError(ProductsExpServiceConstant.SUCCESS_CODE, null, null));
        when(alternativeService.validateNationality(any(), any(), any(), any())).thenReturn(mockTmbStatusError(ProductsExpServiceConstant.SUCCESS_CODE, null, null));
        when(alternativeService.validateCustomerRiskLevel(any(), any(), any(), anyBoolean(), anyBoolean())).thenReturn(mockTmbStatusError(ProductsExpServiceConstant.SUCCESS_CODE, null, null));
    }
}
