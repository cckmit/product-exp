package com.tmb.oneapp.productsexpservice.controller.productexperience.fund;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.dto.fund.dca.validation.DcaValidationDto;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.dcavalidation.DcaValidationRequest;
import com.tmb.oneapp.productsexpservice.service.productexperience.fund.DcaValidationService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DcaValidationControllerTest {

    @InjectMocks
    public DcaValidationController dcaValidationController;

    @Mock
    public DcaValidationService dcaValidationService;

    @Test
    void should_return_dca_information_dto_when_call_get_dca_information_given_correlation_id_and_crmid() throws IOException {
        //Given
        ObjectMapper mapper = new ObjectMapper();
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        String crmId = "001100000000000000000001184383";

        DcaValidationDto dcaValidationDto = DcaValidationDto.builder().factsheetData("fundfactsheet").build();
        TmbOneServiceResponse tmbOneServiceResponse = new TmbOneServiceResponse();
        tmbOneServiceResponse.setStatus(TmbStatusUtil.successStatus());
        tmbOneServiceResponse.setData(dcaValidationDto);

        DcaValidationRequest dcaValidationRequest = DcaValidationRequest.builder()
                .fundHouseCode("TFUND")
                .language("TH")
                .portfolioNumber("portfolioNumber")
                .tranType("1")
                .build();
        when(dcaValidationService.dcaValidation(correlationId, crmId,dcaValidationRequest)).thenReturn(tmbOneServiceResponse);

        //When
        ResponseEntity<TmbOneServiceResponse<DcaValidationDto>> actual =
                dcaValidationController.getFundFactSheetWithValidation(correlationId, crmId,dcaValidationRequest);
        //Then
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(dcaValidationDto, actual.getBody().getData());
    }

    @Test
    void should_return_notfound_when_call_get_dca_information_given_correlation_id_and_crmid() throws IOException {
        //Given
        ObjectMapper mapper = new ObjectMapper();
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
        when(dcaValidationService.dcaValidation(correlationId, crmId,dcaValidationRequest)).thenReturn(tmbOneServiceResponse);

        //When
        ResponseEntity<TmbOneServiceResponse<DcaValidationDto>> actual =
                dcaValidationController.getFundFactSheetWithValidation(correlationId,crmId,dcaValidationRequest);
        //Then
        assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        assertEquals(TmbStatusUtil.notFoundStatus().getCode(),actual.getBody().getStatus().getCode());
        assertNull(actual.getBody().getData());
    }

}
