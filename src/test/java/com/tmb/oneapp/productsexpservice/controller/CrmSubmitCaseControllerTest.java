package com.tmb.oneapp.productsexpservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.model.request.crm.CrmSubmitCaseBody;
import com.tmb.oneapp.productsexpservice.service.CrmSubmitCaseService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
class CrmSubmitCaseControllerTest {

    private final CrmSubmitCaseService crmSubmitCaseService = Mockito.mock(CrmSubmitCaseService.class);
    private final CrmSubmitCaseController crmSubmitCaseController = new CrmSubmitCaseController(crmSubmitCaseService);

    @Test
    void submitCaseStatus_Success() throws JsonProcessingException {
        Map<String, String> result = new HashMap<>();
        String caseNumberSnakeCase = "case_number";

        result.put(caseNumberSnakeCase, "123456789");

        when(crmSubmitCaseService.createNcbCase(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString())).thenReturn(result);

        Map<String, String> header = new HashMap<>();
        header.put(X_CRMID, "crmId");
        header.put(X_CORRELATION_ID, "correlationId");

        String requestBody = "{\n" +
                "                \"firstname_th\": \"NAME\",\n" +
                "                \"lastname_th\": \"TEST\",\n" +
                "                \"firstname_en\": \"NAME\",\n" +
                "                \"lastname_en\": \"TEST\",\n" +
                "                \"service_type_matrix_code\": \"O0001\"\n" +
                "        }";

        CrmSubmitCaseBody crmSubmitCaseBody = new ObjectMapper().readValue(requestBody, CrmSubmitCaseBody.class);

        ResponseEntity<TmbOneServiceResponse<Map<String, String>>> response =
                crmSubmitCaseController.submitCaseStatus(header, crmSubmitCaseBody);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void submitCaseStatus_Data_Not_Found() throws JsonProcessingException {
        when(crmSubmitCaseService.createNcbCase(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString())).thenReturn(new HashMap<>());

        Map<String, String> header = new HashMap<>();
        header.put(X_CRMID, "crmId");
        header.put(X_CORRELATION_ID, "correlationId");

        String requestBody = "{\n" +
                "                \"firstname_th\": \"NAME\",\n" +
                "                \"lastname_th\": \"TEST\",\n" +
                "                \"firstname_en\": \"NAME\",\n" +
                "                \"lastname_en\": \"TEST\",\n" +
                "                \"service_type_matrix_code\": \"O0001\"\n" +
                "        }";

        CrmSubmitCaseBody crmSubmitCaseBody = new ObjectMapper().readValue(requestBody, CrmSubmitCaseBody.class);

        ResponseEntity<TmbOneServiceResponse<Map<String, String>>> response =
                crmSubmitCaseController.submitCaseStatus(header, crmSubmitCaseBody);


        assertNotEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void submitCaseStatus_Fail() throws JsonProcessingException {
        when(crmSubmitCaseService.createNcbCase(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString())).thenThrow(new IllegalArgumentException());

        Map<String, String> header = new HashMap<>();
        header.put(X_CRMID, "crmId");
        header.put(X_CORRELATION_ID, "correlationId");

        String requestBody = "{\n" +
                "                \"firstname_th\": \"NAME\",\n" +
                "                \"lastname_th\": \"TEST\",\n" +
                "                \"firstname_en\": \"NAME\",\n" +
                "                \"lastname_en\": \"TEST\",\n" +
                "                \"service_type_matrix_code\": \"O0001\"\n" +
                "        }";

        CrmSubmitCaseBody crmSubmitCaseBody = new ObjectMapper().readValue(requestBody, CrmSubmitCaseBody.class);

        ResponseEntity<TmbOneServiceResponse<Map<String, String>>> response =
                crmSubmitCaseController.submitCaseStatus(header, crmSubmitCaseBody);

        assertNotEquals(HttpStatus.OK, response.getStatusCode());
    }
}