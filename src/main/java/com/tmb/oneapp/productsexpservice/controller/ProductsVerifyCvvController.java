package com.tmb.oneapp.productsexpservice.controller;

import java.util.Map;

import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.ActivateCardResponse;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.VerifyCvvResponse;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@Api("Verify cvv endpoint for Activate card Api")
public class ProductsVerifyCvvController {
    private static final TMBLogger<ProductsVerifyCvvController> logger = new TMBLogger<>(ProductsVerifyCvvController.class);

   CreditCardClient creditCardClient;


    @Autowired
    public ProductsVerifyCvvController(CreditCardClient creditCardClient) {
       this.creditCardClient=creditCardClient;
    }
    @LogAround
    @ApiOperation(value = "Activate card Api")
    @ApiImplicitParams({
            @ApiImplicitParam(name = ProductsExpServiceConstant.X_CORRELATION_ID, value = "Correlation Id", required = true, dataType = "string", paramType = "header", example = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da"),
            @ApiImplicitParam(name = "account-id", value = "Account Id", required = true, dataType = "string", paramType = "header", example = "0000000050078360141010286")})

    @PostMapping(value = "/credit-card/activateCreditCard/verifyCvv")
    public ResponseEntity<TmbOneServiceResponse<ActivateCardResponse>> verifyCvv(
            @RequestHeader Map<String, String> headers)
             {
        logger.info("Get Verify Cvv Details for Corresponding Headers: {}", headers);
        ActivateCardResponse response = new ActivateCardResponse();
        TmbOneServiceResponse<ActivateCardResponse> oneServiceResponse = new TmbOneServiceResponse<>();
        HttpHeaders responseHeaders = new HttpHeaders();
        try {
            ResponseEntity<VerifyCvvResponse> verifyCvvResponse = creditCardClient.verifyCvv(headers);
            int statusCodeValue = verifyCvvResponse.getStatusCodeValue();
            HttpStatus statusCode = verifyCvvResponse.getStatusCode();
            if (statusCodeValue == 200 && statusCode == HttpStatus.OK && verifyCvvResponse.getBody() != null) {

                oneServiceResponse
                        .setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                                ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
                oneServiceResponse.setData(response);
                return ResponseEntity.ok().headers(responseHeaders).body(oneServiceResponse);
            } else {
                oneServiceResponse.setStatus(new TmbStatus(ResponseCode.DATA_NOT_FOUND_ERROR.getCode(),
                        ResponseCode.DATA_NOT_FOUND_ERROR.getMessage(), ResponseCode.DATA_NOT_FOUND_ERROR.getService(),
                        ResponseCode.DATA_NOT_FOUND_ERROR.getDesc()));
                return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
            }
        }
        catch (Exception e) {
            logger.error("Error while getCreditCardDetails: {}", e);
            oneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService()));
            return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
        }
    }
}