package com.tmb.oneapp.productsexpservice.controller.productexperience.fund;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.tradeoccupation.request.TradeOccupationRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.tradeoccupation.response.TradeOccupationResponse;
import com.tmb.oneapp.productsexpservice.service.productexperience.fund.BuyFirstTradeService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BuyFirstTradeControllerTest {

    @Mock
    public BuyFirstTradeService buyFirstTradeService;

    @InjectMocks
    public BuyFirstTradeController buyFirstTradeController;

    private final String crmId = "001100000000000000000012035644";

    private final String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";

    @Test
    public void should_return_success_status_when_call_get_trade_occupation_inquiry_given_correlation_id_and_crmid_first_trade_occupation_request() throws TMBCommonException {

        // given
        TmbOneServiceResponse<TradeOccupationResponse> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        tmbOneServiceResponse.setStatus(TmbStatusUtil.successStatus());
        when(buyFirstTradeService.tradeOuccupationInquiry(any(), any(), any())).thenReturn(tmbOneServiceResponse);
        // when
        ResponseEntity<TmbOneServiceResponse<TradeOccupationResponse>> actual =
                buyFirstTradeController.tradeOccupationInquiry(correlationId, crmId, TradeOccupationRequest.builder().build());

        // then
        assertEquals(HttpStatus.OK,actual.getStatusCode());
        assertEquals(ProductsExpServiceConstant.SUCCESS_CODE,actual.getBody().getStatus().getCode());
    }

    @Test
    public void should_return_not_found_status_when_call_get_trade_occupation_inquiry_given_correlation_id_and_crmid_first_trade_occupation_request() throws TMBCommonException {

        // given
        TmbOneServiceResponse<TradeOccupationResponse> tmbOneServiceResponse = new TmbOneServiceResponse<>();
        tmbOneServiceResponse.setStatus(null);
        tmbOneServiceResponse.setData(null);
        when(buyFirstTradeService.tradeOuccupationInquiry(any(), any(), any())).thenReturn(tmbOneServiceResponse);

        // when
        ResponseEntity<TmbOneServiceResponse<TradeOccupationResponse>> actual =
                buyFirstTradeController.tradeOccupationInquiry(correlationId, crmId, TradeOccupationRequest.builder().build());

        // then
        assertEquals(HttpStatus.NOT_FOUND,actual.getStatusCode());
        assertEquals(ProductsExpServiceConstant.DATA_NOT_FOUND_CODE,actual.getBody().getStatus().getCode());
    }

}
