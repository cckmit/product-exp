package com.tmb.oneapp.productsexpservice.controller;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.FetchCardResponse;
import com.tmb.oneapp.productsexpservice.model.lending.loan.ProductDetailRequest;
import com.tmb.oneapp.productsexpservice.model.lending.loan.ProductDetailResponse;
import com.tmb.oneapp.productsexpservice.model.lending.loan.ProductRequest;
import com.tmb.oneapp.productsexpservice.model.request.TransferApplicationRequest;
import com.tmb.oneapp.productsexpservice.service.LoanService;

import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;

@RunWith(JUnit4.class)
public class LendingServiceControllerTest {


    @Mock
    LendingServiceClient lendingServiceClient;
    @Mock
    LoanService loanService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

    }

    @Test
    void getLoanProductsSuccess() throws TMBCommonException {
        HttpStatus status = HttpStatus.OK;
        ResponseEntity<TmbOneServiceResponse<Object>> mockResponse = new ResponseEntity<>(status);
        when(lendingServiceClient.getLoanProducts(any(), any())).thenReturn(mockResponse);
        LendingServiceController lendingServiceController = new LendingServiceController(lendingServiceClient, loanService);
        lendingServiceController.getProducts("", new ProductRequest());
        verify(lendingServiceClient, times(1)).getLoanProducts(any(), any());
    }

    @Test
    void getLoanProductsHandleErrorShouldThrowTMBCommonException() {
        when(lendingServiceClient.getLoanProducts(any(), any())).thenAnswer(invocation -> {
            Map<String, Collection<String>> headers = new HashMap<>();
            String errorBody = "{\"status\":{\"code\":\"0002\",\"message\":\"Data Not Found\",\"service\":\"lending-service\",\"description\":{\"en\":\"We cannot proceed your request right now. Please contact 1428 for more information. Sorry for the inconvenience.\",\"th\":\"ไม่สามารถทำรายการได้ ธนาคารขออภัยในความไม่สะดวกมา ณ ที่นี้  สอบถามเพิ่มเติม โทร.  1428\"}},\"data\":null}\n";

            Request.Body body = Request.Body.create("".getBytes(StandardCharsets.UTF_8));
            RequestTemplate template = new RequestTemplate();
            Request request = Request.create(Request.HttpMethod.POST, "http://localhost", headers, body, template);
            FeignException.BadRequest e = new FeignException.BadRequest("", request, errorBody.getBytes(StandardCharsets.UTF_8));
            throw e;
        });
        try {
            LendingServiceController lendingServiceController = new LendingServiceController(lendingServiceClient, loanService);
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
            LendingServiceController lendingServiceController = new LendingServiceController(lendingServiceClient, loanService);
            lendingServiceController.getProducts("", new ProductRequest());
            fail("Should get exception");
        } catch (TMBCommonException e) {
            Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), e.getStatus().value());
            Assertions.assertEquals("0001", e.getErrorCode());
            Assertions.assertEquals("products-exp-service", e.getService());
        }
    }
    
    @Test
    void getProductOrientationSuccess() throws TMBCommonException {
        TmbOneServiceResponse<ProductDetailResponse> oneServiceResponse = new TmbOneServiceResponse<ProductDetailResponse>();
        ProductDetailResponse data = new ProductDetailResponse();
        oneServiceResponse.setData(data);
        when(loanService.fetchProductOrientation(any(),any(), any())).thenReturn(ResponseEntity.ok(oneServiceResponse));
        LendingServiceController lendingServiceController = new LendingServiceController(lendingServiceClient, loanService);
        lendingServiceController.getProductOrientation("", "",new ProductDetailRequest());
        verify(loanService, times(1)).fetchProductOrientation(any(), any(),any());
    }

    @Test
    void getProductOrientationHandleErrorShouldThrowTMBCommonException() throws TMBCommonException {
        when(loanService.fetchProductOrientation(any(), any(), any())).thenAnswer(invocation -> {
            Map<String, Collection<String>> headers = new HashMap<>();
            String errorBody = "{\"status\":{\"code\":\"0002\",\"message\":\"Data Not Found\",\"service\":\"lending-service\",\"description\":{\"en\":\"We cannot proceed your request right now. Please contact 1428 for more information. Sorry for the inconvenience.\",\"th\":\"ไม่สามารถทำรายการได้ ธนาคารขออภัยในความไม่สะดวกมา ณ ที่นี้  สอบถามเพิ่มเติม โทร.  1428\"}},\"data\":null}\n";

            Request.Body body = Request.Body.create("".getBytes(StandardCharsets.UTF_8));
            RequestTemplate template = new RequestTemplate();
            Request request = Request.create(Request.HttpMethod.POST, "http://localhost", headers, body, template);
            FeignException.BadRequest e = new FeignException.BadRequest("", request, errorBody.getBytes(StandardCharsets.UTF_8));
            throw e;
        });
        try {
            LendingServiceController lendingServiceController = new LendingServiceController(lendingServiceClient, loanService);
            lendingServiceController.getProductOrientation("", "",new ProductDetailRequest());
            fail("Should get exception");
        } catch (TMBCommonException e) {
            Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), e.getStatus().value());
            Assertions.assertEquals("0002", e.getErrorCode());
            Assertions.assertEquals("lending-service", e.getService());
            Assertions.assertEquals("Data Not Found", e.getErrorMessage());
        }

    }
    
    @Test
    void transferApplicationSuccess() throws TMBCommonException {
        HttpStatus status = HttpStatus.OK;
        ResponseEntity<TmbOneServiceResponse<com.tmb.common.model.legacy.rsl.ws.instant.transfer.response.ResponseTransfer>> mockResponse = new ResponseEntity<>(status);
        when(lendingServiceClient.transferApplication(any(),any(), any())).thenReturn(mockResponse);
        LendingServiceController lendingServiceController = new LendingServiceController(lendingServiceClient, loanService);
        TransferApplicationRequest req = new TransferApplicationRequest();
        req.setCaId("2021");
        lendingServiceController.transferApplication("", "", req);
        verify(lendingServiceClient, times(1)).transferApplication(any(), any(),any());
    }

    @Test
    void transferApplicationHandleErrorShouldThrowTMBCommonException() throws TMBCommonException {
        when(lendingServiceClient.transferApplication(any(), any(), any())).thenAnswer(invocation -> {
            Map<String, Collection<String>> headers = new HashMap<>();
            String errorBody = "{\"status\":{\"code\":\"0002\",\"message\":\"Data Not Found\",\"service\":\"lending-service\",\"description\":{\"en\":\"We cannot proceed your request right now. Please contact 1428 for more information. Sorry for the inconvenience.\",\"th\":\"ไม่สามารถทำรายการได้ ธนาคารขออภัยในความไม่สะดวกมา ณ ที่นี้  สอบถามเพิ่มเติม โทร.  1428\"}},\"data\":null}\n";

            Request.Body body = Request.Body.create("".getBytes(StandardCharsets.UTF_8));
            RequestTemplate template = new RequestTemplate();
            Request request = Request.create(Request.HttpMethod.POST, "http://localhost", headers, body, template);
            FeignException.BadRequest e = new FeignException.BadRequest("", request, errorBody.getBytes(StandardCharsets.UTF_8));
            throw e;
        });
        try {
            LendingServiceController lendingServiceController = new LendingServiceController(lendingServiceClient, loanService);
            TransferApplicationRequest req = new TransferApplicationRequest();
            req.setCaId("2021");
            lendingServiceController.transferApplication("", "", req);
            fail("Should get exception");
        } catch (TMBCommonException e) {
            Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), e.getStatus().value());
            Assertions.assertEquals("0002", e.getErrorCode());
            Assertions.assertEquals("lending-service", e.getService());
            Assertions.assertEquals("Data Not Found", e.getErrorMessage());
        }

    }
    
}
