package com.tmb.oneapp.productsexpservice.controller;

import com.google.common.base.Strings;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.request.buildstatement.CardStatement;
import com.tmb.oneapp.productsexpservice.model.request.buildstatement.GetBilledStatementQuery;
import com.tmb.oneapp.productsexpservice.model.response.buildstatement.BilledStatementResponse;
import io.swagger.annotations.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "Controller for billed statement")
public class BilledStatementWithPeriodController {
    private final CreditCardClient creditCardClient;
    private static final TMBLogger<BilledStatementWithPeriodController> logger = new TMBLogger<>(
            BilledStatementWithPeriodController.class);

    public BilledStatementWithPeriodController(CreditCardClient creditCardClient) {
        this.creditCardClient = creditCardClient;
    }

    /**
     * @param correlationId
     * @param requestBody
     * @return
     */
    @LogAround
    @ApiOperation(value = "get billed statement with period")
    @PostMapping(value = "/creditcard-billed-statement-period")
    @ApiImplicitParams({
            @ApiImplicitParam(name = ProductsExpServiceConstant.X_CORRELATION_ID, value = "Correlation Id", required = true, dataType = "string", paramType = "header", example = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da")
    })
    public ResponseEntity<TmbOneServiceResponse<BilledStatementResponse>> getBilledStatementWithPeriod(
            @ApiParam(value = "Correlation ID", defaultValue = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da", required = true) @RequestHeader("X-Correlation-ID") String correlationId,
            @RequestBody GetBilledStatementQuery requestBody) {
        logger.info("Get billed statement for correlation id: {}", correlationId);
        TmbOneServiceResponse<BilledStatementResponse> response = new TmbOneServiceResponse<>();

        HttpHeaders responseHeaders = new HttpHeaders();
        logger.info("Get creditcard-billed-statement for period for correlation-ID: {}", correlationId);

        try {

            if (requestBody != null && !Strings.isNullOrEmpty(requestBody.getAccountId()) && !Strings.isNullOrEmpty(correlationId)) {
                ResponseEntity<BilledStatementResponse> billedStatementRes = creditCardClient.getBilledStatementWithPeriod(correlationId, requestBody.getAccountId(), requestBody);

                if (billedStatementRes != null && billedStatementRes.getStatusCode() == HttpStatus.OK
                        && billedStatementRes.getBody().getStatus().getStatusCode() == ProductsExpServiceConstant.ZERO) {

                    return handlingResponseData(billedStatementRes, response,
                            responseHeaders);

                } else {
                    return this.handlingFailedResponse(response, responseHeaders);
                }

            } else {
                this.handlingFailedResponse(response, responseHeaders);
            }

        } catch (Exception exception) {
            generalError(response, exception);

            return ResponseEntity.badRequest().headers(responseHeaders).body(response);
        }
        return ResponseEntity.badRequest().headers(responseHeaders).body(response);
    }

    /**
     * @param response
     * @param exception
     */
    void generalError(TmbOneServiceResponse<BilledStatementResponse> response, Exception exception) {
        logger.error("Unable to fetch billed statement for this account ID: {}", exception);
        response.setStatus(new TmbStatus(ResponseCode.GENERAL_ERROR.getCode(),
                ResponseCode.GENERAL_ERROR.getMessage(), ResponseCode.GENERAL_ERROR.getService()));
    }

    /**
     * @param oneServiceResponse
     * @param responseHeaders
     * @return
     */
    public ResponseEntity<TmbOneServiceResponse<BilledStatementResponse>> handlingFailedResponse(
            TmbOneServiceResponse<BilledStatementResponse> oneServiceResponse, HttpHeaders responseHeaders) {
        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                ResponseCode.FAILED.getService()));
        return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
    }

    /**
     * @param oneServiceResponse
     * @param responseHeaders
     * @return
     */
    public ResponseEntity<TmbOneServiceResponse<BilledStatementResponse>> handlingResponseData(
            ResponseEntity<BilledStatementResponse> billedStatementRes,
            TmbOneServiceResponse<BilledStatementResponse> oneServiceResponse,
            HttpHeaders responseHeaders) {

        BilledStatementResponse billedStatementResponse = billedStatementRes.getBody();

        String moreRecords = billedStatementResponse.getCardStatement() != null ? billedStatementResponse.getMoreRecords()
                : ProductsExpServiceConstant.EMPTY;
        String searchKeys = billedStatementResponse.getCardStatement() != null
                ? billedStatementResponse.getSearchKeys()
                : ProductsExpServiceConstant.EMPTY;
        Integer totalRecords = billedStatementResponse.getCardStatement() != null ? billedStatementResponse.getTotalRecords()
                : ProductsExpServiceConstant.ZERO;
        Integer maxRecords = billedStatementResponse.getCardStatement() != null ? billedStatementResponse.getMaxRecords()
                : ProductsExpServiceConstant.ZERO;
        CardStatement cardStatement = billedStatementResponse.getCardStatement();
        billedStatementResponse.setCardStatement(cardStatement);
        billedStatementResponse.setMaxRecords(maxRecords);
        billedStatementResponse.setMoreRecords(moreRecords);
        billedStatementResponse.setSearchKeys(searchKeys);
        billedStatementResponse.setTotalRecords(totalRecords);
        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
        oneServiceResponse.setData(billedStatementResponse);
        return ResponseEntity.ok().headers(responseHeaders).body(oneServiceResponse);
    }

}

