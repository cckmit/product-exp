package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.service.CrmSubmitCaseService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
class CrmSubmitCaseControllerTest {

    private final CrmSubmitCaseService crmSubmitCaseService = Mockito.mock(CrmSubmitCaseService.class);
    private final CrmSubmitCaseController crmSubmitCaseController = new CrmSubmitCaseController(crmSubmitCaseService);

    @Test
    void submitCaseStatus_Success() {
        Map<String, String> result = new HashMap<>();
        String caseNumberSnakeCase = "case_number";

        result.put(caseNumberSnakeCase, "123456789");

        when(crmSubmitCaseService.createNcbCase(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString())).thenReturn(result);

        String correlationId = "1234";
        String serviceTypeMatrixCode = "O0001";
        String crmId = "001100000000000000000099999998";
        String firstname = "abc";
        String lastname = "def";

        ResponseEntity<TmbOneServiceResponse<Map<String, String>>> response =
                crmSubmitCaseController.submitCaseStatus(crmId, correlationId, firstname, lastname, firstname, lastname, serviceTypeMatrixCode);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void submitCaseStatus_Data_Not_Found() {
        when(crmSubmitCaseService.createNcbCase(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString())).thenReturn(new HashMap<>());

        String correlationId = "";
        String serviceTypeMatrixCode = "";
        String crmId = "";
        String firstname = "";
        String lastname = "";

        ResponseEntity<TmbOneServiceResponse<Map<String, String>>> response =
                crmSubmitCaseController.submitCaseStatus(crmId, correlationId, firstname, lastname, firstname, lastname, serviceTypeMatrixCode);

        assertNotEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void submitCaseStatus_Fail() {
        when(crmSubmitCaseService.createNcbCase(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString())).thenThrow(new IllegalArgumentException());

        String correlationId = "";
        String serviceTypeMatrixCode = "";
        String crmId = "";
        String firstname = "";
        String lastname = "";

        ResponseEntity<TmbOneServiceResponse<Map<String, String>>> response =
                crmSubmitCaseController.submitCaseStatus(crmId, correlationId, firstname, lastname, firstname, lastname, serviceTypeMatrixCode);

        assertNotEquals(HttpStatus.OK, response.getStatusCode());
    }
}