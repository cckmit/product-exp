package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.model.request.accdetail.FundAccountRq;
import com.tmb.oneapp.productsexpservice.model.request.fundsummary.FundSummaryRq;
import com.tmb.oneapp.productsexpservice.model.response.accdetail.FundAccountRs;
import com.tmb.oneapp.productsexpservice.model.response.fundsummary.FundSummaryResponse;
import com.tmb.oneapp.productsexpservice.service.ProductsExpService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;

public class ProductExpServiceControllerTestFundSummary {
    @Mock
    TMBLogger<ProductExpServiceController> logger;
    @Mock
    ProductsExpService productsExpService;
    @InjectMocks
    ProductExpServiceController productExpServiceController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }



    @Test
    public void testGetFundSummary() throws Exception {
        when(productsExpService.getFundSummary(anyString(), any())).thenReturn(new FundSummaryResponse());

        ResponseEntity<TmbOneServiceResponse<FundSummaryResponse>> result = productExpServiceController.getFundSummary("correlationId", new FundSummaryRq());
        Assert.assertEquals(null, result);
    }
}
