package com.tmb.oneapp.productsexpservice.service.productexperience.portfolio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.CommonData;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.activitylog.portfolio.service.OpenPortfolioActivityLogService;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.enums.AlternativeOpenPortfolioErrorEnums;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.mapper.customer.CustomerInformationMapper;
import com.tmb.oneapp.productsexpservice.model.common.teramandcondition.response.TermAndConditionResponse;
import com.tmb.oneapp.productsexpservice.model.common.teramandcondition.response.TermAndConditionResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.response.servicehour.ValidateServiceHourResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.request.OpenPortfolioValidationRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.response.CustomerInformation;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.response.ValidateOpenPortfolioResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.DepositAccount;
import com.tmb.oneapp.productsexpservice.service.productexperience.account.EligibleDepositAccountService;
import com.tmb.oneapp.productsexpservice.service.productexperience.alternative.AlternativeService;
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
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OpenPortfolioTransactionValidationRequestServiceTest {

    @Mock
    private TMBLogger<OpenPortfolioTransactionValidationRequestServiceTest> logger;

    @Mock
    private CustomerServiceClient customerServiceClient;

    @Mock
    private CommonServiceClient commonServiceClient;

    @Mock
    private EligibleDepositAccountService eligibleDepositAccountService;

    @Mock
    private OpenPortfolioActivityLogService openPortfolioActivityLogService;

    @Mock
    private CustomerInformationMapper customerInformationMapper;

    @Mock
    private AlternativeService alternativeService;

    @InjectMocks
    private OpenPortfolioValidationService openPortfolioValidationService;

    private final String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";

    private final String crmId = "001100000000000000000012035644";

    private final String ipAddress = "0.0.0.0";

    private void mockCommonConfig() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<CommonData> list = new ArrayList<>();
        TmbOneServiceResponse<List<CommonData>> response = new TmbOneServiceResponse<List<CommonData>>();
        list.add(objectMapper.readValue(Paths.get("src/test/resources/investment/common/investment_config.json").toFile(), CommonData.class));
        response.setData(list);
        when(commonServiceClient.getCommonConfig(any(), any())).thenReturn(ResponseEntity.ok(response));
    }

    private void mockCustomerResponse(AlternativeOpenPortfolioErrorEnums alternativeOpenPortfolioErrorEnums) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        CustomerSearchResponse customerSearchResponse = objectMapper.readValue(Paths.get("src/test/resources/investment/customer/search_customer.json").toFile(), CustomerSearchResponse.class);
        TmbOneServiceResponse<List<CustomerSearchResponse>> oneServiceResponse = new TmbOneServiceResponse<List<CustomerSearchResponse>>();
        oneServiceResponse.setData(List.of(customerSearchResponse));
        ResponseEntity<TmbOneServiceResponse<List<CustomerSearchResponse>>> response = new ResponseEntity<TmbOneServiceResponse<List<CustomerSearchResponse>>>(
                oneServiceResponse, HttpStatus.OK);

        if (alternativeOpenPortfolioErrorEnums != null) {
            response.getBody().getData().get(0).setEkycIdentifyAssuranceLevel("300");
            switch (alternativeOpenPortfolioErrorEnums.getCode()) {
                case "2000025":
                    response.getBody().getData().get(0).setBirthDate("2010-07-08");
                    break;
                case "2000018":

                    if (alternativeOpenPortfolioErrorEnums.getMessage().equals(AlternativeOpenPortfolioErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getMessage())) {
                        response.getBody().getData().get(0).setCustomerRiskLevel("B3");
                    } else if (alternativeOpenPortfolioErrorEnums.getMessage().equals(AlternativeOpenPortfolioErrorEnums.CUSTOMER_IDENTIFY_ASSURANCE_LEVEL.getMessage())) {
                        response.getBody().getData().get(0).setEkycIdentifyAssuranceLevel("100");
                    } else if (alternativeOpenPortfolioErrorEnums.getMessage().equals(AlternativeOpenPortfolioErrorEnums.CUSTOMER_HAS_US_NATIONALITY_OR_OTHER_THIRTY_RESTRICTED.getMessage())) {
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

        CustomerInformation customerInformation = objectMapper.readValue(Paths.get("src/test/resources/investment/portfolio/customer_info.json").toFile(), CustomerInformation.class);
        when(customerServiceClient.customerSearch(anyString(), anyString(), any())).thenReturn(response);
        when(customerInformationMapper.map(any())).thenReturn(customerInformation);
    }

    @Test
    void should_return_status_0000_and_body_not_null_when_call_validation_give_open_portfolio_request_with_new_customer() throws Exception {
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

        when(eligibleDepositAccountService.getEligibleDepositAccounts(any(), any(), anyBoolean())).thenReturn(newArrayList(depositAccount));
        when(commonServiceClient.getTermAndConditionByServiceCodeAndChannel(any(), any(), any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse));

        OpenPortfolioValidationRequest openPortfolioValidationRequest = OpenPortfolioValidationRequest.builder().existingCustomer(false).build();
        mockCommonConfig();
        mockCustomerResponse(null);
        mockSuccessAllAlternative();


        // When
        TmbOneServiceResponse<ValidateOpenPortfolioResponse> actual = openPortfolioValidationService.validateOpenPortfolioService(correlationId, crmId, ipAddress, openPortfolioValidationRequest);

        // Then
        assertEquals("0000", actual.getStatus().getCode());
        assertNotNull(actual.getData().getCustomerInformation());
        assertNotNull(actual.getData().getTermsConditions());
        assertNotNull(actual.getData().getDepositAccountList());
        verify(openPortfolioActivityLogService).openPortfolio(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void should_return_status_0000_and_body_not_null_when_call_validation_give_open_portfolio_request_with_exist_customer() throws Exception {
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

        OpenPortfolioValidationRequest openPortfolioValidationRequest = OpenPortfolioValidationRequest.builder().existingCustomer(true).build();
        mockCustomerResponse(null);
        mockCommonConfig();

        mockSuccessAllAlternative();
        // When
        TmbOneServiceResponse<ValidateOpenPortfolioResponse> actual = openPortfolioValidationService.validateOpenPortfolioService(correlationId, crmId, ipAddress, openPortfolioValidationRequest);

        // Then
        assertEquals("0000", actual.getStatus().getCode());
        assertNotNull(actual.getData().getCustomerInformation());
        assertNotNull(actual.getData().getTermsConditions());
        verify(openPortfolioActivityLogService).openPortfolio(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void should_return_status_code_2000001_when_call_validate_open_portfolio_service_given_validate_service_hour() throws Exception {
        // Given
        OpenPortfolioValidationRequest openPortfolioValidationRequest = OpenPortfolioValidationRequest.builder().existingCustomer(true).build();
        mockCustomerResponse(null);
        ValidateServiceHourResponse tmbStatus = new ValidateServiceHourResponse();
        tmbStatus.setCode(AlternativeOpenPortfolioErrorEnums.NOT_IN_SERVICE_HOUR.getCode());
        tmbStatus.setDescription(AlternativeOpenPortfolioErrorEnums.NOT_IN_SERVICE_HOUR.getDescription());
        tmbStatus.setMessage(AlternativeOpenPortfolioErrorEnums.NOT_IN_SERVICE_HOUR.getMessage());
        tmbStatus.setStartTime("19:00");
        tmbStatus.setEndTime("20:00");

        when(alternativeService.validateServiceHour(any(), any())).thenReturn(tmbStatus);

        // When
        TmbOneServiceResponse<ValidateOpenPortfolioResponse> actual = openPortfolioValidationService.validateOpenPortfolioService(correlationId, crmId, ipAddress, openPortfolioValidationRequest);

        // Then
        assertEquals(AlternativeOpenPortfolioErrorEnums.NOT_IN_SERVICE_HOUR.getCode(), actual.getStatus().getCode());
        assertEquals("19:00-20:00", actual.getData().getServiceHour());
        verify(openPortfolioActivityLogService).openPortfolio(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void should_return_status_code_2000025_when_call_validate_open_portfolio_service_given_validate_age_is_not_over_twenty() throws Exception {
        // Given
        OpenPortfolioValidationRequest openPortfolioValidationRequest = OpenPortfolioValidationRequest.builder().existingCustomer(true).build();
        mockCustomerResponse(AlternativeOpenPortfolioErrorEnums.AGE_NOT_OVER_TWENTY);
        mockSuccessAllAlternative();
        when(alternativeService.validateDateNotOverTwentyYearOld(any(), any())).thenReturn(
                mockTmbStatusError(AlternativeOpenPortfolioErrorEnums.AGE_NOT_OVER_TWENTY.getCode(),
                        AlternativeOpenPortfolioErrorEnums.AGE_NOT_OVER_TWENTY.getMessage(),
                        AlternativeOpenPortfolioErrorEnums.AGE_NOT_OVER_TWENTY.getDescription()));

        // When
        TmbOneServiceResponse<ValidateOpenPortfolioResponse> actual = openPortfolioValidationService.validateOpenPortfolioService(correlationId, crmId, ipAddress, openPortfolioValidationRequest);

        // Then
        assertEquals(AlternativeOpenPortfolioErrorEnums.AGE_NOT_OVER_TWENTY.getCode(), actual.getStatus().getCode());
        verify(openPortfolioActivityLogService).openPortfolio(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    private ValidateServiceHourResponse mockTmbStatusWithTimeSuccess(String code, String message, String description) {
        ValidateServiceHourResponse validateServiceHourResponse = new ValidateServiceHourResponse();
        validateServiceHourResponse.setCode(code);
        validateServiceHourResponse.setDescription(description);
        validateServiceHourResponse.setMessage(message);
        return validateServiceHourResponse;
    }

    private TmbStatus mockTmbStatusError(String code, String message, String desc) {
        TmbStatus tmbStatus = new TmbStatus();
        tmbStatus.setCode(code);
        tmbStatus.setDescription(desc);
        tmbStatus.setMessage(message);
        return tmbStatus;
    }

    @Test
    void should_return_status_code_2000019_when_call_validate_open_portfolio_service_given_validate_no_casa_active() throws Exception {
        // Given
        OpenPortfolioValidationRequest openPortfolioValidationRequest = OpenPortfolioValidationRequest.builder().existingCustomer(false).build();
        mockCustomerResponse(AlternativeOpenPortfolioErrorEnums.NO_ACTIVE_CASA_ACCOUNT);

        DepositAccount depositAccount = new DepositAccount();
        depositAccount.setProductNameTH("บัญชีออลล์ฟรี");
        depositAccount.setProductNameEN("TMB All Free Account");
        depositAccount.setAccountNumber("1102416367");
        depositAccount.setAccountStatus("ACTIVE");
        depositAccount.setAccountType("S");
        depositAccount.setAccountTypeShort("SDA");
        depositAccount.setAccountStatusCode(ProductsExpServiceConstant.DORMANT_STATUS_CODE);
        depositAccount.setAvailableBalance(new BigDecimal("1033583777.38"));

        when(eligibleDepositAccountService.getEligibleDepositAccounts(any(), any(), anyBoolean())).thenReturn(newArrayList(depositAccount));
        mockSuccessAllAlternative();
        when(alternativeService.validateCasaAccountActiveOnce(any(), any())).thenReturn(
                mockTmbStatusError(AlternativeOpenPortfolioErrorEnums.NO_ACTIVE_CASA_ACCOUNT.getCode(),
                        AlternativeOpenPortfolioErrorEnums.NO_ACTIVE_CASA_ACCOUNT.getMessage(),
                        AlternativeOpenPortfolioErrorEnums.NO_ACTIVE_CASA_ACCOUNT.getDescription()));
        // When
        TmbOneServiceResponse<ValidateOpenPortfolioResponse> actual = openPortfolioValidationService.validateOpenPortfolioService(correlationId, crmId, ipAddress, openPortfolioValidationRequest);

        // Then
        assertEquals(AlternativeOpenPortfolioErrorEnums.NO_ACTIVE_CASA_ACCOUNT.getCode(), actual.getStatus().getCode());
        verify(openPortfolioActivityLogService).openPortfolio(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void should_return_status_code_2000018_when_call_validate_open_portfolio_service_given_validate_risk_level_not_valid() throws Exception {
        // Given
        OpenPortfolioValidationRequest openPortfolioValidationRequest = OpenPortfolioValidationRequest.builder().existingCustomer(false).build();
        mockCommonConfig();
        mockCustomerResponse(AlternativeOpenPortfolioErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3);

        DepositAccount depositAccount = new DepositAccount();
        depositAccount.setProductNameTH("บัญชีออลล์ฟรี");
        depositAccount.setProductNameEN("TMB All Free Account");
        depositAccount.setAccountNumber("1102416367");
        depositAccount.setAccountStatus("ACTIVE");
        depositAccount.setAccountType("S");
        depositAccount.setAccountTypeShort("SDA");
        depositAccount.setAccountStatusCode(ProductsExpServiceConstant.ACTIVE_STATUS_CODE);
        depositAccount.setAvailableBalance(new BigDecimal("1033583777.38"));

        when(eligibleDepositAccountService.getEligibleDepositAccounts(any(), any(), anyBoolean())).thenReturn(newArrayList(depositAccount));
        mockSuccessAllAlternative();
        when(alternativeService.validateCustomerRiskLevel(any(), any(), any(), any())).thenReturn(
                mockTmbStatusError(AlternativeOpenPortfolioErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getCode(),
                        AlternativeOpenPortfolioErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getMessage(),
                        AlternativeOpenPortfolioErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getDescription()));

        // When
        TmbOneServiceResponse<ValidateOpenPortfolioResponse> actual = openPortfolioValidationService.validateOpenPortfolioService(correlationId, crmId, ipAddress, openPortfolioValidationRequest);

        // Then
        assertEquals(AlternativeOpenPortfolioErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getCode(), actual.getStatus().getCode());
        assertEquals(AlternativeOpenPortfolioErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getMessage(), actual.getStatus().getMessage());
        verify(openPortfolioActivityLogService).openPortfolio(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void should_return_status_code_2000018_when_call_validate_open_portfolio_service_given_validate_customer_assurance_level() throws Exception {
        // Given
        OpenPortfolioValidationRequest openPortfolioValidationRequest = OpenPortfolioValidationRequest.builder().existingCustomer(false).build();
        mockCommonConfig();
        mockCustomerResponse(AlternativeOpenPortfolioErrorEnums.CUSTOMER_IDENTIFY_ASSURANCE_LEVEL);

        DepositAccount depositAccount = new DepositAccount();
        depositAccount.setProductNameTH("บัญชีออลล์ฟรี");
        depositAccount.setProductNameEN("TMB All Free Account");
        depositAccount.setAccountNumber("1102416367");
        depositAccount.setAccountStatus("ACTIVE");
        depositAccount.setAccountType("S");
        depositAccount.setAccountTypeShort("SDA");
        depositAccount.setAccountStatusCode(ProductsExpServiceConstant.ACTIVE_STATUS_CODE);
        depositAccount.setAvailableBalance(new BigDecimal("1033583777.38"));

        when(eligibleDepositAccountService.getEligibleDepositAccounts(any(), any(), anyBoolean())).thenReturn(newArrayList(depositAccount));
        mockSuccessAllAlternative();
        when(alternativeService.validateIdentityAssuranceLevel(any(), any(), anyString())).thenReturn(
                mockTmbStatusError(AlternativeOpenPortfolioErrorEnums.CUSTOMER_IDENTIFY_ASSURANCE_LEVEL.getCode(),
                        AlternativeOpenPortfolioErrorEnums.CUSTOMER_IDENTIFY_ASSURANCE_LEVEL.getMessage(),
                        AlternativeOpenPortfolioErrorEnums.CUSTOMER_IDENTIFY_ASSURANCE_LEVEL.getDescription()));
        // When
        TmbOneServiceResponse<ValidateOpenPortfolioResponse> actual = openPortfolioValidationService.validateOpenPortfolioService(correlationId, crmId, ipAddress, openPortfolioValidationRequest);

        // Then
        assertEquals(AlternativeOpenPortfolioErrorEnums.CUSTOMER_IDENTIFY_ASSURANCE_LEVEL.getCode(), actual.getStatus().getCode());
        assertEquals(AlternativeOpenPortfolioErrorEnums.CUSTOMER_IDENTIFY_ASSURANCE_LEVEL.getMessage(), actual.getStatus().getMessage());
        verify(openPortfolioActivityLogService).openPortfolio(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void should_return_status_code_2000018_when_call_validate_open_portfolio_service_given_validate_customer_nationality() throws Exception {
        // Given
        OpenPortfolioValidationRequest openPortfolioValidationRequest = OpenPortfolioValidationRequest.builder().existingCustomer(false).build();
        mockCommonConfig();
        mockCustomerResponse(AlternativeOpenPortfolioErrorEnums.CUSTOMER_HAS_US_NATIONALITY_OR_OTHER_THIRTY_RESTRICTED);

        DepositAccount depositAccount = new DepositAccount();
        depositAccount.setProductNameTH("บัญชีออลล์ฟรี");
        depositAccount.setProductNameEN("TMB All Free Account");
        depositAccount.setAccountNumber("1102416367");
        depositAccount.setAccountStatus("ACTIVE");
        depositAccount.setAccountType("S");
        depositAccount.setAccountTypeShort("SDA");
        depositAccount.setAccountStatusCode(ProductsExpServiceConstant.ACTIVE_STATUS_CODE);
        depositAccount.setAvailableBalance(new BigDecimal("1033583777.38"));

        when(eligibleDepositAccountService.getEligibleDepositAccounts(any(), any(), anyBoolean())).thenReturn(newArrayList(depositAccount));
        mockSuccessAllAlternative();
        when(alternativeService.validateNationality(any(), any(), any(), any())).thenReturn(
                mockTmbStatusError(AlternativeOpenPortfolioErrorEnums.CUSTOMER_HAS_US_NATIONALITY_OR_OTHER_THIRTY_RESTRICTED.getCode(),
                        AlternativeOpenPortfolioErrorEnums.CUSTOMER_HAS_US_NATIONALITY_OR_OTHER_THIRTY_RESTRICTED.getMessage(),
                        AlternativeOpenPortfolioErrorEnums.CUSTOMER_HAS_US_NATIONALITY_OR_OTHER_THIRTY_RESTRICTED.getDescription()));

        // When
        TmbOneServiceResponse<ValidateOpenPortfolioResponse> actual = openPortfolioValidationService.validateOpenPortfolioService(correlationId, crmId, ipAddress, openPortfolioValidationRequest);

        // Then
        assertEquals(AlternativeOpenPortfolioErrorEnums.CUSTOMER_HAS_US_NATIONALITY_OR_OTHER_THIRTY_RESTRICTED.getCode(), actual.getStatus().getCode());
        assertEquals(AlternativeOpenPortfolioErrorEnums.CUSTOMER_HAS_US_NATIONALITY_OR_OTHER_THIRTY_RESTRICTED.getMessage(), actual.getStatus().getMessage());
        verify(openPortfolioActivityLogService).openPortfolio(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void should_return_status_code_2000034_when_call_validate_open_portfolio_service_given_validate_customer_not_fill_fatca_form() throws Exception {
        // Given
        OpenPortfolioValidationRequest openPortfolioValidationRequest = OpenPortfolioValidationRequest.builder().existingCustomer(false).build();
        mockCommonConfig();
        mockCustomerResponse(AlternativeOpenPortfolioErrorEnums.NOT_COMPLETED_FATCA_FORM);

        DepositAccount depositAccount = new DepositAccount();
        depositAccount.setProductNameTH("บัญชีออลล์ฟรี");
        depositAccount.setProductNameEN("TMB All Free Account");
        depositAccount.setAccountNumber("1102416367");
        depositAccount.setAccountStatus("ACTIVE");
        depositAccount.setAccountType("S");
        depositAccount.setAccountTypeShort("SDA");
        depositAccount.setAccountStatusCode(ProductsExpServiceConstant.ACTIVE_STATUS_CODE);
        depositAccount.setAvailableBalance(new BigDecimal("1033583777.38"));

        when(eligibleDepositAccountService.getEligibleDepositAccounts(any(), any(), anyBoolean())).thenReturn(newArrayList(depositAccount));
        mockSuccessAllAlternative();
        when(alternativeService.validateFatcaFlagNotValid(any(), any(), anyString())).thenReturn(
                mockTmbStatusError(AlternativeOpenPortfolioErrorEnums.NOT_COMPLETED_FATCA_FORM.getCode(),
                        AlternativeOpenPortfolioErrorEnums.NOT_COMPLETED_FATCA_FORM.getMessage(),
                        AlternativeOpenPortfolioErrorEnums.NOT_COMPLETED_FATCA_FORM.getDescription()));

        // When
        TmbOneServiceResponse<ValidateOpenPortfolioResponse> actual = openPortfolioValidationService.validateOpenPortfolioService(correlationId, crmId, ipAddress, openPortfolioValidationRequest);

        // Then
        assertEquals(AlternativeOpenPortfolioErrorEnums.NOT_COMPLETED_FATCA_FORM.getCode(), actual.getStatus().getCode());
        assertEquals(AlternativeOpenPortfolioErrorEnums.NOT_COMPLETED_FATCA_FORM.getMessage(), actual.getStatus().getMessage());
        verify(openPortfolioActivityLogService).openPortfolio(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void should_return_status_code_2000022_when_call_validate_open_portfolio_service_given_validate_kyc_and_id_card_expired() throws Exception {
        // Given
        OpenPortfolioValidationRequest openPortfolioValidationRequest = OpenPortfolioValidationRequest.builder().existingCustomer(false).build();
        mockCustomerResponse(AlternativeOpenPortfolioErrorEnums.FAILED_VERIFY_KYC);

        DepositAccount depositAccount = new DepositAccount();
        depositAccount.setProductNameTH("บัญชีออลล์ฟรี");
        depositAccount.setProductNameEN("TMB All Free Account");
        depositAccount.setAccountNumber("1102416367");
        depositAccount.setAccountStatus("ACTIVE");
        depositAccount.setAccountType("S");
        depositAccount.setAccountTypeShort("SDA");
        depositAccount.setAccountStatusCode(ProductsExpServiceConstant.ACTIVE_STATUS_CODE);
        depositAccount.setAvailableBalance(new BigDecimal("1033583777.38"));

        mockSuccessAllAlternative();
        when(alternativeService.validateKycAndIdCardExpire(any(), any(), any(), any())).thenReturn(
                mockTmbStatusError(AlternativeOpenPortfolioErrorEnums.FAILED_VERIFY_KYC.getCode(),
                        AlternativeOpenPortfolioErrorEnums.FAILED_VERIFY_KYC.getMessage(),
                        AlternativeOpenPortfolioErrorEnums.FAILED_VERIFY_KYC.getDescription()));

        // When
        TmbOneServiceResponse<ValidateOpenPortfolioResponse> actual = openPortfolioValidationService.validateOpenPortfolioService(correlationId, crmId, ipAddress, openPortfolioValidationRequest);

        // Then
        assertEquals(AlternativeOpenPortfolioErrorEnums.FAILED_VERIFY_KYC.getCode(), actual.getStatus().getCode());
        assertEquals(AlternativeOpenPortfolioErrorEnums.FAILED_VERIFY_KYC.getMessage(), actual.getStatus().getMessage());
        verify(openPortfolioActivityLogService).openPortfolio(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void should_return_status_code_2000018_when_call_validate_open_portfolio_service_given_validate_nationality() throws Exception {
        // Given
        OpenPortfolioValidationRequest openPortfolioValidationRequest = OpenPortfolioValidationRequest.builder().existingCustomer(false).build();
        mockCustomerResponse(AlternativeOpenPortfolioErrorEnums.FAILED_VERIFY_KYC);

        DepositAccount depositAccount = new DepositAccount();
        depositAccount.setProductNameTH("บัญชีออลล์ฟรี");
        depositAccount.setProductNameEN("TMB All Free Account");
        depositAccount.setAccountNumber("1102416367");
        depositAccount.setAccountStatus("ACTIVE");
        depositAccount.setAccountType("S");
        depositAccount.setAccountTypeShort("SDA");
        depositAccount.setAccountStatusCode(ProductsExpServiceConstant.ACTIVE_STATUS_CODE);
        depositAccount.setAvailableBalance(new BigDecimal("1033583777.38"));

        when(eligibleDepositAccountService.getEligibleDepositAccounts(any(), any(), anyBoolean())).thenReturn(newArrayList(depositAccount));
        mockSuccessAllAlternative();
        when(alternativeService.validateKycAndIdCardExpire(any(), any(), any(), any())).thenReturn(
                mockTmbStatusError(AlternativeOpenPortfolioErrorEnums.FAILED_VERIFY_KYC.getCode(),
                        AlternativeOpenPortfolioErrorEnums.FAILED_VERIFY_KYC.getMessage(),
                        AlternativeOpenPortfolioErrorEnums.FAILED_VERIFY_KYC.getDescription()));
        // When
        TmbOneServiceResponse<ValidateOpenPortfolioResponse> actual = openPortfolioValidationService.validateOpenPortfolioService(correlationId, crmId, ipAddress, openPortfolioValidationRequest);

        // Then
        assertEquals(AlternativeOpenPortfolioErrorEnums.FAILED_VERIFY_KYC.getCode(), actual.getStatus().getCode());
        assertEquals(AlternativeOpenPortfolioErrorEnums.FAILED_VERIFY_KYC.getMessage(), actual.getStatus().getMessage());
        verify(openPortfolioActivityLogService).openPortfolio(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    private void mockSuccessAllAlternative() {
        when(alternativeService.validateServiceHour(any(), any())).thenReturn(mockTmbStatusWithTimeSuccess(ProductsExpServiceConstant.SUCCESS_CODE, null, null));
        when(alternativeService.validateDateNotOverTwentyYearOld(any(), any())).thenReturn(mockTmbStatusError(ProductsExpServiceConstant.SUCCESS_CODE, null, null));
        when(alternativeService.validateCasaAccountActiveOnce(any(), any())).thenReturn(mockTmbStatusError(ProductsExpServiceConstant.SUCCESS_CODE, null, null));
        when(alternativeService.validateFatcaFlagNotValid(any(), any(), anyString())).thenReturn(mockTmbStatusError(ProductsExpServiceConstant.SUCCESS_CODE, null, null));
        when(alternativeService.validateKycAndIdCardExpire(any(), any(), any(), any())).thenReturn(mockTmbStatusError(ProductsExpServiceConstant.SUCCESS_CODE, null, null));
        when(alternativeService.validateIdentityAssuranceLevel(any(), any(), anyString())).thenReturn(mockTmbStatusError(ProductsExpServiceConstant.SUCCESS_CODE, null, null));
        when(alternativeService.validateNationality(any(), any(), any(), any())).thenReturn(mockTmbStatusError(ProductsExpServiceConstant.SUCCESS_CODE, null, null));
        when(alternativeService.validateCustomerRiskLevel(any(), any(), any(), any())).thenReturn(mockTmbStatusError(ProductsExpServiceConstant.SUCCESS_CODE, null, null));
    }
}