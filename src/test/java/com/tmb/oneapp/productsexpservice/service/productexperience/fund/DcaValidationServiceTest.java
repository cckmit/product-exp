package com.tmb.oneapp.productsexpservice.service.productexperience.fund;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.dto.fund.dca.validation.DcaValidationDto;
import com.tmb.oneapp.productsexpservice.enums.DcaValidationErrorEnums;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.dcavalidation.DcaValidationRequest;
import com.tmb.oneapp.productsexpservice.model.response.PtesDetail;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FfsData;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FfsResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleBody;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleInfoList;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DcaValidationServiceTest {

    @InjectMocks
    public DcaValidationService dcaValidationService;

    @Mock
    private TMBLogger<DcaValidationService> logger;

    @Mock
    public InvestmentRequestClient investmentRequestClient;

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

        TmbOneServiceResponse<FfsResponse> tmbFundFactSheetResponse = new TmbOneServiceResponse<>();
        tmbFundFactSheetResponse.setStatus(TmbStatusUtil.successStatus());
        tmbFundFactSheetResponse.setData(FfsResponse.builder().body(FfsData.builder().factSheetData(fundFactSheetData).build()).build());
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
}
