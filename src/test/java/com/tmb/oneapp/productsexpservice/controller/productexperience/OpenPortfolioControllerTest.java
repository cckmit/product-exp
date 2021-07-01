package com.tmb.oneapp.productsexpservice.controller.productexperience;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.model.common.teramandcondition.response.TermAndConditionResponseBody;
import com.tmb.oneapp.productsexpservice.model.customer.account.purpose.response.AccountPurposeResponse;
import com.tmb.oneapp.productsexpservice.model.customer.account.redeem.response.AccountRedeemResponse;
import com.tmb.oneapp.productsexpservice.model.customer.request.CustomerRequestBody;
import com.tmb.oneapp.productsexpservice.model.openportfolio.request.OpenPortfolioRequest;
import com.tmb.oneapp.productsexpservice.model.openportfolio.response.CustomerInfo;
import com.tmb.oneapp.productsexpservice.model.openportfolio.response.OpenPortfolioResponse;
import com.tmb.oneapp.productsexpservice.model.openportfolio.response.ValidateOpenPortfolioResponse;
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
    void should_term_and_condition_body_not_null_when_call_validate_open_portfolio_give_correlation_id_and_open_portfolio_request() throws IOException {
        // Given
        ObjectMapper mapper = new ObjectMapper();
        OpenPortfolioRequest openPortfolioRequest = OpenPortfolioRequest.builder().crmId("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da").build();
        TmbOneServiceResponse<ValidateOpenPortfolioResponse> responseService = new TmbOneServiceResponse<ValidateOpenPortfolioResponse>();
        responseService.setStatus(new TmbStatus(SUCCESS_CODE, SUCCESS_MESSAGE, SERVICE_NAME, SUCCESS_MESSAGE));
        ValidateOpenPortfolioResponse validateOpenPortfolioResponse = new ValidateOpenPortfolioResponse();

        validateOpenPortfolioResponse.setTermAndCondition(mapper.readValue(Paths.get("src/test/resources/investment/openportfolio/termandcondition.json").toFile(),
                TermAndConditionResponseBody.class));
        validateOpenPortfolioResponse.setCustomerInfo(mapper.readValue(Paths.get("src/test/resources/investment/openportfolio/customer_info.json").toFile(),
                CustomerInfo.class));
        List<DepositAccount> depositAccountList = new ArrayList<>();
        depositAccountList.add(mapper.readValue(Paths.get("src/test/resources/investment/account/deposit_account.json").toFile(),DepositAccount.class));
        validateOpenPortfolioResponse.setDepositAccountList(depositAccountList);
        responseService.setData(validateOpenPortfolioResponse);
        when(openPortfolioService.validateOpenPortfolioService("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", openPortfolioRequest))
                .thenReturn(responseService);

        // When
        ResponseEntity<TmbOneServiceResponse<ValidateOpenPortfolioResponse>> actual = openPortfolioController.validateOpenPortfolio("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", openPortfolioRequest);

        // Then
        assertNotNull(actual.getBody());
    }

    @Test
    void should_open_portfolio_response_when_call_create_customer_give_correlation_id_and_customer_request() throws IOException {
        // Given
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

        ObjectMapper mapper = new ObjectMapper();
        AccountPurposeResponse accountPurposeResponse = mapper.readValue(Paths.get("src/test/resources/investment/customer/account_purpose.json").toFile(),
                AccountPurposeResponse.class);
        AccountRedeemResponse accountRedeemResponse = mapper.readValue(Paths.get("src/test/resources/investment/customer/account_redeem.json").toFile(),
                AccountRedeemResponse.class);

        OpenPortfolioResponse openPortfolioResponse = OpenPortfolioResponse.builder()
                .accountPurposeResponseBody(accountPurposeResponse.getData())
                .accountRedeemResponseBody(accountRedeemResponse.getData())
                .build();

        when(openPortfolioService.createCustomer("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", customerRequestBody)).thenReturn(openPortfolioResponse);

        // When
        ResponseEntity<TmbOneServiceResponse<OpenPortfolioResponse>> actual = openPortfolioController.createCustomer("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", customerRequestBody);

        // Then
        assertNotNull(actual.getBody());
    }
}