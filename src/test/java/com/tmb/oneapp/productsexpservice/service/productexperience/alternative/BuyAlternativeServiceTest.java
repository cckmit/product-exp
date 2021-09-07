package com.tmb.oneapp.productsexpservice.service.productexperience.alternative;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.enums.AlternativeBuySellSwitchDcaErrorEnums;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.buy.request.AlternativeBuyRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.response.servicehour.ValidateServiceHourResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.firsttrade.response.FirstTradeResponseBody;
import com.tmb.oneapp.productsexpservice.service.ProductsExpService;
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
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BuyAlternativeServiceTest {

    @Mock
    public AlternativeService alternativeService;

    @Mock
    public CustomerService customerService;

    @Mock
    public ProductsExpService productsExpService;

    @Mock
    public InvestmentRequestClient investmentRequestClient;

    @InjectMocks
    public BuyAlternativeService buyAlternativeService;

    public static final String correlationId = "correlationID";

    public static final String crmId = "crmId";

    private void mockCustomerInfo(AlternativeBuySellSwitchDcaErrorEnums alternativeEnums) {
        // given
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
        when(alternativeService.validateCustomerRiskLevel(any(), any(), any(), anyBoolean(), anyBoolean())).thenReturn(successStatus);
        when(alternativeService.validateCASADormant(any(), any(), any())).thenReturn(successStatus);
        when(alternativeService.validateSuitabilityExpired(any(), any(), any())).thenReturn(successStatus);
        when(alternativeService.validateIdCardExpired(any(), any())).thenReturn(successStatus);
        when(alternativeService.validateFatcaFlagNotValid(any(), any())).thenReturn(successStatus);
    }

    @Test
    public void should_return_status_null_when_call_validation_buy_given_correlation_id_and_crm_id_and_alternative_request() {
        // given
        // when
        when(customerService.getCustomerInfo(any(), any())).thenThrow(MockitoException.class);
        TmbOneServiceResponse<String> actual = buyAlternativeService.validationBuy(correlationId, crmId, AlternativeBuyRequest.builder().build());

        // then
        assertNull(actual.getStatus());
        assertNull(actual.getData());
    }

    @Test
    public void should_return_failed_cant_buy_fund_when_call_validation_buy_given_correlation_id_and_crm_id_and_alternative_request() {
        // given
        byPassAllAlternative();

        // when
        AlternativeBuyRequest alternativeBuyRequest = AlternativeBuyRequest.builder().processFlag("N").build();
        TmbOneServiceResponse<String> actual = buyAlternativeService.validationBuy(correlationId, crmId, alternativeBuyRequest);

        // then
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CANT_BUY_FUND.getCode(),
                actual.getStatus().getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CANT_BUY_FUND.getMsg(),
                actual.getStatus().getMessage());
    }

    @Test
    public void should_return_failed_validate_service_hour_when_call_validation_buy_given_correlation_id_and_crm_id_and_alternative_request() {
        // given
        byPassAllAlternative();
        ValidateServiceHourResponse status = new ValidateServiceHourResponse();
        status.setCode(AlternativeBuySellSwitchDcaErrorEnums.NOT_IN_SERVICE_HOUR.getCode());
        status.setDescription(AlternativeBuySellSwitchDcaErrorEnums.NOT_IN_SERVICE_HOUR.getDesc());
        status.setMessage(AlternativeBuySellSwitchDcaErrorEnums.NOT_IN_SERVICE_HOUR.getMsg());
        status.setService(ProductsExpServiceConstant.SERVICE_NAME);
        status.setStartTime("19:00");
        status.setEndTime("20:00");
        when(alternativeService.validateServiceHour(any(), any())).thenReturn(status);

        // when
        AlternativeBuyRequest alternativeBuyRequest = AlternativeBuyRequest.builder().processFlag("Y").build();
        TmbOneServiceResponse<String> actual = buyAlternativeService.validationBuy(correlationId, crmId, alternativeBuyRequest);

        // then
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.NOT_IN_SERVICE_HOUR.getCode(),
                actual.getStatus().getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.NOT_IN_SERVICE_HOUR.getMsg(),
                actual.getStatus().getMessage());
        assertEquals("19:00-20:00", (actual.getData()));
    }

    @Test
    public void should_return_failed_validate_age_not_over_twenty_when_call_validation_buy_given_correlation_id_and_crm_id_and_alternative_request() {
        // given
        mockCustomerInfo(AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY);
        byPassAllAlternative();
        TmbStatus status = new TmbStatus();
        status.setCode(AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY.getCode());
        status.setDescription(AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY.getDesc());
        status.setMessage(AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY.getMsg());
        status.setService(ProductsExpServiceConstant.SERVICE_NAME);
        when(alternativeService.validateDateNotOverTwentyYearOld(any(), any())).thenReturn(status);

        // when
        AlternativeBuyRequest alternativeBuyRequest = AlternativeBuyRequest.builder().processFlag("Y").build();
        TmbOneServiceResponse<String> actual = buyAlternativeService.validationBuy(correlationId, crmId, alternativeBuyRequest);

        // then
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY.getCode(),
                actual.getStatus().getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY.getMsg(),
                actual.getStatus().getMessage());
    }

    @Test
    public void should_return_failed_customer_risk_level_when_call_validation_buy_given_correlation_id_and_crm_id_and_alternative_request() {
        // given
        mockCustomerInfo(AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY);
        byPassAllAlternative();
        TmbStatus status = new TmbStatus();
        status.setCode(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getCode());
        status.setDescription(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getDesc());
        status.setMessage(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getMsg());
        status.setService(ProductsExpServiceConstant.SERVICE_NAME);
        when(alternativeService.validateCustomerRiskLevel(any(), any(), any(), anyBoolean(), anyBoolean())).thenReturn(status);

        // when
        AlternativeBuyRequest alternativeBuyRequest = AlternativeBuyRequest.builder().processFlag("Y").build();
        TmbOneServiceResponse<String> actual = buyAlternativeService.validationBuy(correlationId, crmId, alternativeBuyRequest);

        // then
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getCode(),
                actual.getStatus().getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getMsg(),
                actual.getStatus().getMessage());
    }

    @Test
    public void should_return_failed_casa_dormant_when_call_validation_buy_given_correlation_id_and_crm_id_and_alternative_request() {
        // given
        mockCustomerInfo(AlternativeBuySellSwitchDcaErrorEnums.CASA_DORMANT);
        byPassAllAlternative();
        TmbStatus status = new TmbStatus();
        status.setCode(AlternativeBuySellSwitchDcaErrorEnums.CASA_DORMANT.getCode());
        status.setDescription(AlternativeBuySellSwitchDcaErrorEnums.CASA_DORMANT.getDesc());
        status.setMessage(AlternativeBuySellSwitchDcaErrorEnums.CASA_DORMANT.getMsg());
        status.setService(ProductsExpServiceConstant.SERVICE_NAME);
        when(alternativeService.validateCASADormant(any(), any(), any())).thenReturn(status);

        // when
        AlternativeBuyRequest alternativeBuyRequest = AlternativeBuyRequest.builder().processFlag("Y").build();
        TmbOneServiceResponse<String> actual = buyAlternativeService.validationBuy(correlationId, crmId, alternativeBuyRequest);

        // then
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CASA_DORMANT.getCode(),
                actual.getStatus().getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CASA_DORMANT.getMsg(),
                actual.getStatus().getMessage());
    }

    @Test
    public void should_return_failed_customer_suitability_expired_when_call_validation_buy_given_correlation_id_and_crm_id_and_alternative_request() {
        // given
        mockCustomerInfo(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_SUIT_EXPIRED);
        byPassAllAlternative();
        TmbStatus status = new TmbStatus();
        status.setCode(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_SUIT_EXPIRED.getCode());
        status.setDescription(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_SUIT_EXPIRED.getDesc());
        status.setMessage(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_SUIT_EXPIRED.getMsg());
        status.setService(ProductsExpServiceConstant.SERVICE_NAME);
        when(alternativeService.validateSuitabilityExpired(any(), any(), any())).thenReturn(status);

        // when
        AlternativeBuyRequest alternativeBuyRequest = AlternativeBuyRequest.builder().processFlag("Y").build();
        TmbOneServiceResponse<String> actual = buyAlternativeService.validationBuy(correlationId, crmId, alternativeBuyRequest);

        // then
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_SUIT_EXPIRED.getCode(),
                actual.getStatus().getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_SUIT_EXPIRED.getMsg(),
                actual.getStatus().getMessage());
    }

    @Test
    public void should_return_failed_customer_id_card_expired_when_call_validation_buy_given_correlation_id_and_crm_id_and_alternative_request() {
        // given
        mockCustomerInfo(AlternativeBuySellSwitchDcaErrorEnums.ID_CARD_EXPIRED);
        byPassAllAlternative();
        TmbStatus status = new TmbStatus();
        status.setCode(AlternativeBuySellSwitchDcaErrorEnums.ID_CARD_EXPIRED.getCode());
        status.setDescription(AlternativeBuySellSwitchDcaErrorEnums.ID_CARD_EXPIRED.getDesc());
        status.setMessage(AlternativeBuySellSwitchDcaErrorEnums.ID_CARD_EXPIRED.getMsg());
        status.setService(ProductsExpServiceConstant.SERVICE_NAME);
        when(alternativeService.validateIdCardExpired(any(), any())).thenReturn(status);

        // when
        AlternativeBuyRequest alternativeBuyRequest = AlternativeBuyRequest.builder().processFlag("Y").build();
        TmbOneServiceResponse<String> actual = buyAlternativeService.validationBuy(correlationId, crmId, alternativeBuyRequest);

        // then
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.ID_CARD_EXPIRED.getCode(),
                actual.getStatus().getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.ID_CARD_EXPIRED.getMsg(),
                actual.getStatus().getMessage());
    }

    @Test
    public void should_return_failed_customer_not_fill_fatca_form_when_call_validation_buy_given_correlation_id_and_crm_id_and_alternative_request() {
        // given
        mockCustomerInfo(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_NOT_FILL_FATCA_FORM);
        byPassAllAlternative();
        TmbStatus status = new TmbStatus();
        status.setCode(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_NOT_FILL_FATCA_FORM.getCode());
        status.setDescription(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_NOT_FILL_FATCA_FORM.getDesc());
        status.setMessage(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_NOT_FILL_FATCA_FORM.getMsg());
        status.setService(ProductsExpServiceConstant.SERVICE_NAME);
        when(alternativeService.validateFatcaFlagNotValid(any(), any())).thenReturn(status);

        // when
        AlternativeBuyRequest alternativeBuyRequest = AlternativeBuyRequest.builder().processFlag("Y").build();
        TmbOneServiceResponse<String> actual = buyAlternativeService.validationBuy(correlationId, crmId, alternativeBuyRequest);

        // then
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_NOT_FILL_FATCA_FORM.getCode(),
                actual.getStatus().getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_NOT_FILL_FATCA_FORM.getMsg(),
                actual.getStatus().getMessage());
    }
}
