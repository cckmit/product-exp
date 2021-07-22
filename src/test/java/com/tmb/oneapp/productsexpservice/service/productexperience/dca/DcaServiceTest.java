package com.tmb.oneapp.productsexpservice.service.productexperience.dca;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.oneapp.productsexpservice.dto.aip.validation.DcaValidationDto;
import com.tmb.oneapp.productsexpservice.mapper.dca.DcaMapper;
import com.tmb.oneapp.productsexpservice.model.productexperience.aip.response.AipValidationResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.dca.validation.request.DcaValidationRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.transaction.response.TransactionValidationResponse;
import com.tmb.oneapp.productsexpservice.service.productexperience.async.InvestmentAsyncService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DcaServiceTest {

    @Mock
    private TMBLogger<DcaServiceTest> logger;

    @Mock
    private DcaMapper dcaMapper;

    @Mock
    private InvestmentAsyncService investmentAsyncService;

    @InjectMocks
    private DcaService dcaService;

    @Test
    void should_return_dca_validation_dto_when_call_get_validation_given_correlation_id_and_crm_id_and_dca_validation_request() throws IOException, TMBCommonException {
        //Given
        ObjectMapper mapper = new ObjectMapper();
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        String crmId = "001100000000000000000000028365";

        TransactionValidationResponse transactionValidationResponse = mapper.readValue(Paths.get("src/test/resources/investment/transaction/transaction_validation.json").toFile(),
                TransactionValidationResponse.class);
        when(investmentAsyncService.fetchTransactionValidation(any(), anyString(), any())).thenReturn(CompletableFuture.completedFuture(transactionValidationResponse.getData()));

        AipValidationResponse aipValidationResponse = mapper.readValue(Paths.get("src/test/resources/investment/aip/aip_validation.json").toFile(),
                AipValidationResponse.class);
        when(investmentAsyncService.fetchAipValidation(any(), any())).thenReturn(CompletableFuture.completedFuture(aipValidationResponse.getData()));

        //When
        DcaValidationDto actual = dcaService.getValidation(correlationId, crmId, DcaValidationRequest.builder().build());

        //Then
        DcaValidationDto expected = DcaValidationDto.builder()
                .transactionValidation(transactionValidationResponse.getData())
                .aipValidation(aipValidationResponse.getData())
                .build();

        assertEquals(expected, actual);
    }

    @Test
    void should_return_null_when_call_get_validation_given_throw_runtime_exception_from_product_exp_async_service() throws TMBCommonException {
        //Given
        String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        String crmId = "001100000000000000000000028365";

        when(investmentAsyncService.fetchTransactionValidation(any(), anyString(), any())).thenThrow(TMBCommonException.class);

        //When
        DcaValidationDto actual = dcaService.getValidation(correlationId, crmId, DcaValidationRequest.builder().build());

        //Then
        assertNull(actual);
    }
}