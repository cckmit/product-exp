package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.model.response.CaseStatusResponse;
import com.tmb.oneapp.productsexpservice.service.CaseService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
class CaseControllerTest {

    private final CaseService caseService = Mockito.mock(CaseService.class);
    private final CaseController caseController = new CaseController(caseService);

    @Test
    void getCaseStatus_success() throws TMBCommonException {

        when(caseService.getCaseStatus(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(new CaseStatusResponse());

        ResponseEntity<TmbOneServiceResponse<CaseStatusResponse>> response =
                caseController.getCaseStatus("correlationId", "crmId", "deviceId", "CST");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

    }

    @Test
    void getCaseStatus_fail() throws TMBCommonException {

        when(caseService.getCaseStatus(anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new TMBCommonException("fail"));

        ResponseEntity<TmbOneServiceResponse<CaseStatusResponse>> response =
                caseController.getCaseStatus("correlationId", "crmId", "deviceId", "CST");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    }


}