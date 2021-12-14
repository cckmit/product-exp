package com.tmb.oneapp.productsexpservice.controller.productexperience.fund;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.LogAround;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.tradeoccupation.request.TradeOccupationRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.tradeoccupation.response.TradeOccupationResponse;
import com.tmb.oneapp.productsexpservice.service.productexperience.fund.BuyFirstTradeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.tmb.oneapp.productsexpservice.util.TmbStatusUtil.notFoundStatus;

@Api(tags = "for inquiry first trade and occupation require flag")
@RequestMapping("/funds")
@RestController
public class BuyFirstTradeController {

    private final BuyFirstTradeService buyFirstTradeService;

    @Autowired
    public BuyFirstTradeController(BuyFirstTradeService buyFirstTradeService) {
        this.buyFirstTradeService = buyFirstTradeService;
    }

    /**
     * Description:- Method for handle alternative dca
     *
     * @param correlationId the correlation id
     * @param crmId         the crm id
     * @return return valid status code
     */
    @ApiOperation(value = "Validation alternative case")
    @LogAround
    @PostMapping(value = "/tradeOccupationInquiry")
    public ResponseEntity<TmbOneServiceResponse<TradeOccupationResponse>> tradeOccupationInquiry(
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId,
            @Valid @RequestBody TradeOccupationRequest tradeOccupationRequest) throws TMBCommonException {

        TmbOneServiceResponse<TradeOccupationResponse> oneServiceResponse =
                buyFirstTradeService.tradeOuccupationInquiry(correlationId, crmId, tradeOccupationRequest);
        if (!StringUtils.isEmpty(oneServiceResponse.getStatus())) {
            return ResponseEntity.ok(oneServiceResponse);
        } else {
            oneServiceResponse.setStatus(notFoundStatus());
            return new ResponseEntity(oneServiceResponse, HttpStatus.NOT_FOUND);
        }
    }
}
