package com.tmb.oneapp.productsexpservice.controller.productexperience.fund;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.controller.productexperience.alternative.DcaValidationController;
import com.tmb.oneapp.productsexpservice.dto.fund.dca.validation.DcaValidationDto;
import com.tmb.oneapp.productsexpservice.enums.AlternativeBuySellSwitchDcaErrorEnums;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.dca.request.AlternativeDcaRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.dcavalidation.DcaValidationRequest;
import com.tmb.oneapp.productsexpservice.service.productexperience.alternative.DcaValidationService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DcaValidationControllerTest {

    @Mock
    private DcaValidationService dcaValidationService;

    @InjectMocks
    private DcaValidationController dcaValidationController;

    private static final String correlationId = "correlationID";

    private static final String crmId = "crmId";

    private static final String ipAddress = "0.0.0.0";

    @Test
    void should_return_dca_information_dto_when_call_get_dca_information_given_correlation_id_and_crm_id() {
        // Given
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        String crmId = "001100000000000000000001184383";

        DcaValidationDto dcaValidationDto = DcaValidationDto.builder().factSheetData("fundfactsheet").build();
        TmbOneServiceResponse tmbOneServiceResponse = new TmbOneServiceResponse();
        tmbOneServiceResponse.setStatus(TmbStatusUtil.successStatus());
        tmbOneServiceResponse.setData(dcaValidationDto);

        DcaValidationRequest dcaValidationRequest = DcaValidationRequest.builder()
                .fundHouseCode("TFUND")
                .language("TH")
                .portfolioNumber("portfolioNumber")
                .tranType("1")
                .build();
        when(dcaValidationService.dcaValidation(correlationId, crmId, dcaValidationRequest)).thenReturn(tmbOneServiceResponse);

        // When
        ResponseEntity<TmbOneServiceResponse<DcaValidationDto>> actual =
                dcaValidationController.getFundFactSheetWithValidation(correlationId, crmId, dcaValidationRequest);
        // Then
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(dcaValidationDto, actual.getBody().getData());
    }

    @Test
    void should_return_not_found_when_call_get_dca_information_given_correlation_id_and_crm_id() {
        // Given
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        String crmId = "001100000000000000000001184383";

        TmbOneServiceResponse tmbOneServiceResponse = new TmbOneServiceResponse();
        tmbOneServiceResponse.setStatus(null);
        tmbOneServiceResponse.setData(null);

        DcaValidationRequest dcaValidationRequest = DcaValidationRequest.builder()
                .fundHouseCode("TFUND")
                .language("TH")
                .portfolioNumber("portfolioNumber")
                .tranType("1")
                .build();
        when(dcaValidationService.dcaValidation(correlationId, crmId, dcaValidationRequest)).thenReturn(tmbOneServiceResponse);

        // When
        ResponseEntity<TmbOneServiceResponse<DcaValidationDto>> actual =
                dcaValidationController.getFundFactSheetWithValidation(correlationId, crmId, dcaValidationRequest);
        // Then
        assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        assertEquals(TmbStatusUtil.notFoundStatus().getCode(), actual.getBody().getStatus().getCode());
        assertNull(actual.getBody().getData());
    }

    @Test
    void should_return_success_status_when_call_validation_buy_given_correlation_id_and_crm_id_and_alternative_request() {
        // Given
        TmbOneServiceResponse<String> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        tmbOneServiceResponse.setStatus(TmbStatusUtil.successStatus());
        when(dcaValidationService.validationAlternativeDca(anyString(), anyString(), anyString(), anyString())).thenReturn(tmbOneServiceResponse);

        // When
        ResponseEntity<TmbOneServiceResponse<String>> actual = dcaValidationController.validationDca(correlationId, crmId, ipAddress, AlternativeDcaRequest.builder().processFlag("Y").build());

        // Then
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(ProductsExpServiceConstant.SUCCESS_CODE, actual.getBody().getStatus().getCode());
    }

    @Test
    void should_return_bad_request_status_when_call_validation_buy_given_correlation_id_and_crm_id_and_alternative_request() {
        // Given
        TmbOneServiceResponse<String> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        TmbStatus tmbStatus = new TmbStatus();
        tmbStatus.setCode(AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY.getCode());
        tmbOneServiceResponse.setStatus(tmbStatus);
        when(dcaValidationService.validationAlternativeDca(anyString(), anyString(), anyString(), anyString())).thenReturn(tmbOneServiceResponse);

        // When
        ResponseEntity<TmbOneServiceResponse<String>> actual = dcaValidationController.validationDca(correlationId, crmId, ipAddress, AlternativeDcaRequest.builder().processFlag("Y").build());

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.AGE_NOT_OVER_TWENTY.getCode(),
                actual.getBody().getStatus().getCode());
    }

    @Test
    void should_return_not_found_status_when_call_validation_buy_given_correlation_id_and_crm_id_and_alternative_request() {
        // Given
        TmbOneServiceResponse<String> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        tmbOneServiceResponse.setStatus(null);
        tmbOneServiceResponse.setData(null);
        when(dcaValidationService.validationAlternativeDca(anyString(), anyString(), anyString(), anyString())).thenReturn(tmbOneServiceResponse);

        // When
        ResponseEntity<TmbOneServiceResponse<String>> actual = dcaValidationController.validationDca(correlationId, crmId, ipAddress, AlternativeDcaRequest.builder().processFlag("Y").build());

        // Then
        assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        assertEquals(ProductsExpServiceConstant.DATA_NOT_FOUND_CODE,
                actual.getBody().getStatus().getCode());
    }
}
