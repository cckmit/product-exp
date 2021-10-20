package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.StatusResponse;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.blockcard.BlockCardRequest;
import com.tmb.oneapp.productsexpservice.model.blockcard.BlockCardResponse;
import com.tmb.oneapp.productsexpservice.model.blockcard.Status;
import com.tmb.oneapp.productsexpservice.service.CacheService;
import com.tmb.oneapp.productsexpservice.service.CreditCardLogService;
import com.tmb.oneapp.productsexpservice.service.NotificationService;
import feign.FeignException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class BlockCardControllerTest {
    BlockCardController blockCardController;
    @Mock
    CreditCardClient creditCardClient;
    @Mock
    CreditCardLogService creditCardLogService;
    @Mock
    NotificationService notificationService;
    @Mock
    CacheService cacheService;

    @BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		blockCardController = new BlockCardController(creditCardClient, creditCardLogService, notificationService,
				cacheService);

	}

    @Test
    void testBlockCardDetailsSuccess() throws Exception {
        Map<String, String> requestHeadersParameter = new HashMap<>();
        requestHeadersParameter.put(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID, "test");
        BlockCardRequest requestBodyParameter = new BlockCardRequest();
        requestBodyParameter.setAccountId("0000000050078360018000167");
        requestBodyParameter.setBlockReason("L");
        com.tmb.common.model.creditcard.BlockCardResponse blockCardResponse = new com.tmb.common.model.creditcard.BlockCardResponse();
        StatusResponse status = new StatusResponse();
        status.setStatusCode("0");
        blockCardResponse.setStatus(status);
        handleBlockCardResponse(blockCardResponse, HttpStatus.OK);
        ResponseEntity<TmbOneServiceResponse<BlockCardResponse>> res = blockCardController
                .blockCardDetails(requestBodyParameter, requestHeadersParameter);
        assertEquals(200, res.getStatusCodeValue());

    }

    @Test
    void testBlockCardDetailsSuccessNoDataFound() throws Exception {
        Map<String, String> requestHeadersParameter = new HashMap<>();
        requestHeadersParameter.put(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID, "test");
        BlockCardRequest bodyParameter = new BlockCardRequest();
        ResponseEntity<TmbOneServiceResponse<BlockCardResponse>> res = blockCardController
                .blockCardDetails(bodyParameter, requestHeadersParameter);
        assertEquals(400, res.getStatusCodeValue());

    }

    public void handleBlockCardResponse(com.tmb.common.model.creditcard.BlockCardResponse response, HttpStatus status) {

        TmbOneServiceResponse<com.tmb.common.model.creditcard.BlockCardResponse> oneServiceResponse = new TmbOneServiceResponse<com.tmb.common.model.creditcard.BlockCardResponse>();
//        oneServiceResponse.setData(blockCardResponse);
        ResponseEntity<com.tmb.common.model.creditcard.BlockCardResponse> res = new ResponseEntity<com.tmb.common.model.creditcard.BlockCardResponse>(response, status);
        when(creditCardClient.getBlockCardDetails(any())).thenReturn(res);

    }

    @Test
    void testBlockCardDetailsSuccessStatusOne() throws Exception {
        Map<String, String> requestHeadersParameter = new HashMap<>();
        requestHeadersParameter.put(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID, "test");
        BlockCardRequest requestBodyParameter = new BlockCardRequest();
        requestBodyParameter.setAccountId("0000000050078360018000167");
        requestBodyParameter.setBlockReason("L");
        com.tmb.common.model.creditcard.BlockCardResponse blockCardResponse = new com.tmb.common.model.creditcard.BlockCardResponse();
        StatusResponse status = new StatusResponse();
        status.setStatusCode("1");
        blockCardResponse.setStatus(status);
        handleBlockCardResponse(blockCardResponse, HttpStatus.OK);
        ResponseEntity<TmbOneServiceResponse<BlockCardResponse>> res = blockCardController
                .blockCardDetails(requestBodyParameter, requestHeadersParameter);
        assertEquals(400, res.getStatusCodeValue());

    }

    @Test
    void testBlockCardDetailsError() throws Exception {
        Map<String, String> requestHeadersParameter = new HashMap<>();
        requestHeadersParameter.put(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID, "test");
        BlockCardRequest requestBodyParameter = new BlockCardRequest();
        requestBodyParameter.setAccountId("0000000050078360018000167");
        requestBodyParameter.setBlockReason("L");
        BlockCardResponse blockCardResponse = new BlockCardResponse();
        Status status = new Status();
        status.setStatusCode("0");
        status.setDate("dd/mm/yyyy");
        status.setTxnId("42342311");
        blockCardResponse.setStatus(status);
        when(creditCardClient.getBlockCardDetails(any())).thenThrow(FeignException.FeignClientException.class);
        Assertions.assertThrows(TMBCommonException.class,
                () -> blockCardController.blockCardDetails(requestBodyParameter, requestHeadersParameter));

    }
}
