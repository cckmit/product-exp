package com.tmb.oneapp.productsexpservice.controller.productexperience.alternative;

import com.tmb.common.logger.LogAround;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.service.productexperience.alternative.SellAlternativeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.tmb.oneapp.productsexpservice.util.TmbStatusUtil.notFoundStatus;

@Api(tags = "for sell fund validation")
@RequestMapping("/funds")
@RestController
public class SellValidationController {

    private final SellAlternativeService sellAlternativeService;

    @Autowired
    public SellValidationController(SellAlternativeService sellAlternativeService) {
        this.sellAlternativeService = sellAlternativeService;
    }

    /**
     * Description:- method for handle alternative sell
     *
     * @param correlationId            the correlation id
     * @param crmId                    the crm id
     * @return return valid status code
     */
    @ApiOperation(value = "Validation alternative case, then return fund sheet")
    @LogAround
    @PostMapping(value = "/alternative/sell")
    public ResponseEntity<TmbOneServiceResponse<String>> validationBuy(
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId) {

        TmbOneServiceResponse<String> oneServiceResponse = sellAlternativeService.validationSell(correlationId,crmId);
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
