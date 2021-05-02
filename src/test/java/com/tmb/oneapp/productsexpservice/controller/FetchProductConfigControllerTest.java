package com.tmb.oneapp.productsexpservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbServiceResponse;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.ProductConfig;
import feign.FeignException;
import feign.FeignException.FeignClientException;
import feign.Request;
import feign.Request.HttpMethod;
import feign.RequestTemplate;
import feign.Response;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class FetchProductConfigControllerTest {

    @Mock
    CommonServiceClient commonServiceClient;
    @InjectMocks
    FetchProductConfigController fetchProductConfigController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        fetchProductConfigController = new FetchProductConfigController(commonServiceClient);
    }

    @Test
    void testProductConfigListSuccess() throws Exception {
        String correlationId = "c83936c284cb398fA46CF16F399C";
        TmbOneServiceResponse<List<ProductConfig>> oneServiceResponse = new TmbOneServiceResponse<List<ProductConfig>>();
        ProductConfig productConfig = new ProductConfig();
        productConfig.setIconId("1234");
        List<ProductConfig> list = new ArrayList<>();
        list.add(productConfig);
        ResponseEntity<TmbOneServiceResponse<List<ProductConfig>>> response = new ResponseEntity<TmbOneServiceResponse<List<ProductConfig>>>(
                oneServiceResponse, HttpStatus.OK);
        when(commonServiceClient.getProductConfig(anyString())).thenReturn(response);
        ResponseEntity<TmbOneServiceResponse<List<ProductConfig>>> ProductConfigRes = fetchProductConfigController
                .getProductConfigList(correlationId);
        assertEquals(200, ProductConfigRes.getStatusCodeValue());

    }

    @Test
    void testProductConfigListSuccessNull() throws Exception {
        String correlationId = "c83936c284cb398fA46CF16F399C";
        ResponseEntity<TmbOneServiceResponse<List<ProductConfig>>> response = null;
        when(commonServiceClient.getProductConfig(anyString())).thenReturn(response);
        ResponseEntity<TmbOneServiceResponse<List<ProductConfig>>> ProductConfigRes = fetchProductConfigController
                .getProductConfigList(correlationId);
        assertEquals(400, ProductConfigRes.getStatusCodeValue());

    }

    @Test
    void testProductConfigListError() throws Exception {
        String correlationId = "c83936c284cb398fA46CF16F399C";
        when(commonServiceClient.getProductConfig(anyString())).thenThrow(RuntimeException.class);
        ResponseEntity<TmbOneServiceResponse<List<ProductConfig>>> ProductConfigRes = fetchProductConfigController
                .getProductConfigList(correlationId);
        assertNull(ProductConfigRes.getBody().getData());
    }

    @Test
    void testProductConfigFilterEkycListSuccess() throws Exception {
        String correlationId = "c83936c284cb398fA46CF16F399C";
        TmbOneServiceResponse<List<ProductConfig>> oneServiceResponse = new TmbOneServiceResponse<List<ProductConfig>>();
        ProductConfig productConfig = new ProductConfig();
        productConfig.setIconId("1234");
        List<ProductConfig> list = new ArrayList<>();
        list.add(productConfig);
        ResponseEntity<TmbOneServiceResponse<List<ProductConfig>>> response = new ResponseEntity<TmbOneServiceResponse<List<ProductConfig>>>(
                oneServiceResponse, HttpStatus.OK);
        response.getBody().setData(list);
        when(commonServiceClient.getProductConfig(anyString())).thenReturn(response);
        ResponseEntity<TmbOneServiceResponse<List<ProductConfig>>> ProductConfigRes = fetchProductConfigController
                .getProductConfigListByEKYCFilter(correlationId, "1");
        assertEquals(200, ProductConfigRes.getStatusCodeValue());

    }

    @Test
    void testProductConfigFilterWithNull() throws Exception {
        String correlationId = "c83936c284cb398fA46CF16F399C";
        ResponseEntity<TmbOneServiceResponse<List<ProductConfig>>> response = null;
        when(commonServiceClient.getProductConfig(anyString())).thenReturn(response);
        ResponseEntity<TmbOneServiceResponse<List<ProductConfig>>> ProductConfigRes = fetchProductConfigController
                .getProductConfigListByEKYCFilter(correlationId, "1");
        assertEquals(400, ProductConfigRes.getStatusCodeValue());

    }

    @Test
    void testProductConfigFilterEkycWithETEError() throws Exception {
        String correlationId = "c83936c284cb398fA46CF16F399C";
        byte[] body = "{\"status\":{\"code\":\"404\",\"message\":\"ETE Service down\",\"service\":\"customers-service\",\"description\":{\"en\":\"Failed\",\"th\":\"Failed\"}},\"data\":null}".getBytes(StandardCharsets.UTF_8);
        feign.Request.Body b = null;
        Map<String, Collection<String>> headers = new HashMap<>();
        RequestTemplate requestTemplate = new RequestTemplate();
        Request request = Request.create(HttpMethod.GET,
                "https://oneapp-dev1.tau2904.com/apis/customer/ekyc/scan",
                headers,
                b,
                requestTemplate);
        FeignClientException exception = new FeignClientException(404,
                "ETE service down",
                request, body);
        when(commonServiceClient.getProductConfig(anyString())).thenThrow(exception);
        assertThrows(TMBCommonException.class, () -> {
            fetchProductConfigController.getProductConfigListByEKYCFilter(correlationId, "1");
        });
    }

}
