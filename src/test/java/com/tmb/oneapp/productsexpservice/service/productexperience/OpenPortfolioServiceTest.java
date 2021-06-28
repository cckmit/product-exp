package com.tmb.oneapp.productsexpservice.service.productexperience;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.model.common.teramandcondition.response.TermAndConditionResponse;
import com.tmb.oneapp.productsexpservice.model.common.teramandcondition.response.TermAndConditionResponseBody;
import com.tmb.oneapp.productsexpservice.model.openportfolio.request.OpenPortfolioRequest;
import com.tmb.oneapp.productsexpservice.service.ProductsExpService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenPortfolioServiceTest {

    @Mock
    private TMBLogger<ProductsExpService> logger;

    @Mock
    private CommonServiceClient commonServiceClient;

    @InjectMocks
    private OpenPortfolioService openPortfolioService;

    @Test
    void should_return_status_0000_and_body_not_null_when_call_validation_give_correlation_id_and_open_portfolio_request() throws IOException {
        // Given
        ObjectMapper mapper = new ObjectMapper();
        TermAndConditionResponse termAndConditionResponse = mapper.readValue(Paths.get("src/test/resources/investment/openportfolio/validation.json").toFile(),
                TermAndConditionResponse.class);

        TmbOneServiceResponse<TermAndConditionResponseBody> oneServiceResponse = new TmbOneServiceResponse<>();
        oneServiceResponse.setData(termAndConditionResponse.getData());
        oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                ProductsExpServiceConstant.SUCCESS_MESSAGE,
                ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

        when(commonServiceClient.getTermAndConditionByServiceCodeAndChannel(any(), any(), any())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse));

        OpenPortfolioRequest openPortfolioRequest = OpenPortfolioRequest.builder().crmId("001100000000000000000012035644").build();

        // When
        ResponseEntity<TmbOneServiceResponse<TermAndConditionResponseBody>> actual = openPortfolioService.validateOpenPortfolio("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", openPortfolioRequest);

        // Then
        assertEquals("0000", actual.getBody().getStatus().getCode());
        assertNotNull(actual.getBody());
    }
}