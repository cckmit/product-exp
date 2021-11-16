package com.tmb.oneapp.productsexpservice.service.productexperience.transaction;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.CardInfo;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.CreditCardDetail;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.FetchCardResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.transaction.orderaip.request.OrderAIPRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.transaction.orderaip.response.OrderAIPResponseBody;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AipServiceTest {

    @Mock
    public InvestmentRequestClient investmentRequestClient;

    @Mock
    public CreditCardClient creditCardClient;

    @InjectMocks
    public AipService aipService;

    private final String crmId = "001100000000000000000012035644";

    private final String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";

    @Test
    void should_return_status_0000_and_body_not_null_when_call_create_aip_order_give_correlation_id_and_crm_id_and_order_aip_request_body() throws Exception {

        // Given
        FetchCardResponse cardResponse = new FetchCardResponse();
        CreditCardDetail creditCardDetail = new CreditCardDetail();
        CardInfo cardInfo = new CardInfo();
        cardInfo.setExpiredBy("0722");
        creditCardDetail.setCardInfo(cardInfo);
        cardResponse.setCreditCard(creditCardDetail);
        when(creditCardClient.getCreditCardDetails(any(), any())).thenReturn(ResponseEntity.ok(cardResponse));

        TmbOneServiceResponse<OrderAIPResponseBody> orderAIPResponse = new TmbOneServiceResponse<>();
        orderAIPResponse.setStatus(TmbStatusUtil.successStatus());
        when(investmentRequestClient.createAipOrder(any(),any())).thenReturn(ResponseEntity.ok(orderAIPResponse));

        // when
        TmbOneServiceResponse<OrderAIPResponseBody> actual = aipService.createAipOrder(correlationId,crmId, OrderAIPRequestBody.builder().orderType("C").build());

        // then
        assertEquals(ProductsExpServiceConstant.SUCCESS_CODE,actual.getStatus().getCode());
        assertEquals(ProductsExpServiceConstant.SUCCESS_MESSAGE,actual.getStatus().getMessage());

    }

    @Test
    void should_return_null_when_call_create_aip_order_give_correlation_id_and_crm_id_and_order_aip_request_body() throws Exception {

        // Given
        when(creditCardClient.getCreditCardDetails(any(), any())).thenThrow(MockitoException.class);

        // when
        TmbOneServiceResponse<OrderAIPResponseBody> actual = aipService.createAipOrder(correlationId,crmId, OrderAIPRequestBody.builder().orderType("C").build());

        // then
        assertNull(actual.getStatus());
        assertNull(actual.getData());

    }


    @Test
    void should_throw_tmb_common_exception_when_call_create_aip_order_give_correlation_id_and_crm_id_and_order_aip_request_body() {

        // Given
        String errorCode = "2000005";
        String errorMessage = "Bad Request";
        when(investmentRequestClient.createAipOrder(any(),any())).thenThrow(mockFeignExceptionBadRequest(errorCode,errorMessage));

        // when
        try {
            aipService.createAipOrder(correlationId,crmId, OrderAIPRequestBody.builder().build());
        }catch (TMBCommonException e){

            // then
            assertEquals(errorCode,e.getErrorCode());
            assertEquals(errorMessage,e.getErrorMessage());

        }
    }

    private FeignException mockFeignExceptionBadRequest(String errorCode, String errorMessage) {
        Request.Body body = Request.Body.create("".getBytes(StandardCharsets.UTF_8));
        RequestTemplate template = new RequestTemplate();
        Map<String, Collection<String>> headers = new HashMap<>();
        String errorBody = "{\n" +
                "    \"status\": {\n" +
                "        \"code\": \"" + errorCode + "\",\n" +
                "        \"message\": \"" + errorMessage + "\",\n" +
                "        \"service\": null,\n" +
                "        \"description\": \"Please enter PIN\"\n" +
                "    },\n" +
                "    \"data\": null\n" +
                "}";
        Request request = Request.create(Request.HttpMethod.POST, "http://localhost", headers, body, template);
        FeignException.BadRequest e = new FeignException.BadRequest("", request, errorBody.getBytes(StandardCharsets.UTF_8));
        return e;
    }




}
