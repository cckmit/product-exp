package com.tmb.oneapp.productsexpservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundSummaryBody;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundSummaryResponse;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.byport.FundSummaryByPortBody;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.byport.FundSummaryByPortResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.countprocessorder.response.CountOrderProcessingResponseBody;
import com.tmb.oneapp.productsexpservice.model.response.PtesDetail;
import com.tmb.oneapp.productsexpservice.service.productexperience.customer.CustomerService;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductsExpServiceTest {

    @Mock
    private TMBLogger<ProductsExpServiceTest> logger;

    @Mock
    private InvestmentRequestClient investmentRequestClient;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private ProductsExpService productsExpService;

    @Test
    public void testGetFundSummary() throws TMBCommonException {
        String corrId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        String crmId = "001100000000000000000012025950";
        FundSummaryBody expectedResponse = new FundSummaryBody();

        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectMapper mapperPort = new ObjectMapper();

            FileInputStream fis = new FileInputStream("src/test/resources/investment/investment_port_list.txt");
            String data = IOUtils.toString(fis, "UTF-8");
            when(customerService.getAccountSaving(anyString(), anyString())).thenReturn(data);

            TmbOneServiceResponse<List<PtesDetail>> oneServiceResponsePtes = new TmbOneServiceResponse<>();
            List<PtesDetail> ptesDetailList = mapperPort.readValue(Paths.get("src/test/resources/investment/ptest.json").toFile(),
                    new TypeReference<>() {
                    });
            oneServiceResponsePtes.setData(ptesDetailList);
            oneServiceResponsePtes.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
            when(investmentRequestClient.getPtesPort(any(), anyString())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders())
                    .body(oneServiceResponsePtes));

            TmbOneServiceResponse<FundSummaryBody> oneServiceResponse = new TmbOneServiceResponse<>();
            expectedResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund_summary_data.json").toFile(),
                    FundSummaryBody.class);
            oneServiceResponse.setData(expectedResponse);
            oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
            when(investmentRequestClient.callInvestmentFundSummaryService(any(), any()))
                    .thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse));

            TmbOneServiceResponse<FundSummaryByPortBody> portResponse = new TmbOneServiceResponse<>();
            FundSummaryByPortBody fundSummaryByPortResponse = mapperPort.readValue(Paths.get("src/test/resources/investment/fund_summary_by_port.json").toFile(),
                    FundSummaryByPortBody.class);
            portResponse.setData(fundSummaryByPortResponse);
            portResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
            when(investmentRequestClient.callInvestmentFundSummaryByPortService(any(), any()))
                    .thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(portResponse));

            TmbOneServiceResponse<CountOrderProcessingResponseBody> oneServiceResponseCountToBeProcessOrder = new TmbOneServiceResponse<>();
            oneServiceResponseCountToBeProcessOrder.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
            oneServiceResponseCountToBeProcessOrder.setData(CountOrderProcessingResponseBody.builder().countProcessOrder("1").build());
            when(investmentRequestClient.callInvestmentCountProcessOrderService(any(), anyString(), any())).thenReturn(
                    ResponseEntity.ok().headers(TMBUtils.getResponseHeaders())
                            .body(oneServiceResponseCountToBeProcessOrder));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FundSummaryBody result = productsExpService.getFundSummary(corrId, crmId);
        Assert.assertEquals(expectedResponse.getFundClassList().getFundClass().size(), result.getFundClass().size());
        Assert.assertEquals(Boolean.TRUE, result.getIsPtes());
        Assert.assertEquals(0, result.getSmartPortList().size());
        assertEquals(Boolean.FALSE, result.getIsJointPortOnly());
    }

    @Test
    public void testGetFundSummaryWithSmartPort() throws TMBCommonException {
        String corrId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        String crmId = "001100000000000000000012025950";
        ObjectMapper mapperPort = new ObjectMapper();
        FundSummaryBody expectedResponse = new FundSummaryBody();

        try {
            ObjectMapper mapper = new ObjectMapper();

            FileInputStream fis = new FileInputStream("src/test/resources/investment/investment_port_list.txt");
            String data = IOUtils.toString(fis, "UTF-8");
            when(customerService.getAccountSaving(anyString(), anyString())).thenReturn(data);

            TmbOneServiceResponse<List<PtesDetail>> oneServiceResponsePtes = new TmbOneServiceResponse<>();
            List<PtesDetail> ptesDetailList = mapperPort.readValue(Paths.get("src/test/resources/investment/ptest.json").toFile(),
                    new TypeReference<>() {
                    });
            oneServiceResponsePtes.setData(ptesDetailList);
            oneServiceResponsePtes.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
            when(investmentRequestClient.getPtesPort(any(), anyString())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders())
                    .body(oneServiceResponsePtes));

            TmbOneServiceResponse<FundSummaryBody> oneServiceResponse = new TmbOneServiceResponse<>();
            expectedResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund_summary_data_with_smart_port.json").toFile(),
                    FundSummaryBody.class);
            oneServiceResponse.setData(expectedResponse);
            oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
            when(investmentRequestClient.callInvestmentFundSummaryService(any(), any()))
                    .thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse));

            TmbOneServiceResponse<FundSummaryByPortBody> portResponse = new TmbOneServiceResponse<>();
            FundSummaryByPortBody fundSummaryByPortResponse = mapperPort.readValue(Paths.get("src/test/resources/investment/fund_summary_by_port.json").toFile(),
                    FundSummaryByPortBody.class);
            portResponse.setData(fundSummaryByPortResponse);
            portResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
            when(investmentRequestClient.callInvestmentFundSummaryByPortService(any(), any()))
                    .thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(portResponse));

            TmbOneServiceResponse<CountOrderProcessingResponseBody> oneServiceResponseCountToBeProcessOrder = new TmbOneServiceResponse<>();
            oneServiceResponseCountToBeProcessOrder.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
            oneServiceResponseCountToBeProcessOrder.setData(CountOrderProcessingResponseBody.builder().countProcessOrder("1").build());
            when(investmentRequestClient.callInvestmentCountProcessOrderService(any(), anyString(), any())).thenReturn(
                    ResponseEntity.ok().headers(TMBUtils.getResponseHeaders())
                            .body(oneServiceResponseCountToBeProcessOrder));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FundSummaryBody result = productsExpService.getFundSummary(corrId, crmId);
        Assert.assertEquals(expectedResponse.getFundClassList()
                .getFundClass().size(), result.getFundClass().size());
        Assert.assertEquals(Boolean.TRUE, result.getIsPtes());
        Assert.assertEquals(2, result.getSmartPortList().size());
        Assert.assertEquals(Boolean.TRUE, result.getIsPt());
    }

    @Test
    public void testGetFundSummaryWithNoSummaryByPort() throws TMBCommonException {
        String corrId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
        String crmId = "001100000000000000000012025950";
        FundSummaryBody expectedResponse = new FundSummaryBody();

        try {
            FileInputStream fis = new FileInputStream("src/test/resources/investment/investment_port_list.txt");
            String data = IOUtils.toString(fis, "UTF-8");
            when(customerService.getAccountSaving(anyString(), anyString())).thenReturn(data);

            ObjectMapper mapper = new ObjectMapper();
            TmbOneServiceResponse<FundSummaryBody> oneServiceResponse = new TmbOneServiceResponse<>();
            expectedResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund_summary_data.json").toFile(),
                    FundSummaryBody.class);
            oneServiceResponse.setData(expectedResponse);
            oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
            when(investmentRequestClient.callInvestmentFundSummaryService(any(), any()))
                    .thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse));

            ObjectMapper mapperPort = new ObjectMapper();
            TmbOneServiceResponse<FundSummaryByPortBody> portResponse = new TmbOneServiceResponse<>();
            FundSummaryByPortBody fundSummaryByPortResponse = mapperPort.readValue(Paths.get("src/test/resources/investment/fund_summary_by_port_data_not_found.json").toFile(),
                    FundSummaryByPortBody.class);
            portResponse.setData(fundSummaryByPortResponse);
            portResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
            when(investmentRequestClient.callInvestmentFundSummaryByPortService(any(), any()))
                    .thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(portResponse));

            ObjectMapper ptestMapperPort = new ObjectMapper();
            TmbOneServiceResponse<List<PtesDetail>> oneServiceResponsePtes = new TmbOneServiceResponse<>();
            List<PtesDetail> ptesDetailList = ptestMapperPort.readValue(Paths.get("src/test/resources/investment/ptest.json").toFile(),
                    new TypeReference<>() {
                    });
            oneServiceResponsePtes.setData(ptesDetailList);
            oneServiceResponsePtes.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
            when(investmentRequestClient.getPtesPort(any(), anyString())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders())
                    .body(oneServiceResponsePtes));

            TmbOneServiceResponse<CountOrderProcessingResponseBody> oneServiceResponseCountToBeProcessOrder = new TmbOneServiceResponse<>();
            oneServiceResponseCountToBeProcessOrder.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
            oneServiceResponseCountToBeProcessOrder.setData(CountOrderProcessingResponseBody.builder().countProcessOrder("1").build());
            when(investmentRequestClient.callInvestmentCountProcessOrderService(any(), anyString(), any())).thenReturn(
                    ResponseEntity.ok().headers(TMBUtils.getResponseHeaders())
                            .body(oneServiceResponseCountToBeProcessOrder));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FundSummaryBody result = productsExpService.getFundSummary(corrId, crmId);
        Assert.assertEquals(expectedResponse.getFundClassList()
                .getFundClass().size(), result.getFundClass().size());
        Assert.assertNull(expectedResponse.getSummaryByPort());
    }
}