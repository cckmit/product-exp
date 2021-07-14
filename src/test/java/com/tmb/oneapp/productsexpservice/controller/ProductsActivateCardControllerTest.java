package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.ActivateCardResponse;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.ActivateCardStatusResponse;
import com.tmb.oneapp.productsexpservice.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ProductsActivateCardControllerTest {
    ProductsActivateCardController productsActivateCardController;
    @Mock
    CreditCardClient creditCardClient;
    @Mock
    NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        productsActivateCardController = new ProductsActivateCardController(creditCardClient, notificationService);
    }

    @Test
    void testActivateCardDetailsSuccess() throws Exception {
        Map<String, String> requestHeadersParameter = headerRequestParameter("c83936c284cb398fA46CF16F399C",
                "0000000050078360018000167");
        ActivateCardResponse activateCardResponse = new ActivateCardResponse();
        activateCardResponse.setAccountId("0000000050078360018000167");
        ActivateCardStatusResponse activateCardStatusResponse = new ActivateCardStatusResponse();
        activateCardStatusResponse.setStatusCode(0);
        activateCardResponse.setStatus(activateCardStatusResponse);
        handleGetCardBlockCodeResponse(activateCardResponse, HttpStatus.OK);
        ResponseEntity<TmbOneServiceResponse<ActivateCardResponse>> res = productsActivateCardController
                .activateCard(requestHeadersParameter);
        assertEquals(200, res.getStatusCodeValue());

    }

    @Test
    void testActivateCardDetailsDataNotFound() throws Exception {
        Map<String, String> requestHeadersParameter = headerRequestParameter("c83936c284cb398fA46CF16F399C",
                "0000000050078360018000167");
        ActivateCardResponse activateCardResponse = new ActivateCardResponse();
        activateCardResponse.setAccountId("0000000050078360018000167");
        handleGetCardBlockCodeResponse(activateCardResponse, HttpStatus.BAD_REQUEST);
        ResponseEntity<TmbOneServiceResponse<ActivateCardResponse>> res = productsActivateCardController
                .activateCard(requestHeadersParameter);
        assertEquals(400, res.getStatusCodeValue());

    }

    @Test
    void testActivateCardDetailsDataError() throws Exception {
        Map<String, String> requestHeadersParameter = headerRequestParameter("c83936c284cb398fA46CF16F399C",
                "0000000050078360018000167");
        when(creditCardClient.activateCard(any())).thenThrow(RuntimeException.class);
        ResponseEntity<TmbOneServiceResponse<ActivateCardResponse>> res = productsActivateCardController
                .activateCard(requestHeadersParameter);
        assertNull(res.getBody().getData());

    }

    public Map<String, String> headerRequestParameter(String correlationId, String accountNo) {
        Map<String, String> reqData = new HashMap<String, String>();
        reqData.put(ProductsExpServiceConstant.X_CORRELATION_ID, correlationId);
        reqData.put(ProductsExpServiceConstant.ACCOUNT_ID, accountNo);
        return reqData;

    }

	public void handleGetCardBlockCodeResponse(ActivateCardResponse activateCardResponse, HttpStatus status) {
		HttpHeaders responseHeaders = new HttpHeaders();
		TmbOneServiceResponse<ActivateCardResponse> oneServiceResponse = new TmbOneServiceResponse<ActivateCardResponse>();
		oneServiceResponse.setData(activateCardResponse);
		when(creditCardClient.activateCard(any()))
				.thenReturn(ResponseEntity.ok().headers(responseHeaders).body(oneServiceResponse));

	}

    @Test
    void testDataNotFoundError() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("test", "test");
        TmbOneServiceResponse<ActivateCardResponse> oneServiceResponse = new TmbOneServiceResponse<>();
        TmbStatus tmbStatus = new TmbStatus();
        tmbStatus.setCode("0");
        tmbStatus.setService("activate-card-service");
        oneServiceResponse.setStatus(tmbStatus);
        ResponseEntity<TmbOneServiceResponse<ActivateCardResponse>> response = productsActivateCardController.dataNotFoundError(responseHeaders, oneServiceResponse);
        assertEquals(400, response.getStatusCodeValue());
    }
}
