package com.tmb.oneapp.productsexpservice.service.productexperience.alternative;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.enums.AlternativeBuySellSwitchDcaErrorEnums;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.buyfirstrade.request.AlternativeBuyFirstTTradeRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.response.servicehour.ValidateServiceHourResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.occupation.response.OccupationInquiryResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.firsttrade.response.FirstTradeResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.tradeoccupation.response.TradeOccupationResponse;
import com.tmb.oneapp.productsexpservice.service.productexperience.async.InvestmentAsyncService;
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

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BuyFirstTradeAlternativeServiceTest {

    @Mock
    private InvestmentAsyncService investmentAsyncService;

    @Mock
    private CustomerService customerService;

    @Mock
    private AlternativeService alternativeService;

    @InjectMocks
    private BuyFirstTradeAlternativeService buyFirstTradeAlternativeService;

    private static final String correlationId = "correlationID";

    private static final String crmId = "crmId";

    private void mockCustomerInfo(AlternativeBuySellSwitchDcaErrorEnums alternativeEnums) {
        // given
        CustomerSearchResponse customerSearchResponse = CustomerSearchResponse.builder().build();
        if (alternativeEnums.equals(
                AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY)) {
            customerSearchResponse.setBirthDate("2010-07-08");
        }

        when(customerService.getCustomerInfo(any(), any())).thenReturn(customerSearchResponse);
    }

    private void byPassAllAlternative() throws TMBCommonException, IOException {
        // given
        ObjectMapper mapper = new ObjectMapper();
        FirstTradeResponseBody firstTradeResponseBody = mapper.readValue(Paths.get("src/test/resources/investment/fund/first_trade_body.json").toFile(),
                FirstTradeResponseBody.class);
        firstTradeResponseBody.setFirstTradeFlag("N");
        when(investmentAsyncService.getFirstTrade(any(), any())).thenReturn(CompletableFuture.completedFuture(firstTradeResponseBody));

        OccupationInquiryResponseBody occupationInquiryResponseBody = mapper.readValue(Paths.get("src/test/resources/investment/customer/occupation_inquiry_body.json").toFile(),
                OccupationInquiryResponseBody.class);
        when(investmentAsyncService.fetchOccupationInquiry(any(), any())).thenReturn(CompletableFuture.completedFuture(occupationInquiryResponseBody));

        ValidateServiceHourResponse statusWithTime = new ValidateServiceHourResponse();
        TmbStatus successStatus = TmbStatusUtil.successStatus();
        BeanUtils.copyProperties(successStatus, statusWithTime);

        when(alternativeService.validateCustomerRiskLevel(any(), any(), any(), any())).thenReturn(successStatus);
        when(alternativeService.validateIdCardExpired(any(), any())).thenReturn(successStatus);
        when(alternativeService.validateFatcaFlagNotValid(any(), any())).thenReturn(successStatus);
        when(alternativeService.validateIdentityAssuranceLevel(any(), any())).thenReturn(successStatus);
        when(alternativeService.validateNationality(any(), any(), any(), any())).thenReturn(successStatus);
    }

    @Test
    public void should_return_status_null_when_call_validation_buy_given_correlation_id_and_crm_id_and_alternative_request() throws TMBCommonException {
        // given
        // when
        when(customerService.getCustomerInfo(any(), any())).thenThrow(MockitoException.class);
        TmbOneServiceResponse<TradeOccupationResponse> actual = buyFirstTradeAlternativeService.validationBuyFirstTrade(correlationId, crmId, AlternativeBuyFirstTTradeRequest.builder().build());

        // then
        assertNull(actual.getStatus());
        assertNull(actual.getData());
    }

    @Test
    public void should_return_failed_validate_customer_risk_level_when_call_validation_buy_given_correlation_id_and_crm_id_and_alternative_request() throws TMBCommonException, IOException {
        // given
        mockCustomerInfo(AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY);
        byPassAllAlternative();
        TmbStatus status = new TmbStatus();
        status.setCode(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getCode());
        status.setDescription(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getDesc());
        status.setMessage(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getMsg());
        status.setService(ProductsExpServiceConstant.SERVICE_NAME);

        when(alternativeService.validateCustomerRiskLevel(any(), any(), any(), any())).thenReturn(status);

        // when
        AlternativeBuyFirstTTradeRequest alternativeBuyRequest = AlternativeBuyFirstTTradeRequest.builder().build();
        TmbOneServiceResponse<TradeOccupationResponse> actual = buyFirstTradeAlternativeService.validationBuyFirstTrade(correlationId, crmId, alternativeBuyRequest);

        // then
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getCode(),
                actual.getStatus().getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getMsg(),
                actual.getStatus().getMessage());
    }

    @Test
    public void should_return_failed_validate_IdCardExpired_when_call_validation_buy_given_correlation_id_and_crm_id_and_alternative_request() throws TMBCommonException, IOException {
        // given
        mockCustomerInfo(AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY);
        byPassAllAlternative();
        TmbStatus status = new TmbStatus();
        status.setCode(AlternativeBuySellSwitchDcaErrorEnums.ID_CARD_EXPIRED.getCode());
        status.setDescription(AlternativeBuySellSwitchDcaErrorEnums.ID_CARD_EXPIRED.getDesc());
        status.setMessage(AlternativeBuySellSwitchDcaErrorEnums.ID_CARD_EXPIRED.getMsg());
        status.setService(ProductsExpServiceConstant.SERVICE_NAME);

        when(alternativeService.validateIdCardExpired(any(), any())).thenReturn(status);

        // when
        AlternativeBuyFirstTTradeRequest alternativeBuyRequest = AlternativeBuyFirstTTradeRequest.builder().build();
        TmbOneServiceResponse<TradeOccupationResponse> actual = buyFirstTradeAlternativeService.validationBuyFirstTrade(correlationId, crmId, alternativeBuyRequest);

        // then
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.ID_CARD_EXPIRED.getCode(),
                actual.getStatus().getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.ID_CARD_EXPIRED.getMsg(),
                actual.getStatus().getMessage());
    }

    @Test
    public void should_return_failed_validate_IdentityAssuranceLevel_when_call_validation_buy_given_correlation_id_and_crm_id_and_alternative_request() throws TMBCommonException, IOException {
        // given
        mockCustomerInfo(AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY);
        byPassAllAlternative();
        TmbStatus status = new TmbStatus();
        status.setCode(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getCode());
        status.setDescription(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getDesc());
        status.setMessage(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getMsg());
        status.setService(ProductsExpServiceConstant.SERVICE_NAME);

        when(alternativeService.validateIdentityAssuranceLevel(any(), any())).thenReturn(status);

        // when
        AlternativeBuyFirstTTradeRequest alternativeBuyRequest = AlternativeBuyFirstTTradeRequest.builder().build();
        TmbOneServiceResponse<TradeOccupationResponse> actual = buyFirstTradeAlternativeService.validationBuyFirstTrade(correlationId, crmId, alternativeBuyRequest);

        // then
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getCode(),
                actual.getStatus().getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getMsg(),
                actual.getStatus().getMessage());
    }

    @Test
    public void should_return_failed_validate_nationality_when_call_validation_buy_given_correlation_id_and_crm_id_and_alternative_request() throws TMBCommonException, IOException {
        // given
        mockCustomerInfo(AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY);
        byPassAllAlternative();
        TmbStatus status = new TmbStatus();
        status.setCode(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getCode());
        status.setDescription(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getDesc());
        status.setMessage(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getMsg());
        status.setService(ProductsExpServiceConstant.SERVICE_NAME);

        when(alternativeService.validateNationality(any(), any(), any(), any())).thenReturn(status);

        // when
        AlternativeBuyFirstTTradeRequest alternativeBuyRequest = AlternativeBuyFirstTTradeRequest.builder().build();
        TmbOneServiceResponse<TradeOccupationResponse> actual = buyFirstTradeAlternativeService.validationBuyFirstTrade(correlationId, crmId, alternativeBuyRequest);

        // then
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getCode(),
                actual.getStatus().getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getMsg(),
                actual.getStatus().getMessage());
    }
}
