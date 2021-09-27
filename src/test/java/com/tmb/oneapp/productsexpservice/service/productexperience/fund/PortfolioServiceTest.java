package com.tmb.oneapp.productsexpservice.service.productexperience.fund;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.byport.FundSummaryByPortBody;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.byport.FundSummaryByPortResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.portfolio.response.PortfolioResponse;
import com.tmb.oneapp.productsexpservice.service.ProductsExpService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {

    @Mock
    private TMBLogger<PortfolioServiceTest> logger = new TMBLogger<>(PortfolioServiceTest.class);

    @Mock
    private ProductsExpService productsExpService;

    @Mock
    private InvestmentRequestClient investmentRequestClient;

    @InjectMocks
    private PortfolioService portfolioService;

    private String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";

    private String crmId = "001100000000000000000001184383";

    @Test
    void should_return_portfolio_normal_response_when_call_get_portfolio_list_given_correlation_id_and_crm_id_and_type_normal() throws IOException, TMBCommonException {
        // Given
        ObjectMapper mapper = new ObjectMapper();
        when(productsExpService.getPortList(any(), anyString(), anyBoolean())).thenReturn(List.of("222222"));

        when(productsExpService.getPortList(any(), anyString(), anyBoolean())).thenReturn(List.of("222222"));

        TmbOneServiceResponse<FundSummaryByPortBody> portResponse = new TmbOneServiceResponse<>();
        FundSummaryByPortBody fundSummaryByPortResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund/portfolio/fund_summary_by_port_normal.json").toFile(),
                FundSummaryByPortBody.class);
        portResponse.setData(fundSummaryByPortResponse);
        portResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                ProductsExpServiceConstant.SUCCESS_MESSAGE,
                ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
        when(investmentRequestClient.callInvestmentFundSummaryByPortService(any(), any()))
                .thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(portResponse));

        // When
        PortfolioResponse actual = portfolioService.getPortfolioList(correlationId, crmId, "a");

        // Then
        PortfolioResponse expected = mapper.readValue(Paths.get("src/test/resources/investment/fund/portfolio/portfolio_normal_port.json").toFile(), PortfolioResponse.class);
        assertEquals(expected, actual);
    }

    @Test
    void should_return_portfolio_joint_response_when_call_get_portfolio_list_given_correlation_id_and_crm_id_and_type_joint() throws IOException, TMBCommonException {
        // Given
        ObjectMapper mapper = new ObjectMapper();
        when(productsExpService.getPortList(any(), anyString(), anyBoolean())).thenReturn(List.of("222222"));

        when(productsExpService.getPortList(any(), anyString(), anyBoolean())).thenReturn(List.of("222222"));

        TmbOneServiceResponse<FundSummaryByPortBody> portResponse = new TmbOneServiceResponse<>();
        FundSummaryByPortBody fundSummaryByPortResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund/portfolio/fund_summary_by_port_joint.json").toFile(),
                FundSummaryByPortBody.class);
        portResponse.setData(fundSummaryByPortResponse);
        portResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                ProductsExpServiceConstant.SUCCESS_MESSAGE,
                ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
        when(investmentRequestClient.callInvestmentFundSummaryByPortService(any(), any()))
                .thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(portResponse));

        // When
        PortfolioResponse actual = portfolioService.getPortfolioList(correlationId, crmId, "j");

        // Then
        PortfolioResponse expected = mapper.readValue(Paths.get("src/test/resources/investment/fund/portfolio/portfolio_joint_port.json").toFile(), PortfolioResponse.class);
        assertEquals(expected, actual);
    }

    @Test
    void should_return_null_when_call_get_get_portfolio_list_given_throw_runtime_exception_from_product_exp_service() throws JsonProcessingException, TMBCommonException {
        //Given
        when(productsExpService.getPortList(any(), anyString(), anyBoolean()))
                .thenThrow(new RuntimeException("Error"));
        // When
        PortfolioResponse actual = portfolioService.getPortfolioList(correlationId, crmId, "a");

        //Then
        assertNull(actual);
    }
}