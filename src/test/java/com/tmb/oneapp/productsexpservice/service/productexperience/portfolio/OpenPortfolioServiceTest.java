package com.tmb.oneapp.productsexpservice.service.productexperience.portfolio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.activitylog.portfolio.service.OpenPortfolioActivityLogService;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CacheServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerExpServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.mapper.portfolio.OpenPortfolioMapper;
import com.tmb.oneapp.productsexpservice.model.productexperience.client.response.RelationshipResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.client.response.RelationshipResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.account.purpose.response.AccountPurposeResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.account.purpose.response.AccountPurposeResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.account.redeem.response.AccountRedeemResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.account.redeem.response.AccountRedeemResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.occupation.request.OccupationRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.occupation.response.OccupationInquiryResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.occupation.response.OccupationInquiryResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.occupation.response.OccupationResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.occupation.response.OccupationResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.request.CustomerRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.response.CustomerResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.response.CustomerResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.nickname.response.PortfolioNicknameResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.nickname.response.PortfolioNicknameResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.request.OpenPortfolioRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.response.OpenPortfolioResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.response.OpenPortfolioResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.response.OpenPortfolioValidationResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.response.PortfolioResponse;
import com.tmb.oneapp.productsexpservice.service.productexperience.async.InvestmentAsyncService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpenPortfolioServiceTest {

    @Mock
    private TMBLogger<OpenPortfolioServiceTest> logger;

    @Mock
    private InvestmentRequestClient investmentRequestClient;

    @Mock
    private InvestmentAsyncService investmentAsyncService;

    @Mock
    private OpenPortfolioActivityLogService openPortfolioActivityLogService;

    @Mock
    private CustomerExpServiceClient customerExpServiceClient;

    @Mock
    private CacheServiceClient cacheServiceClient;

    @Mock
    private OpenPortfolioMapper openPortfolioMapper;

    @InjectMocks
    private OpenPortfolioService openPortfolioService;

    private final String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";

    private final String crmId = "00000018592884";

    private final String ipAddress = "0.0.0.0";

    @Test
    void should_return_status_0000_and_body_not_null_when_call_create_existing_customer_given_correlation_id_and_crm_id_and_ip_address_and_customer_request() throws Exception {
        // Given
        ObjectMapper mapper = new ObjectMapper();
        CustomerResponse customerResponse = mapper.readValue(Paths.get("src/test/resources/investment/customer/create_customer.json").toFile(),
                CustomerResponse.class);
        TmbOneServiceResponse<CustomerResponseBody> oneServiceCustomerResponse = new TmbOneServiceResponse<>();
        oneServiceCustomerResponse.setData(customerResponse.getData());
        oneServiceCustomerResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                ProductsExpServiceConstant.SUCCESS_MESSAGE,
                ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
        when(investmentRequestClient.createCustomer(any(), anyString(), any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceCustomerResponse));

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
        when(investmentRequestClient.getCustomerAccountRedeem(any(), anyString())).thenReturn(ResponseEntity.ok(oneServiceAccountRedeemResponse));

        OccupationInquiryResponse occupationInquiryResponse = mapper.readValue(Paths.get("src/test/resources/investment/customer/occupation_inquiry.json").toFile(),
                OccupationInquiryResponse.class);
        TmbOneServiceResponse<OccupationInquiryResponseBody> oneServiceOccupationInquiryResponse = new TmbOneServiceResponse<>();
        oneServiceOccupationInquiryResponse.setData(occupationInquiryResponse.getData());
        oneServiceOccupationInquiryResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                ProductsExpServiceConstant.SUCCESS_MESSAGE,
                ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
        when(investmentAsyncService.fetchOccupationInquiry(any(), anyString())).thenReturn(CompletableFuture.completedFuture(oneServiceOccupationInquiryResponse.getData()));

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
                .existingCustomer(true)
                .build();

        String depositAccountReponse = "{\"status\":{\"code\":\"0000\",\"message\":\"success\",\"service\":\"customers-ex-service\",\"description\":\"success\"},\"data\":{\"accountNo\":\"1112469166\",\"accountType\":\"SDA\",\"accountBalance\":\"0.31\",\"branchNameTh\":\"ถนนติวานนท์\",\"branchNameEn\":\"THANONTIWANON\",\"accountName\":\"NAMETEST\",\"ledgerBalance\":\"0.31\",\"interestRate\":\"0.824193\",\"accruedInterest\":\"0.0\",\"accountStatus\":\"Active|ปกติ(Active)\",\"productNameTh\":\"บัญชีโนฟิกซ์\",\"productNameEn\":\"NoFixedAccount\",\"productCode\":\"221\",\"productBonesRateUrl\":\"https://www.tmbbank.com/accounts/savings/tmb-no-fixed-account.html\",\"accountDetailView\":\"5\",\"iconId\":\"https://storage.googleapis.com/oneapp-vit.appspot.com/product/logo/icon_03.png\",\"linkedAccount\":\"\",\"openingDate\":\"9-2021\"}}";
        when(customerExpServiceClient.getAccountDetail(any(), any())).thenReturn(depositAccountReponse);

        // When
        OpenPortfolioValidationResponse actual = openPortfolioService.createCustomer(correlationId, crmId, ipAddress, customerRequest);

        // Then
        assertNotNull(actual);
        verify(openPortfolioActivityLogService).acceptTermAndCondition(anyString(), anyString(), anyString());
    }

    @Test
    void should_return_status_0000_and_body_not_null_when_call_create_new_customer_given_correlation_id_and_crm_id_and_ip_address_and_customer_request() throws Exception {
        // Given
        ObjectMapper mapper = new ObjectMapper();
        CustomerResponse customerResponse = mapper.readValue(Paths.get("src/test/resources/investment/customer/create_customer.json").toFile(),
                CustomerResponse.class);
        TmbOneServiceResponse<CustomerResponseBody> oneServiceCustomerResponse = new TmbOneServiceResponse<>();
        oneServiceCustomerResponse.setData(customerResponse.getData());
        oneServiceCustomerResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                ProductsExpServiceConstant.SUCCESS_MESSAGE,
                ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
        when(investmentRequestClient.createCustomer(any(), anyString(), any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceCustomerResponse));

        AccountPurposeResponse accountPurposeResponse = mapper.readValue(Paths.get("src/test/resources/investment/customer/account_purpose.json").toFile(),
                AccountPurposeResponse.class);
        TmbOneServiceResponse<AccountPurposeResponseBody> oneServiceAccountPurposeResponse = new TmbOneServiceResponse<>();
        oneServiceAccountPurposeResponse.setData(accountPurposeResponse.getData());
        oneServiceAccountPurposeResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                ProductsExpServiceConstant.SUCCESS_MESSAGE,
                ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
        when(investmentAsyncService.fetchAccountPurpose(any())).thenReturn(CompletableFuture.completedFuture(oneServiceAccountPurposeResponse.getData()));

        OccupationInquiryResponse occupationInquiryResponse = mapper.readValue(Paths.get("src/test/resources/investment/customer/occupation_inquiry.json").toFile(),
                OccupationInquiryResponse.class);
        TmbOneServiceResponse<OccupationInquiryResponseBody> oneServiceOccupationInquiryResponse = new TmbOneServiceResponse<>();
        oneServiceOccupationInquiryResponse.setData(occupationInquiryResponse.getData());
        oneServiceOccupationInquiryResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                ProductsExpServiceConstant.SUCCESS_MESSAGE,
                ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
        when(investmentAsyncService.fetchOccupationInquiry(any(), anyString())).thenReturn(CompletableFuture.completedFuture(oneServiceOccupationInquiryResponse.getData()));

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
                .existingCustomer(false)
                .build();

        OccupationInquiryResponseBody occupationInquiry = OccupationInquiryResponseBody.builder()
                .crmId("00000018592884")
                .occupationCode("308")
                .occupationDescription("308 - พนักงานและลูกจ้างบริษัทห้างร้านกิจการอื่นๆ")
                .positionDescription(null)
                .requirePosition("Y")
                .requireUpdate("Y")
                .build();

        // When
        OpenPortfolioValidationResponse actual = openPortfolioService.createCustomer(correlationId, crmId, ipAddress, customerRequest);

        // Then
        OpenPortfolioValidationResponse expected = OpenPortfolioValidationResponse.builder()
                .accountPurposeResponse(accountPurposeResponse.getData())
                .depositAccount(null)
                .occupationInquiryResponse(occupationInquiry)
                .build();

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(openPortfolioActivityLogService).acceptTermAndCondition(anyString(), anyString(), anyString());
    }

    @Test
    void should_throw_common_exception_with_error_and_message_when_call_create_new_customer_given_correlation_id_and_crm_id_and_ip_address_and_customer_request() throws Exception {
        // Given
        TmbOneServiceResponse<CustomerResponseBody> oneServiceCustomerResponse = new TmbOneServiceResponse<>();
        oneServiceCustomerResponse.setStatus(TmbStatusUtil.successStatus());
        when(investmentRequestClient.createCustomer(any(), anyString(), any())).thenReturn(ResponseEntity.status(HttpStatus.OK).headers(TMBUtils.getResponseHeaders()).body(oneServiceCustomerResponse));

        String errorCode = "2000005";
        String errorMessage = "Bad Request";
        when(investmentAsyncService.fetchAccountPurpose(any())).thenThrow(getMockCommonException(errorCode, errorMessage));

        // When
        try {
            CustomerRequest customerRequest = CustomerRequest.builder().build();
            OpenPortfolioValidationResponse actual = openPortfolioService.createCustomer(correlationId, crmId, ipAddress, customerRequest);
        } catch (TMBCommonException e) {

            // Then
            assertEquals(errorCode, e.getErrorCode());
            assertEquals(errorMessage, e.getErrorMessage());
        }
    }

    private TMBCommonException getMockCommonException(String errorCode, String errorMessage) {
        return new TMBCommonException(
                errorCode,
                errorMessage,
                ProductsExpServiceConstant.SERVICE_NAME,
                HttpStatus.BAD_REQUEST, null);
    }

    @Test
    void should_return_null_when_call_create_customer_given_create_customer_failed() throws TMBCommonException {
        // Given
        TmbOneServiceResponse<CustomerResponseBody> oneServiceCustomerResponse = new TmbOneServiceResponse<>();
        oneServiceCustomerResponse.setData(null);
        when(investmentRequestClient.createCustomer(any(), anyString(), any())).thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(TMBUtils.getResponseHeaders()).body(oneServiceCustomerResponse));

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

        // When
        OpenPortfolioValidationResponse actual = openPortfolioService.createCustomer(correlationId, crmId, ipAddress, customerRequest);

        // Then
        assertNull(actual);
        verify(openPortfolioActivityLogService).acceptTermAndCondition(anyString(), anyString(), anyString());
    }

    @Test
    void should_return_status_0000_and_body_and_occupation_not_null_when_call_open_portfolio_given_correlation_id_and_crm_id_and_ip_address_and_open_portfolio_request() throws IOException, TMBCommonException {
        // Given
        ObjectMapper mapper = new ObjectMapper();

        RelationshipResponse relationshipResponse = mapper.readValue(Paths.get("src/test/resources/investment/client/relationship.json").toFile(),
                RelationshipResponse.class);
        TmbOneServiceResponse<RelationshipResponseBody> oneServiceRelationshipResponse = new TmbOneServiceResponse<>();
        oneServiceRelationshipResponse.setData(relationshipResponse.getData());
        oneServiceRelationshipResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                ProductsExpServiceConstant.SUCCESS_MESSAGE,
                ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
        when(investmentAsyncService.updateClientRelationship(any(), anyString(), any())).thenReturn(CompletableFuture.completedFuture(oneServiceRelationshipResponse.getData()));

        OpenPortfolioResponse openPortfolioResponse = mapper.readValue(Paths.get("src/test/resources/investment/portfolio/open_portfolio.json").toFile(),
                OpenPortfolioResponse.class);
        TmbOneServiceResponse<OpenPortfolioResponseBody> oneServiceOpenPortfolioResponse = new TmbOneServiceResponse<>();
        oneServiceOpenPortfolioResponse.setData(openPortfolioResponse.getData());
        oneServiceOpenPortfolioResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                ProductsExpServiceConstant.SUCCESS_MESSAGE,
                ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
        when(investmentAsyncService.openPortfolio(any(), anyString(), any())).thenReturn(CompletableFuture.completedFuture(oneServiceOpenPortfolioResponse.getData()));

        OccupationResponse occupationResponse = mapper.readValue(Paths.get("src/test/resources/investment/customer/occupation_update.json").toFile(),
                OccupationResponse.class);
        TmbOneServiceResponse<OccupationResponseBody> oneServiceOccupationResponse = new TmbOneServiceResponse<>();
        oneServiceOccupationResponse.setData(occupationResponse.getData());
        oneServiceOccupationResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                ProductsExpServiceConstant.SUCCESS_MESSAGE,
                ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
        when(investmentAsyncService.updateOccupation(any(), anyString(), any())).thenReturn(CompletableFuture.completedFuture(oneServiceOccupationResponse.getData()));

        PortfolioNicknameResponse portfolioNicknameResponse = mapper.readValue(Paths.get("src/test/resources/investment/portfolio/nickname.json").toFile(),
                PortfolioNicknameResponse.class);
        TmbOneServiceResponse<PortfolioNicknameResponseBody> oneServicePortfolioNicknameResponse = new TmbOneServiceResponse<>();
        oneServicePortfolioNicknameResponse.setData(portfolioNicknameResponse.getData());
        oneServicePortfolioNicknameResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                ProductsExpServiceConstant.SUCCESS_MESSAGE,
                ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
        when(investmentRequestClient.updatePortfolioNickname(any(), any())).thenReturn(ResponseEntity.ok(oneServicePortfolioNicknameResponse));

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
                .occupationRequest(OccupationRequest.builder()
                        .occupationCode("406")
                        .positionDescription("ผู้ช่วยผู้จัดการ")
                        .build())
                .build();

        // When
        PortfolioResponse actual = openPortfolioService.openPortfolio(correlationId, crmId, ipAddress, openPortfolioRequestBody);

        // Then
        assertNotNull(actual);
        assertNotNull(actual.getOccupationResponse());
        verify(openPortfolioActivityLogService).enterPinIsCorrect(anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        verify(cacheServiceClient, times(2)).deleteCacheByKey(anyString(), anyString());
    }

    @Test
    void should_return_status_0000_and_body_not_null_and_occupation_null_when_call_open_portfolio_given_correlation_id_and_crm_id_and_ip_address_and_occupation_request_null() throws IOException, TMBCommonException {
        // Given
        ObjectMapper mapper = new ObjectMapper();

        RelationshipResponse relationshipResponse = mapper.readValue(Paths.get("src/test/resources/investment/client/relationship.json").toFile(),
                RelationshipResponse.class);
        TmbOneServiceResponse<RelationshipResponseBody> oneServiceRelationshipResponse = new TmbOneServiceResponse<>();
        oneServiceRelationshipResponse.setData(relationshipResponse.getData());
        oneServiceRelationshipResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                ProductsExpServiceConstant.SUCCESS_MESSAGE,
                ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
        when(investmentAsyncService.updateClientRelationship(any(), anyString(), any())).thenReturn(CompletableFuture.completedFuture(oneServiceRelationshipResponse.getData()));

        OpenPortfolioResponse openPortfolioResponse = mapper.readValue(Paths.get("src/test/resources/investment/portfolio/open_portfolio.json").toFile(),
                OpenPortfolioResponse.class);
        TmbOneServiceResponse<OpenPortfolioResponseBody> oneServiceOpenPortfolioResponse = new TmbOneServiceResponse<>();
        oneServiceOpenPortfolioResponse.setData(openPortfolioResponse.getData());
        oneServiceOpenPortfolioResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                ProductsExpServiceConstant.SUCCESS_MESSAGE,
                ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
        when(investmentAsyncService.openPortfolio(any(), anyString(), any())).thenReturn(CompletableFuture.completedFuture(oneServiceOpenPortfolioResponse.getData()));

        PortfolioNicknameResponse portfolioNicknameResponse = mapper.readValue(Paths.get("src/test/resources/investment/portfolio/nickname.json").toFile(),
                PortfolioNicknameResponse.class);
        TmbOneServiceResponse<PortfolioNicknameResponseBody> oneServicePortfolioNicknameResponse = new TmbOneServiceResponse<>();
        oneServicePortfolioNicknameResponse.setData(portfolioNicknameResponse.getData());
        oneServicePortfolioNicknameResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                ProductsExpServiceConstant.SUCCESS_MESSAGE,
                ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
        when(investmentRequestClient.updatePortfolioNickname(any(), any())).thenReturn(ResponseEntity.ok(oneServicePortfolioNicknameResponse));

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

        // When
        PortfolioResponse actual = openPortfolioService.openPortfolio(correlationId, crmId, ipAddress, openPortfolioRequestBody);

        // Then
        assertNotNull(actual);
        assertNull(actual.getOccupationResponse());
        verify(openPortfolioActivityLogService).enterPinIsCorrect(anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        verify(cacheServiceClient, times(2)).deleteCacheByKey(anyString(), anyString());
    }

    @Test
    void should_throw_tmb_common_exception__when_call_open_portfolio_given_correlation_id_and_crm_id_and_ip_address_and_open_portfolio_request() throws TMBCommonException {
        // Given
        String errorCode = "2000009";
        String errorMessage = "Bad Request";
        TmbOneServiceResponse<RelationshipResponseBody> oneServiceRelationshipResponse = new TmbOneServiceResponse<>();
        oneServiceRelationshipResponse.setStatus(getMockBadRequest(errorCode, errorMessage));
        when(investmentAsyncService.updateClientRelationship(any(), anyString(), any())).thenReturn(CompletableFuture.completedFuture(oneServiceRelationshipResponse.getData()));

        // When
        try {
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
            openPortfolioService.openPortfolio(correlationId, crmId, ipAddress, openPortfolioRequestBody);
        } catch (TMBCommonException ex) {

            // Then
            assertEquals(errorCode, ex.getErrorCode());
            assertEquals(errorMessage, ex.getErrorMessage());
        }
    }

    private TmbStatus getMockBadRequest(String errorCode, String errorMessage) {
        return new TmbStatus(errorCode, errorMessage, "investment-service", errorMessage);
    }
}
