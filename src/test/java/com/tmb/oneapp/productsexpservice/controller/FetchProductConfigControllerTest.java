package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.ProductConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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
        fetchProductConfigController = new FetchProductConfigController (commonServiceClient);
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
}
