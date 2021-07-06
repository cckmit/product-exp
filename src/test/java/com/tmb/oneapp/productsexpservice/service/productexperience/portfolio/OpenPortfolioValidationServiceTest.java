package com.tmb.oneapp.productsexpservice.service.productexperience.portfolio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.mapper.customer.CustomerInfoMapper;
import com.tmb.oneapp.productsexpservice.model.common.teramandcondition.response.TermAndConditionResponse;
import com.tmb.oneapp.productsexpservice.model.common.teramandcondition.response.TermAndConditionResponseBody;
import com.tmb.oneapp.productsexpservice.model.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.model.portfolio.request.OpenPortfolioValidationRequest;
import com.tmb.oneapp.productsexpservice.model.portfolio.response.CustomerInfo;
import com.tmb.oneapp.productsexpservice.model.portfolio.response.ValidateOpenPortfolioResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FundResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.DepositAccount;
import com.tmb.oneapp.productsexpservice.service.ProductsExpService;
import com.tmb.oneapp.productsexpservice.service.productexperience.account.EligibleDepositAccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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
    private CustomerInfoMapper customerInfoMapper;

    @InjectMocks
    private OpenPortfolioValidationService openPortfolioValidationService;

    private void mockPassServiceHour() {
        FundResponse fundResponse = new FundResponse();
        fundResponse.setError(false);
        when(productsExpService.isServiceHour(any(), any())).thenReturn(fundResponse);
    }

    private void mockCustomerResponse() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        CustomerSearchResponse customerSearchResponse = objectMapper.readValue(Paths.get("src/test/resources/investment/customer/search_customer.json").toFile(), CustomerSearchResponse.class);
        TmbOneServiceResponse<List<CustomerSearchResponse>> oneServiceResponse = new TmbOneServiceResponse<List<CustomerSearchResponse>>();
        oneServiceResponse.setData(List.of(customerSearchResponse));
        ResponseEntity<TmbOneServiceResponse<List<CustomerSearchResponse>>> response = new ResponseEntity<TmbOneServiceResponse<List<CustomerSearchResponse>>>(
                oneServiceResponse, HttpStatus.OK);

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
        depositAccount.setAccountType("S");
        depositAccount.setAccountTypeShort("SDA");
        depositAccount.setAvailableBalance(new BigDecimal("1033583777.38"));

        when(eligibleDepositAccountService.getEligibleDepositAccounts(any(), any())).thenReturn(newArrayList(depositAccount));
        when(commonServiceClient.getTermAndConditionByServiceCodeAndChannel(any(), any(), any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse));

        OpenPortfolioValidationRequest openPortfolioValidationRequest = OpenPortfolioValidationRequest.builder().crmId("001100000000000000000012035644").existingCustomer(false).build();
        mockPassServiceHour();
        mockCustomerResponse();

        // When
        TmbOneServiceResponse<ValidateOpenPortfolioResponse> actual = openPortfolioValidationService.validateOpenPortfolioService("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", openPortfolioValidationRequest);

        // Then
        assertEquals("0000", actual.getStatus().getCode());
        assertNotNull(actual.getData().getCustomerInfo());
        assertNotNull(actual.getData().getTermsConditions());
        assertNotNull(actual.getData().getDepositAccountList());
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
        mockCustomerResponse();

        // When
        TmbOneServiceResponse<ValidateOpenPortfolioResponse> actual = openPortfolioValidationService.validateOpenPortfolioService("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", openPortfolioValidationRequest);

        // Then
        assertEquals("0000", actual.getStatus().getCode());
        assertNotNull(actual.getData().getCustomerInfo());
        assertNotNull(actual.getData().getTermsConditions());
        assertNull(actual.getData().getDepositAccountList());
    }
}