package com.tmb.oneapp.productsexpservice.controller;

import com.google.common.base.Strings;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
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

    /**
     * @param creditCardClient
     */
    @Autowired
    public ProductsVerifyCvvController(CreditCardClient creditCardClient) {
        this.creditCardClient = creditCardClient;
    }

    /**
     * @param headers
     * @return
     */
    @LogAround
    @ApiOperation(value = "Activate card Api")
    @ApiImplicitParams({
            @ApiImplicitParam(name = ProductsExpServiceConstant.X_CORRELATION_ID, value = "Correlation Id", required = true, dataType = "string", paramType = "header", example = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da"),
            @ApiImplicitParam(name = "account-id", value = "Account Id", required = true, dataType = "string", paramType = "header", example = "0000000050078360141010286")})

    @PostMapping(value = "/credit-card/activateCreditCard/verifyCvv")
    public ResponseEntity<TmbOneServiceResponse<VerifyCvvResponse>> verifyCvv(
            @RequestHeader Map<String, String> headers) {
        logger.info("Get Verify Cvv Details for Corresponding Headers: {}", headers);
        VerifyCvvResponse response = new VerifyCvvResponse();
        TmbOneServiceResponse<VerifyCvvResponse> oneServiceResponse = new TmbOneServiceResponse<>();
        HttpHeaders responseHeaders = new HttpHeaders();
        try {
            String accountId = headers.get(ProductsExpServiceConstant.ACCOUNT_ID);

            String cvv = headers.get(ProductsExpServiceConstant.CVV);
            String cardExpiry = headers.get(ProductsExpServiceConstant.CARD_EXPIRY);

            if (!Strings.isNullOrEmpty(accountId) && !Strings.isNullOrEmpty(cvv)
                    && !Strings.isNullOrEmpty(cardExpiry)) {
                ResponseEntity<TmbOneServiceResponse<VerifyCvvResponse>> verifyCvvResponse = creditCardClient.verifyCvv(headers);
                int statusCodeValue = verifyCvvResponse.getStatusCodeValue();
                HttpStatus statusCode = verifyCvvResponse.getStatusCode();
                if (statusCodeValue == 200 && statusCode == HttpStatus.OK) {


                    return getTmbOneServiceResponseResponseEntity(response, oneServiceResponse, responseHeaders, verifyCvvResponse);
                }
                oneServiceResponse.setStatus(new TmbStatus(ResponseCode.DATA_NOT_FOUND_ERROR.getCode(),
                        ResponseCode.DATA_NOT_FOUND_ERROR.getMessage(),
                        ResponseCode.DATA_NOT_FOUND_ERROR.getService(),
                        ResponseCode.DATA_NOT_FOUND_ERROR.getDesc()));
                return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
            } else {
                logger.info("VerifyCvvController data not found");
                oneServiceResponse.setStatus(new TmbStatus(ResponseCode.DATA_NOT_FOUND_ERROR.getCode(),
                        ResponseCode.DATA_NOT_FOUND_ERROR.getMessage(), ResponseCode.DATA_NOT_FOUND_ERROR.getService(),
                        ResponseCode.DATA_NOT_FOUND_ERROR.getDesc()));
                return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
            }
        } catch (Exception e) {
            logger.error("Error while getCreditCardDetails: {}", e);
            oneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService()));
            return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
        }
    }

    /**
     * @param response
     * @param oneServiceResponse
     * @param responseHeaders
     * @param verifyCvvResponse
     * @return
     */
    ResponseEntity<TmbOneServiceResponse<VerifyCvvResponse>> getTmbOneServiceResponseResponseEntity(VerifyCvvResponse response, TmbOneServiceResponse<VerifyCvvResponse> oneServiceResponse, HttpHeaders responseHeaders, ResponseEntity<TmbOneServiceResponse<VerifyCvvResponse>> verifyCvvResponse) {
        String code = verifyCvvResponse.getBody().getStatus().getCode();
        String message = verifyCvvResponse.getBody().getStatus().getMessage();
        String service = verifyCvvResponse.getBody().getStatus().getService();
        VerifyCvvResponse data = verifyCvvResponse.getBody().getData();
        if (data.getStatus().getStatusCode() == 0) {
            oneServiceResponse
                    .setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                            ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
            oneServiceResponse.setData(response);
            return ResponseEntity.ok().headers(responseHeaders).body(oneServiceResponse);
        } else {

            return failureResponse(response, oneServiceResponse, responseHeaders, code, message, service);
        }
    }

    ResponseEntity<TmbOneServiceResponse<VerifyCvvResponse>> failureResponse(VerifyCvvResponse response, TmbOneServiceResponse<VerifyCvvResponse> oneServiceResponse, HttpHeaders responseHeaders, String code, String message, String service) {
        failedResponse(response, oneServiceResponse, code, message, service);
        return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
    }

    /**
     * @param response
     * @param oneServiceResponse
     * @param code
     * @param message
     * @param service
     */
    public void failedResponse(VerifyCvvResponse response, TmbOneServiceResponse<VerifyCvvResponse> oneServiceResponse, String code, String message, String service) {
        oneServiceResponse.setStatus(
                new TmbStatus(code, message,
                        service, ResponseCode.FAILED.getDesc()));
        oneServiceResponse.setData(response);
    }
}