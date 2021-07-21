package com.tmb.oneapp.productsexpservice.controller.productexperience.dca;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.dto.aip.validation.DcaValidationDto;
import com.tmb.oneapp.productsexpservice.model.productexperience.aip.response.AipValidationResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.dca.validation.request.DcaValidationRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.transaction.response.TransactionValidationResponse;
import com.tmb.oneapp.productsexpservice.service.productexperience.dca.DcaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DcaControllerTest {

    @Mock
    private DcaService dcaService;

    @InjectMocks
    private DcaController dcaController;

    @Test
    void should_return_dca_validation_dto_when_call_get_validation_given_correlation_id_and_crm_id_and_dca_validation_request() throws IOException {
        //Given
        ObjectMapper mapper = new ObjectMapper();
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        String crmId = "001100000000000000000000028365";

        TransactionValidationResponse transactionValidationResponse = mapper.readValue(Paths.get("src/test/resources/investment/transaction/transaction_validation.json").toFile(),
                TransactionValidationResponse.class);
        AipValidationResponse aipValidationResponse = mapper.readValue(Paths.get("src/test/resources/investment/aip/aip_validation.json").toFile(),
                AipValidationResponse.class);

        DcaValidationDto dcaValidationDto = DcaValidationDto.builder()
                .transactionValidation(transactionValidationResponse.getData())
                .aipValidation(aipValidationResponse.getData())
                .build();
        when(dcaService.getValidation(correlationId, crmId, DcaValidationRequest.builder().build())).thenReturn(dcaValidationDto);

        //When
        ResponseEntity<TmbOneServiceResponse<DcaValidationDto>> actual = dcaController.getValidation(correlationId, crmId, DcaValidationRequest.builder().build());

        //Then
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(dcaValidationDto, actual.getBody().getData());
    }

    @Test
    void should_return_dca_validation_dto_null_when_call_validation_given_dca_validation_dto_empty_from_service() {
        //Given
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        String crmId = "001100000000000000000000028365";

        when(dcaService.getValidation(correlationId, crmId, DcaValidationRequest.builder().build())).thenReturn(null);

        //When
        ResponseEntity<TmbOneServiceResponse<DcaValidationDto>> actual = dcaController.getValidation(correlationId, crmId, DcaValidationRequest.builder().build());

        //Then
        assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        assertNull(actual.getBody().getData());
    }

    @Test
    void should_return_dca_validation_dto_null_when_call_get_validation_given_throw_exception_from_service() {
        //Given
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        String crmId = "001100000000000000000000028365";

        when(dcaService.getValidation(correlationId, crmId, DcaValidationRequest.builder().build())).thenThrow(RuntimeException.class);

        //When
        ResponseEntity<TmbOneServiceResponse<DcaValidationDto>> actual = dcaController.getValidation(correlationId, crmId, DcaValidationRequest.builder().build());

        //Then
        assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        assertNull(actual.getBody().getData());
    }
}