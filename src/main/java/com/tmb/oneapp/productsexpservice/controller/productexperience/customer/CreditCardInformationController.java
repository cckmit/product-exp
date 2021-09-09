package com.tmb.oneapp.productsexpservice.controller.productexperience.customer;

import com.tmb.common.logger.LogAround;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.customer.creditcard.CreditCardInformationRequestBody;
import com.tmb.oneapp.productsexpservice.model.customer.creditcard.response.CreditCardInformationResponse;
import com.tmb.oneapp.productsexpservice.service.productexperience.customer.CreditCardInformationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.tmb.oneapp.productsexpservice.util.TmbStatusUtil.notFoundStatus;

@Api(tags = "Get credit card list information for customer")
@RequestMapping("/customer")
@RestController
public class CreditCardInformationController {

    private final CreditCardInformationService creditcardInformationService;

    @Autowired
    public CreditCardInformationController(CreditCardInformationService creditcardInformationService) {
        this.creditcardInformationService = creditcardInformationService;
    }

    /**
     * Description:- method get dca list
     *
     * @param correlationId                    the correlation id
     * @param crmId the crm id request
     * @return return dca list
     */
    @ApiOperation(value = "Get credit card list information for customer")
    @LogAround
    @PostMapping(value = "/accnt/creditcard")
    public ResponseEntity<TmbOneServiceResponse<CreditCardInformationResponse>> getCreditCardInformation(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_CORRELATION_ID_DESC, defaultValue = ProductsExpServiceConstant.X_COR_ID_DEFAULT, required = true)
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId) {
        TmbOneServiceResponse<CreditCardInformationResponse> oneServiceResponse = creditcardInformationService.getCreditCardInformation(correlationId, crmId);
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