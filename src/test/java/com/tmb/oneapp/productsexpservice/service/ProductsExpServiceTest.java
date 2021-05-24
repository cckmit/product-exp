package com.tmb.oneapp.productsexpservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.CommonTime;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.AccountRequestClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerExpServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundSummaryBody;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundSummaryResponse;
import com.tmb.oneapp.productsexpservice.model.request.fundffs.FfsRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fundsummary.FundSummaryRq;
import com.tmb.oneapp.productsexpservice.model.response.PtesDetail;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FfsData;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FfsResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FfsRsAndValidation;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FundResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundsummary.FundSummaryByPortResponse;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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
            when(investmentRequestClient.callInvestmentFundSummaryByPortService(any(), any()))
                    .thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(portResponse));

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
        Assert.assertEquals(0,result.getSmartPortList().size());
        Assert.assertEquals(expectedResponse.getBody().getFundClassList()
                .getFundClass().size(), result.getPtPortList().size());
    }

    @Test
    public void testGetFundSummaryWithSmartPort() throws Exception {

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
            expectedResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund_summary_data_with_smart_port.json").toFile(),
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
            when(investmentRequestClient.callInvestmentFundSummaryByPortService(any(), any()))
                    .thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(portResponse));

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
        Assert.assertEquals(2,result.getSmartPortList().size());

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

    @Test
    void testFundResponseSuccess() {
        FundResponse fundResponse = getFundResponse();
        productsExpService.fundResponseSuccess(fundResponse);
        assertNotNull(fundResponse);
    }

    private FundResponse getFundResponse() {
        FundResponse fundResponse = new FundResponse();
        fundResponse.setError(false);
        fundResponse.setErrorCode(ProductsExpServiceConstant.ID_EXPIRED_CODE);
        fundResponse.setErrorDesc(ProductsExpServiceConstant.ID_EXPIRED_MESSAGE);
        fundResponse.setErrorMsg(ProductsExpServiceConstant.ID_EXPIRED_DESC);
        return fundResponse;
    }

    @Test
    void testFundResponseError() {
        FundResponse fundResponse = getFundResponse();
        productsExpService.fundResponseError(getFundResponse(), true);
        assertNotNull(fundResponse);
    }

    @Test
    void testFundResponseData() {
        FundResponse fundResponse = getFundResponse();
        ;
        CommonTime noneServiceHour = new CommonTime();
        noneServiceHour.setStart("00:00");
        noneServiceHour.setEnd("12:00");
        productsExpService.fundResponseData(fundResponse, noneServiceHour);
        assertNotNull(noneServiceHour);

    }

    @Test
    void testErrorResponse() {
        FfsRsAndValidation validation = new FfsRsAndValidation();
        validation.setError(true);
        productsExpService.errorResponse(validation, true);
        assertNotNull(validation);
    }

    @Test
    void testFfsData() {
        FfsRsAndValidation validation = new FfsRsAndValidation();
        validation.setError(true);
        TmbOneServiceResponse<FfsResponse> response = new TmbOneServiceResponse<>();
        FfsResponse data = new FfsResponse();
        FfsData body = new FfsData();
        body.setFactSheetData("test");
        data.setBody(body);
        response.setData(data);
        ResponseEntity<TmbOneServiceResponse<FfsResponse>> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
        productsExpService.ffsData(validation, responseEntity);
        assertNotNull(responseEntity);
    }

    @Test
    void testErrorData() {
        FfsRsAndValidation validation = new FfsRsAndValidation();
        validation.setError(true);
        FundResponse fundResponse = new FundResponse();
        fundResponse.setError(true);
        productsExpService.errorData(validation, fundResponse);
        assertNotNull(fundResponse);
    }
}

