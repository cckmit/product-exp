package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.model.response.statustracking.CaseStatusResponse;
import com.tmb.oneapp.productsexpservice.service.CaseService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
class CaseControllerTest {

    private final CaseService caseService = Mockito.mock(CaseService.class);
    private final CaseController caseController = new CaseController(caseService);

    @Test
    void getCaseStatus_success() throws TMBCommonException {

        when(caseService.getCaseStatus(anyMap(), anyString()))
                .thenReturn(new CaseStatusResponse());

        Map<String, String> header = new HashMap<>();
        header.put("x-correlation-id", "correlationId");
        header.put("x-crmid", "crmId");
        header.put("device-id", "deviceId");

        ResponseEntity<TmbOneServiceResponse<CaseStatusResponse>> response =
                caseController.getCaseStatus(header, "CST");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

    }

    @Test
    void getCaseStatus_fail() throws TMBCommonException {

        when(caseService.getCaseStatus(anyMap(), anyString()))
                .thenThrow(new TMBCommonException("fail"));

        Map<String, String> header = new HashMap<>();
        header.put("x-correlation-id", "correlationId");
        header.put("x-crmid", "crmId");
        header.put("device-id", "deviceId");

        ResponseEntity<TmbOneServiceResponse<CaseStatusResponse>> response =
                caseController.getCaseStatus(header, "CST");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    }


    @Test
    void getErrorStatus() {
        TmbOneServiceResponse<CaseStatusResponse> response = new TmbOneServiceResponse<>();
        TmbStatus tmbStatus= new TmbStatus();
        tmbStatus.setDescription("error");
        tmbStatus.setMessage("error");
        tmbStatus.setService("case-service");
        tmbStatus.setCode("0001");
        response.setStatus(tmbStatus);
        CaseStatusResponse data = new CaseStatusResponse();
        data.setServiceTypeId("1234");
        response.setData(data);
        ResponseEntity<TmbOneServiceResponse<CaseStatusResponse>> result = caseController.getErrorStatus(response);
        assertEquals(400,result.getStatusCodeValue());
    }
}