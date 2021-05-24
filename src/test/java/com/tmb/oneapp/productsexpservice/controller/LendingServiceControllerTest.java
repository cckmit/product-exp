package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;
import com.tmb.oneapp.productsexpservice.model.lending.loan.ProductRequest;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class LendingServiceControllerTest {


    @Mock
    LendingServiceClient lendingServiceClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

    }

    @Test
    void getLoanProductsSuccess() throws TMBCommonException {
        HttpStatus status = HttpStatus.OK;
        ResponseEntity<TmbOneServiceResponse<Object>> mockResponse = new ResponseEntity<>(status);
        when(lendingServiceClient.getLoanProducts(any(), any())).thenReturn(mockResponse);
        LendingServiceController lendingServiceController = new LendingServiceController(lendingServiceClient);
        lendingServiceController.getProducts("", new ProductRequest());
        verify(lendingServiceClient, times(1)).getLoanProducts(any(), any());
    }

    @Test
    void getLoanProductsHandleErrorShouldThrowTMBCommonException() {
        when(lendingServiceClient.getLoanProducts(any(), any())).thenAnswer(invocation -> {
            Map<String, Collection<String>> headers = new HashMap<>();
            String errorBody = "{\"status\":{\"code\":\"0002\",\"message\":\"Data Not Found\",\"service\":\"lending-service\",\"description\":{\"en\":\"We cannot proceed your request right now. Please contact 1428 for more information. Sorry for the inconvenience.\",\"th\":\"???????????????????? ???????????????????????????? ? ??????  ??????????????? ???. 1428\"}},\"data\":null}\n";

            Request.Body body = Request.Body.create("".getBytes(StandardCharsets.UTF_8));
            RequestTemplate template = new RequestTemplate();
            Request request = Request.create(Request.HttpMethod.POST, "http://localhost", headers, body, template);
            FeignException.BadRequest e = new FeignException.BadRequest("", request, errorBody.getBytes(StandardCharsets.UTF_8));
            throw e;
        });
        try {
            LendingServiceController lendingServiceController = new LendingServiceController(lendingServiceClient);
            lendingServiceController.getProducts("", new ProductRequest());
            fail("Should get exception");
        } catch (TMBCommonException e) {
            Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), e.getStatus().value());
            Assertions.assertEquals("0002", e.getErrorCode());
            Assertions.assertEquals("lending-service", e.getService());
            Assertions.assertEquals("Data Not Found", e.getErrorMessage());
        }

    }

    @Test
    void getLoanProductsCannotDecodeErrorShouldThrowTMBCommonException() {
        when(lendingServiceClient.getLoanProducts(any(), any())).thenAnswer(invocation -> {
            Map<String, Collection<String>> headers = new HashMap<>();

            Request.Body body = Request.Body.create("".getBytes(StandardCharsets.UTF_8));
            RequestTemplate template = new RequestTemplate();
            Request request = Request.create(Request.HttpMethod.POST, "http://localhost", headers, body, template);
            FeignException.BadRequest e = new FeignException.BadRequest("", request, "".getBytes(StandardCharsets.UTF_8));
            throw e;
        });
        try {
            LendingServiceController lendingServiceController = new LendingServiceController(lendingServiceClient);
            lendingServiceController.getProducts("", new ProductRequest());
            fail("Should get exception");
        } catch (TMBCommonException e) {
            Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), e.getStatus().value());
            Assertions.assertEquals("0001", e.getErrorCode());
            Assertions.assertEquals("products-exp-service", e.getService());
        }
    }

}