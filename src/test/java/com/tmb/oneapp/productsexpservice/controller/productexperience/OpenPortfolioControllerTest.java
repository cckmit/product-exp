package com.tmb.oneapp.productsexpservice.controller.productexperience;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.model.common.teramandcondition.response.TermAndConditionResponse;
import com.tmb.oneapp.productsexpservice.model.common.teramandcondition.response.TermAndConditionResponseBody;
import com.tmb.oneapp.productsexpservice.model.openportfolio.request.OpenPortfolioRequest;
import com.tmb.oneapp.productsexpservice.service.ProductsExpService;
import com.tmb.oneapp.productsexpservice.service.productexperience.OpenPortfolioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Paths;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.*;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenPortfolioControllerTest {

    @Mock
    private TMBLogger<ProductsExpService> logger;

    @Mock
    private OpenPortfolioService openPortfolioService;

    @InjectMocks
    private OpenPortfolioController openPortfolioController;

    @Test
    void should_term_and_condition_body_not_null_when_call_validation_give_correlation_id_and_open_portfolio_request() throws IOException {
        // Given
        ObjectMapper mapper = new ObjectMapper();
        TermAndConditionResponse termAndConditionResponse = mapper.readValue(Paths.get("src/test/resources/investment/openportfolio/validation.json").toFile(),
                TermAndConditionResponse.class);

        TmbOneServiceResponse<TermAndConditionResponseBody> oneServiceResponse = new TmbOneServiceResponse<>();
        oneServiceResponse.setData(termAndConditionResponse.getData());
        oneServiceResponse.setStatus(new TmbStatus(SUCCESS_CODE, SUCCESS_MESSAGE, SERVICE_NAME, SUCCESS_MESSAGE));

        OpenPortfolioRequest openPortfolioRequest = OpenPortfolioRequest.builder().crmId("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da").build();
        when(openPortfolioService.validateOpenPortfolio("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", openPortfolioRequest)).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse));

        // When
        ResponseEntity<TmbOneServiceResponse<TermAndConditionResponseBody>> actual = openPortfolioController.getFundAccountDetail("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", openPortfolioRequest);

        // Then
        assertNotNull(actual.getBody());
    }
}