package com.tmb.oneapp.productsexpservice.service.productexperience;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.CommonData;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.AccountRequestClient;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.mapper.CustomerInfoMapper;
import com.tmb.oneapp.productsexpservice.model.common.teramandcondition.response.TermAndConditionResponse;
import com.tmb.oneapp.productsexpservice.model.common.teramandcondition.response.TermAndConditionResponseBody;
import com.tmb.oneapp.productsexpservice.model.customer.account.purpose.response.AccountPurposeResponse;
import com.tmb.oneapp.productsexpservice.model.customer.account.purpose.response.AccountPurposeResponseBody;
import com.tmb.oneapp.productsexpservice.model.customer.account.redeem.response.AccountRedeemResponse;
import com.tmb.oneapp.productsexpservice.model.customer.account.redeem.response.AccountRedeemResponseBody;
import com.tmb.oneapp.productsexpservice.model.customer.request.CustomerRequestBody;
import com.tmb.oneapp.productsexpservice.model.customer.response.CustomerResponse;
import com.tmb.oneapp.productsexpservice.model.customer.response.CustomerResponseBody;
import com.tmb.oneapp.productsexpservice.model.openportfolio.request.OpenPortfolioRequest;
import com.tmb.oneapp.productsexpservice.model.openportfolio.response.CustomerInfo;
import com.tmb.oneapp.productsexpservice.model.openportfolio.response.OpenPortfolioResponse;
import com.tmb.oneapp.productsexpservice.model.openportfolio.response.ValidateOpenPortfolioResponse;
import com.tmb.oneapp.productsexpservice.model.response.customer.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FundResponse;
import com.tmb.oneapp.productsexpservice.service.ProductExpAsynService;
import com.tmb.oneapp.productsexpservice.service.ProductsExpService;
import com.tmb.oneapp.productsexpservice.service.productexperience.async.InvestmentAsyncService;
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
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenPortfolioServiceTest {

    @InjectMocks
    private OpenPortfolioService openPortfolioService;

    @Mock
    private TMBLogger<OpenPortfolioServiceTest> logger;

    @Mock
    private CommonServiceClient commonServiceClient;

    @Mock
    private InvestmentRequestClient investmentRequestClient;

    @Mock
    private InvestmentAsyncService investmentAsyncService;

    @Mock
    private CustomerServiceClient customerServiceClient;

    @Mock
    private ProductsExpService productsExpService;

    @Mock
    private AccountRequestClient accountRequestClient;

    @Mock
    private ProductExpAsynService productExpAsynService;

    @Mock
    private CustomerInfoMapper customerInfoMapper;

    private void mockPassServiceHour(){
        FundResponse fundResponse = new FundResponse();
        fundResponse.setError(false);
        when(productsExpService.isServiceHour(any(),any())).thenReturn(fundResponse);
    }

    private void mockCustomerResponse() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        CustomerSearchResponse customerSearchResponse = objectMapper.readValue(Paths.get("src/test/resources/investment/customer/search_customer.json").toFile(),CustomerSearchResponse.class);
        TmbOneServiceResponse<List<CustomerSearchResponse>> oneServiceResponse = new TmbOneServiceResponse<List<CustomerSearchResponse>>();
        oneServiceResponse.setData(List.of(customerSearchResponse));
        ResponseEntity<TmbOneServiceResponse<List<CustomerSearchResponse>>> response = new ResponseEntity<TmbOneServiceResponse<List<CustomerSearchResponse>>>(
                oneServiceResponse, HttpStatus.OK);

        CustomerInfo customerInfo = objectMapper.readValue(Paths.get("src/test/resources/investment/openportfolio/customer_info.json").toFile(),CustomerInfo.class);

        when(customerServiceClient.customerSearch(any(), any(), any())).thenReturn(response);
        when(customerInfoMapper.map(any())).thenReturn(customerInfo);
    }

    private void mockAccountResponse() throws IOException, TMBCommonException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<CommonData> commonData = new ArrayList<>();
        commonData.add(objectMapper.readValue(Paths.get("src/test/resources/investment/common/investment_config.json").toFile(),CommonData.class));
        String accountList = "{\"status\":{\"code\":\"0000\",\"message\":\"success\",\"service\":\"accounts-service\",\"description\":{\"en\":\"success\",\"th\":\"success\"}},\"data\":[{\"product_name_Eng\":\"TMB All Free Account\",\"product_name_TH\":\"บัญชีออลล์ฟรี\",\"product_code\":\"225\",\"balance_currency\":\"THB\",\"current_balance\":\"1033583777.38\",\"account_number\":\"00001102416367\",\"relationship_code\":\"PRIIND\",\"account_status_code\":\"0\",\"account_status_text\":\"ACTIVE\",\"product_group_code\":\"SDA\",\"icon_id\":\"/product/logo/icon_01.png\",\"sort_order\":\"10001\",\"allow_transfer_from_account\":\"1\",\"allow_transfer_other_account\":\"1\",\"transfer_own_tmb\":[\"DDA\",\"SDA\",\"CDA\"],\"transfer_other_tmb\":[\"DDA\",\"SDA\"],\"personalized_acct_nickname_EN\":\"TMB All Free Account\",\"personalized_acct_nickname_TH\":\"บัญชีออลล์ฟรี\",\"account_name\":\"MIBITSIE01 LMIB1\",\"isRegisterPromptpay\":\"1\",\"account_number_display\":\"1102416367\",\"allow_transfer_to_promptpay\":\"1\",\"waive_fee_for_promptpay\":\"1\",\"waive_fee_for_promptpay_account\":\"1\"},{\"product_name_Eng\":\"No Fixed Account\",\"product_name_TH\":\"บัญชีโนฟิกซ์\",\"product_code\":\"221\",\"balance_currency\":\"THB\",\"current_balance\":\"922963.66\",\"account_number\":\"00001102416458\",\"relationship_code\":\"PRIIND\",\"account_status_code\":\"0\",\"account_status_text\":\"ACTIVE\",\"product_group_code\":\"SDA\",\"icon_id\":\"/product/logo/icon_03.png\",\"sort_order\":\"10023\",\"allow_transfer_from_account\":\"1\",\"allow_transfer_other_account\":\"0\",\"transfer_own_tmb\":[\"DDA\",\"SDA\",\"CDA\"],\"transfer_other_tmb\":[\"DDA\",\"SDA\"],\"personalized_acct_nickname_EN\":\"No Fixed Account\",\"personalized_acct_nickname_TH\":\"บัญชีโนฟิกซ์\",\"account_name\":\"นาย MIBITSIE01 LMIB1\",\"isRegisterPromptpay\":\"0\",\"account_number_display\":\"1102416458\",\"allow_transfer_to_promptpay\":\"1\",\"waive_fee_for_promptpay\":\"0\",\"waive_fee_for_promptpay_account\":\"0\"},{\"product_name_Eng\":\"Savings Care\",\"product_name_TH\":\"Savings Care\",\"product_code\":\"211\",\"balance_currency\":\"THB\",\"current_balance\":\"5000.00\",\"account_number\":\"00001102416524\",\"relationship_code\":\"PRIIND\",\"account_status_code\":\"0\",\"account_status_text\":\"ACTIVE\",\"product_group_code\":\"SDA\",\"icon_id\":\"/product/logo/icon_05.png\",\"sort_order\":\"10024\",\"allow_transfer_from_account\":\"1\",\"allow_transfer_other_account\":\"0\",\"transfer_own_tmb\":[\"DDA\",\"SDA\",\"CDA\"],\"transfer_other_tmb\":[\"DDA\",\"SDA\"],\"personalized_acct_nickname_EN\":\"Savings Care\",\"personalized_acct_nickname_TH\":\"Savings Care\",\"account_name\":\"นาย MIBITSIE01 LMIB1\",\"isRegisterPromptpay\":\"0\",\"account_number_display\":\"1102416524\",\"allow_transfer_to_promptpay\":\"0\",\"waive_fee_for_promptpay\":\"0\",\"waive_fee_for_promptpay_account\":\"0\"},{\"product_name_Eng\":\"Quick Interest Account 12 Months\",\"product_name_TH\":\"บัญชีดอกเบี้ยด่วน 12 เดือน\",\"product_code\":\"664\",\"balance_currency\":\"THB\",\"current_balance\":\"10000.00\",\"account_number\":\"1103318497\",\"relationship_code\":\"PRIIND\",\"account_status_code\":\"0\",\"account_status_text\":\"ACTIVE\",\"product_group_code\":\"CDA\",\"icon_id\":\"/product/logo/icon_06.png\",\"sort_order\":\"10027\",\"allow_transfer_from_account\":\"1\",\"allow_transfer_other_account\":\"0\",\"transfer_own_tmb\":[\"DDA\",\"SDA\"],\"transfer_other_tmb\":[],\"personalized_acct_nickname_EN\":\"Quick Interest Account 12 Months\",\"personalized_acct_nickname_TH\":\"บัญชีดอกเบี้ยด่วน 12 เดือน\",\"account_name\":\"นาย MIBITSIE01 LMIB1\",\"isRegisterPromptpay\":\"0\",\"account_number_display\":\"1103318497\",\"allow_transfer_to_promptpay\":\"0\",\"waive_fee_for_promptpay\":\"0\",\"waive_fee_for_promptpay_account\":\"0\"}]}";
        when(productExpAsynService.fetchCommonConfigByModule(any(),any())).thenReturn(CompletableFuture.completedFuture(commonData));
        when(accountRequestClient.callCustomerExpService(any(),any())).thenReturn(accountList);
    }

    @Test
    void should_return_status_0000_and_body_not_null_when_call_validation_give_correlation_id_and_open_portfolio_request_with_new_customer() throws IOException, TMBCommonException {
        // Given
        ObjectMapper mapper = new ObjectMapper();
        TermAndConditionResponse termAndConditionResponse = mapper.readValue(Paths.get("src/test/resources/investment/openportfolio/validation.json").toFile(),
                TermAndConditionResponse.class);

        TmbOneServiceResponse<TermAndConditionResponseBody> oneServiceResponse = new TmbOneServiceResponse<>();
        oneServiceResponse.setData(termAndConditionResponse.getData());
        oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                ProductsExpServiceConstant.SUCCESS_MESSAGE,
                ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

        when(commonServiceClient.getTermAndConditionByServiceCodeAndChannel(any(), any(), any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse));

        OpenPortfolioRequest openPortfolioRequest = OpenPortfolioRequest.builder().crmId("001100000000000000000012035644").existingCustomer(true).build();
        mockPassServiceHour();
        mockCustomerResponse();
        mockAccountResponse();

        // When
        TmbOneServiceResponse<ValidateOpenPortfolioResponse> actual = openPortfolioService.validateOpenPortfolioService("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", openPortfolioRequest);

        // Then
        assertEquals("0000", actual.getStatus().getCode());
        assertNotNull(actual.getData().getCustomerInfo());
        assertNotNull(actual.getData().getTermAndCondition());
        assertNotNull(actual.getData().getDepositAccountList());
    }

    @Test
    void should_return_status_0000_and_body_not_null_when_call_validation_give_correlation_id_and_open_portfolio_request_with_exist_customer() throws IOException, TMBCommonException {
        // Given
        ObjectMapper mapper = new ObjectMapper();
        TermAndConditionResponse termAndConditionResponse = mapper.readValue(Paths.get("src/test/resources/investment/openportfolio/validation.json").toFile(),
                TermAndConditionResponse.class);

        TmbOneServiceResponse<TermAndConditionResponseBody> oneServiceResponse = new TmbOneServiceResponse<>();
        oneServiceResponse.setData(termAndConditionResponse.getData());
        oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                ProductsExpServiceConstant.SUCCESS_MESSAGE,
                ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

        when(commonServiceClient.getTermAndConditionByServiceCodeAndChannel(any(), any(), any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse));

        OpenPortfolioRequest openPortfolioRequest = OpenPortfolioRequest.builder().crmId("001100000000000000000012035644").existingCustomer(false).build();
        mockPassServiceHour();
        mockCustomerResponse();

        // When
        TmbOneServiceResponse<ValidateOpenPortfolioResponse> actual = openPortfolioService.validateOpenPortfolioService("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", openPortfolioRequest);

        // Then
        assertEquals("0000", actual.getStatus().getCode());
        assertNotNull(actual.getData().getCustomerInfo());
        assertNotNull(actual.getData().getTermAndCondition());
        assertNull(actual.getData().getDepositAccountList());
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

        CustomerRequestBody customerRequestBody = CustomerRequestBody.builder()
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
        OpenPortfolioResponse actual = openPortfolioService.createCustomer("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", customerRequestBody);

        // Then
        OpenPortfolioResponse expected = OpenPortfolioResponse.builder()
                .accountPurposeResponseBody(accountPurposeResponse.getData())
                .accountRedeemResponseBody(accountRedeemResponse.getData())
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

        CustomerRequestBody customerRequestBody = CustomerRequestBody.builder()
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
        OpenPortfolioResponse actual = openPortfolioService.createCustomer("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", customerRequestBody);

        // Then
        assertNull(actual);
    }
}