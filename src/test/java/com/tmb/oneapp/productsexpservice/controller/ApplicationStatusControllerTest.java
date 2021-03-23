package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.model.response.statustracking.ApplicationStatusResponse;
import com.tmb.oneapp.productsexpservice.service.ApplicationStatusService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
class ApplicationStatusControllerTest {

    private final ApplicationStatusService applicationStatusService = Mockito.mock(ApplicationStatusService.class);
    private final ApplicationStatusController applicationStatusController = new ApplicationStatusController(applicationStatusService);

    @Test
    void getApplicationStatus() throws TMBCommonException {
        when(applicationStatusService.getApplicationStatus(anyMap(), anyString()))
                .thenReturn(new ApplicationStatusResponse()
                        .setHpStatus(0)
                        .setRslStatus(0));

        Map<String, String> header = new HashMap<>();
        header.put(X_CORRELATION_ID, "correlationId");
        header.put(X_CRMID, "crmId");
        header.put(DEVICE_ID, "deviceId");
        header.put(ACCEPT_LANGUAGE, "en");

        ResponseEntity<TmbOneServiceResponse<ApplicationStatusResponse>> response =
                applicationStatusController.getApplicationStatus(header, "AST");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getApplicationStatus_hp_rsl_error() throws TMBCommonException {
        testHpRslError(2, 2, "AST_0001");
        testHpRslError(1, 1, "AST_0004");
        testHpRslError(1, 0, "AST_0003");
        testHpRslError(0, 1, "AST_0002");

    }

    void testHpRslError(int hpSuccess, int rslSuccess, String errorCode) throws TMBCommonException {
        when(applicationStatusService.getApplicationStatus(anyMap(), anyString()))
                .thenReturn(new ApplicationStatusResponse()
                        .setHpStatus(hpSuccess)
                        .setRslStatus(rslSuccess));

        Map<String, String> header = new HashMap<>();
        header.put(X_CORRELATION_ID, "correlationId");
        header.put(X_CRMID, "crmId");
        header.put(DEVICE_ID, "deviceId");
        header.put(ACCEPT_LANGUAGE, "en");

        ResponseEntity<TmbOneServiceResponse<ApplicationStatusResponse>> response =
                applicationStatusController.getApplicationStatus(header, "AST");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorCode, response.getBody().getStatus().getCode());

    }

    @Test
    void getCaseStatus_exception() throws TMBCommonException {

        when(applicationStatusService.getApplicationStatus(anyMap(), anyString()))
                .thenThrow(new TMBCommonException("fail"));

        Map<String, String> header = new HashMap<>();
        header.put(X_CORRELATION_ID, "correlationId");
        header.put(X_CRMID, "crmId");
        header.put(DEVICE_ID, "deviceId");
        header.put(ACCEPT_LANGUAGE, "en");

        ResponseEntity<TmbOneServiceResponse<ApplicationStatusResponse>> response =
                applicationStatusController.getApplicationStatus(header, "AST");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    }

    @Test
    void getCaseStatus_missingField() {

        Map<String, String> header = new HashMap<>();
        header.put(X_CORRELATION_ID, "correlationId");
        header.put(X_CRMID, "crmId");
        header.put(ACCEPT_LANGUAGE, "en");

        ResponseEntity<TmbOneServiceResponse<ApplicationStatusResponse>> response =
                applicationStatusController.getApplicationStatus(header, "AST");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    }

}