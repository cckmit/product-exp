package com.tmb.oneapp.productsexpservice.service.productexperience.alternative;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.model.*;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.enums.AlternativeBuySellSwitchDcaErrorEnums;
import com.tmb.oneapp.productsexpservice.enums.AlternativeOpenPortfolioErrorEnums;
import com.tmb.oneapp.productsexpservice.feignclients.AccountRequestClient;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.customer.calculaterisk.response.EkycRiskCalculateResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.BuyFlowFirstTrade;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.response.servicehour.ValidateServiceHourResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.account.redeem.response.AccountRedeemResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.AddressWithPhone;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.model.request.fundrule.FundRuleRequestBody;
import com.tmb.oneapp.productsexpservice.model.response.fundfactsheet.FundResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.DepositAccount;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleResponse;
import com.tmb.oneapp.productsexpservice.model.response.suitability.SuitabilityInfo;
import com.tmb.oneapp.productsexpservice.service.ProductExpAsyncService;
import com.tmb.oneapp.productsexpservice.service.ProductsExpService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AlternativeServiceTest {

    @Mock
    private ProductsExpService productsExpService;

    @Mock
    private CommonServiceClient commonServiceClient;

    @Mock
    private CustomerServiceClient customerServiceClient;

    @Mock
    private AccountRequestClient accountRequestClient;

    @Mock
    private InvestmentRequestClient investmentRequestClient;

    @Mock
    private ProductExpAsyncService productExpAsyncService;

    @InjectMocks
    private AlternativeService alternativeService;

    private final String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";

    private void mockCommonConfig() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<CommonData> list = new ArrayList<>();
        TmbOneServiceResponse<List<CommonData>> response = new TmbOneServiceResponse<List<CommonData>>();
        list.add(objectMapper.readValue(Paths.get("src/test/resources/investment/common/investment_config.json").toFile(), CommonData.class));
        response.setData(list);
        when(commonServiceClient.getCommonConfig(any(), any())).thenReturn(ResponseEntity.ok(response));
    }

    private void mockNotPassServiceHour() {
        TmbOneServiceResponse<List<CommonData>> responseCommon = new TmbOneServiceResponse<>();
        FundResponse fundResponse = new FundResponse();
        fundResponse.setError(true);
        CommonData commonData = new CommonData();
        CommonTime commonTime = new CommonTime();
        List<CommonData> commonDataList = new ArrayList<>();

        commonTime.setStart("00:00");
        commonTime.setEnd("00:00");
        commonData.setNoneServiceHour(commonTime);
        commonDataList.add(commonData);

        responseCommon.setData(commonDataList);
        responseCommon.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                ProductsExpServiceConstant.SUCCESS_MESSAGE,
                ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

        when(commonServiceClient.getCommonConfigByModule(anyString(), anyString())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(responseCommon));
    }

    @Test
    void should_return_status_code_2000001_when_call_validate_service_hour() {
        // Given
        mockNotPassServiceHour();

        // When
        ValidateServiceHourResponse actual = alternativeService.validateServiceHour(correlationId, TmbStatusUtil.successStatus());

        // Then
        assertEquals(AlternativeOpenPortfolioErrorEnums.NOT_IN_SERVICE_HOUR.getCode(), actual.getCode());
        assertEquals(AlternativeOpenPortfolioErrorEnums.NOT_IN_SERVICE_HOUR.getMessage(), actual.getMessage());
        assertEquals(AlternativeOpenPortfolioErrorEnums.NOT_IN_SERVICE_HOUR.getDescription(), actual.getDescription());
        assertEquals("00:00", actual.getStartTime());
        assertEquals("00:00", actual.getEndTime());
    }

    @Test
    void should_return_status_code_2000025_when_call_validate_age_is_not_over_twenty() {
        // Given
        // When
        TmbStatus actual = alternativeService.validateDateNotOverTwentyYearOld("2010-07-08", TmbStatusUtil.successStatus());

        // Then
        assertEquals(AlternativeOpenPortfolioErrorEnums.AGE_NOT_OVER_TWENTY.getCode(), actual.getCode());
        assertEquals(AlternativeOpenPortfolioErrorEnums.AGE_NOT_OVER_TWENTY.getMessage(), actual.getMessage());
        assertEquals(AlternativeOpenPortfolioErrorEnums.AGE_NOT_OVER_TWENTY.getDescription(), actual.getDescription());
    }

    @Test
    void should_return_status_code_2000003_when_call_validate_casa_dormant() {
        // Given
        String accountResponse = "{\n" +
                "\t\"status\": {\n" +
                "\t\t\"code\": \"0000\",\n" +
                "\t\t\"message\": \"success\",\n" +
                "\t\t\"service\": \"accounts-service\"\n" +
                "\t},\n" +
                "\t\"data\": [{\n" +
                "\t\t\"product_name_Eng\": \"All Free Account\",\n" +
                "\t\t\"product_name_TH\": \"บัญชีออลล์ฟรี\",\n" +
                "\t\t\"product_code\": \"225\",\n" +
                "\t\t\"balance_currency\": \"THB\",\n" +
                "\t\t\"current_balance\": \"0.00\",\n" +
                "\t\t\"account_number\": \"00001102416367\",\n" +
                "\t\t\"relationship_code\": \"PRIIND\",\n" +
                "\t\t\"account_status_code\": \"1\",\n" +
                "\t\t\"account_status_text\": \"ACTIVE\"\n" +
                "\t}, {\n" +
                "\t\t\"product_name_Eng\": \"No Fixed Account\",\n" +
                "\t\t\"product_name_TH\": \"บัญชีโนฟิกซ์\",\n" +
                "\t\t\"product_code\": \"221\",\n" +
                "\t\t\"balance_currency\": \"THB\",\n" +
                "\t\t\"current_balance\": \"922963.66\",\n" +
                "\t\t\"account_number\": \"00001102416458\",\n" +
                "\t\t\"relationship_code\": \"PRIIND\",\n" +
                "\t\t\"account_status_code\": \"2\",\n" +
                "\t\t\"account_status_text\": \"ACTIVE\"\n" +
                "\t}]\n" +
                "}";
        when(accountRequestClient.getAccountList(any(), any())).thenReturn(accountResponse);

        // When
        TmbStatus actual = alternativeService.validateCASADormant("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", "00000018592884", TmbStatusUtil.successStatus());

        // Then
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CASA_DORMANT.getCode(), actual.getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CASA_DORMANT.getMessage(), actual.getMessage());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CASA_DORMANT.getDescription(), actual.getDescription());
    }

    @Test
    void should_return_status_code_2000004_when_call_validate_suitability_expired() throws Exception {
        // Given
        TmbOneServiceResponse<SuitabilityInfo> suitabilityInfo = new TmbOneServiceResponse<>();
        ObjectMapper mapper = new ObjectMapper();
        SuitabilityInfo suitabilityInfoResponse = mapper.readValue(Paths.get("src/test/resources/investment/suitability/suitabilityinfo.json").toFile(), SuitabilityInfo.class);
        suitabilityInfo.setData(suitabilityInfoResponse);

        when(investmentRequestClient.callInvestmentFundSuitabilityService(any(), any())).thenReturn(ResponseEntity.ok(suitabilityInfo));

        // When
        TmbStatus actual = alternativeService.validateSuitabilityExpired("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", "00000018592884", TmbStatusUtil.successStatus());

        // Then
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_SUIT_EXPIRED.getCode(), actual.getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_SUIT_EXPIRED.getMessage(), actual.getMessage());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_SUIT_EXPIRED.getDescription(), actual.getDescription());
    }

    @Test
    void should_return_status_code_2000009_when_call_validate_id_card_expired() throws Exception {
        // Given
        CustGeneralProfileResponse fundHolidayBody;
        ObjectMapper mapper = new ObjectMapper();
        fundHolidayBody = mapper.readValue(Paths.get("src/test/resources/investment/customer/customers_profile_idcard_expired.json").toFile(), CustGeneralProfileResponse.class);

        when(productExpAsyncService.fetchCustomerProfile(anyString())).thenReturn(CompletableFuture.completedFuture(fundHolidayBody));
        // When
        TmbStatus actual = alternativeService.validateIdCardExpired("2021-01-01", TmbStatusUtil.successStatus());

        // Then
        assertEquals("2000009", actual.getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.ID_CARD_EXPIRED.getCode(), actual.getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.ID_CARD_EXPIRED.getMessage(), actual.getMessage());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.ID_CARD_EXPIRED.getDescription(), actual.getDescription());
    }

    @Test
    void should_return_status_code_2000045_when_call_validate_kyc_flag_invalid() throws Exception {
        /// When
        TmbStatus actual = alternativeService.validateKycFlag("Z", "CI", TmbStatusUtil.successStatus());

        // Then
        assertEquals("2000045", actual.getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.FAILED_VERIFY_KYC.getCode(), actual.getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.FAILED_VERIFY_KYC.getMessage(), actual.getMessage());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.FAILED_VERIFY_KYC.getDescription(), actual.getDescription());
    }

    @Test
    void should_return_status_code_2000019_when_call_validate_no_casa_active() {
        // Given
        DepositAccount depositAccount = new DepositAccount();
        depositAccount.setProductNameTH("บัญชีออลล์ฟรี");
        depositAccount.setProductNameEN("TMB All Free Account");
        depositAccount.setAccountNumber("1102416367");
        depositAccount.setAccountStatus("ACTIVE");
        depositAccount.setAccountType("S");
        depositAccount.setAccountTypeShort("SDA");
        depositAccount.setAccountStatusCode(ProductsExpServiceConstant.DORMANT_STATUS_CODE);
        depositAccount.setAvailableBalance(new BigDecimal("1033583777.38"));

        // When
        TmbStatus actual = alternativeService.validateCasaAccountActiveOnce(newArrayList(depositAccount), TmbStatusUtil.successStatus());

        // Then
        assertEquals(AlternativeOpenPortfolioErrorEnums.NO_ACTIVE_CASA_ACCOUNT.getCode(), actual.getCode());
        assertEquals(AlternativeOpenPortfolioErrorEnums.NO_ACTIVE_CASA_ACCOUNT.getMessage(), actual.getMessage());
        assertEquals(AlternativeOpenPortfolioErrorEnums.NO_ACTIVE_CASA_ACCOUNT.getDescription(), actual.getDescription());
    }

    @Test
    void should_return_status_code_2000018_when_call_validate_risk_level_not_valid_at_buy_flow_and_first_trade() {
        // Given
        List<CommonData> list = new ArrayList<>();
        TmbOneServiceResponse<List<CommonData>> commonResponse = new TmbOneServiceResponse<List<CommonData>>();
        CommonData commonData = new CommonData();
        commonData.setEnableCalRisk("Y");
        list.add(commonData);
        commonResponse.setData(list);
        when(commonServiceClient.getCommonConfig(any(), any())).thenReturn(ResponseEntity.ok(commonResponse));

        TmbServiceResponse<EkycRiskCalculateResponse> response = new TmbServiceResponse<>();
        response.setData(EkycRiskCalculateResponse.builder().maxRisk("B3").maxRiskRM("B3").build());
        when(customerServiceClient.customerEkycRiskCalculate(any(), any())).thenReturn(ResponseEntity.ok(response));

        // When
        CustomerSearchResponse customerSearchResponse = CustomerSearchResponse
                .builder()
                .businessTypeCode("22")
                .officeAddressData(AddressWithPhone.builder().build())
                .registeredAddressData(AddressWithPhone.builder().build())
                .primaryAddressData(AddressWithPhone.builder().build())
                .build();
        BuyFlowFirstTrade buyFlowFirstTrade = BuyFlowFirstTrade.builder().isBuyFlow(true).isFirstTrade(true).build();
        TmbStatus actual = alternativeService.validateCustomerRiskLevel(correlationId, customerSearchResponse, TmbStatusUtil.successStatus(), buyFlowFirstTrade);

        // Then
        assertEquals(AlternativeOpenPortfolioErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getCode(), actual.getCode());
        assertEquals(AlternativeOpenPortfolioErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getMessage(), actual.getMessage());
        assertEquals(AlternativeOpenPortfolioErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getDescription(), actual.getDescription());
    }

    @Test
    void should_return_status_code_2000018_when_call_validate_risk_level_not_valid_at_buy_flow_only() {
        // Given
        List<CommonData> list = new ArrayList<>();
        TmbOneServiceResponse<List<CommonData>> commonResponse = new TmbOneServiceResponse<List<CommonData>>();
        CommonData commonData = new CommonData();
        commonData.setEnableCalRisk("Y");
        list.add(commonData);
        commonResponse.setData(list);
        when(commonServiceClient.getCommonConfig(any(), any())).thenReturn(ResponseEntity.ok(commonResponse));

        TmbServiceResponse<EkycRiskCalculateResponse> response = new TmbServiceResponse<>();
        response.setData(EkycRiskCalculateResponse.builder().maxRisk("C3").maxRiskRM("C3").build());
        when(customerServiceClient.customerEkycRiskCalculate(any(), any())).thenReturn(ResponseEntity.ok(response));

        // When
        CustomerSearchResponse customerSearchResponse = CustomerSearchResponse
                .builder()
                .businessTypeCode("22")
                .officeAddressData(AddressWithPhone.builder().build())
                .registeredAddressData(AddressWithPhone.builder().build())
                .primaryAddressData(AddressWithPhone.builder().build())
                .build();
        BuyFlowFirstTrade buyFlowFirstTrade = BuyFlowFirstTrade.builder().isBuyFlow(true).isFirstTrade(false).build();
        TmbStatus actual = alternativeService.validateCustomerRiskLevel(correlationId, customerSearchResponse, TmbStatusUtil.successStatus(), buyFlowFirstTrade);

        // Then
        assertEquals(AlternativeOpenPortfolioErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getCode(), actual.getCode());
        assertEquals(AlternativeOpenPortfolioErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getMessage(), actual.getMessage());
        assertEquals(AlternativeOpenPortfolioErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getDescription(), actual.getDescription());
    }

    @Test
    void should_return_status_code_2000018_when_call_validate_risk_level_not_valid_at_open_port_flow() {
        // Given
        List<CommonData> list = new ArrayList<>();
        TmbOneServiceResponse<List<CommonData>> commonResponse = new TmbOneServiceResponse<List<CommonData>>();
        CommonData commonData = new CommonData();
        commonData.setEnableCalRisk("Y");
        list.add(commonData);
        commonResponse.setData(list);
        when(commonServiceClient.getCommonConfig(any(), any())).thenReturn(ResponseEntity.ok(commonResponse));

        TmbServiceResponse<EkycRiskCalculateResponse> response = new TmbServiceResponse<>();
        response.setData(EkycRiskCalculateResponse.builder().maxRisk("B3").maxRiskRM("B3").build());
        when(customerServiceClient.customerEkycRiskCalculate(any(), any())).thenReturn(ResponseEntity.ok(response));

        // When
        CustomerSearchResponse customerSearchResponse = CustomerSearchResponse
                .builder()
                .businessTypeCode("22")
                .officeAddressData(AddressWithPhone.builder().build())
                .registeredAddressData(AddressWithPhone.builder().build())
                .primaryAddressData(AddressWithPhone.builder().build())
                .build();
        BuyFlowFirstTrade buyFlowFirstTrade = BuyFlowFirstTrade.builder().isBuyFlow(false).isFirstTrade(false).build();
        TmbStatus actual = alternativeService.validateCustomerRiskLevel(correlationId, customerSearchResponse, TmbStatusUtil.successStatus(), buyFlowFirstTrade);

        // Then
        assertEquals(AlternativeOpenPortfolioErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getCode(), actual.getCode());
        assertEquals(AlternativeOpenPortfolioErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getMessage(), actual.getMessage());
        assertEquals(AlternativeOpenPortfolioErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getDescription(), actual.getDescription());
    }

    @Test
    void for_enable_calrisk_flag_should_return_status_code_0000_when_call_validate_risk_level_not_valid_at_sell_flow() {
        // Given
        List<CommonData> list = new ArrayList<>();
        TmbOneServiceResponse<List<CommonData>> commonResponse = new TmbOneServiceResponse<List<CommonData>>();
        CommonData commonData = new CommonData();
        commonData.setEnableCalRisk("Y");
        list.add(commonData);
        commonResponse.setData(list);
        when(commonServiceClient.getCommonConfig(any(), any())).thenReturn(ResponseEntity.ok(commonResponse));

        TmbServiceResponse<EkycRiskCalculateResponse> response = new TmbServiceResponse<>();
        response.setData(EkycRiskCalculateResponse.builder().maxRisk("B3").maxRiskRM("A3").build());
        when(customerServiceClient.customerEkycRiskCalculate(any(), any())).thenReturn(ResponseEntity.ok(response));

        // When
        CustomerSearchResponse customerSearchResponse = CustomerSearchResponse
                .builder()
                .businessTypeCode("22")
                .officeAddressData(AddressWithPhone.builder().build())
                .registeredAddressData(AddressWithPhone.builder().build())
                .primaryAddressData(AddressWithPhone.builder().build())
                .build();
        BuyFlowFirstTrade buyFlowFirstTrade = BuyFlowFirstTrade.builder().isBuyFlow(false).isFirstTrade(true).build();
        TmbStatus actual = alternativeService.validateCustomerRiskLevel(correlationId, customerSearchResponse, TmbStatusUtil.successStatus(), buyFlowFirstTrade);

        // Then
        assertEquals(ProductsExpServiceConstant.SUCCESS_CODE, actual.getCode());
        assertEquals(ProductsExpServiceConstant.SUCCESS_MESSAGE, actual.getMessage());
        assertEquals(ProductsExpServiceConstant.SUCCESS_MESSAGE, actual.getDescription());
    }

    @Test
    void for_disable_calrisk_flag_should_return_status_code_0000_when_call_validate_risk_level_not_valid_at_sell_flow() {
        // Given
        List<CommonData> list = new ArrayList<>();
        TmbOneServiceResponse<List<CommonData>> commonResponse = new TmbOneServiceResponse<List<CommonData>>();
        CommonData commonData = new CommonData();
        commonData.setEnableCalRisk("N");
        list.add(commonData);
        commonResponse.setData(list);
        when(commonServiceClient.getCommonConfig(any(), any())).thenReturn(ResponseEntity.ok(commonResponse));

        // When
        CustomerSearchResponse customerSearchResponse = CustomerSearchResponse
                .builder()
                .customerRiskLevel("A3")
                .businessTypeCode("22")
                .officeAddressData(AddressWithPhone.builder().build())
                .registeredAddressData(AddressWithPhone.builder().build())
                .primaryAddressData(AddressWithPhone.builder().build())
                .build();
        BuyFlowFirstTrade buyFlowFirstTrade = BuyFlowFirstTrade.builder().isBuyFlow(false).isFirstTrade(true).build();
        TmbStatus actual = alternativeService.validateCustomerRiskLevel(correlationId, customerSearchResponse, TmbStatusUtil.successStatus(), buyFlowFirstTrade);

        // Then
        assertEquals(ProductsExpServiceConstant.SUCCESS_CODE, actual.getCode());
        assertEquals(ProductsExpServiceConstant.SUCCESS_MESSAGE, actual.getMessage());
        assertEquals(ProductsExpServiceConstant.SUCCESS_MESSAGE, actual.getDescription());
    }

    @Test
    void should_return_status_code_mf999_when_call_validate_risk_level_not_valid() {
        // Given
        TmbServiceResponse<EkycRiskCalculateResponse> response = new TmbServiceResponse<>();
        response.setData(null);
        when(customerServiceClient.customerEkycRiskCalculate(any(), any())).thenReturn(ResponseEntity.ok(response));

        // When
        CustomerSearchResponse customerSearchResponse = CustomerSearchResponse
                .builder()
                .businessTypeCode("22")
                .officeAddressData(AddressWithPhone.builder().build())
                .registeredAddressData(AddressWithPhone.builder().build())
                .primaryAddressData(AddressWithPhone.builder().build())
                .build();
        BuyFlowFirstTrade buyFlowFirstTrade = BuyFlowFirstTrade.builder().isBuyFlow(false).isFirstTrade(true).build();
        TmbStatus actual = alternativeService.validateCustomerRiskLevel(correlationId, customerSearchResponse, TmbStatusUtil.successStatus(), buyFlowFirstTrade);

        // Then
        assertEquals(ProductsExpServiceConstant.SERVICE_NOT_READY, actual.getCode());
        assertEquals(ProductsExpServiceConstant.SERVICE_NOT_READY_MESSAGE, actual.getMessage());
        assertEquals(String.format(ProductsExpServiceConstant.SERVICE_NOT_READY_DESC_MESSAGE, "Customer Cal Risk"), actual.getDescription());
    }

    @Test
    void should_return_error_mf999_when_call_validate_risk_level_not_valid() {
        // Given
        when(customerServiceClient.customerEkycRiskCalculate(any(), any())).thenThrow(FeignException.class);

        // When
        CustomerSearchResponse customerSearchResponse = CustomerSearchResponse
                .builder()
                .businessTypeCode("22")
                .officeAddressData(AddressWithPhone.builder().build())
                .registeredAddressData(AddressWithPhone.builder().build())
                .primaryAddressData(AddressWithPhone.builder().build())
                .build();
        BuyFlowFirstTrade buyFlowFirstTrade = BuyFlowFirstTrade.builder().isBuyFlow(false).isFirstTrade(true).build();
        TmbStatus actual = alternativeService.validateCustomerRiskLevel(correlationId, customerSearchResponse, TmbStatusUtil.successStatus(), buyFlowFirstTrade);

        // Then
        assertEquals(ProductsExpServiceConstant.SERVICE_NOT_READY, actual.getCode());
    }

    @Test
    void should_return_status_code_2000018_when_call_validate_customer_nationality() throws Exception {
        // Given
        mockCommonConfig();

        // When
        TmbStatus actual = alternativeService.validateNationality(correlationId, "TH", "US", TmbStatusUtil.successStatus());

        // Then
        assertEquals(AlternativeOpenPortfolioErrorEnums.CUSTOMER_HAS_US_NATIONALITY_OR_OTHER_THIRTY_RESTRICTED.getCode(), actual.getCode());
        assertEquals(AlternativeOpenPortfolioErrorEnums.CUSTOMER_HAS_US_NATIONALITY_OR_OTHER_THIRTY_RESTRICTED.getMessage(), actual.getMessage());
        assertEquals(AlternativeOpenPortfolioErrorEnums.CUSTOMER_HAS_US_NATIONALITY_OR_OTHER_THIRTY_RESTRICTED.getDescription(), actual.getDescription());
    }


    @Test
    void should_return_status_code_2000013_when_call_validate_account_redemption_given_throw_exception_when_call_investment_service() throws Exception {
        // Given
        mockCommonConfig();
        when(investmentRequestClient.getCustomerAccountRedeem(any(), any())).thenThrow(MockitoException.class);

        // When
        TmbStatus actual = alternativeService.validateAccountRedemption(correlationId, "crmId", TmbStatusUtil.successStatus());

        // Then
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.NO_ACCOUNT_REDEMPTION.getCode(), actual.getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.NO_ACCOUNT_REDEMPTION.getMessage(), actual.getMessage());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.NO_ACCOUNT_REDEMPTION.getDescription(), actual.getDescription());
    }

    @Test
    void should_return_status_code_0000_when_call_validateAccountRedemption() throws Exception {
        // Given
        mockCommonConfig();
        TmbOneServiceResponse<AccountRedeemResponseBody> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        tmbOneServiceResponse.setStatus(TmbStatusUtil.successStatus());
        AccountRedeemResponseBody response = AccountRedeemResponseBody.builder().accountRedeem("1212312121").build();
        tmbOneServiceResponse.setData(response);
        when(investmentRequestClient.getCustomerAccountRedeem(any(), any())).thenReturn(ResponseEntity.ok(tmbOneServiceResponse));

        // When
        TmbStatus actual = alternativeService.validateAccountRedemption(correlationId, "crmid", TmbStatusUtil.successStatus());

        // Then
        assertEquals(ProductsExpServiceConstant.SUCCESS_CODE, actual.getCode());
    }

    @Test
    void should_return_status_code_0000_when_call_validate_fund_off_shelf_given_correlation_id_and_fund_rule_request_body_and_tmb_status_success() throws Exception {
        // Given
        mockCommonConfig();
        TmbOneServiceResponse<FundRuleResponse> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        tmbOneServiceResponse.setStatus(TmbStatusUtil.successStatus());
        when(investmentRequestClient.callInvestmentFundRuleService(any(), any())).thenReturn(ResponseEntity.ok(tmbOneServiceResponse));

        // When
        TmbStatus actual = alternativeService.validateFundOffShelf(correlationId, FundRuleRequestBody.builder().build(), TmbStatusUtil.successStatus());

        // Then
        assertEquals(ProductsExpServiceConstant.SUCCESS_CODE, actual.getCode());
    }

    @Test
    void should_return_status_code_2000005_when_call_validate_fund_off_shelf_given_throw_exception_when_call_investment_service() throws Exception {
        // Given
        mockCommonConfig();
        when(investmentRequestClient.callInvestmentFundRuleService(any(), any())).thenThrow(MockitoException.class);

        // When
        TmbStatus actual = alternativeService.validateFundOffShelf(correlationId, FundRuleRequestBody.builder().build(), TmbStatusUtil.successStatus());

        // Then
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.FUND_OFF_SHELF.getCode(), actual.getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.FUND_OFF_SHELF.getMessage(), actual.getMessage());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.FUND_OFF_SHELF.getDescription(), actual.getDescription());
    }

    @Test
    void should_return_status_code_success_when_call_validateOpenPortfolioService_validate_kyc_and_id_card_expired_given_id_card_not_expired_and_allow_kyc_flag() {
        // Given
        // When
        TmbStatus actual = alternativeService.validateKycAndIdCardExpire("U", "CI", "2121-07-08", TmbStatusUtil.successStatus());

        // Then
        assertEquals(ProductsExpServiceConstant.SUCCESS_CODE, actual.getCode());
        assertEquals(ProductsExpServiceConstant.SUCCESS_MESSAGE, actual.getMessage());
        assertEquals(ProductsExpServiceConstant.SUCCESS_MESSAGE, actual.getDescription());
    }

    @Test
    void should_return_status_code_2000022_when_call_validateOpenPortfolioService_validate_kyc_and_id_card_expired_given_id_card_expired_and_allow_kyc_flag() {
        // Given
        // When
        TmbStatus actual = alternativeService.validateKycAndIdCardExpire("S", "CI", "2021-07-08", TmbStatusUtil.successStatus());

        // Then
        assertEquals(AlternativeOpenPortfolioErrorEnums.FAILED_VERIFY_KYC.getCode(), actual.getCode());
        assertEquals(AlternativeOpenPortfolioErrorEnums.FAILED_VERIFY_KYC.getMessage(), actual.getMessage());
        assertEquals(AlternativeOpenPortfolioErrorEnums.FAILED_VERIFY_KYC.getDescription(), actual.getDescription());
    }

    @Test
    void should_return_status_code_2000022_when_call_validateOpenPortfolioService_validate_kyc_and_id_card_expired_given_id_card_not_expired_and_not_allow_kyc_flag() {
        // Given
        // When
        TmbStatus actual = alternativeService.validateKycAndIdCardExpire("0", "CI", "2121-07-08", TmbStatusUtil.successStatus());

        // Then
        assertEquals(AlternativeOpenPortfolioErrorEnums.FAILED_VERIFY_KYC.getCode(), actual.getCode());
        assertEquals(AlternativeOpenPortfolioErrorEnums.FAILED_VERIFY_KYC.getMessage(), actual.getMessage());
        assertEquals(AlternativeOpenPortfolioErrorEnums.FAILED_VERIFY_KYC.getDescription(), actual.getDescription());
    }

    @Test
    void should_return_status_code_2000022_when_call_validateOpenPortfolioService_validate_kyc_and_id_card_expired_given_id_card_expired_and_not_allow_kyc_flag() {
        // Given
        // When
        TmbStatus actual = alternativeService.validateKycAndIdCardExpire("0", "CI", "2021-07-08", TmbStatusUtil.successStatus());

        // Then
        assertEquals(AlternativeOpenPortfolioErrorEnums.FAILED_VERIFY_KYC.getCode(), actual.getCode());
        assertEquals(AlternativeOpenPortfolioErrorEnums.FAILED_VERIFY_KYC.getMessage(), actual.getMessage());
        assertEquals(AlternativeOpenPortfolioErrorEnums.FAILED_VERIFY_KYC.getDescription(), actual.getDescription());
    }
}
