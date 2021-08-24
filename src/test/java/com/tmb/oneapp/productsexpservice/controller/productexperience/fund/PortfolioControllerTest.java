package com.tmb.oneapp.productsexpservice.controller.productexperience.fund;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.portfolio.response.PortfolioResponse;
import com.tmb.oneapp.productsexpservice.service.productexperience.fund.PortfolioService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortfolioControllerTest {

    @Mock
    private TMBLogger<PortfolioController> logger = new TMBLogger<>(PortfolioController.class);

    @Mock
    private PortfolioService portfolioService;

    @InjectMocks
    private PortfolioController portfolioController;

    private String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";

    private String crmId = "001100000000000000000001184383";

    @Test
    void should_return_portfolio_response_when_call_get_portfolio_list_given_correlation_id_and_crm_id_and_type() throws IOException {
        //Given
        ObjectMapper mapper = new ObjectMapper();

        PortfolioResponse portfolioResponse = mapper.readValue(Paths.get("src/test/resources/investment/fund/portfolio/portfolio_normal_port.json").toFile(),
                PortfolioResponse.class);

        when(portfolioService.getPortfolioList(correlationId, crmId, "a")).thenReturn(portfolioResponse);

        //When
        ResponseEntity<TmbOneServiceResponse<PortfolioResponse>> actual = portfolioController.getPortfolioList(correlationId, crmId, "a");

        //Then
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(portfolioResponse, actual.getBody().getData());
    }

    @Test
    void should_return_not_found_when_call_get_portfolio_list_given_return_null_from_service() {
        //Given
        when(portfolioService.getPortfolioList(correlationId, crmId, "a")).thenReturn(null);

        //When
        ResponseEntity<TmbOneServiceResponse<PortfolioResponse>> actual = portfolioController.getPortfolioList(correlationId, crmId, "a");
        //Then
        assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        assertEquals(TmbStatusUtil.notFoundStatus().getCode(), actual.getBody().getStatus().getCode());
        assertNull(actual.getBody().getData());
    }

    @Test
    void should_return_not_found_when_call_get_portfolio_list_given_throw_exception_from_service() {
        //Given
        when(portfolioService.getPortfolioList(correlationId, crmId, "a")).thenThrow(new RuntimeException("Error"));

        //When
        ResponseEntity<TmbOneServiceResponse<PortfolioResponse>> actual = portfolioController.getPortfolioList(correlationId, crmId, "a");
        //Then
        assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        assertEquals(TmbStatusUtil.notFoundStatus().getCode(), actual.getBody().getStatus().getCode());
        assertNull(actual.getBody().getData());
    }
}