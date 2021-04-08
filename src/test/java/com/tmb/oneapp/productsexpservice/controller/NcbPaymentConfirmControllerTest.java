package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.model.response.ncb.NcbPaymentConfirmResponse;
import com.tmb.oneapp.productsexpservice.model.response.statustracking.ApplicationStatusResponse;
import com.tmb.oneapp.productsexpservice.service.ApplicationStatusService;
import com.tmb.oneapp.productsexpservice.service.NcbPaymentConfirmService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
class NcbPaymentConfirmControllerTest {

    private final NcbPaymentConfirmService ncbPaymentConfirmService = Mockito.mock(NcbPaymentConfirmService.class);
    private final NcbPaymentConfirmController ncbPaymentConfirmController = new NcbPaymentConfirmController(ncbPaymentConfirmService);

    private static final String serviceTypeId = "NCBR";
    private static final String firstnameTh = "NAME";
    private static final String lastnameTh = "TEST";
    private static final String firstnameEn = "NAME";
    private static final String lastnameEn = "TEST";
    private static final String email = "abc@tmb.com";
    private static final String address = "123/12 asdfweaoifjawof";
    private static final String deliveryMethod = "email";
    private static final String accountNumber = "12345678980";

    @Test
    void confirmNcbPayment_Success() throws TMBCommonException {
        NcbPaymentConfirmResponse ncbPaymentConfirmResponse = new NcbPaymentConfirmResponse();
        ncbPaymentConfirmResponse.setTransactionDate("2021-04-02T10:35");
        ncbPaymentConfirmResponse.setReferenceNo("1234567890");

        when(ncbPaymentConfirmService.confirmNcbPayment(anyMap(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString()))
        .thenReturn(ncbPaymentConfirmResponse);

        Map<String, String> header = new HashMap<>();
        header.put(X_CORRELATION_ID, "correlationId");
        header.put(X_CRMID, "crmId");
        header.put(DEVICE_ID, "deviceId");
        header.put(ACCEPT_LANGUAGE, "en");

        ResponseEntity<TmbOneServiceResponse<NcbPaymentConfirmResponse>> response =
                ncbPaymentConfirmController.confirmNcbPayment(header, serviceTypeId, firstnameTh, lastnameTh, firstnameEn, lastnameEn,
                        email, address, deliveryMethod, accountNumber);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void confirmNcbPayment_NoData() throws TMBCommonException {
        NcbPaymentConfirmResponse ncbPaymentConfirmResponse = new NcbPaymentConfirmResponse();
        ncbPaymentConfirmResponse.setTransactionDate("2021-04-02T10:35");
        ncbPaymentConfirmResponse.setReferenceNo("1234567890");

        when(ncbPaymentConfirmService.confirmNcbPayment(anyMap(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString()))
                .thenReturn(ncbPaymentConfirmResponse);

        Map<String, String> header = new HashMap<>();
        header.put(X_CORRELATION_ID, "correlationId");
        header.put(X_CRMID, "crmId");
        header.put(DEVICE_ID, "deviceId");

        ResponseEntity<TmbOneServiceResponse<NcbPaymentConfirmResponse>> response =
                ncbPaymentConfirmController.confirmNcbPayment(header, serviceTypeId, firstnameTh, lastnameTh, firstnameEn, lastnameEn,
                        email, address, deliveryMethod, accountNumber);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void confirmNcbPayment_Fail() throws TMBCommonException {
        when(ncbPaymentConfirmService.confirmNcbPayment(anyMap(), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new IllegalArgumentException());

        Map<String, String> header = new HashMap<>();
        header.put(X_CORRELATION_ID, "correlationId");
        header.put(X_CRMID, "crmId");
        header.put(DEVICE_ID, "deviceId");

        ResponseEntity<TmbOneServiceResponse<NcbPaymentConfirmResponse>> response =
                ncbPaymentConfirmController.confirmNcbPayment(header, serviceTypeId, firstnameTh, lastnameTh, firstnameEn, lastnameEn,
                        email, address, deliveryMethod, accountNumber);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}