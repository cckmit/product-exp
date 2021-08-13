package com.tmb.oneapp.productsexpservice.controller.productexperience.portfolio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.enums.AlternativeOpenPortfolioErrorEnums;
import com.tmb.oneapp.productsexpservice.model.common.teramandcondition.response.TermAndConditionResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.client.response.RelationshipResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.account.purpose.response.AccountPurposeResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.request.CustomerRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.nickname.response.PortfolioNicknameResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.request.OpenPortfolioRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.request.OpenPortfolioValidationRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.response.*;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.DepositAccount;
import com.tmb.oneapp.productsexpservice.service.productexperience.portfolio.OpenPortfolioService;
import com.tmb.oneapp.productsexpservice.service.productexperience.portfolio.OpenPortfolioValidationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenPortfolioControllerTest {

    @InjectMocks
    private OpenPortfolioController openPortfolioController;

    @Mock
    private TMBLogger<OpenPortfolioControllerTest> logger;

    @Mock
    private OpenPortfolioService openPortfolioService;

    @Mock
    private OpenPortfolioValidationService openPortfolioValidationService;

    private final String crmId = "23423423423423";

    @Test
    void should_return_term_and_condition_body_not_null_when_call_validate_open_portfolio_given_correlation_id_and_crm_id_and_open_portfolio_request() throws IOException {
        // Given
        ObjectMapper mapper = new ObjectMapper();

        ValidateOpenPortfolioResponse validateOpenPortfolioResponse = new ValidateOpenPortfolioResponse();
        validateOpenPortfolioResponse.setTermsConditions(mapper.readValue(Paths.get("src/test/resources/investment/portfolio/termandcondition.json").toFile(),
                TermAndConditionResponseBody.class));
        validateOpenPortfolioResponse.setCustomerInformation(mapper.readValue(Paths.get("src/test/resources/investment/portfolio/customer_info.json").toFile(),
                CustomerInformation.class));
        List<DepositAccount> depositAccountList = new ArrayList<>();
        depositAccountList.add(mapper.readValue(Paths.get("src/test/resources/investment/account/deposit_account.json").toFile(), DepositAccount.class));
        validateOpenPortfolioResponse.setDepositAccountList(depositAccountList);

        TmbOneServiceResponse<ValidateOpenPortfolioResponse> responseService = new TmbOneServiceResponse<ValidateOpenPortfolioResponse>();
        responseService.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE, ProductsExpServiceConstant.SUCCESS_MESSAGE, ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
        responseService.setData(validateOpenPortfolioResponse);

        OpenPortfolioValidationRequest request = OpenPortfolioValidationRequest.builder().build();
        request.setExistingCustomer(true);
        when(openPortfolioValidationService.validateOpenPortfolioService("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", "00000018592884", request))
                .thenReturn(responseService);

        // When
        ResponseEntity<TmbOneServiceResponse<ValidateOpenPortfolioResponse>> actual = openPortfolioController.validateOpenPortfolio("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", "00000018592884", request);

        // Then
        assertNotNull(actual.getBody());
    }

    @Test
    void should_return_error_code_with_null_body_when_call_validate_open_portfolio_given_correlation_id_and_and_crm_id_open_portfolio_request() throws IOException {
        // Given
        ObjectMapper mapper = new ObjectMapper();

        ValidateOpenPortfolioResponse validateOpenPortfolioResponse = new ValidateOpenPortfolioResponse();
        validateOpenPortfolioResponse.setTermsConditions(mapper.readValue(Paths.get("src/test/resources/investment/portfolio/termandcondition.json").toFile(),
                TermAndConditionResponseBody.class));
        validateOpenPortfolioResponse.setCustomerInformation(mapper.readValue(Paths.get("src/test/resources/investment/portfolio/customer_info.json").toFile(),
                CustomerInformation.class));
        List<DepositAccount> depositAccountList = new ArrayList<>();
        depositAccountList.add(mapper.readValue(Paths.get("src/test/resources/investment/account/deposit_account.json").toFile(), DepositAccount.class));
        validateOpenPortfolioResponse.setDepositAccountList(depositAccountList);

        TmbOneServiceResponse<ValidateOpenPortfolioResponse> responseService = new TmbOneServiceResponse<ValidateOpenPortfolioResponse>();
        responseService.setStatus(new TmbStatus(
                AlternativeOpenPortfolioErrorEnums.AGE_NOT_OVER_TWENTY.getCode(),
                AlternativeOpenPortfolioErrorEnums.AGE_NOT_OVER_TWENTY.getMsg(),
                ProductsExpServiceConstant.SERVICE_NAME,
                AlternativeOpenPortfolioErrorEnums.AGE_NOT_OVER_TWENTY.getDesc()));

        OpenPortfolioValidationRequest request = OpenPortfolioValidationRequest.builder().build();
        request.setExistingCustomer(true);
        when(openPortfolioValidationService.validateOpenPortfolioService("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da","00000018592884", request))
                .thenReturn(responseService);

        // When
        ResponseEntity<TmbOneServiceResponse<ValidateOpenPortfolioResponse>> actual = openPortfolioController.validateOpenPortfolio("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", "00000018592884", request);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
        assertEquals(AlternativeOpenPortfolioErrorEnums.AGE_NOT_OVER_TWENTY.getCode(), actual.getBody().getStatus().getCode());
        assertEquals(AlternativeOpenPortfolioErrorEnums.AGE_NOT_OVER_TWENTY.getMsg(), actual.getBody().getStatus().getMessage());
        assertEquals(AlternativeOpenPortfolioErrorEnums.AGE_NOT_OVER_TWENTY.getDesc(), actual.getBody().getStatus().getDescription());
        assertNull(actual.getBody().getData());
    }

    @Test
    void should_return_open_portfolio_validation_response_when_call_create_customer_given_correlation_id_and_crm_id_and_customer_request() throws IOException {
        // Given
        CustomerRequest customerRequest = CustomerRequest.builder()
                .wealthCrmId("D0000000988")
                .phoneNumber("0948096953")
                .dateOfBirth("2019-04-03T09:23:45")
                .emailAddress("test@tmbbank.com")
                .maritalStatus("M")
                .residentGeoCode("TH")
                .taxNumber("1234567890123")
                .branchCode("D0000000988")
                .makerCode("D0000000988")
                .kycFlag("Y")
                .amloFlag("N")
                .lastDateSync("2019-04-03T09:23:45")
                .nationalDocumentExpireDate("2019-04-03T09:23:45")
                .nationalDocumentId("1909057937549")
                .nationalDocumentIdentificationType("TMB_CITIZEN_ID")
                .customerThaiName("นาย นัท")
                .customerEnglishName("MR NUT")
                .build();

        ObjectMapper mapper = new ObjectMapper();
        AccountPurposeResponse accountPurposeResponse = mapper.readValue(Paths.get("src/test/resources/investment/customer/account_purpose.json").toFile(),
                AccountPurposeResponse.class);

        DepositAccount depositAccount = new DepositAccount();
        OpenPortfolioValidationResponse openPortfolioValidationResponse = OpenPortfolioValidationResponse.builder()
                .accountPurposeResponse(accountPurposeResponse.getData())
                .depositAccount(depositAccount)
                .build();

        when(openPortfolioService.createCustomer("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", "00000018592884", customerRequest)).thenReturn(openPortfolioValidationResponse);

        // When
        ResponseEntity<TmbOneServiceResponse<OpenPortfolioValidationResponse>> actual = openPortfolioController.createCustomer("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", "00000018592884", customerRequest);

        // Then
        assertNotNull(actual.getBody());
    }

    @Test
    void should_return_portfolio_response_when_call_open_portfolio_given_correlation_id_and_crm_id_and_open_portfolio_request() throws IOException, TMBCommonException {
        // Given
        OpenPortfolioRequestBody openPortfolioRequestBody = OpenPortfolioRequestBody.builder()
                .jointType("Single")
                .preferredRedemptionAccountCode("0632964227")
                .preferredRedemptionAccountName("นาง สุนิสา ผลงาม 00000632964227 (SDA)")
                .preferredSubscriptionAccountCode("0632324919")
                .preferredSubscriptionAccountName("นาง สุนิสา ผลงาม 00000632324919 (SDA)")
                .registeredForVat("No")
                .vatEstablishmentBranchCode("nul")
                .withHoldingTaxPreference("TaxWithheld")
                .preferredAddressType("Contact")
                .status("Active")
                .suitabilityScore("5")
                .portfolioType("TMB_ADVTYPE_10_ADVISORY")
                .purposeTypeCode("TMB_PTFPURPOSE_10_RETIREMENT")
                .portfolioNickName("อนาคตเพื่อการศึกษ")
                .build();

        ObjectMapper mapper = new ObjectMapper();
        RelationshipResponse relationshipResponse = mapper.readValue(Paths.get("src/test/resources/investment/client/relationship.json").toFile(),
                RelationshipResponse.class);
        OpenPortfolioResponse openPortfolioResponse = mapper.readValue(Paths.get("src/test/resources/investment/portfolio/open_portfolio.json").toFile(),
                OpenPortfolioResponse.class);
        PortfolioNicknameResponse portfolioNicknameResponse = mapper.readValue(Paths.get("src/test/resources/investment/portfolio/nickname.json").toFile(),
                PortfolioNicknameResponse.class);

        PortfolioResponse portfolioResponse = PortfolioResponse.builder()
                .relationshipResponse(relationshipResponse.getData())
                .openPortfolioResponse(openPortfolioResponse.getData())
                .portfolioNicknameResponse(portfolioNicknameResponse.getData())
                .build();

        when(openPortfolioService.openPortfolio("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", "00000018592884", openPortfolioRequestBody)).thenReturn(portfolioResponse);

        // When
        ResponseEntity<TmbOneServiceResponse<PortfolioResponse>> actual = openPortfolioController.openPortfolio("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", "00000018592884", openPortfolioRequestBody);

        // Then
        assertNotNull(actual.getBody());
    }
}