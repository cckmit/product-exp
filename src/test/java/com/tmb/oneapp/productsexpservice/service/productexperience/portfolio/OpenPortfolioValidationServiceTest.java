package com.tmb.oneapp.productsexpservice.service.productexperience.portfolio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.CommonData;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.activitylog.portfolio.service.OpenPortfolioActivityLogService;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.enums.OpenPortfolioErrorEnums;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.mapper.customer.CustomerInfoMapper;
import com.tmb.oneapp.productsexpservice.model.common.teramandcondition.response.TermAndConditionResponse;
import com.tmb.oneapp.productsexpservice.model.common.teramandcondition.response.TermAndConditionResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.request.OpenPortfolioValidationRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.response.CustomerInfo;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.response.ValidateOpenPortfolioResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FundResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.DepositAccount;
import com.tmb.oneapp.productsexpservice.service.ProductsExpService;
import com.tmb.oneapp.productsexpservice.service.productexperience.account.EligibleDepositAccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OpenPortfolioValidationServiceTest {

    @Mock
    private TMBLogger<OpenPortfolioValidationServiceTest> logger;

    @Mock
    private CustomerServiceClient customerServiceClient;

    @Mock
    private CommonServiceClient commonServiceClient;

    @Mock
    private ProductsExpService productsExpService;

    @Mock
    private EligibleDepositAccountService eligibleDepositAccountService;

    @Mock
    private OpenPortfolioActivityLogService openPortfolioActivityLogService;

    @Mock
    private CustomerInfoMapper customerInfoMapper;

    @InjectMocks
    private OpenPortfolioValidationService openPortfolioValidationService;

    private void mockPassServiceHour() {
        FundResponse fundResponse = new FundResponse();
        fundResponse.setError(false);
        when(productsExpService.isServiceHour(any(), any())).thenReturn(fundResponse);
    }

    private void mockNotPassServiceHour() {
        FundResponse fundResponse = new FundResponse();
        fundResponse.setError(true);
        when(productsExpService.isServiceHour(any(), any())).thenReturn(fundResponse);
    }

    private void mockCommonConfig() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<CommonData> list = new ArrayList<>();
        TmbOneServiceResponse<List<CommonData>> response = new TmbOneServiceResponse<List<CommonData>>();
        list.add(objectMapper.readValue(Paths.get("src/test/resources/investment/common/investment_config.json").toFile(), CommonData.class));
        response.setData(list);
        when(commonServiceClient.getCommonConfig(any(), any())).thenReturn(ResponseEntity.ok(response));
    }

    private void mockCustomerResponse(OpenPortfolioErrorEnums openPortfolioErrorEnums) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        CustomerSearchResponse customerSearchResponse = objectMapper.readValue(Paths.get("src/test/resources/investment/customer/search_customer.json").toFile(), CustomerSearchResponse.class);
        TmbOneServiceResponse<List<CustomerSearchResponse>> oneServiceResponse = new TmbOneServiceResponse<List<CustomerSearchResponse>>();
        oneServiceResponse.setData(List.of(customerSearchResponse));
        ResponseEntity<TmbOneServiceResponse<List<CustomerSearchResponse>>> response = new ResponseEntity<TmbOneServiceResponse<List<CustomerSearchResponse>>>(
                oneServiceResponse, HttpStatus.OK);

        if (openPortfolioErrorEnums != null) {
            response.getBody().getData().get(0).setEkycIdentifyAssuranceLevel("300");
            switch (openPortfolioErrorEnums.getCode()) {
                case "2000025":
                    response.getBody().getData().get(0).setBirthDate("2010-07-08");
                    break;
                case "2000018":

                    if (openPortfolioErrorEnums.getMsg().equals(OpenPortfolioErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getMsg())) {
                        response.getBody().getData().get(0).setCustomerRiskLevel("B3");
                    } else if (openPortfolioErrorEnums.getMsg().equals(OpenPortfolioErrorEnums.CUSTOMER_IDENTIFY_ASSURANCE_LEVEL.getMsg())) {
                        response.getBody().getData().get(0).setEkycIdentifyAssuranceLevel("100");
                    } else if (openPortfolioErrorEnums.getMsg().equals(OpenPortfolioErrorEnums.CUSTOMER_HAS_US_NATIONALITY_OR_OTHER_THIRTY_RESTRICTED.getMsg())) {
                        response.getBody().getData().get(0).setNationality("TH");
                        response.getBody().getData().get(0).setNationalitySecond("US");
                    }

                    break;
                case "2000034":
                    response.getBody().getData().get(0).setEkycIdentifyAssuranceLevel("220");
                    response.getBody().getData().get(0).setFatcaFlag("0");
                    break;
                case "2000022":
                    response.getBody().getData().get(0).setKycLimitedFlag("");
                    response.getBody().getData().get(0).setExpiryDate("2021-07-08");
                    response.getBody().getData().get(0).setEkycIdentifyAssuranceLevel("");
                    break;

            }
        } else {
            response.getBody().getData().get(0).setExpiryDate("2025-07-08");
            response.getBody().getData().get(0).setEkycIdentifyAssuranceLevel("220");
            response.getBody().getData().get(0).setKycLimitedFlag("U");
        }

        CustomerInfo customerInfo = objectMapper.readValue(Paths.get("src/test/resources/investment/portfolio/customer_info.json").toFile(), CustomerInfo.class);
        when(customerServiceClient.customerSearch(any(), any(), any())).thenReturn(response);
        when(customerInfoMapper.map(any())).thenReturn(customerInfo);
    }

    @Test
    void should_return_status_0000_and_body_not_null_when_call_validation_give_correlation_id_and_open_portfolio_request_with_new_customer() throws Exception {
        // Given
        ObjectMapper mapper = new ObjectMapper();
        TermAndConditionResponse termAndConditionResponse = mapper.readValue(Paths.get("src/test/resources/investment/portfolio/validation.json").toFile(),
                TermAndConditionResponse.class);

        TmbOneServiceResponse<TermAndConditionResponseBody> oneServiceResponse = new TmbOneServiceResponse<>();
        oneServiceResponse.setData(termAndConditionResponse.getData());
        oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                ProductsExpServiceConstant.SUCCESS_MESSAGE,
                ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

        DepositAccount depositAccount = new DepositAccount();
        depositAccount.setProductNameTH("บัญชีออลล์ฟรี");
        depositAccount.setProductNameEN("TMB All Free Account");
        depositAccount.setAccountNumber("1102416367");
        depositAccount.setAccountStatus("ACTIVE");
        depositAccount.setAccountStatusCode(ProductsExpServiceConstant.ACTIVE_STATUS_CODE);
        depositAccount.setAccountType("S");
        depositAccount.setAccountTypeShort("SDA");
        depositAccount.setAvailableBalance(new BigDecimal("1033583777.38"));

        when(eligibleDepositAccountService.getEligibleDepositAccounts(any(), any())).thenReturn(newArrayList(depositAccount));
        when(commonServiceClient.getTermAndConditionByServiceCodeAndChannel(any(), any(), any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse));

        OpenPortfolioValidationRequest openPortfolioValidationRequest = OpenPortfolioValidationRequest.builder().crmId("001100000000000000000012035644").existingCustomer(false).build();
        mockPassServiceHour();
        mockCommonConfig();
        mockCustomerResponse(null);

        // When
        TmbOneServiceResponse<ValidateOpenPortfolioResponse> actual = openPortfolioValidationService.validateOpenPortfolioService("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", "00000018592885", openPortfolioValidationRequest);

        // Then
        assertEquals("0000", actual.getStatus().getCode());
        assertNotNull(actual.getData().getCustomerInfo());
        assertNotNull(actual.getData().getTermsConditions());
        assertNotNull(actual.getData().getDepositAccountList());
        verify(openPortfolioActivityLogService).openPortfolio(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void should_return_status_0000_and_body_not_null_when_call_validation_give_correlation_id_and_open_portfolio_request_with_exist_customer() throws Exception {
        // Given
        ObjectMapper mapper = new ObjectMapper();
        TermAndConditionResponse termAndConditionResponse = mapper.readValue(Paths.get("src/test/resources/investment/portfolio/validation.json").toFile(),
                TermAndConditionResponse.class);

        TmbOneServiceResponse<TermAndConditionResponseBody> oneServiceResponse = new TmbOneServiceResponse<>();
        oneServiceResponse.setData(termAndConditionResponse.getData());
        oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                ProductsExpServiceConstant.SUCCESS_MESSAGE,
                ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

        when(commonServiceClient.getTermAndConditionByServiceCodeAndChannel(any(), any(), any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse));

        OpenPortfolioValidationRequest openPortfolioValidationRequest = OpenPortfolioValidationRequest.builder().crmId("001100000000000000000012035644").existingCustomer(true).build();
        mockPassServiceHour();
        mockCustomerResponse(null);
        mockCommonConfig();

        // When
        TmbOneServiceResponse<ValidateOpenPortfolioResponse> actual = openPortfolioValidationService.validateOpenPortfolioService("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", "00000018592885", openPortfolioValidationRequest);

        // Then
        assertEquals("0000", actual.getStatus().getCode());
        assertNotNull(actual.getData().getCustomerInfo());
        assertNotNull(actual.getData().getTermsConditions());
        assertNull(actual.getData().getDepositAccountList());
        verify(openPortfolioActivityLogService).openPortfolio(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void should_return_status_code_2000001_when_call_validateOpenPortfolioService_validate_service_hour() throws Exception {
        // Given
        ObjectMapper mapper = new ObjectMapper();
        OpenPortfolioValidationRequest openPortfolioValidationRequest = OpenPortfolioValidationRequest.builder().crmId("001100000000000000000012035644").existingCustomer(true).build();
        mockNotPassServiceHour();
        mockCustomerResponse(null);

        // When
        TmbOneServiceResponse<ValidateOpenPortfolioResponse> actual = openPortfolioValidationService.validateOpenPortfolioService("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", "00000018592885", openPortfolioValidationRequest);

        // Then
        assertEquals(OpenPortfolioErrorEnums.NOT_IN_SERVICE_HOUR.getCode(), actual.getStatus().getCode());
        assertNull(actual.getData());
        verify(openPortfolioActivityLogService).openPortfolio(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void should_return_status_code_2000025_when_call_validateOpenPortfolioService_validate_age_is_not_over_twenty() throws Exception {
        // Given
        ObjectMapper mapper = new ObjectMapper();
        OpenPortfolioValidationRequest openPortfolioValidationRequest = OpenPortfolioValidationRequest.builder().crmId("001100000000000000000012035644").existingCustomer(true).build();
        mockPassServiceHour();
        mockCustomerResponse(OpenPortfolioErrorEnums.AGE_NOT_OVER_TWENTY);

        // When
        TmbOneServiceResponse<ValidateOpenPortfolioResponse> actual = openPortfolioValidationService.validateOpenPortfolioService("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", "00000018592885", openPortfolioValidationRequest);

        // Then
        assertEquals(OpenPortfolioErrorEnums.AGE_NOT_OVER_TWENTY.getCode(), actual.getStatus().getCode());
        assertNull(actual.getData());
        verify(openPortfolioActivityLogService).openPortfolio(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void should_return_status_code_2000019_when_call_validateOpenPortfolioService_validate_no_casa_active() throws Exception {
        // Given
        ObjectMapper mapper = new ObjectMapper();
        OpenPortfolioValidationRequest openPortfolioValidationRequest = OpenPortfolioValidationRequest.builder().crmId("001100000000000000000012035644").existingCustomer(false).build();
        mockPassServiceHour();
        mockCustomerResponse(OpenPortfolioErrorEnums.NO_ACTIVE_CASA_ACCOUNT);

        DepositAccount depositAccount = new DepositAccount();
        depositAccount.setProductNameTH("บัญชีออลล์ฟรี");
        depositAccount.setProductNameEN("TMB All Free Account");
        depositAccount.setAccountNumber("1102416367");
        depositAccount.setAccountStatus("ACTIVE");
        depositAccount.setAccountType("S");
        depositAccount.setAccountTypeShort("SDA");
        depositAccount.setAccountStatusCode(ProductsExpServiceConstant.DORMANT_STATUS_CODE);
        depositAccount.setAvailableBalance(new BigDecimal("1033583777.38"));

        when(eligibleDepositAccountService.getEligibleDepositAccounts(any(), any())).thenReturn(newArrayList(depositAccount));

        // When
        TmbOneServiceResponse<ValidateOpenPortfolioResponse> actual = openPortfolioValidationService.validateOpenPortfolioService("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", "00000018592885", openPortfolioValidationRequest);

        // Then
        assertEquals(OpenPortfolioErrorEnums.NO_ACTIVE_CASA_ACCOUNT.getCode(), actual.getStatus().getCode());
        assertNull(actual.getData());
        verify(openPortfolioActivityLogService).openPortfolio(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void should_return_status_code_2000018_when_call_validateOpenPortfolioService_validate_risk_level_not_valid() throws Exception {
        // Given
        ObjectMapper mapper = new ObjectMapper();
        OpenPortfolioValidationRequest openPortfolioValidationRequest = OpenPortfolioValidationRequest.builder().crmId("001100000000000000000012035644").existingCustomer(false).build();
        mockPassServiceHour();
        mockCommonConfig();
        mockCustomerResponse(OpenPortfolioErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3);

        DepositAccount depositAccount = new DepositAccount();
        depositAccount.setProductNameTH("บัญชีออลล์ฟรี");
        depositAccount.setProductNameEN("TMB All Free Account");
        depositAccount.setAccountNumber("1102416367");
        depositAccount.setAccountStatus("ACTIVE");
        depositAccount.setAccountType("S");
        depositAccount.setAccountTypeShort("SDA");
        depositAccount.setAccountStatusCode(ProductsExpServiceConstant.ACTIVE_STATUS_CODE);
        depositAccount.setAvailableBalance(new BigDecimal("1033583777.38"));

        when(eligibleDepositAccountService.getEligibleDepositAccounts(any(), any())).thenReturn(newArrayList(depositAccount));

        // When
        TmbOneServiceResponse<ValidateOpenPortfolioResponse> actual = openPortfolioValidationService.validateOpenPortfolioService("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", "00000018592885", openPortfolioValidationRequest);

        // Then
        assertEquals(OpenPortfolioErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getCode(), actual.getStatus().getCode());
        assertEquals(OpenPortfolioErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getMsg(), actual.getStatus().getMessage());
        assertNull(actual.getData());
        verify(openPortfolioActivityLogService).openPortfolio(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void should_return_status_code_2000018_when_call_validateOpenPortfolioService_validate_customer_assurance_level() throws Exception {
        // Given
        ObjectMapper mapper = new ObjectMapper();
        OpenPortfolioValidationRequest openPortfolioValidationRequest = OpenPortfolioValidationRequest.builder().crmId("001100000000000000000012035644").existingCustomer(false).build();
        mockPassServiceHour();
        mockCommonConfig();
        mockCustomerResponse(OpenPortfolioErrorEnums.CUSTOMER_IDENTIFY_ASSURANCE_LEVEL);

        DepositAccount depositAccount = new DepositAccount();
        depositAccount.setProductNameTH("บัญชีออลล์ฟรี");
        depositAccount.setProductNameEN("TMB All Free Account");
        depositAccount.setAccountNumber("1102416367");
        depositAccount.setAccountStatus("ACTIVE");
        depositAccount.setAccountType("S");
        depositAccount.setAccountTypeShort("SDA");
        depositAccount.setAccountStatusCode(ProductsExpServiceConstant.ACTIVE_STATUS_CODE);
        depositAccount.setAvailableBalance(new BigDecimal("1033583777.38"));

        when(eligibleDepositAccountService.getEligibleDepositAccounts(any(), any())).thenReturn(newArrayList(depositAccount));

        // When
        TmbOneServiceResponse<ValidateOpenPortfolioResponse> actual = openPortfolioValidationService.validateOpenPortfolioService("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", "00000018592885", openPortfolioValidationRequest);

        // Then
        assertEquals(OpenPortfolioErrorEnums.CUSTOMER_IDENTIFY_ASSURANCE_LEVEL.getCode(), actual.getStatus().getCode());
        assertEquals(OpenPortfolioErrorEnums.CUSTOMER_IDENTIFY_ASSURANCE_LEVEL.getMsg(), actual.getStatus().getMessage());
        assertNull(actual.getData());
        verify(openPortfolioActivityLogService).openPortfolio(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void should_return_status_code_2000018_when_call_validateOpenPortfolioService_validate_customer_nationality() throws Exception {
        // Given
        ObjectMapper mapper = new ObjectMapper();
        OpenPortfolioValidationRequest openPortfolioValidationRequest = OpenPortfolioValidationRequest.builder().crmId("001100000000000000000012035644").existingCustomer(false).build();
        mockPassServiceHour();
        mockCommonConfig();
        mockCustomerResponse(OpenPortfolioErrorEnums.CUSTOMER_HAS_US_NATIONALITY_OR_OTHER_THIRTY_RESTRICTED);

        DepositAccount depositAccount = new DepositAccount();
        depositAccount.setProductNameTH("บัญชีออลล์ฟรี");
        depositAccount.setProductNameEN("TMB All Free Account");
        depositAccount.setAccountNumber("1102416367");
        depositAccount.setAccountStatus("ACTIVE");
        depositAccount.setAccountType("S");
        depositAccount.setAccountTypeShort("SDA");
        depositAccount.setAccountStatusCode(ProductsExpServiceConstant.ACTIVE_STATUS_CODE);
        depositAccount.setAvailableBalance(new BigDecimal("1033583777.38"));

        when(eligibleDepositAccountService.getEligibleDepositAccounts(any(), any())).thenReturn(newArrayList(depositAccount));

        // When
        TmbOneServiceResponse<ValidateOpenPortfolioResponse> actual = openPortfolioValidationService.validateOpenPortfolioService("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", "00000018592885", openPortfolioValidationRequest);

        // Then
        assertEquals(OpenPortfolioErrorEnums.CUSTOMER_HAS_US_NATIONALITY_OR_OTHER_THIRTY_RESTRICTED.getCode(), actual.getStatus().getCode());
        assertEquals(OpenPortfolioErrorEnums.CUSTOMER_HAS_US_NATIONALITY_OR_OTHER_THIRTY_RESTRICTED.getMsg(), actual.getStatus().getMessage());
        assertNull(actual.getData());
        verify(openPortfolioActivityLogService).openPortfolio(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void should_return_status_code_2000034_when_call_validateOpenPortfolioService_validate_customer_not_fill_fatca_form() throws Exception {
        // Given
        ObjectMapper mapper = new ObjectMapper();
        OpenPortfolioValidationRequest openPortfolioValidationRequest = OpenPortfolioValidationRequest.builder().crmId("001100000000000000000012035644").existingCustomer(false).build();
        mockPassServiceHour();
        mockCommonConfig();
        mockCustomerResponse(OpenPortfolioErrorEnums.CUSTOMER_NOT_FILL_FATCA_FORM);

        DepositAccount depositAccount = new DepositAccount();
        depositAccount.setProductNameTH("บัญชีออลล์ฟรี");
        depositAccount.setProductNameEN("TMB All Free Account");
        depositAccount.setAccountNumber("1102416367");
        depositAccount.setAccountStatus("ACTIVE");
        depositAccount.setAccountType("S");
        depositAccount.setAccountTypeShort("SDA");
        depositAccount.setAccountStatusCode(ProductsExpServiceConstant.ACTIVE_STATUS_CODE);
        depositAccount.setAvailableBalance(new BigDecimal("1033583777.38"));

        when(eligibleDepositAccountService.getEligibleDepositAccounts(any(), any())).thenReturn(newArrayList(depositAccount));

        // When
        TmbOneServiceResponse<ValidateOpenPortfolioResponse> actual = openPortfolioValidationService.validateOpenPortfolioService("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", "00000018592885", openPortfolioValidationRequest);

        // Then
        assertEquals(OpenPortfolioErrorEnums.CUSTOMER_NOT_FILL_FATCA_FORM.getCode(), actual.getStatus().getCode());
        assertEquals(OpenPortfolioErrorEnums.CUSTOMER_NOT_FILL_FATCA_FORM.getMsg(), actual.getStatus().getMessage());
        assertNull(actual.getData());
        verify(openPortfolioActivityLogService).openPortfolio(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void should_return_status_code_2000022_when_call_validateOpenPortfolioService_validate_kyc_and_id_card_expired() throws Exception {
        // Given
        ObjectMapper mapper = new ObjectMapper();
        OpenPortfolioValidationRequest openPortfolioValidationRequest = OpenPortfolioValidationRequest.builder().crmId("001100000000000000000012035644").existingCustomer(false).build();
        mockPassServiceHour();
        mockCustomerResponse(OpenPortfolioErrorEnums.FAILED_VERIFY_KYC);

        DepositAccount depositAccount = new DepositAccount();
        depositAccount.setProductNameTH("บัญชีออลล์ฟรี");
        depositAccount.setProductNameEN("TMB All Free Account");
        depositAccount.setAccountNumber("1102416367");
        depositAccount.setAccountStatus("ACTIVE");
        depositAccount.setAccountType("S");
        depositAccount.setAccountTypeShort("SDA");
        depositAccount.setAccountStatusCode(ProductsExpServiceConstant.ACTIVE_STATUS_CODE);
        depositAccount.setAvailableBalance(new BigDecimal("1033583777.38"));

        when(eligibleDepositAccountService.getEligibleDepositAccounts(any(), any())).thenReturn(newArrayList(depositAccount));

        // When
        TmbOneServiceResponse<ValidateOpenPortfolioResponse> actual = openPortfolioValidationService.validateOpenPortfolioService("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", "00000018592885", openPortfolioValidationRequest);

        // Then
        assertEquals(OpenPortfolioErrorEnums.FAILED_VERIFY_KYC.getCode(), actual.getStatus().getCode());
        assertEquals(OpenPortfolioErrorEnums.FAILED_VERIFY_KYC.getMsg(), actual.getStatus().getMessage());
        assertNull(actual.getData());
        verify(openPortfolioActivityLogService).openPortfolio(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void should_return_status_code_2000018_when_call_validateOpenPortfolioService_validate_nationality() throws Exception {
        // Given
        ObjectMapper mapper = new ObjectMapper();
        OpenPortfolioValidationRequest openPortfolioValidationRequest = OpenPortfolioValidationRequest.builder().crmId("001100000000000000000012035644").existingCustomer(false).build();
        mockPassServiceHour();
        mockCustomerResponse(OpenPortfolioErrorEnums.FAILED_VERIFY_KYC);

        DepositAccount depositAccount = new DepositAccount();
        depositAccount.setProductNameTH("บัญชีออลล์ฟรี");
        depositAccount.setProductNameEN("TMB All Free Account");
        depositAccount.setAccountNumber("1102416367");
        depositAccount.setAccountStatus("ACTIVE");
        depositAccount.setAccountType("S");
        depositAccount.setAccountTypeShort("SDA");
        depositAccount.setAccountStatusCode(ProductsExpServiceConstant.ACTIVE_STATUS_CODE);
        depositAccount.setAvailableBalance(new BigDecimal("1033583777.38"));

        when(eligibleDepositAccountService.getEligibleDepositAccounts(any(), any())).thenReturn(newArrayList(depositAccount));

        // When
        TmbOneServiceResponse<ValidateOpenPortfolioResponse> actual = openPortfolioValidationService.validateOpenPortfolioService("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", "00000018592885", openPortfolioValidationRequest);

        // Then
        assertEquals(OpenPortfolioErrorEnums.FAILED_VERIFY_KYC.getCode(), actual.getStatus().getCode());
        assertEquals(OpenPortfolioErrorEnums.FAILED_VERIFY_KYC.getMsg(), actual.getStatus().getMessage());
        assertNull(actual.getData());
        verify(openPortfolioActivityLogService).openPortfolio(anyString(), anyString(), anyString(), anyString());
    }
}