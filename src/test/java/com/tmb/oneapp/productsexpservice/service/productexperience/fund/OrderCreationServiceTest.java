package com.tmb.oneapp.productsexpservice.service.productexperience.fund;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.activitylog.transaction.service.EnterPinIsCorrectActivityLogService;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.*;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.CardInfo;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.CreditCardDetail;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.FetchCardResponse;
import com.tmb.oneapp.productsexpservice.model.common.findbyfundhouse.FundHouseBankData;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request.Account;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request.Fee;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request.OrderCreationPaymentRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.response.AccountDetail;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.response.OrderConfirmPayment;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.response.OrderCreationPaymentResponse;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderCreationServiceTest {

    @Mock
    private CacheServiceClient cacheServiceClient;

    @Mock
    private InvestmentRequestClient investmentRequestClient;

    @Mock
    private EnterPinIsCorrectActivityLogService enterPinIsCorrectActivityLogService;

    @Mock
    private CreditCardClient creditCardClient;

    @Mock
    private CommonServiceClient commonServiceClient;

    @Mock
    private FinancialServiceClient financialServiceClient;

    @InjectMocks
    private OrderCreationService orderCreationService;

    private final String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";

    private final String crmId = "001100000000000000000001184383";

    private final String ipAddress = "0.0.0.0";

    @Test
    void should_return_failed_INVESTMENT_PIN_INVALID_CODE_when_call_make_transaction_with_correlation_id_and_crm_id_and_ip_address_and_order_creation_request_body() throws TMBCommonException {
        // Given
        TmbOneServiceResponse<String> response = new TmbOneServiceResponse<>();
        response.setStatus(TmbStatusUtil.successStatus());
        response.setData(null);
        when(cacheServiceClient.getCacheByKey(any(), any())).thenReturn(ResponseEntity.ok(response));

        // When
        TmbOneServiceResponse<OrderCreationPaymentResponse> actual =
                orderCreationService.makeTransaction(correlationId, crmId, ipAddress, OrderCreationPaymentRequestBody.builder().build());

        // Then
        assertEquals(ProductsExpServiceConstant.INVESTMENT_PIN_INVALID_CODE, actual.getStatus().getCode());
        assertEquals(ProductsExpServiceConstant.INVESTMENT_PIN_INVALID_MSG, actual.getStatus().getMessage());
    }

    @Test
    void should_return_failed_duplicate_transaction_when_call_make_transaction_with_correlation_id_and_crm_id_and_ip_address_and_order_creation_request_body() throws TMBCommonException {
        // Given
        TmbOneServiceResponse<String> response = new TmbOneServiceResponse<>();
        response.setStatus(TmbStatusUtil.successStatus());
        response.setData("true");
        when(cacheServiceClient.getCacheByKey(any(), any())).thenReturn(ResponseEntity.ok(response));

        TmbOneServiceResponse<String> pinVerifyResponse = new TmbOneServiceResponse<>();
        pinVerifyResponse.setData("true");
        when(cacheServiceClient.getCacheByKey(any(), any())).thenReturn(ResponseEntity.ok(pinVerifyResponse));
        // When
        TmbOneServiceResponse<OrderCreationPaymentResponse> actual =
                orderCreationService.makeTransaction(correlationId, crmId, ipAddress, OrderCreationPaymentRequestBody.builder().build());

        // Then
        assertEquals(ProductsExpServiceConstant.PIN_DUPLICATE_ERROR_CODE, actual.getStatus().getCode());
        assertEquals(ProductsExpServiceConstant.PIN_DUPLICATE_ERROR_MESSAGE, actual.getStatus().getMessage());
    }

    @Test
    void buy_flow_saving_account_should_return_success_when_call_make_transaction_with_correlation_id_and_crm_id_and_ip_address_and_order_creation_request_body() throws TMBCommonException {
        // Given
        TmbOneServiceResponse<String> response = new TmbOneServiceResponse<>();
        response.setStatus(TmbStatusUtil.successStatus());
        response.setData("true");
        when(cacheServiceClient.getCacheByKey(any(), any())).thenReturn(ResponseEntity.ok(response));

        TmbOneServiceResponse<String> pinVerifyResponse = new TmbOneServiceResponse<>();
        pinVerifyResponse.setData("not true");
        when(cacheServiceClient.getCacheByKey(any(), any())).thenReturn(ResponseEntity.ok(pinVerifyResponse));

        TmbOneServiceResponse<FundHouseBankData> tmbFundHouseResponse = new TmbOneServiceResponse<>();
        tmbFundHouseResponse.setData(FundHouseBankData.builder()
                .toAccountNo("a").accountType("884").financialId("441").ltfMerchantId("ltf").rmfMerchantId("rmf")
                .build());
        when(commonServiceClient.fetchBankInfoByFundHouse(any(), any())).thenReturn(ResponseEntity.ok(tmbFundHouseResponse));

        TmbOneServiceResponse<OrderCreationPaymentResponse> orderCreationResponse = new TmbOneServiceResponse<>();
        orderCreationResponse.setStatus(TmbStatusUtil.successStatus());
        OrderConfirmPayment orderConfirmPayment = new OrderConfirmPayment();
        orderConfirmPayment.setPaymentChannel("mib");
        orderConfirmPayment.setFromAccount(Account.builder().build());
        orderConfirmPayment.setToAccount(Account.builder().build());
        orderConfirmPayment.setFee(Fee.builder().build());
        orderConfirmPayment.setAccount(new AccountDetail());
        orderCreationResponse.setData(OrderCreationPaymentResponse.builder().paymentObject(orderConfirmPayment).build());
        when(investmentRequestClient.createOrderPayment(any(), any())).thenReturn(ResponseEntity.ok(orderCreationResponse));

        TmbOneServiceResponse<String> saveOrderResponse = new TmbOneServiceResponse<>();
        saveOrderResponse.setStatus(TmbStatusUtil.successStatus());
        when(investmentRequestClient.saveOrderPayment(any(), any())).thenReturn(ResponseEntity.ok(saveOrderResponse));

        TmbOneServiceResponse<String> processFirstTradeResponse = new TmbOneServiceResponse<>();
        processFirstTradeResponse.setStatus(TmbStatusUtil.successStatus());
        when(investmentRequestClient.processFirstTrade(any(), any())).thenReturn(ResponseEntity.ok(processFirstTradeResponse));

        // When
        OrderCreationPaymentRequestBody request = OrderCreationPaymentRequestBody.builder()
                .orderType("P").creditCard(false).build();
        TmbOneServiceResponse<OrderCreationPaymentResponse> actual =
                orderCreationService.makeTransaction(correlationId, crmId, ipAddress, request);

        // Then
        assertEquals(ProductsExpServiceConstant.SUCCESS_CODE, actual.getStatus().getCode());
        verify(cacheServiceClient, times(1)).putCacheByKey(any(), any());

        // after payment
        verify(financialServiceClient, times(1)).syncData(any(), any());
        verify(financialServiceClient, times(1)).saveActivity(any(), any());
        verify(investmentRequestClient, times(1)).saveOrderPayment(any(), any());
        verify(investmentRequestClient, times(1)).processFirstTrade(any(), any());
        verify(enterPinIsCorrectActivityLogService, times(1)).save(anyString(), anyString(), anyString(), any(), any());
    }

    @Test
    void buy_flow_credit_card_should_return_success_when_call_make_transaction_with_correlation_id_and_crm_id_and_ip_address_and_order_creation_request_body() throws TMBCommonException {
        // Given
        TmbOneServiceResponse<String> response = new TmbOneServiceResponse<>();
        response.setStatus(TmbStatusUtil.successStatus());
        response.setData("true");
        when(cacheServiceClient.getCacheByKey(any(), any())).thenReturn(ResponseEntity.ok(response));

        TmbOneServiceResponse<String> pinVerifyResponse = new TmbOneServiceResponse<>();
        pinVerifyResponse.setData("not true");
        when(cacheServiceClient.getCacheByKey(any(), any())).thenReturn(ResponseEntity.ok(pinVerifyResponse));

        TmbOneServiceResponse<FundHouseBankData> tmbFundHouseResponse = new TmbOneServiceResponse<>();
        tmbFundHouseResponse.setData(FundHouseBankData.builder()
                .toAccountNo("a").accountType("884").financialId("441").ltfMerchantId("ltf").rmfMerchantId("rmf")
                .build());
        when(commonServiceClient.fetchBankInfoByFundHouse(any(), any())).thenReturn(ResponseEntity.ok(tmbFundHouseResponse));

        CreditCardDetail creditCardDetail = new CreditCardDetail();
        CardInfo cardInfo = new CardInfo();

        creditCardDetail.setCardInfo(cardInfo);
        FetchCardResponse fetchCardResponse = new FetchCardResponse();
        fetchCardResponse.setCreditCard(creditCardDetail);
        when(creditCardClient.getCreditCardDetails(any(), any())).thenReturn(ResponseEntity.ok(fetchCardResponse));

        TmbOneServiceResponse<OrderCreationPaymentResponse> orderCreationResponse = new TmbOneServiceResponse<>();
        orderCreationResponse.setStatus(TmbStatusUtil.successStatus());
        OrderConfirmPayment orderConfirmPayment = new OrderConfirmPayment();
        orderConfirmPayment.setPaymentChannel("mib");
        orderConfirmPayment.setFromAccount(Account.builder().build());
        orderConfirmPayment.setToAccount(Account.builder().build());
        orderConfirmPayment.setFee(Fee.builder().build());
        orderConfirmPayment.setAccount(new AccountDetail());
        orderCreationResponse.setData(OrderCreationPaymentResponse.builder().paymentObject(orderConfirmPayment).build());
        when(investmentRequestClient.createOrderPayment(any(), any())).thenReturn(ResponseEntity.ok(orderCreationResponse));

        TmbOneServiceResponse<String> saveOrderResponse = new TmbOneServiceResponse<>();
        saveOrderResponse.setStatus(TmbStatusUtil.successStatus());
        when(investmentRequestClient.saveOrderPayment(any(), any())).thenReturn(ResponseEntity.ok(saveOrderResponse));

        TmbOneServiceResponse<String> processFirstTradeResponse = new TmbOneServiceResponse<>();
        processFirstTradeResponse.setStatus(TmbStatusUtil.successStatus());
        when(investmentRequestClient.processFirstTrade(any(), any())).thenReturn(ResponseEntity.ok(processFirstTradeResponse));

        // When
        OrderCreationPaymentRequestBody request = OrderCreationPaymentRequestBody.builder()
                .orderType("P")
                .creditCard(true)
                .fromAccount(Account.builder().accountId("accid").build())
                .build();
        TmbOneServiceResponse<OrderCreationPaymentResponse> actual =
                orderCreationService.makeTransaction(correlationId, crmId, ipAddress, request);

        // Then
        assertEquals(ProductsExpServiceConstant.SUCCESS_CODE, actual.getStatus().getCode());
        verify(cacheServiceClient, times(1)).putCacheByKey(any(), any());
        verify(creditCardClient, times(1)).getCreditCardDetails(any(), any());

        // After Payment
        verify(financialServiceClient, times(1)).syncData(any(), any());
        verify(financialServiceClient, times(1)).saveActivity(any(), any());
        verify(investmentRequestClient, times(1)).saveOrderPayment(any(), any());
        verify(investmentRequestClient, times(1)).processFirstTrade(any(), any());
        verify(enterPinIsCorrectActivityLogService, times(1)).save(anyString(), anyString(), anyString(), any(), any());
    }

    @Test
    void sell_or_switch_flow_credit_card_should_return_success_when_call_make_transaction_with_correlation_id_and_crm_id_and_ip_address_and_order_creation_request_body() throws TMBCommonException {
        // Given
        TmbOneServiceResponse<String> response = new TmbOneServiceResponse<>();
        response.setStatus(TmbStatusUtil.successStatus());
        response.setData("true");
        when(cacheServiceClient.getCacheByKey(any(), any())).thenReturn(ResponseEntity.ok(response));

        TmbOneServiceResponse<String> pinVerifyResponse = new TmbOneServiceResponse<>();
        pinVerifyResponse.setData("not true");
        when(cacheServiceClient.getCacheByKey(any(), any())).thenReturn(ResponseEntity.ok(pinVerifyResponse));

        TmbOneServiceResponse<OrderCreationPaymentResponse> orderCreationResponse = new TmbOneServiceResponse<>();
        orderCreationResponse.setStatus(TmbStatusUtil.successStatus());
        orderCreationResponse.setData(OrderCreationPaymentResponse.builder().build());
        when(investmentRequestClient.createOrderPayment(any(), any())).thenReturn(ResponseEntity.ok(orderCreationResponse));

        TmbOneServiceResponse<String> saveOrderResponse = new TmbOneServiceResponse<>();
        saveOrderResponse.setStatus(TmbStatusUtil.successStatus());
        when(investmentRequestClient.saveOrderPayment(any(), any())).thenReturn(ResponseEntity.ok(saveOrderResponse));

        TmbOneServiceResponse<String> processFirstTradeResponse = new TmbOneServiceResponse<>();
        processFirstTradeResponse.setStatus(TmbStatusUtil.successStatus());
        when(investmentRequestClient.processFirstTrade(any(), any())).thenReturn(ResponseEntity.ok(processFirstTradeResponse));

        // When
        OrderCreationPaymentRequestBody request = OrderCreationPaymentRequestBody.builder()
                .orderType("S")
                .creditCard(false)
                .fromAccount(Account.builder().accountId("accid").build())
                .build();
        TmbOneServiceResponse<OrderCreationPaymentResponse> actual =
                orderCreationService.makeTransaction(correlationId, crmId, ipAddress, request);

        // Then
        assertEquals(ProductsExpServiceConstant.SUCCESS_CODE, actual.getStatus().getCode());
        verify(cacheServiceClient, times(1)).putCacheByKey(any(), any());

        // After Payment
        verify(investmentRequestClient, times(1)).saveOrderPayment(any(), any());
        verify(investmentRequestClient, times(1)).processFirstTrade(any(), any());
        verify(enterPinIsCorrectActivityLogService, times(1)).save(anyString(), anyString(), anyString(), any(), any());
    }

    @Test
    void should_return_tmb_common_exception_bad_request_when_call_make_transaction_with_correlation_id_and_crm_id_and_ip_address_and_order_creation_request_body() {
        String errorCode = "2000009";
        String errorMessage = "Bad Request";

        // Given
        try {
            TmbOneServiceResponse<String> response = new TmbOneServiceResponse<>();
            response.setStatus(TmbStatusUtil.successStatus());
            response.setData("true");
            when(cacheServiceClient.getCacheByKey(any(), any())).thenReturn(ResponseEntity.ok(response));

            TmbOneServiceResponse<String> pinVerifyResponse = new TmbOneServiceResponse<>();
            pinVerifyResponse.setData("not true");
            when(cacheServiceClient.getCacheByKey(any(), any())).thenReturn(ResponseEntity.ok(pinVerifyResponse));

            TmbOneServiceResponse<FundHouseBankData> tmbFundHouseResponse = new TmbOneServiceResponse<>();
            tmbFundHouseResponse.setData(FundHouseBankData.builder()
                    .toAccountNo("a").accountType("884").financialId("441").ltfMerchantId("ltf").rmfMerchantId("rmf")
                    .build());
            when(commonServiceClient.fetchBankInfoByFundHouse(any(), any())).thenReturn(ResponseEntity.ok(tmbFundHouseResponse));

            when(investmentRequestClient.createOrderPayment(any(), any())).thenThrow(mockFeignExceptionBadRequest(errorCode, errorMessage));

            // When
            OrderCreationPaymentRequestBody request = OrderCreationPaymentRequestBody.builder()
                    .orderType("P").creditCard(false).build();
            TmbOneServiceResponse<OrderCreationPaymentResponse> actual =
                    orderCreationService.makeTransaction(correlationId, crmId, ipAddress, request);

        } catch (TMBCommonException ex) {
            assertEquals(errorCode, ex.getErrorCode());
            assertEquals(errorMessage, ex.getErrorMessage());
            verify(enterPinIsCorrectActivityLogService, times(1)).save(anyString(), anyString(), anyString(), any(), any());
        }
    }

    @Test
    void should_return_tmb_common_exception_data_not_found_when_call_make_transaction_with_correlation_id_and_crm_id_and_ip_address_and_order_creation_request_body() {
        String errorCode = "2000009";
        String errorMessage = "Data not found";

        // Given
        try {
            TmbOneServiceResponse<String> response = new TmbOneServiceResponse<>();
            response.setStatus(TmbStatusUtil.successStatus());
            response.setData("true");
            when(cacheServiceClient.getCacheByKey(any(), any())).thenReturn(ResponseEntity.ok(response));

            TmbOneServiceResponse<String> pinVerifyResponse = new TmbOneServiceResponse<>();
            pinVerifyResponse.setData("not true");
            when(cacheServiceClient.getCacheByKey(any(), any())).thenReturn(ResponseEntity.ok(pinVerifyResponse));

            TmbOneServiceResponse<FundHouseBankData> tmbFundHouseResponse = new TmbOneServiceResponse<>();
            tmbFundHouseResponse.setData(FundHouseBankData.builder()
                    .toAccountNo("a").accountType("884").financialId("441").ltfMerchantId("ltf").rmfMerchantId("rmf")
                    .build());
            when(commonServiceClient.fetchBankInfoByFundHouse(any(), any())).thenReturn(ResponseEntity.ok(tmbFundHouseResponse));

            when(investmentRequestClient.createOrderPayment(any(), any())).thenThrow(mockFeignExceptionDataNotFound(errorCode, errorMessage));

            // When
            OrderCreationPaymentRequestBody request = OrderCreationPaymentRequestBody.builder()
                    .orderType("P").creditCard(false).build();
            TmbOneServiceResponse<OrderCreationPaymentResponse> actual =
                    orderCreationService.makeTransaction(correlationId, crmId, ipAddress, request);

        } catch (TMBCommonException ex) {
            assertEquals(ProductsExpServiceConstant.DATA_NOT_FOUND_CODE, ex.getErrorCode());
            assertEquals(ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE, ex.getErrorMessage());
            verify(enterPinIsCorrectActivityLogService, times(1)).save(anyString(), anyString(), anyString(), any(), any());
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

    private FeignException mockFeignExceptionDataNotFound(String errorCode, String errorMessage) {
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
        FeignException.NotFound e = new FeignException.NotFound("", request, errorBody.getBytes(StandardCharsets.UTF_8));
        return e;
    }
}
