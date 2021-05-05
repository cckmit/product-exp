package com.tmb.oneapp.productsexpservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.AccountRequestClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerExpServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundSummaryBody;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundSummaryResponse;
import com.tmb.oneapp.productsexpservice.model.request.fundsummary.FundSummaryRq;
import com.tmb.oneapp.productsexpservice.model.response.PtesDetail;
import com.tmb.oneapp.productsexpservice.model.response.fundsummary.FundSummaryByPortResponse;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.List;

import static org.mockito.Mockito.*;

public class ProductsExpServiceTest {
    @Mock
    TMBLogger<ProductsExpService> logger;
    @Mock
    InvestmentRequestClient investmentRequestClient;
    @Mock
    AccountRequestClient accountRequestClient;
    @Mock
    CustomerExpServiceClient customerExpServiceClient;
    @InjectMocks
    ProductsExpService productsExpService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testGetFundSummary() throws Exception {

        FundSummaryRq rq = new FundSummaryRq();
        rq.setCrmId("test");
        String corrID = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        FundSummaryResponse expectedResponse = new FundSummaryResponse();
        FundSummaryByPortResponse fundSummaryByPortResponse;
        TmbOneServiceResponse<FundSummaryResponse> oneServiceResponse = new TmbOneServiceResponse<>();
         TmbOneServiceResponse<List<PtesDetail>> oneServiceResponsePtes = new TmbOneServiceResponse<>();
        List<PtesDetail> ptesDetailList = null ;
        TmbOneServiceResponse<FundSummaryByPortResponse> portResponse = new TmbOneServiceResponse<>();

        try {
            FileInputStream fis = new FileInputStream("src/test/resources/investment/investment_port_list.txt");
            String data = IOUtils.toString(fis, "UTF-8");
            ObjectMapper mapper = new ObjectMapper();
            expectedResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund_summary_data.json").toFile(),
                    FundSummaryResponse.class);
            ObjectMapper mapperPort = new ObjectMapper();
            fundSummaryByPortResponse = mapperPort.readValue(Paths.get("src/test/resources/investment/fund_summary_by_port.json").toFile(),
                    FundSummaryByPortResponse.class);
            ptesDetailList =  mapperPort.readValue(Paths.get("src/test/resources/investment/ptest.json").toFile(),
                    new TypeReference <List<PtesDetail>>() {
                    });
            oneServiceResponse.setData(expectedResponse);
            oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            oneServiceResponsePtes.setData(ptesDetailList);
            oneServiceResponsePtes.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            portResponse.setData(fundSummaryByPortResponse);
            portResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));


            when(investmentRequestClient.callInvestmentFundSummaryService(any(), any()))
                    .thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse));
            when(customerExpServiceClient.getAccountSaving(any(), anyString())).thenReturn(data);
            when(investmentRequestClient.getPtesPort(any(),any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders())
                    .body(oneServiceResponsePtes));


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        FundSummaryBody result = productsExpService.getFundSummary(corrID, rq);
        Assert.assertEquals(expectedResponse.getBody().getFundClassList()
                .getFundClass().size(), result.getFundClass().size());
        Assert.assertEquals(Boolean.TRUE,result.getIsPtes());
    }


    @Test
    public void testGetFundSummaryWithNoSummaryByPort() throws Exception {

        FundSummaryRq rq = new FundSummaryRq();
        rq.setCrmId("test");
        String corrID = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        FundSummaryResponse expectedResponse = new FundSummaryResponse();
        FundSummaryByPortResponse fundSummaryByPortResponse;
        TmbOneServiceResponse<FundSummaryResponse> oneServiceResponse = new TmbOneServiceResponse<>();
        TmbOneServiceResponse<FundSummaryByPortResponse> portResponse = new TmbOneServiceResponse<>();

        try {
            FileInputStream fis = new FileInputStream("src/test/resources/investment/investment_port_list.txt");
            String data = IOUtils.toString(fis, "UTF-8");
            ObjectMapper mapper = new ObjectMapper();
            expectedResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund_summary_data.json").toFile(),
                    FundSummaryResponse.class);
            ObjectMapper mapperPort = new ObjectMapper();
            fundSummaryByPortResponse = mapperPort.readValue(Paths.get("src/test/resources/investment/fund_summary_by_port_data_not_found.json").toFile(),
                    FundSummaryByPortResponse.class);
            oneServiceResponse.setData(expectedResponse);
            oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
            portResponse.setData(fundSummaryByPortResponse);
            portResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

            when(investmentRequestClient.callInvestmentFundSummaryService(any(), any()))
                    .thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse));
            when(customerExpServiceClient.getAccountSaving(any(), anyString())).thenReturn(data);
            when(investmentRequestClient.callInvestmentFundSummaryByPortService(any(), any()))
                    .thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(portResponse));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        FundSummaryBody result = productsExpService.getFundSummary(corrID, rq);
        Assert.assertEquals(expectedResponse.getBody().getFundClassList()
                .getFundClass().size(), result.getFundClass().size());
        Assert.assertNull(expectedResponse.getBody().getSummaryByPort());
    }

}

