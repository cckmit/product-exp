package com.tmb.oneapp.productsexpservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundSummaryBody;
import com.tmb.oneapp.productsexpservice.model.request.fundsummary.FundSummaryRq;
import com.tmb.oneapp.productsexpservice.service.ProductsExpService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.nio.file.Paths;

import static org.mockito.Mockito.*;

public class ProductExpServiceControllerTestFundSummary {
    @Mock
    TMBLogger<ProductExpServiceController> logger;
    @Mock
    ProductsExpService productsExpService;
    @InjectMocks
    ProductExpServiceController productExpServiceController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }



    @Test
    public void testGetFundSummary() throws Exception {
        FundSummaryBody expectedResponse = null ;
        FundSummaryRq rq = new FundSummaryRq();
        rq.setCrmId("test");
        String corrID = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";

        try {

            ObjectMapper mapper = new ObjectMapper();
            expectedResponse = mapper.readValue(Paths.get("src/test/resources/investment/invest_fundsummary_data.json").toFile(),
                    FundSummaryBody.class);
            when(productsExpService.getFundSummary(anyString(), any())).thenReturn(expectedResponse);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ResponseEntity<TmbOneServiceResponse<FundSummaryBody>> result = productExpServiceController
                .getFundSummary(corrID, rq);
        Assert.assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());

        Assert.assertEquals(expectedResponse.getFundClassList().getFundClass().size(),
                result.getBody().getData().getFundClassList().getFundClass().size());
    }


    @Test
    public void testGetFundSummaryNotFound() throws Exception {
        FundSummaryBody expectedResponse = null ;
        FundSummaryRq rq = new FundSummaryRq();
        rq.setCrmId("test");
        String corrID = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";

        try {

            when(productsExpService.getFundSummary(anyString(), any())).thenReturn(expectedResponse);


        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ResponseEntity<TmbOneServiceResponse<FundSummaryBody>> result = productExpServiceController
                .getFundSummary(corrID, rq);
        Assert.assertEquals(HttpStatus.NOT_FOUND.value(), result.getStatusCode().value());

    }
}
