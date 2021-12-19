package com.tmb.oneapp.productsexpservice.service.productexperience.alternative;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.activitylog.buy.service.BuyActivityLogService;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.enums.AlternativeBuySellSwitchDcaErrorEnums;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.buy.request.AlternativeBuyRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.response.servicehour.ValidateServiceHourResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.firsttrade.response.FirstTradeResponseBody;
import com.tmb.oneapp.productsexpservice.service.productexperience.customer.CustomerService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BuyAlternativeServiceTest {

    @Mock
    private InvestmentRequestClient investmentRequestClient;

    @Mock
    private CustomerService customerService;

    @Mock
    private AlternativeService alternativeService;

    @Mock
    private BuyActivityLogService buyActivityLogService;

    @InjectMocks
    private BuyAlternativeService buyAlternativeService;

    private static final String correlationId = "correlationID";

    private static final String crmId = "crmId";

    private static final String ipAddress = "0.0.0.0";

    private void mockCustomerInfo(AlternativeBuySellSwitchDcaErrorEnums alternativeEnums) {
        // Given
        CustomerSearchResponse customerSearchResponse = CustomerSearchResponse.builder().build();
        if (alternativeEnums.equals(
                AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY)) {
            customerSearchResponse.setBirthDate("2010-07-08");
        }

        when(customerService.getCustomerInfo(any(), any())).thenReturn(customerSearchResponse);
    }

    private void byPassAllAlternative() {
        TmbStatus successStatus = TmbStatusUtil.successStatus();
        TmbOneServiceResponse<FirstTradeResponseBody> firstTradeResponse = new TmbOneServiceResponse<>();
        firstTradeResponse.setStatus(successStatus);
        firstTradeResponse.setData(FirstTradeResponseBody.builder().firstTradeFlag("Y").build());
        when(investmentRequestClient.getFirstTrade(any(), any())).thenReturn(ResponseEntity.ok(firstTradeResponse));

        ValidateServiceHourResponse statusWithTime = new ValidateServiceHourResponse();
        BeanUtils.copyProperties(successStatus, statusWithTime);

        when(alternativeService.validateServiceHour(any(), any())).thenReturn(statusWithTime);
        when(alternativeService.validateDateNotOverTwentyYearOld(any(), any())).thenReturn(successStatus);
        when(alternativeService.validateCustomerRiskLevel(any(), any(), any(), any())).thenReturn(successStatus);
        when(alternativeService.validateCASADormant(any(), any(), any())).thenReturn(successStatus);
        when(alternativeService.validateSuitabilityExpired(any(), any(), any())).thenReturn(successStatus);
        when(alternativeService.validateIdCardExpired(any(), any())).thenReturn(successStatus);
        when(alternativeService.validateFatcaFlagNotValid(any(), any(), anyString())).thenReturn(successStatus);
    }

    @Test
    void should_return_status_null_when_call_validation_buy_given_correlation_id_and_crm_id_and_ip_address_and_alternative_request() {
        // Given
        // When
        when(customerService.getCustomerInfo(any(), any())).thenThrow(MockitoException.class);
        TmbOneServiceResponse<String> actual = buyAlternativeService.validationBuy(correlationId, crmId, ipAddress, AlternativeBuyRequest.builder().build());

        // Then
        assertNull(actual.getStatus());
        assertNull(actual.getData());
        verify(buyActivityLogService, times(0)).clickPurchaseButtonAtFundFactSheetScreen(anyString(), anyString(), anyString(), any(), any());
    }

    @Test
    void should_return_failed_can_not_buy_fund_when_call_validation_buy_given_correlation_id_and_crm_id_and_ip_address_and_alternative_request() {
        // Given
        byPassAllAlternative();

        // When
        AlternativeBuyRequest alternativeBuyRequest = AlternativeBuyRequest.builder().processFlag("N").build();
        TmbOneServiceResponse<String> actual = buyAlternativeService.validationBuy(correlationId, crmId, ipAddress, alternativeBuyRequest);

        // Then
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CAN_NOT_BUY_FUND.getCode(),
                actual.getStatus().getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CAN_NOT_BUY_FUND.getMessage(),
                actual.getStatus().getMessage());
        verify(buyActivityLogService, times(1)).clickPurchaseButtonAtFundFactSheetScreen(anyString(), anyString(), anyString(), any(), any());
    }

    @Test
    void should_return_failed_validate_service_hour_when_call_validation_buy_given_correlation_id_and_crm_id_and_ip_address_and_alternative_request() {
        // Given
        byPassAllAlternative();
        ValidateServiceHourResponse status = new ValidateServiceHourResponse();
        status.setCode(AlternativeBuySellSwitchDcaErrorEnums.NOT_IN_SERVICE_HOUR.getCode());
        status.setDescription(AlternativeBuySellSwitchDcaErrorEnums.NOT_IN_SERVICE_HOUR.getDescription());
        status.setMessage(AlternativeBuySellSwitchDcaErrorEnums.NOT_IN_SERVICE_HOUR.getMessage());
        status.setService(ProductsExpServiceConstant.SERVICE_NAME);
        status.setStartTime("19:00");
        status.setEndTime("20:00");
        when(alternativeService.validateServiceHour(any(), any())).thenReturn(status);

        // When
        AlternativeBuyRequest alternativeBuyRequest = AlternativeBuyRequest.builder().processFlag("Y").build();
        TmbOneServiceResponse<String> actual = buyAlternativeService.validationBuy(correlationId, crmId, ipAddress, alternativeBuyRequest);

        // Then
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.NOT_IN_SERVICE_HOUR.getCode(),
                actual.getStatus().getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.NOT_IN_SERVICE_HOUR.getMessage(),
                actual.getStatus().getMessage());
        assertEquals("19:00-20:00", (actual.getData()));
        verify(buyActivityLogService, times(1)).clickPurchaseButtonAtFundFactSheetScreen(anyString(), anyString(), anyString(), any(), any());
    }

    @Test
    void should_return_failed_validate_age_not_over_twenty_when_call_validation_buy_given_correlation_id_and_crm_id_and_ip_address_and_alternative_request() {
        // Given
        mockCustomerInfo(AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY);
        byPassAllAlternative();
        TmbStatus status = new TmbStatus();
        status.setCode(AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY.getCode());
        status.setDescription(AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY.getDescription());
        status.setMessage(AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY.getMessage());
        status.setService(ProductsExpServiceConstant.SERVICE_NAME);
        when(alternativeService.validateDateNotOverTwentyYearOld(any(), any())).thenReturn(status);

        // When
        AlternativeBuyRequest alternativeBuyRequest = AlternativeBuyRequest.builder().processFlag("Y").build();
        TmbOneServiceResponse<String> actual = buyAlternativeService.validationBuy(correlationId, crmId, ipAddress, alternativeBuyRequest);

        // Then
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY.getCode(),
                actual.getStatus().getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY.getMessage(),
                actual.getStatus().getMessage());
        verify(buyActivityLogService, times(1)).clickPurchaseButtonAtFundFactSheetScreen(anyString(), anyString(), anyString(), any(), any());
    }

    @Test
    void should_return_failed_customer_risk_level_when_call_validation_buy_given_correlation_id_and_crm_id_and_ip_address_and_alternative_request() {
        // Given
        mockCustomerInfo(AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY);
        byPassAllAlternative();
        TmbStatus status = new TmbStatus();
        status.setCode(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getCode());
        status.setDescription(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getDescription());
        status.setMessage(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getMessage());
        status.setService(ProductsExpServiceConstant.SERVICE_NAME);
        when(alternativeService.validateCustomerRiskLevel(any(), any(), any(), any())).thenReturn(status);

        // When
        AlternativeBuyRequest alternativeBuyRequest = AlternativeBuyRequest.builder().processFlag("Y").build();
        TmbOneServiceResponse<String> actual = buyAlternativeService.validationBuy(correlationId, crmId, ipAddress, alternativeBuyRequest);

        // Then
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getCode(),
                actual.getStatus().getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getMessage(),
                actual.getStatus().getMessage());
        verify(buyActivityLogService, times(1)).clickPurchaseButtonAtFundFactSheetScreen(anyString(), anyString(), anyString(), any(), any());
    }

    @Test
    void should_return_failed_casa_dormant_when_call_validation_buy_given_correlation_id_and_crm_id_and_ip_address_and_alternative_request() {
        // Given
        mockCustomerInfo(AlternativeBuySellSwitchDcaErrorEnums.CASA_DORMANT);
        byPassAllAlternative();
        TmbStatus status = new TmbStatus();
        status.setCode(AlternativeBuySellSwitchDcaErrorEnums.CASA_DORMANT.getCode());
        status.setDescription(AlternativeBuySellSwitchDcaErrorEnums.CASA_DORMANT.getDescription());
        status.setMessage(AlternativeBuySellSwitchDcaErrorEnums.CASA_DORMANT.getMessage());
        status.setService(ProductsExpServiceConstant.SERVICE_NAME);
        when(alternativeService.validateCASADormant(any(), any(), any())).thenReturn(status);

        // When
        AlternativeBuyRequest alternativeBuyRequest = AlternativeBuyRequest.builder().processFlag("Y").build();
        TmbOneServiceResponse<String> actual = buyAlternativeService.validationBuy(correlationId, crmId, ipAddress, alternativeBuyRequest);

        // Then
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CASA_DORMANT.getCode(),
                actual.getStatus().getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CASA_DORMANT.getMessage(),
                actual.getStatus().getMessage());
        verify(buyActivityLogService, times(1)).clickPurchaseButtonAtFundFactSheetScreen(anyString(), anyString(), anyString(), any(), any());
    }

    @Test
    void should_return_failed_customer_suitability_expired_when_call_validation_buy_given_correlation_id_and_crm_id_and_ip_address_and_alternative_request() {
        // Given
        mockCustomerInfo(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_SUIT_EXPIRED);
        byPassAllAlternative();
        TmbStatus status = new TmbStatus();
        status.setCode(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_SUIT_EXPIRED.getCode());
        status.setDescription(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_SUIT_EXPIRED.getDescription());
        status.setMessage(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_SUIT_EXPIRED.getMessage());
        status.setService(ProductsExpServiceConstant.SERVICE_NAME);
        when(alternativeService.validateSuitabilityExpired(any(), any(), any())).thenReturn(status);

        // When
        AlternativeBuyRequest alternativeBuyRequest = AlternativeBuyRequest.builder().processFlag("Y").build();
        TmbOneServiceResponse<String> actual = buyAlternativeService.validationBuy(correlationId, crmId, ipAddress, alternativeBuyRequest);

        // Then
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_SUIT_EXPIRED.getCode(),
                actual.getStatus().getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_SUIT_EXPIRED.getMessage(),
                actual.getStatus().getMessage());
        verify(buyActivityLogService, times(1)).clickPurchaseButtonAtFundFactSheetScreen(anyString(), anyString(), anyString(), any(), any());
    }

    @Test
    void should_return_failed_fund_Off_shelf_when_call_validation_buy_given_correlation_id_and_crm_id_and_ip_address_and_alternative_request() {
        // Given
        mockCustomerInfo(AlternativeBuySellSwitchDcaErrorEnums.FUND_OFF_SHELF);
        byPassAllAlternative();
        TmbStatus status = new TmbStatus();
        status.setCode(AlternativeBuySellSwitchDcaErrorEnums.FUND_OFF_SHELF.getCode());
        status.setDescription(AlternativeBuySellSwitchDcaErrorEnums.FUND_OFF_SHELF.getDescription());
        status.setMessage(AlternativeBuySellSwitchDcaErrorEnums.FUND_OFF_SHELF.getMessage());
        status.setService(ProductsExpServiceConstant.SERVICE_NAME);
        when(alternativeService.validateFundOffShelf(anyString(), any(), any())).thenReturn(status);

        // When
        AlternativeBuyRequest alternativeBuyRequest = AlternativeBuyRequest
                .builder()
                .fundHouseCode("house code")
                .fundCode("fund code")
                .tranType("tran type")
                .processFlag("Y").build();
        TmbOneServiceResponse<String> actual = buyAlternativeService.validationBuy(correlationId, crmId, ipAddress, alternativeBuyRequest);

        // Then
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.FUND_OFF_SHELF.getCode(),
                actual.getStatus().getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.FUND_OFF_SHELF.getMessage(),
                actual.getStatus().getMessage());
        verify(buyActivityLogService, times(1)).clickPurchaseButtonAtFundFactSheetScreen(anyString(), anyString(), anyString(), any(), any());
    }
}
