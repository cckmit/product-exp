package com.tmb.oneapp.productsexpservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.CommonTime;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerExpServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundSummaryBody;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundSummaryResponse;
import com.tmb.oneapp.productsexpservice.model.response.PtesDetail;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.countprocessorder.response.CountOrderProcessingResponseBody;
import com.tmb.oneapp.productsexpservice.model.response.fundfactsheet.FundFactSheetData;
import com.tmb.oneapp.productsexpservice.model.response.fundfactsheet.FundFactSheetResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundfactsheet.FundFactSheetValidationResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundfactsheet.FundResponse;
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

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class ProductsExpServiceTest {

    @Mock
    private TMBLogger<ProductsExpServiceTest> logger;

    @Mock
    private InvestmentRequestClient investmentRequestClient;

    @Mock
    private CustomerExpServiceClient customerExpServiceClient;

    @InjectMocks
    private ProductsExpService productsExpService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetFundSummary() {
        List<PtesDetail> ptesDetailList;
        String corrId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        String crmId = "001100000000000000000012025950";
        FundSummaryByPortResponse fundSummaryByPortResponse;
        FundSummaryResponse expectedResponse = new FundSummaryResponse();
        TmbOneServiceResponse<FundSummaryResponse> oneServiceResponse = new TmbOneServiceResponse<>();
        TmbOneServiceResponse<List<PtesDetail>> oneServiceResponsePtes = new TmbOneServiceResponse<>();
        TmbOneServiceResponse<CountOrderProcessingResponseBody> oneServiceResponseCountToBeProcessOrder = new TmbOneServiceResponse<>();
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
            ptesDetailList = mapperPort.readValue(Paths.get("src/test/resources/investment/ptest.json").toFile(),
                    new TypeReference<List<PtesDetail>>() {
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

            oneServiceResponseCountToBeProcessOrder.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
            oneServiceResponseCountToBeProcessOrder.setData(CountOrderProcessingResponseBody.builder().countProcessOrder("1").build());

            when(investmentRequestClient.callInvestmentFundSummaryService(any(), any()))
                    .thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse));
            when(investmentRequestClient.callInvestmentFundSummaryByPortService(any(), any()))
                    .thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(portResponse));

            when(customerExpServiceClient.getAccountSaving(anyString(), anyString())).thenReturn(data);
            when(investmentRequestClient.getPtesPort(any(), anyString())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders())
                    .body(oneServiceResponsePtes));

            when(investmentRequestClient.callInvestmentCountProcessOrderService(any(), anyString(), any())).thenReturn(
                    ResponseEntity.ok().headers(TMBUtils.getResponseHeaders())
                            .body(oneServiceResponseCountToBeProcessOrder));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FundSummaryBody result = productsExpService.getFundSummary(corrId, crmId);
        Assert.assertEquals(expectedResponse.getBody().getFundClassList()
                .getFundClass().size(), result.getFundClass().size());
        Assert.assertEquals(Boolean.TRUE, result.getIsPtes());
        Assert.assertEquals(0, result.getSmartPortList().size());
        Assert.assertEquals(expectedResponse.getBody().getFundClassList()
                .getFundClass().size(), result.getPtPortList().size());
    }

    @Test
    public void testGetFundSummaryWithSmartPort() {
        List<PtesDetail> ptesDetailList;
        String corrId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        String crmId = "001100000000000000000012025950";
        FundSummaryByPortResponse fundSummaryByPortResponse;
        FundSummaryResponse expectedResponse = new FundSummaryResponse();
        TmbOneServiceResponse<FundSummaryResponse> oneServiceResponse = new TmbOneServiceResponse<>();
        TmbOneServiceResponse<List<PtesDetail>> oneServiceResponsePtes = new TmbOneServiceResponse<>();
        TmbOneServiceResponse<FundSummaryByPortResponse> portResponse = new TmbOneServiceResponse<>();
        TmbOneServiceResponse<CountOrderProcessingResponseBody> oneServiceResponseCountToBeProcessOrder = new TmbOneServiceResponse<>();

        try {
            FileInputStream fis = new FileInputStream("src/test/resources/investment/investment_port_list.txt");
            String data = IOUtils.toString(fis, "UTF-8");
            ObjectMapper mapper = new ObjectMapper();
            expectedResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund_summary_data_with_smart_port.json").toFile(),
                    FundSummaryResponse.class);
            ObjectMapper mapperPort = new ObjectMapper();
            fundSummaryByPortResponse = mapperPort.readValue(Paths.get("src/test/resources/investment/fund_summary_by_port.json").toFile(),
                    FundSummaryByPortResponse.class);
            ptesDetailList = mapperPort.readValue(Paths.get("src/test/resources/investment/ptest.json").toFile(),
                    new TypeReference<List<PtesDetail>>() {
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

            oneServiceResponseCountToBeProcessOrder.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
            oneServiceResponseCountToBeProcessOrder.setData(CountOrderProcessingResponseBody.builder().countProcessOrder("1").build());

            when(investmentRequestClient.callInvestmentFundSummaryService(any(), any()))
                    .thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse));
            when(investmentRequestClient.callInvestmentFundSummaryByPortService(any(), any()))
                    .thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(portResponse));

            when(customerExpServiceClient.getAccountSaving(anyString(), anyString())).thenReturn(data);
            when(investmentRequestClient.getPtesPort(any(), anyString())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders())
                    .body(oneServiceResponsePtes));

            when(investmentRequestClient.callInvestmentCountProcessOrderService(any(), anyString(), any())).thenReturn(
                    ResponseEntity.ok().headers(TMBUtils.getResponseHeaders())
                            .body(oneServiceResponseCountToBeProcessOrder));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FundSummaryBody result = productsExpService.getFundSummary(corrId, crmId);
        Assert.assertEquals(expectedResponse.getBody().getFundClassList()
                .getFundClass().size(), result.getFundClass().size());
        Assert.assertEquals(Boolean.TRUE, result.getIsPtes());
        Assert.assertEquals(2, result.getSmartPortList().size());
        Assert.assertEquals(Boolean.TRUE, result.getIsPt());
    }

    @Test
    public void testGetFundSummaryWithNoSummaryByPort() {
        String corrId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        String crmId = "001100000000000000000012025950";
        FundSummaryByPortResponse fundSummaryByPortResponse;
        FundSummaryResponse expectedResponse = new FundSummaryResponse();
        TmbOneServiceResponse<FundSummaryResponse> oneServiceResponse = new TmbOneServiceResponse<>();
        TmbOneServiceResponse<FundSummaryByPortResponse> portResponse = new TmbOneServiceResponse<>();
        TmbOneServiceResponse<CountOrderProcessingResponseBody> oneServiceResponseCountToBeProcessOrder = new TmbOneServiceResponse<>();

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

            oneServiceResponseCountToBeProcessOrder.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
            oneServiceResponseCountToBeProcessOrder.setData(CountOrderProcessingResponseBody.builder().countProcessOrder("1").build());

            when(investmentRequestClient.callInvestmentFundSummaryService(any(), any()))
                    .thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse));
            when(customerExpServiceClient.getAccountSaving(anyString(), anyString())).thenReturn(data);
            when(investmentRequestClient.callInvestmentFundSummaryByPortService(any(), any()))
                    .thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(portResponse));
            when(investmentRequestClient.callInvestmentCountProcessOrderService(any(), anyString(), any())).thenReturn(
                    ResponseEntity.ok().headers(TMBUtils.getResponseHeaders())
                            .body(oneServiceResponseCountToBeProcessOrder));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FundSummaryBody result = productsExpService.getFundSummary(corrId, crmId);
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
        CommonTime noneServiceHour = new CommonTime();
        noneServiceHour.setStart("00:00");
        noneServiceHour.setEnd("12:00");
        productsExpService.fundResponseData(fundResponse, noneServiceHour);
        assertNotNull(noneServiceHour);
    }

    @Test
    void testErrorResponse() {
        FundFactSheetValidationResponse validation = new FundFactSheetValidationResponse();
        validation.setError(true);
        productsExpService.errorResponse(validation, true);
        assertNotNull(validation);
    }

    @Test
    void testFfsData() {
        FundFactSheetValidationResponse validation = new FundFactSheetValidationResponse();
        validation.setError(true);
        TmbOneServiceResponse<FundFactSheetResponse> response = new TmbOneServiceResponse<>();
        FundFactSheetResponse data = new FundFactSheetResponse();
        FundFactSheetData body = new FundFactSheetData();
        body.setFactSheetData("test");
        data.setBody(body);
        response.setData(data);
        ResponseEntity<TmbOneServiceResponse<FundFactSheetResponse>> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
        productsExpService.ffsData(validation, responseEntity);
        assertNotNull(responseEntity);
    }

    @Test
    void testErrorData() {
        FundFactSheetValidationResponse validation = new FundFactSheetValidationResponse();
        validation.setError(true);
        FundResponse fundResponse = new FundResponse();
        fundResponse.setError(true);
        productsExpService.errorData(validation, fundResponse);
        assertNotNull(fundResponse);
    }
}