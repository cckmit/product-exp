package com.tmb.oneapp.productsexpservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.AccountRequestClient;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.request.accdetail.FundAccountRq;
import com.tmb.oneapp.productsexpservice.model.request.fundsummary.FundSummaryRq;
import com.tmb.oneapp.productsexpservice.model.response.accdetail.FundAccountRs;
import com.tmb.oneapp.productsexpservice.model.response.fundsummary.FundSummaryResponse;
import com.tmb.oneapp.productsexpservice.model.response.investment.AccDetailBody;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.io.FileInputStream;
import java.nio.file.Paths;

import static org.mockito.Mockito.*;

public class ProductsExpServiceTest {
    @Mock
    TMBLogger<ProductsExpService> logger;
    @Mock
    InvestmentRequestClient investmentRequestClient;
    @Mock
    AccountRequestClient accountRequestClient;
    @InjectMocks
    ProductsExpService productsExpService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @org.junit.jupiter.api.Test
    public void testGetFundSummary() throws Exception {

        com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundSummaryResponse expectedResponse = null ;
        FundSummaryRq rq = new FundSummaryRq();
        rq.setCrmId("test");
        rq.setUnitHolderNo("PO333");
        String corrID = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        TmbOneServiceResponse<com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundSummaryResponse> oneServiceResponse = new TmbOneServiceResponse<>();

        try {
            FileInputStream fis = new FileInputStream("src/test/resources/investment/investment_port.txt");
            String data = IOUtils.toString(fis, "UTF-8");
            ObjectMapper mapper = new ObjectMapper();
            expectedResponse = mapper.readValue(Paths.get("src/test/resources/investment/invest_fundsummary_data.json").toFile(),
                    com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundSummaryResponse.class);
            oneServiceResponse.setData(expectedResponse);
            oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
            when(investmentRequestClient.callInvestmentFundSummaryService(any(), any()))
                    .thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse));
            when(accountRequestClient.getPortList(any(), anyString())).thenReturn(data);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
      FundSummaryResponse result = productsExpService.getFundSummary(corrID,rq);
        Assert.assertEquals(expectedResponse.getBody().getFundClassList()
                .getFundClass().size(),result.getData().getBody().getFundClassList().getFundClass().size());
    }
}

