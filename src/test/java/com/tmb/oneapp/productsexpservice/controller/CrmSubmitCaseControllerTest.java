package com.tmb.oneapp.productsexpservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.request.crm.CrmSubmitCaseBody;
import com.tmb.oneapp.productsexpservice.service.CrmSubmitCaseService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
class CrmSubmitCaseControllerTest {

    private final CrmSubmitCaseService crmSubmitCaseService = Mockito.mock(CrmSubmitCaseService.class);
    private final CrmSubmitCaseController crmSubmitCaseController = new CrmSubmitCaseController(crmSubmitCaseService);

    @Test
    void submitCaseStatus_Success() throws JsonProcessingException, TMBCommonException {
        String requestDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").format(new Date());

        Map<String, String> result = new HashMap<>();
        result.put(ProductsExpServiceConstant.CASE_NUMBER, "123456789");
        result.put(ProductsExpServiceConstant.TRANSACTION_DATE, requestDate);

        when(crmSubmitCaseService.createCrmCase(any(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString())).thenReturn(result);

        Map<String, String> header = new HashMap<>();
        header.put(X_CRMID, "crmId");
        header.put(HEADER_X_CORRELATION_ID, "correlationId");

        String requestBody = "{\n" +
                "                \"firstname_th\": \"NAME\",\n" +
                "                \"lastname_th\": \"TEST\",\n" +
                "                \"firstname_en\": \"NAME\",\n" +
                "                \"lastname_en\": \"TEST\",\n" +
                "                \"service_type_matrix_code\": \"O0001\",\n" +
                "                 \"note\": \"wfawefawf\"" +
                "        }";

        CrmSubmitCaseBody crmSubmitCaseBody = new ObjectMapper().readValue(requestBody, CrmSubmitCaseBody.class);

        ResponseEntity<TmbOneServiceResponse<Map<String, String>>> response =
                crmSubmitCaseController.submitCaseStatus(header, crmSubmitCaseBody);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void submitCaseStatus_Fail() throws JsonProcessingException, TMBCommonException {
        when(crmSubmitCaseService.createCrmCase(any(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenThrow(new IllegalArgumentException());

        Map<String, String> header = new HashMap<>();
        header.put(X_CRMID, "crmId");
        header.put(HEADER_X_CORRELATION_ID, "correlationId");

        String requestBody = "{\n" +
                "                \"firstname_th\": \"NAME\",\n" +
                "                \"lastname_th\": \"TEST\",\n" +
                "                \"firstname_en\": \"NAME\",\n" +
                "                \"lastname_en\": \"TEST\",\n" +
                "                \"service_type_matrix_code\": \"O0001\",\n" +
                "                 \"note\": \"wfawefawf\"" +
                "        }";

        CrmSubmitCaseBody crmSubmitCaseBody = new ObjectMapper().readValue(requestBody, CrmSubmitCaseBody.class);

        ResponseEntity<TmbOneServiceResponse<Map<String, String>>> response =
                crmSubmitCaseController.submitCaseStatus(header, crmSubmitCaseBody);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}