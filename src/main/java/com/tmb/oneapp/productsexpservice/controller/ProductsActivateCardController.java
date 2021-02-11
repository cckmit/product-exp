package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.ActivateCardResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@Api("For activate card Api")
public class ProductsActivateCardController {
    private static final TMBLogger<ProductsActivateCardController> logger = new TMBLogger<>(
            ProductsActivateCardController.class);

    private CreditCardClient creditCardClient;

    @Autowired
    public ProductsActivateCardController(CreditCardClient creditCardClient) {
        this.creditCardClient = creditCardClient;
    }

    @LogAround
    @ApiOperation(value = "Activate card Api")
    @ApiImplicitParams({
            @ApiImplicitParam(name = ProductsExpServiceConstant.X_CORRELATION_ID, value = "Correlation Id", required = true, dataType = "string", paramType = "header", example = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da"),
            @ApiImplicitParam(name = "account-id", value = "Account Id", required = true, dataType = "string", paramType = "header", example = "0000000050078360141010286")})

    @PostMapping(value = "/credit-card/activate-card")
    public ResponseEntity<TmbOneServiceResponse<ActivateCardResponse>> activateCard(
            @RequestHeader Map<String, String> headers) {
        logger.info("Get Activate Card Details for Corresponding Headers: {}", headers);
        ActivateCardResponse response = new ActivateCardResponse();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
        TmbOneServiceResponse<ActivateCardResponse> oneServiceRes1 = new TmbOneServiceResponse<>();

        try {
            ResponseEntity<ActivateCardResponse> activateCardResponse = creditCardClient.activateCard(headers);
            int statusCodeValue = activateCardResponse.getStatusCodeValue();
            HttpStatus statusCode = activateCardResponse.getStatusCode();
            if (activateCardResponse.getBody() != null && statusCodeValue == 200 && statusCode == HttpStatus.OK) {

                oneServiceRes1
                        .setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                                ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
                oneServiceRes1.setData(response);
                return ResponseEntity.ok().headers(responseHeaders).body(oneServiceRes1);
            } else {
                oneServiceRes1.setStatus(new TmbStatus(ResponseCode.DATA_NOT_FOUND_ERROR.getCode(),
                        ResponseCode.DATA_NOT_FOUND_ERROR.getMessage(), ResponseCode.DATA_NOT_FOUND_ERROR.getService(),
                        ResponseCode.DATA_NOT_FOUND_ERROR.getDesc()));
                return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceRes1);
            }
        } catch (Exception e) {
            logger.error("Error while getCreditCardDetails: {}", e);
            oneServiceRes1.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService()));
            return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceRes1);
        }

    }

}
