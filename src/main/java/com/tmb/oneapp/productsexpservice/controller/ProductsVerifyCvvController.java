package com.tmb.oneapp.productsexpservice.controller;

import com.google.common.base.Strings;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.ActivateCardResponse;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.VerifyCvvResponse;
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

import java.util.Map;

@RestController
@Api("Credit Card Verification Service")
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
            String accountId = headers.get(ProductsExpServiceConstant.ACCOUNT_ID);

            String cvv = headers.get(ProductsExpServiceConstant.CVV);
            String cardExpiry = headers.get(ProductsExpServiceConstant.CARD_EXPIRY);

            if (!Strings.isNullOrEmpty(accountId) && !Strings.isNullOrEmpty(cvv)
                    && !Strings.isNullOrEmpty(cardExpiry)) {
            ResponseEntity<VerifyCvvResponse> verifyCvvResponse = creditCardClient.verifyCvv(headers);
            int statusCodeValue = verifyCvvResponse.getStatusCodeValue();
            HttpStatus statusCode = verifyCvvResponse.getStatusCode();
            if (statusCodeValue == 200 && statusCode == HttpStatus.OK && verifyCvvResponse.getBody() != null) {
                    if(verifyCvvResponse.getBody().getStatus().getStatusCode()!=null) {
                        oneServiceResponse
                                .setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                                        ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
                        oneServiceResponse.setData(response);
                        return ResponseEntity.ok().headers(responseHeaders).body(oneServiceResponse);
                    }else {
                          response.getAccountId();
                       /* List<ErrorStatus> list;
                        String desc = list.get(0).getDescription();
                        String errorCode = list.get(0).getErrorCode();



                        StatusCode statusCode = new StatusCode(response.getStatus().getStatusCode(), list);
                        statusCode.setStatusCode(response.getStatus().getStatusCode());*/
                    //    statusCode.setErrorStatus(list);
                        oneServiceResponse.setData(response);
                        oneServiceResponse.setStatus(
                                new TmbStatus(ResponseCode.GENERAL_ERROR.getCode(), ResponseCode.GENERAL_ERROR.getMessage(),
                                        ResponseCode.GENERAL_ERROR.getService(), ResponseCode.GENERAL_ERROR.getDesc()));
                        return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
                    }
            } 	oneServiceResponse.setStatus(new TmbStatus(ResponseCode.DATA_NOT_FOUND_ERROR.getCode(),
                        ResponseCode.DATA_NOT_FOUND_ERROR.getMessage(),
                        ResponseCode.DATA_NOT_FOUND_ERROR.getService(),
                        ResponseCode.DATA_NOT_FOUND_ERROR.getDesc()));
                return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
            }
         else {
                 logger.info("VerifyCvvController data not found");
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