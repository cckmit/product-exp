package com.tmb.oneapp.productsexpservice.service.productexperience.alternative;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.dto.fund.dca.validation.DcaValidationDto;
import com.tmb.oneapp.productsexpservice.enums.AlternativeBuySellSwitchDcaErrorEnums;
import com.tmb.oneapp.productsexpservice.enums.DcaValidationErrorEnums;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.dcavalidation.DcaValidationRequest;
import com.tmb.oneapp.productsexpservice.model.response.PtesDetail;
import com.tmb.oneapp.productsexpservice.model.response.fundfactsheet.FundFactSheetData;
import com.tmb.oneapp.productsexpservice.model.response.fundfactsheet.FundFactSheetResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleBody;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleInfoList;
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
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class DcaValidationServiceTest {

    @Mock
    private TMBLogger<DcaValidationService> logger;

    @Mock
    public InvestmentRequestClient investmentRequestClient;

    @Mock
    public AlternativeService alternativeService;

    @Mock
    public CustomerService customerService;

    @InjectMocks
    public DcaValidationService dcaValidationService;

    public static final String correlationId = "correlationID";

    public static final String crmId = "crmId";

    private void mockCustomerInfo(AlternativeBuySellSwitchDcaErrorEnums alternativeEnums){
        // given
        CustomerSearchResponse customerSearchResponse = CustomerSearchResponse.builder().build();
        if(alternativeEnums.equals(
                AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY)){
            customerSearchResponse.setBirthDate("2010-07-08");
        }


        when(customerService.getCustomerInfo(any(),any())).thenReturn(customerSearchResponse);

    }

    private void byPassAllAlternative(){
        TmbStatus successStatus = TmbStatusUtil.successStatus();
        when(alternativeService.validateServiceHour(any(), any())).thenReturn(successStatus);
        when(alternativeService.validateDateNotOverTwentyYearOld(any(), any())).thenReturn(successStatus);
        when(alternativeService.validateCustomerRiskLevel(any(),any(), any())).thenReturn(successStatus);
        when(alternativeService.validateCASADormant(any(), any(), any())).thenReturn(successStatus);
        when(alternativeService.validateIdCardExpired( any(), any())).thenReturn(successStatus);
        when(alternativeService.validateFatcaFlagNotValid( any(), any())).thenReturn(successStatus);
    }

    @Test
    public void should_return_status_null_when_call_validation_dca_given_correlation_id_and_crm_id_and_alternative_request(){

        // when
        when(customerService.getCustomerInfo(any(),any())).thenThrow(MockitoException.class);
        TmbOneServiceResponse<String>  actual = dcaValidationService.validationAlternativeDca(correlationId,crmId, "Y");

        // then
        assertNull(actual.getStatus());
        assertNull(actual.getData());

    }

    @Test
    public void should_return_failed_cant_buy_fund_when_call_validation_dca_given_correlation_id_and_crm_id_and_alternative_request(){

        // when
        TmbOneServiceResponse<String>  actual = dcaValidationService.validationAlternativeDca(correlationId,crmId, "N");

        // then
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CANT_BUY_FUND.getCode(),
                actual.getStatus().getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CANT_BUY_FUND.getMsg(),
                actual.getStatus().getMessage());

    }

    @Test
    void should_return_dca_validation_dto_when_call_dca_validation_given_correlation_id_and_crm_id_dcaValidation_request() {
        //Given
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        String crmId = "001100000000000000000001184383";
        String fundFactSheetData = "fundfactsheet";

        DcaValidationRequest dcaValidationRequest = DcaValidationRequest.builder()
                .fundHouseCode("TFUND")
                .language("TH")
                .portfolioNumber("portfolioNumber")
                .tranType("1")
                .build();

        List<PtesDetail> ptesDetailList = new ArrayList<>();
        ptesDetailList.add(PtesDetail.builder()
                .portfolioNumber("portfolioNumber")
                .portfolioFlag("1")
                .build());
        TmbOneServiceResponse<List<PtesDetail>> tmbPtesListResponse = new TmbOneServiceResponse<>();
        tmbPtesListResponse.setStatus(TmbStatusUtil.successStatus());
        tmbPtesListResponse.setData(ptesDetailList);
        when(investmentRequestClient.getPtesPort(any(), any())).thenReturn(
                ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(tmbPtesListResponse));

        TmbOneServiceResponse<FundRuleBody> tmbFundRuleResponse = new TmbOneServiceResponse<>();
        tmbFundRuleResponse.setStatus(TmbStatusUtil.successStatus());
        tmbFundRuleResponse.setData(FundRuleBody.builder()
                .fundRuleInfoList(List.of(FundRuleInfoList.builder().allowAipFlag("Y").build())).build());
        when(investmentRequestClient.callInvestmentFundRuleService(any(), any())).thenReturn(
                ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(tmbFundRuleResponse));

        TmbOneServiceResponse<FundFactSheetResponse> tmbFundFactSheetResponse = new TmbOneServiceResponse<>();
        tmbFundFactSheetResponse.setStatus(TmbStatusUtil.successStatus());
        tmbFundFactSheetResponse.setData(FundFactSheetResponse.builder().body(FundFactSheetData.builder().factSheetData(fundFactSheetData).build()).build());
        when(investmentRequestClient.callInvestmentFundFactSheetService(any(), any())).thenReturn(
                ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(tmbFundFactSheetResponse));

        //When
        TmbOneServiceResponse<DcaValidationDto> actual = dcaValidationService.dcaValidation(correlationId, crmId, dcaValidationRequest);

        //Then
        DcaValidationDto mockDto = DcaValidationDto.builder().factSheetData(fundFactSheetData).build();
        assertEquals(TmbStatusUtil.successStatus().getCode(), actual.getStatus().getCode());
        assertEquals(mockDto, actual.getData());
    }

    @Test
    void should_return_error_2000036_ptes_port_is_not_allow_for_dca_when_call_dca_validation_given_correlation_id_and_crm_id_dcaValidation_request() {
        //Given
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        String crmId = "001100000000000000000001184383";

        DcaValidationRequest dcaValidationRequest = DcaValidationRequest.builder()
                .fundHouseCode("TFUND")
                .language("TH")
                .portfolioNumber("portfolioNumber")
                .tranType("1")
                .build();

        List<PtesDetail> ptesDetailList = new ArrayList<>();
        ptesDetailList.add(PtesDetail.builder()
                .portfolioNumber("portfolioNumber")
                .portfolioFlag("2")
                .build());
        TmbOneServiceResponse<List<PtesDetail>> tmbPtesListResponse = new TmbOneServiceResponse<>();
        tmbPtesListResponse.setStatus(TmbStatusUtil.successStatus());
        tmbPtesListResponse.setData(ptesDetailList);
        when(investmentRequestClient.getPtesPort(any(), any())).thenReturn(
                ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(tmbPtesListResponse));

        //When
        TmbOneServiceResponse<DcaValidationDto> actual = dcaValidationService.dcaValidation(correlationId, crmId, dcaValidationRequest);

        //Then
        assertEquals(DcaValidationErrorEnums.PTES_PORT_IS_NOT_ALLOW_FOR_DCA.getCode(), actual.getStatus().getCode());
        assertEquals(DcaValidationErrorEnums.PTES_PORT_IS_NOT_ALLOW_FOR_DCA.getMsg(), actual.getStatus().getMessage());
        assertEquals(DcaValidationErrorEnums.PTES_PORT_IS_NOT_ALLOW_FOR_DCA.getDesc(), actual.getStatus().getDescription());
        assertNull(actual.getData());
    }

    @Test
    void should_return_error_2000037_fund_not_allow_set_dca_when_call_dca_validation_given_correlation_id_and_crm_id_dcaValidation_request() {
        //Given
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        String crmId = "001100000000000000000001184383";

        DcaValidationRequest dcaValidationRequest = DcaValidationRequest.builder()
                .fundHouseCode("TFUND")
                .language("TH")
                .portfolioNumber("portfolioNumber")
                .tranType("1")
                .build();

        List<PtesDetail> ptesDetailList = new ArrayList<>();
        ptesDetailList.add(PtesDetail.builder()
                .portfolioNumber("portfolioNumber")
                .portfolioFlag("1")
                .build());
        TmbOneServiceResponse<List<PtesDetail>> tmbPtesListResponse = new TmbOneServiceResponse<>();
        tmbPtesListResponse.setStatus(TmbStatusUtil.successStatus());
        tmbPtesListResponse.setData(ptesDetailList);
        when(investmentRequestClient.getPtesPort(any(), any())).thenReturn(
                ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(tmbPtesListResponse));

        TmbOneServiceResponse<FundRuleBody> tmbFundRuleResponse = new TmbOneServiceResponse<>();
        tmbFundRuleResponse.setStatus(TmbStatusUtil.successStatus());
        tmbFundRuleResponse.setData(FundRuleBody.builder()
                .fundRuleInfoList(List.of(FundRuleInfoList.builder().allowAipFlag("N").build())).build());
        when(investmentRequestClient.callInvestmentFundRuleService(any(), any())).thenReturn(
                ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(tmbFundRuleResponse));

        //When
        TmbOneServiceResponse<DcaValidationDto> actual = dcaValidationService.dcaValidation(correlationId, crmId, dcaValidationRequest);

        //Then
        assertEquals(DcaValidationErrorEnums.FUND_NOT_ALLOW_SET_DCA.getCode(), actual.getStatus().getCode());
        assertEquals(DcaValidationErrorEnums.FUND_NOT_ALLOW_SET_DCA.getMsg(), actual.getStatus().getMessage());
        assertEquals(DcaValidationErrorEnums.FUND_NOT_ALLOW_SET_DCA.getDesc(), actual.getStatus().getDescription());
        assertNull(actual.getData());
    }

    @Test
    void should_return_null_when_call_dca_validation_given_correlation_id_and_crm_id_dcaValidation_request() {
        //Given
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        String crmId = "001100000000000000000001184383";
        ;
        DcaValidationRequest dcaValidationRequest = DcaValidationRequest.builder()
                .fundHouseCode("TFUND")
                .language("TH")
                .portfolioNumber("TH")
                .tranType("1")
                .build();

        when(investmentRequestClient.getPtesPort(any(), any()))
                .thenThrow(new RuntimeException("Error"));
        //When
        TmbOneServiceResponse<DcaValidationDto> actual = dcaValidationService.dcaValidation(correlationId, crmId, dcaValidationRequest);

        //Then
        assertNull(actual.getStatus());
        assertNull(actual.getData());
    }

    @Test
    public void should_return_failed_validate_service_hour_when_call_validation_dca_given_correlation_id_and_crm_id_and_alternative_request(){

        // given
        TmbStatus status = new TmbStatus();
        status.setCode(AlternativeBuySellSwitchDcaErrorEnums.NOT_IN_SERVICE_HOUR.getCode());
        status.setDescription(AlternativeBuySellSwitchDcaErrorEnums.NOT_IN_SERVICE_HOUR.getDesc());
        status.setMessage(AlternativeBuySellSwitchDcaErrorEnums.NOT_IN_SERVICE_HOUR.getMsg());
        status.setService(ProductsExpServiceConstant.SERVICE_NAME);
        when(alternativeService.validateServiceHour(any(),any())).thenReturn(status);

        // when
        TmbOneServiceResponse<String>  actual = dcaValidationService.validationAlternativeDca(correlationId,crmId,"Y");

        // then
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.NOT_IN_SERVICE_HOUR.getCode(),
                actual.getStatus().getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.NOT_IN_SERVICE_HOUR.getMsg(),
                actual.getStatus().getMessage());

    }

    @Test
    public void should_return_failed_validate_age_not_over_twenty_when_call_validation_dca_given_correlation_id_and_crm_id_and_alternative_request(){

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
        TmbOneServiceResponse<String>  actual = dcaValidationService.validationAlternativeDca(correlationId,crmId,"Y");

        // then
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY.getCode(),
                actual.getStatus().getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY.getMsg(),
                actual.getStatus().getMessage());

    }

    @Test
    public void should_return_failed_customer_risk_level_when_call_validation_dca_given_correlation_id_and_crm_id_and_alternative_request(){

        // given
        mockCustomerInfo(AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY);
        byPassAllAlternative();
        TmbStatus status = new TmbStatus();
        status.setCode(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getCode());
        status.setDescription(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getDesc());
        status.setMessage(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getMsg());
        status.setService(ProductsExpServiceConstant.SERVICE_NAME);
        when(alternativeService.validateCustomerRiskLevel(any(),any(), any())).thenReturn(status);

        // when
        TmbOneServiceResponse<String>  actual = dcaValidationService.validationAlternativeDca(correlationId,crmId,"Y");

        // then
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getCode(),
                actual.getStatus().getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getMsg(),
                actual.getStatus().getMessage());

    }

    @Test
    public void should_return_failed_casa_dormant_when_call_validation_dca_given_correlation_id_and_crm_id_and_alternative_request(){

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
        TmbOneServiceResponse<String>  actual = dcaValidationService.validationAlternativeDca(correlationId,crmId,"Y");

        // then
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CASA_DORMANT.getCode(),
                actual.getStatus().getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CASA_DORMANT.getMsg(),
                actual.getStatus().getMessage());

    }

    @Test
    public void should_return_failed_customer_id_card_expired_when_call_validation_dca_given_correlation_id_and_crm_id_and_alternative_request(){

        // given
        mockCustomerInfo(AlternativeBuySellSwitchDcaErrorEnums.ID_CARD_EXPIRED);
        byPassAllAlternative();
        TmbStatus status = new TmbStatus();
        status.setCode(AlternativeBuySellSwitchDcaErrorEnums.ID_CARD_EXPIRED.getCode());
        status.setDescription(AlternativeBuySellSwitchDcaErrorEnums.ID_CARD_EXPIRED.getDesc());
        status.setMessage(AlternativeBuySellSwitchDcaErrorEnums.ID_CARD_EXPIRED.getMsg());
        status.setService(ProductsExpServiceConstant.SERVICE_NAME);
        when(alternativeService.validateIdCardExpired( any(), any())).thenReturn(status);

        // when
        TmbOneServiceResponse<String>  actual = dcaValidationService.validationAlternativeDca(correlationId,crmId,"Y");

        // then
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.ID_CARD_EXPIRED.getCode(),
                actual.getStatus().getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.ID_CARD_EXPIRED.getMsg(),
                actual.getStatus().getMessage());

    }

    @Test
    public void should_return_failed_customer_not_fill_fatca_form_when_call_validation_dca_given_correlation_id_and_crm_id_and_alternative_request(){

        // given
        mockCustomerInfo(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_NOT_FILL_FATCA_FORM);
        byPassAllAlternative();
        TmbStatus status = new TmbStatus();
        status.setCode(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_NOT_FILL_FATCA_FORM.getCode());
        status.setDescription(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_NOT_FILL_FATCA_FORM.getDesc());
        status.setMessage(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_NOT_FILL_FATCA_FORM.getMsg());
        status.setService(ProductsExpServiceConstant.SERVICE_NAME);
        when(alternativeService.validateFatcaFlagNotValid( any(), any())).thenReturn(status);

        // when
        TmbOneServiceResponse<String>  actual = dcaValidationService.validationAlternativeDca(correlationId,crmId,"Y");

        // then
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_NOT_FILL_FATCA_FORM.getCode(),
                actual.getStatus().getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_NOT_FILL_FATCA_FORM.getMsg(),
                actual.getStatus().getMessage());

    }
}
