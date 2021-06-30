package com.tmb.oneapp.productsexpservice.service.productexperience;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.mapper.portfolio.OpenPortfolioMapper;
import com.tmb.oneapp.productsexpservice.model.client.response.RelationshipResponse;
import com.tmb.oneapp.productsexpservice.model.client.response.RelationshipResponseBody;
import com.tmb.oneapp.productsexpservice.model.common.teramandcondition.response.TermAndConditionResponse;
import com.tmb.oneapp.productsexpservice.model.common.teramandcondition.response.TermAndConditionResponseBody;
import com.tmb.oneapp.productsexpservice.model.customer.account.purpose.response.AccountPurposeResponse;
import com.tmb.oneapp.productsexpservice.model.customer.account.purpose.response.AccountPurposeResponseBody;
import com.tmb.oneapp.productsexpservice.model.customer.account.redeem.response.AccountRedeemResponse;
import com.tmb.oneapp.productsexpservice.model.customer.account.redeem.response.AccountRedeemResponseBody;
import com.tmb.oneapp.productsexpservice.model.customer.request.CustomerRequest;
import com.tmb.oneapp.productsexpservice.model.customer.response.CustomerResponse;
import com.tmb.oneapp.productsexpservice.model.customer.response.CustomerResponseBody;
import com.tmb.oneapp.productsexpservice.model.portfolio.nickname.response.PortfolioNicknameResponse;
import com.tmb.oneapp.productsexpservice.model.portfolio.nickname.response.PortfolioNicknameResponseBody;
import com.tmb.oneapp.productsexpservice.model.portfolio.request.OpenPortfolioRequest;
import com.tmb.oneapp.productsexpservice.model.portfolio.request.OpenPortfolioValidationRequest;
import com.tmb.oneapp.productsexpservice.model.portfolio.response.OpenPortfolioResponse;
import com.tmb.oneapp.productsexpservice.model.portfolio.response.OpenPortfolioResponseBody;
import com.tmb.oneapp.productsexpservice.model.portfolio.response.OpenPortfolioValidationResponse;
import com.tmb.oneapp.productsexpservice.model.portfolio.response.PortfolioResponse;
import com.tmb.oneapp.productsexpservice.service.productexperience.async.InvestmentAsyncService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenPortfolioServiceTest {

    @Mock
    private TMBLogger<OpenPortfolioServiceTest> logger;

    @Mock
    private CommonServiceClient commonServiceClient;

    @Mock
    private InvestmentRequestClient investmentRequestClient;

    @Mock
    private InvestmentAsyncService investmentAsyncService;

    @Mock
    private OpenPortfolioMapper openPortfolioMapper;

    @InjectMocks
    private OpenPortfolioService openPortfolioService;

    @Test
    void should_return_status_0000_and_body_not_null_when_call_validation_give_correlation_id_and_open_portfolio_validation_request() throws IOException {
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

        OpenPortfolioValidationRequest openPortfolioValidationRequest = OpenPortfolioValidationRequest.builder().crmId("001100000000000000000012035644").build();

        // When
        ResponseEntity<TmbOneServiceResponse<TermAndConditionResponseBody>> actual = openPortfolioService.validateOpenPortfolio("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", openPortfolioValidationRequest);

        // Then
        assertEquals("0000", actual.getBody().getStatus().getCode());
        assertNotNull(actual.getBody());
    }

    @Test
    void should_return_status_0000_and_body_not_null_when_call_create_customer_give_correlation_id_and_customer_request() throws IOException, TMBCommonException {
        // Given
        ObjectMapper mapper = new ObjectMapper();
        CustomerResponse customerResponse = mapper.readValue(Paths.get("src/test/resources/investment/customer/create_customer.json").toFile(),
                CustomerResponse.class);
        TmbOneServiceResponse<CustomerResponseBody> oneServiceCustomerResponse = new TmbOneServiceResponse<>();
        oneServiceCustomerResponse.setData(customerResponse.getData());
        oneServiceCustomerResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                ProductsExpServiceConstant.SUCCESS_MESSAGE,
                ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
        when(investmentRequestClient.createCustomer(any(), any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceCustomerResponse));

        AccountPurposeResponse accountPurposeResponse = mapper.readValue(Paths.get("src/test/resources/investment/customer/account_purpose.json").toFile(),
                AccountPurposeResponse.class);
        TmbOneServiceResponse<AccountPurposeResponseBody> oneServiceAccountPurposeResponse = new TmbOneServiceResponse<>();
        oneServiceAccountPurposeResponse.setData(accountPurposeResponse.getData());
        oneServiceAccountPurposeResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                ProductsExpServiceConstant.SUCCESS_MESSAGE,
                ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
        when(investmentAsyncService.fetchAccountPurpose(any())).thenReturn(CompletableFuture.completedFuture(oneServiceAccountPurposeResponse.getData()));

        AccountRedeemResponse accountRedeemResponse = mapper.readValue(Paths.get("src/test/resources/investment/customer/account_redeem.json").toFile(),
                AccountRedeemResponse.class);
        TmbOneServiceResponse<AccountRedeemResponseBody> oneServiceAccountRedeemResponse = new TmbOneServiceResponse<>();
        oneServiceAccountRedeemResponse.setData(accountRedeemResponse.getData());
        oneServiceAccountRedeemResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                ProductsExpServiceConstant.SUCCESS_MESSAGE,
                ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
        when(investmentAsyncService.fetchAccountRedeem(any(), any())).thenReturn(CompletableFuture.completedFuture(oneServiceAccountRedeemResponse.getData()));

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

        // When
        OpenPortfolioValidationResponse actual = openPortfolioService.createCustomer("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", customerRequest);

        // Then
        OpenPortfolioValidationResponse expected = OpenPortfolioValidationResponse.builder()
                .accountPurposeResponse(accountPurposeResponse.getData())
                .accountRedeemResponse(accountRedeemResponse.getData())
                .build();

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void should_return_null_when_call_create_customer_give_create_customer_failed() {
        // Given
        TmbOneServiceResponse<CustomerResponseBody> oneServiceCustomerResponse = new TmbOneServiceResponse<>();
        oneServiceCustomerResponse.setData(null);
        when(investmentRequestClient.createCustomer(any(), any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceCustomerResponse));

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

        // When
        OpenPortfolioValidationResponse actual = openPortfolioService.createCustomer("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", customerRequest);

        // Then
        assertNull(actual);
    }

    @Test
    void should_return_status_0000_and_body_not_null_when_call_open_portfolio_give_correlation_id_and_open_portfolio_request() throws IOException, TMBCommonException {
        // Given
        ObjectMapper mapper = new ObjectMapper();

        RelationshipResponse relationshipResponse = mapper.readValue(Paths.get("src/test/resources/investment/client/relationship.json").toFile(),
                RelationshipResponse.class);
        TmbOneServiceResponse<RelationshipResponseBody> oneServiceRelationshipResponse = new TmbOneServiceResponse<>();
        oneServiceRelationshipResponse.setData(relationshipResponse.getData());
        oneServiceRelationshipResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                ProductsExpServiceConstant.SUCCESS_MESSAGE,
                ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
        when(investmentAsyncService.updateClientRelationship(any(), any())).thenReturn(CompletableFuture.completedFuture(oneServiceRelationshipResponse.getData()));

        OpenPortfolioResponse openPortfolioResponse = mapper.readValue(Paths.get("src/test/resources/investment/portfolio/open_portfolio.json").toFile(),
                OpenPortfolioResponse.class);
        TmbOneServiceResponse<OpenPortfolioResponseBody> oneServiceOpenPortfolioResponse = new TmbOneServiceResponse<>();
        oneServiceOpenPortfolioResponse.setData(openPortfolioResponse.getData());
        oneServiceOpenPortfolioResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                ProductsExpServiceConstant.SUCCESS_MESSAGE,
                ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
        when(investmentAsyncService.openPortfolio(any(), any())).thenReturn(CompletableFuture.completedFuture(oneServiceOpenPortfolioResponse.getData()));

        PortfolioNicknameResponse portfolioNicknameResponse = mapper.readValue(Paths.get("src/test/resources/investment/portfolio/nickname.json").toFile(),
                PortfolioNicknameResponse.class);
        TmbOneServiceResponse<PortfolioNicknameResponseBody> oneServicePortfolioNicknameResponse = new TmbOneServiceResponse<>();
        oneServicePortfolioNicknameResponse.setData(portfolioNicknameResponse.getData());
        oneServicePortfolioNicknameResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                ProductsExpServiceConstant.SUCCESS_MESSAGE,
                ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
        when(investmentAsyncService.updatePortfolioNickname(any(), any())).thenReturn(CompletableFuture.completedFuture(oneServicePortfolioNicknameResponse.getData()));

        OpenPortfolioRequest openPortfolioRequest = OpenPortfolioRequest.builder()
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

        // When
        PortfolioResponse actual = openPortfolioService.openPortfolio("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", openPortfolioRequest);

        // Then
        assertNotNull(actual);
    }
}