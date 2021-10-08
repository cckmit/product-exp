package com.tmb.oneapp.productsexpservice.service.productexperience.fund;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.activitylog.transaction.service.EnterPinIsCorrectActivityLogService;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CacheServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request.OrderCreationPaymentRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.response.OrderCreationPaymentResponse;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderCreationServiceTest {

    @Mock
    public CacheServiceClient cacheServiceClient;

    @Mock
    public InvestmentRequestClient investmentRequestClient;

    @Mock
    public EnterPinIsCorrectActivityLogService enterPinIsCorrectActivityLogService;

    @Mock
    public CreditCardClient creditCardClient;

    @Mock
    public CommonServiceClient commonServiceClient;

    @InjectMocks
    OrderCreationService orderCreationService;

    private final String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";

    private final String crmId = "001100000000000000000001184383";

    @Test
    void should_return_failed_INVESTMENT_PIN_INVALID_CODE_when_call_make_transaction_with_correlationId_and_crm_id_and_ordercreation_request_body() throws TMBCommonException {

        // given
        TmbOneServiceResponse<String> response = new TmbOneServiceResponse<>();
        response.setStatus(TmbStatusUtil.successStatus());
        response.setData(null);
        when(cacheServiceClient.getCacheByKey(any(),any())).thenReturn(ResponseEntity.ok(response));

        // when
        TmbOneServiceResponse<OrderCreationPaymentResponse> actual =
                orderCreationService.makeTransaction(correlationId,crmId, OrderCreationPaymentRequestBody.builder().build());

        // then
        assertEquals(ProductsExpServiceConstant.INVESTMENT_PIN_INVALID_CODE,actual.getStatus().getCode());
        assertEquals(ProductsExpServiceConstant.INVESTMENT_PIN_INVALID_MSG,actual.getStatus().getMessage());

    }

    @Test
    void should_return_failed_duplicate_transaction_when_call_make_transaction_with_correlationId_and_crm_id_and_ordercreation_request_body() throws TMBCommonException {

        // given
        TmbOneServiceResponse<String> response = new TmbOneServiceResponse<>();
        response.setStatus(TmbStatusUtil.successStatus());
        response.setData("true");
        when(cacheServiceClient.getCacheByKey(any(),any())).thenReturn(ResponseEntity.ok(response));

        TmbOneServiceResponse<String> pinVerifyResponse = new TmbOneServiceResponse<>();
        pinVerifyResponse.setData("true");
        when(cacheServiceClient.getCacheByKey(any(),any())).thenReturn(ResponseEntity.ok(pinVerifyResponse));
        // when
        TmbOneServiceResponse<OrderCreationPaymentResponse> actual =
                orderCreationService.makeTransaction(correlationId,crmId, OrderCreationPaymentRequestBody.builder().build());

        // then
        assertEquals(ProductsExpServiceConstant.PIN_DUPLICATE_ERROR_CODE,actual.getStatus().getCode());
        assertEquals(ProductsExpServiceConstant.PIN_DUPLICATE_ERROR_MESSAGE,actual.getStatus().getMessage());

    }

//    @Test
//    void should_return_success_when_call_make_transaction_with_correlationId_and_crm_id_and_ordercreation_request_body() throws TMBCommonException {
//
//        // given
//        TmbOneServiceResponse<String> response = new TmbOneServiceResponse<>();
//        response.setStatus(TmbStatusUtil.successStatus());
//        response.setData("pin");
//        when(cacheServiceClient.getCacheByKey(any(),any())).thenReturn(ResponseEntity.ok(response));
//
//        // when
//        ResponseEntity<TmbOneServiceResponse<OrderCreationPaymentResponse>> actual =
//                orderCreationController.orderCreationPayment(correlationId,crmId, OrderCreationPaymentRequestBody.builder().build());
//
//        // then
//        assertEquals(HttpStatus.OK,actual.getStatusCode());
//        assertEquals(ProductsExpServiceConstant.SUCCESS_CODE,actual.getBody().getStatus().getCode());
//
//    }


}
