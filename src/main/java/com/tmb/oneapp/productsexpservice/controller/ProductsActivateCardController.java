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
import com.tmb.oneapp.productsexpservice.service.NotificationService;
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
@Api("Credit Card Verification Service")
public class ProductsActivateCardController {
    private static final TMBLogger<ProductsActivateCardController> logger = new TMBLogger<>(
            ProductsActivateCardController.class);

    private final CreditCardClient creditCardClient;
    private final NotificationService notificationService;

    @Autowired
    public ProductsActivateCardController(CreditCardClient creditCardClient, NotificationService notificationService) {
        this.creditCardClient = creditCardClient;
        this.notificationService = notificationService;
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
        TmbOneServiceResponse<ActivateCardResponse> oneServiceResponse = new TmbOneServiceResponse<>();

        try {
            String accountId = headers.get(ProductsExpServiceConstant.ACCOUNT_ID);
            String correlationId = headers.get(ProductsExpServiceConstant.X_CORRELATION_ID);
            String crmId = headers.get(ProductsExpServiceConstant.X_CRMID);
            if (!Strings.isNullOrEmpty(accountId)) {
                ResponseEntity<ActivateCardResponse> activateCardResponse = creditCardClient.activateCard(headers);
                int statusCodeValue = activateCardResponse.getStatusCodeValue();
                HttpStatus statusCode = activateCardResponse.getStatusCode();
                if (activateCardResponse.getBody() != null && statusCodeValue == 200 && statusCode == HttpStatus.OK) {

                    oneServiceResponse
                            .setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                                    ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
                    oneServiceResponse.setData(response);
                    notificationService.sendCardActiveEmail(correlationId, accountId, crmId);
                    return ResponseEntity.ok().headers(responseHeaders).body(oneServiceResponse);
                } else {
                    oneServiceResponse.setStatus(new TmbStatus(ResponseCode.DATA_NOT_FOUND_ERROR.getCode(),
                            ResponseCode.DATA_NOT_FOUND_ERROR.getMessage(), ResponseCode.DATA_NOT_FOUND_ERROR.getService(),
                            ResponseCode.DATA_NOT_FOUND_ERROR.getDesc()));
                    return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);

                }
            } else {
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
}
