package com.tmb.oneapp.productsexpservice.service.productexperience.fund;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.activitylog.transaction.service.EnterPinIsCorrectActivityLogService;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CacheServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.CardInfo;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.CreditCardDetail;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.FetchCardResponse;
import com.tmb.oneapp.productsexpservice.model.common.findbyfundhouse.FundHouseBankData;
import com.tmb.oneapp.productsexpservice.model.common.findbyfundhouse.FundHouseResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request.Account;
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
import static org.mockito.Mockito.*;

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

    @Test
    void buy_flow_saving_account_should_return_success_when_call_make_transaction_with_correlationId_and_crm_id_and_ordercreation_request_body() throws TMBCommonException {

        // given
        TmbOneServiceResponse<String> response = new TmbOneServiceResponse<>();
        response.setStatus(TmbStatusUtil.successStatus());
        response.setData("true");
        when(cacheServiceClient.getCacheByKey(any(),any())).thenReturn(ResponseEntity.ok(response));

        TmbOneServiceResponse<String> pinVerifyResponse = new TmbOneServiceResponse<>();
        pinVerifyResponse.setData("not true");
        when(cacheServiceClient.getCacheByKey(any(),any())).thenReturn(ResponseEntity.ok(pinVerifyResponse));

        TmbOneServiceResponse<FundHouseResponse> tmbFundHouseResponse = new TmbOneServiceResponse<>();
        FundHouseResponse fundHouseResponse = new FundHouseResponse();
        fundHouseResponse.setData(FundHouseBankData.builder()
                .toAccountNo("a").accountType("884").financialId("441").ltfMerchantId("ltf").rmfMerchantId("rmf")
                .build());
        tmbFundHouseResponse.setData(fundHouseResponse);
        when(commonServiceClient.fetchBankInfoByFundHouse(any(),any())).thenReturn(tmbFundHouseResponse);

        TmbOneServiceResponse<OrderCreationPaymentResponse> orderCreationResponse = new TmbOneServiceResponse<>();
        orderCreationResponse.setStatus(TmbStatusUtil.successStatus());
        orderCreationResponse.setData(OrderCreationPaymentResponse.builder().build());
        when(investmentRequestClient.createOrderPayment(any(),any())).thenReturn(ResponseEntity.ok(orderCreationResponse));

        TmbOneServiceResponse<String> saveOrderResponse = new TmbOneServiceResponse<>();
        saveOrderResponse.setStatus(TmbStatusUtil.successStatus());
        when(investmentRequestClient.saveOrderPayment(any(),any())).thenReturn(ResponseEntity.ok(saveOrderResponse));

        TmbOneServiceResponse<String> processFirstTradeResponse = new TmbOneServiceResponse<>();
        processFirstTradeResponse.setStatus(TmbStatusUtil.successStatus());
        when(investmentRequestClient.processFirstTrade(any(),any())).thenReturn(ResponseEntity.ok(processFirstTradeResponse));

        // when
        OrderCreationPaymentRequestBody request = OrderCreationPaymentRequestBody.builder()
                .orderType("P").creditCard(false).build();
        TmbOneServiceResponse<OrderCreationPaymentResponse> actual =
                orderCreationService.makeTransaction(correlationId,crmId, request);

        // then
        assertEquals(ProductsExpServiceConstant.SUCCESS_CODE,actual.getStatus().getCode());
        verify(cacheServiceClient,times(1)).putCacheByKey(any(),any());
        // after payment
        verify(investmentRequestClient,times(1)).saveOrderPayment(any(),any());
        verify(investmentRequestClient,times(1)).processFirstTrade(any(),any());
        verify(enterPinIsCorrectActivityLogService,times(1)).save(any(),any(),any(),any(),any(),any());

    }

    @Test
    void buy_flow_creditcard_should_return_success_when_call_make_transaction_with_correlationId_and_crm_id_and_ordercreation_request_body() throws TMBCommonException {

        // given
        TmbOneServiceResponse<String> response = new TmbOneServiceResponse<>();
        response.setStatus(TmbStatusUtil.successStatus());
        response.setData("true");
        when(cacheServiceClient.getCacheByKey(any(),any())).thenReturn(ResponseEntity.ok(response));

        TmbOneServiceResponse<String> pinVerifyResponse = new TmbOneServiceResponse<>();
        pinVerifyResponse.setData("not true");
        when(cacheServiceClient.getCacheByKey(any(),any())).thenReturn(ResponseEntity.ok(pinVerifyResponse));

        TmbOneServiceResponse<FundHouseResponse> tmbFundHouseResponse = new TmbOneServiceResponse<>();
        FundHouseResponse fundHouseResponse = new FundHouseResponse();
        fundHouseResponse.setData(FundHouseBankData.builder()
                .toAccountNo("a").accountType("884").financialId("441").ltfMerchantId("ltf").rmfMerchantId("rmf")
                .build());
        tmbFundHouseResponse.setData(fundHouseResponse);
        when(commonServiceClient.fetchBankInfoByFundHouse(any(),any())).thenReturn(tmbFundHouseResponse);


        CreditCardDetail creditCardDetail = new CreditCardDetail();
        CardInfo cardInfo = new CardInfo();

        creditCardDetail.setCardInfo(cardInfo);
        FetchCardResponse fetchCardResponse = new FetchCardResponse();
        fetchCardResponse.setCreditCard(creditCardDetail);
        when(creditCardClient.getCreditCardDetails(any(),any())).thenReturn(ResponseEntity.ok(fetchCardResponse));

        TmbOneServiceResponse<OrderCreationPaymentResponse> orderCreationResponse = new TmbOneServiceResponse<>();
        orderCreationResponse.setStatus(TmbStatusUtil.successStatus());
        orderCreationResponse.setData(OrderCreationPaymentResponse.builder().build());
        when(investmentRequestClient.createOrderPayment(any(),any())).thenReturn(ResponseEntity.ok(orderCreationResponse));

        TmbOneServiceResponse<String> saveOrderResponse = new TmbOneServiceResponse<>();
        saveOrderResponse.setStatus(TmbStatusUtil.successStatus());
        when(investmentRequestClient.saveOrderPayment(any(),any())).thenReturn(ResponseEntity.ok(saveOrderResponse));

        TmbOneServiceResponse<String> processFirstTradeResponse = new TmbOneServiceResponse<>();
        processFirstTradeResponse.setStatus(TmbStatusUtil.successStatus());
        when(investmentRequestClient.processFirstTrade(any(),any())).thenReturn(ResponseEntity.ok(processFirstTradeResponse));

        // when
        OrderCreationPaymentRequestBody request = OrderCreationPaymentRequestBody.builder()
                .orderType("P")
                .creditCard(true)
                .fromAccount(Account.builder().accountId("accid").build())
                .build();
        TmbOneServiceResponse<OrderCreationPaymentResponse> actual =
                orderCreationService.makeTransaction(correlationId,crmId, request);

        // then
        assertEquals(ProductsExpServiceConstant.SUCCESS_CODE,actual.getStatus().getCode());
        verify(cacheServiceClient,times(1)).putCacheByKey(any(),any());
        verify(creditCardClient,times(1)).getCreditCardDetails(any(),any());
        // after payment
        verify(investmentRequestClient,times(1)).saveOrderPayment(any(),any());
        verify(investmentRequestClient,times(1)).processFirstTrade(any(),any());
        verify(enterPinIsCorrectActivityLogService,times(1)).save(any(),any(),any(),any(),any(),any());

    }

    @Test
    void sell_or_switch_flow_creditcard_should_return_success_when_call_make_transaction_with_correlationId_and_crm_id_and_ordercreation_request_body() throws TMBCommonException {

        // given
        TmbOneServiceResponse<String> response = new TmbOneServiceResponse<>();
        response.setStatus(TmbStatusUtil.successStatus());
        response.setData("true");
        when(cacheServiceClient.getCacheByKey(any(),any())).thenReturn(ResponseEntity.ok(response));

        TmbOneServiceResponse<String> pinVerifyResponse = new TmbOneServiceResponse<>();
        pinVerifyResponse.setData("not true");
        when(cacheServiceClient.getCacheByKey(any(),any())).thenReturn(ResponseEntity.ok(pinVerifyResponse));

        TmbOneServiceResponse<OrderCreationPaymentResponse> orderCreationResponse = new TmbOneServiceResponse<>();
        orderCreationResponse.setStatus(TmbStatusUtil.successStatus());
        orderCreationResponse.setData(OrderCreationPaymentResponse.builder().build());
        when(investmentRequestClient.createOrderPayment(any(),any())).thenReturn(ResponseEntity.ok(orderCreationResponse));

        TmbOneServiceResponse<String> saveOrderResponse = new TmbOneServiceResponse<>();
        saveOrderResponse.setStatus(TmbStatusUtil.successStatus());
        when(investmentRequestClient.saveOrderPayment(any(),any())).thenReturn(ResponseEntity.ok(saveOrderResponse));

        TmbOneServiceResponse<String> processFirstTradeResponse = new TmbOneServiceResponse<>();
        processFirstTradeResponse.setStatus(TmbStatusUtil.successStatus());
        when(investmentRequestClient.processFirstTrade(any(),any())).thenReturn(ResponseEntity.ok(processFirstTradeResponse));

        // when
        OrderCreationPaymentRequestBody request = OrderCreationPaymentRequestBody.builder()
                .orderType("S")
                .creditCard(false)
                .fromAccount(Account.builder().accountId("accid").build())
                .build();
        TmbOneServiceResponse<OrderCreationPaymentResponse> actual =
                orderCreationService.makeTransaction(correlationId,crmId, request);

        // then
        assertEquals(ProductsExpServiceConstant.SUCCESS_CODE,actual.getStatus().getCode());
        verify(cacheServiceClient,times(1)).putCacheByKey(any(),any());
        // after payment
        verify(investmentRequestClient,times(1)).saveOrderPayment(any(),any());
        verify(investmentRequestClient,times(1)).processFirstTrade(any(),any());
        verify(enterPinIsCorrectActivityLogService,times(1)).save(any(),any(),any(),any(),any(),any());

    }

}
