package com.tmb.oneapp.productsexpservice.controller.productexperience.alternative;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.LogAround;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.buyfirstrade.request.AlternativeBuyFirstTTradeRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.tradeoccupation.response.TradeOccupationResponse;
import com.tmb.oneapp.productsexpservice.service.productexperience.alternative.BuyFirstTradeAlternativeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.tmb.oneapp.productsexpservice.util.TmbStatusUtil.notFoundStatus;

@Api(tags = "for buy fund validation")
@RequestMapping("/funds")
@RestController
public class BuyFirstTradeValidationController {

    private final BuyFirstTradeAlternativeService buyFirstTradeAlternativeService;

    @Autowired
    public BuyFirstTradeValidationController(BuyFirstTradeAlternativeService buyFirstTradeAlternativeService) {
        this.buyFirstTradeAlternativeService = buyFirstTradeAlternativeService;
    }

    /**
     * Description:- method for handle alternative buy first trade
     *
     * @param correlationId            the correlation id
     * @param crmId                    the crm id
     * @param alternativeBuyFirstTTradeRequest the alternativeBuyFirstTTradeRequest request body
     * @return return valid status code
     */
    @ApiOperation(value = "Validation alternative buy first trade case")
    @LogAround
    @PostMapping(value = "/alternative/buyFirstTrade")
    public ResponseEntity<TmbOneServiceResponse<TradeOccupationResponse>> validationBuyFirstTrade(
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId,
            @Valid @RequestBody AlternativeBuyFirstTTradeRequest alternativeBuyFirstTTradeRequest) throws TMBCommonException {

        TmbOneServiceResponse<TradeOccupationResponse> oneServiceResponse = buyFirstTradeAlternativeService
                .validationBuyFirstTrade(correlationId,crmId, alternativeBuyFirstTTradeRequest);
        if (!StringUtils.isEmpty(oneServiceResponse.getStatus())) {
            if(ProductsExpServiceConstant.SUCCESS_CODE.equals(oneServiceResponse.getStatus().getCode())){
                return ResponseEntity.ok(oneServiceResponse);
            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(oneServiceResponse);
            }
        } else {
            oneServiceResponse.setStatus(notFoundStatus());
            return new ResponseEntity(oneServiceResponse, HttpStatus.NOT_FOUND);
        }

    }

}
