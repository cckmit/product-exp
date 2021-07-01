package com.tmb.oneapp.productsexpservice.controller.productexperience;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.model.client.response.RelationshipResponse;
import com.tmb.oneapp.productsexpservice.model.common.teramandcondition.response.TermAndConditionResponseBody;
import com.tmb.oneapp.productsexpservice.model.customer.account.purpose.response.AccountPurposeResponse;
import com.tmb.oneapp.productsexpservice.model.customer.account.redeem.response.AccountRedeemResponse;
import com.tmb.oneapp.productsexpservice.model.customer.request.CustomerRequest;
import com.tmb.oneapp.productsexpservice.model.portfolio.nickname.response.PortfolioNicknameResponse;
import com.tmb.oneapp.productsexpservice.model.portfolio.request.OpenPortfolioRequestBody;
import com.tmb.oneapp.productsexpservice.model.portfolio.request.OpenPortfolioValidationRequest;
import com.tmb.oneapp.productsexpservice.model.portfolio.response.*;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.DepositAccount;
import com.tmb.oneapp.productsexpservice.service.productexperience.OpenPortfolioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.*;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenPortfolioControllerTest {

    @InjectMocks
    private OpenPortfolioController openPortfolioController;

    @Mock
    private TMBLogger<OpenPortfolioControllerTest> logger;

    @Mock
    private OpenPortfolioService openPortfolioService;



    @Test
    void should_return_term_and_condition_body_not_null_when_call_validate_open_portfolio_given_correlation_id_and_open_portfolio_request() throws IOException {
        // Given
        ObjectMapper mapper = new ObjectMapper();

        ValidateOpenPortfolioResponse validateOpenPortfolioResponse = new ValidateOpenPortfolioResponse();
        validateOpenPortfolioResponse.setTermAndCondition(mapper.readValue(Paths.get("src/test/resources/investment/portfolio/termandcondition.json").toFile(),
                TermAndConditionResponseBody.class));
        validateOpenPortfolioResponse.setCustomerInfo(mapper.readValue(Paths.get("src/test/resources/investment/portfolio/customer_info.json").toFile(),
                CustomerInfo.class));
        List<DepositAccount> depositAccountList = new ArrayList<>();
        depositAccountList.add(mapper.readValue(Paths.get("src/test/resources/investment/account/deposit_account.json").toFile(),DepositAccount.class));
        validateOpenPortfolioResponse.setDepositAccountList(depositAccountList);

        TmbOneServiceResponse<ValidateOpenPortfolioResponse> responseService = new TmbOneServiceResponse<ValidateOpenPortfolioResponse>();
        responseService.setStatus(new TmbStatus(SUCCESS_CODE, SUCCESS_MESSAGE, SERVICE_NAME, SUCCESS_MESSAGE));
        responseService.setData(validateOpenPortfolioResponse);

        OpenPortfolioValidationRequest request = OpenPortfolioValidationRequest.builder().build();
        request.setExistingCustomer(true);
        request.setCrmId("23423423423423");
        when(openPortfolioService.validateOpenPortfolioService("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", request))
                .thenReturn(responseService);

        // When
        ResponseEntity<TmbOneServiceResponse<ValidateOpenPortfolioResponse>> actual = openPortfolioController.validateOpenPortfolio("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", request);

        // Then
        assertNotNull(actual.getBody());
    }

    @Test
    void should_return_open_portfolio_validation_response_when_call_create_customer_given_correlation_id_and_customer_request() throws IOException {
        // Given
        CustomerRequest customerRequest = CustomerRequest.builder()
                .crmId("00000007924129")
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
        AccountRedeemResponse accountRedeemResponse = mapper.readValue(Paths.get("src/test/resources/investment/customer/account_redeem.json").toFile(),
                AccountRedeemResponse.class);

        OpenPortfolioValidationResponse openPortfolioValidationResponse = OpenPortfolioValidationResponse.builder()
                .accountPurposeResponse(accountPurposeResponse.getData())
                .accountRedeemResponse(accountRedeemResponse.getData())
                .build();

        when(openPortfolioService.createCustomer("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", customerRequest)).thenReturn(openPortfolioValidationResponse);

        // When
        ResponseEntity<TmbOneServiceResponse<OpenPortfolioValidationResponse>> actual = openPortfolioController.createCustomer("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", customerRequest);

        // Then
        assertNotNull(actual.getBody());
    }

    @Test
    void should_return_portfolio_response_when_call_open_portfolio_given_correlation_id_and_open_portfolio_request() throws IOException, TMBCommonException {
        // Given
        OpenPortfolioRequestBody openPortfolioRequestBody = OpenPortfolioRequestBody.builder()
                .crmId("00000000002914")
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
                .riskProfile("5")
                .portfolioType("TMB_ADVTYPE_10_ADVISORY")
                .purposeTypeCode("TMB_PTFPURPOSE_10_RETIREMENT")
                .portfolioNumber("PT000000000000108261")
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

        when(openPortfolioService.openPortfolio("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", openPortfolioRequestBody)).thenReturn(portfolioResponse);

        // When
        ResponseEntity<TmbOneServiceResponse<PortfolioResponse>> actual = openPortfolioController.openPortfolio("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", openPortfolioRequestBody);

        // Then
        assertNotNull(actual.getBody());
    }
}