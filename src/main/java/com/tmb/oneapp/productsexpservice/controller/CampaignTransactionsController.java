package com.tmb.oneapp.productsexpservice.controller;

import com.google.common.base.Strings;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.blockcard.Status;
import com.tmb.oneapp.productsexpservice.model.cardinstallment.CampaignTransactionQuery;
import com.tmb.oneapp.productsexpservice.model.cardinstallment.CampaignTransactionResponse;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.X_CORRELATION_ID;

@RestController
@Api(tags = "Apply So GooOD Feature Api")
public class CampaignTransactionsController {
    private final CreditCardClient creditCardClient;
    private static final TMBLogger<CampaignTransactionsController> logger = new TMBLogger<>(CampaignTransactionsController.class);

    /**
     * Constructor
     * @param
     * @param creditCardClient
     */

    @Autowired
    public CampaignTransactionsController(CreditCardClient creditCardClient) {
        this.creditCardClient = creditCardClient;
    }

    /**
     * campaign transaction api
     *
     * @param requestBodyParameter
     * @return campaign transaction response
     */

    @LogAround
    @ApiOperation(value = "Get Campaign Transactions Api")
    @PostMapping(value = "/creditcard/get-campaign-transactions")
    @ApiImplicitParams({
            @ApiImplicitParam(name = X_CORRELATION_ID, defaultValue = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", required = true, paramType = "header")

    })
    public ResponseEntity<TmbOneServiceResponse<CampaignTransactionResponse>> campaignTransactionResponse(
            @RequestBody CampaignTransactionQuery requestBodyParameter,
            @ApiParam(hidden = true) @RequestHeader Map<String, String> requestHeadersParameter)
            throws TMBCommonException {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
        TmbOneServiceResponse<CampaignTransactionResponse> oneServiceResponse = new TmbOneServiceResponse<>();
        String accountId = requestBodyParameter.getAccountId();
        String moreRecords = requestBodyParameter.getMoreRecords();


        String correlationId="32fbd3b2-3f97-4a89-ar39-b4f628fbc8da";

        try {
            if (!Strings.isNullOrEmpty(accountId) && !Strings.isNullOrEmpty(moreRecords)) {
                ResponseEntity<TmbOneServiceResponse<CampaignTransactionResponse>> campaignTransactionsDetails = creditCardClient.getCampaignTransactionsDetails(correlationId,requestBodyParameter);
                if (campaignTransactionsDetails != null) {
                    Status status = new Status();
                    status.setStatusCode(campaignTransactionsDetails.getBody().getStatus().getCode());
                    TmbOneServiceResponse<CampaignTransactionResponse> body = campaignTransactionsDetails.getBody();
                    CampaignTransactionResponse data = body.getData();
                    oneServiceResponse.setData(data);
                    oneServiceResponse
                            .setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                                    ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));

                    return ResponseEntity.ok().headers(responseHeaders).body(oneServiceResponse);

                } else {
                    oneServiceResponse.setStatus(
                            new TmbStatus(ResponseCode.GENERAL_ERROR.getCode(), ResponseCode.GENERAL_ERROR.getMessage(),
                                    ResponseCode.GENERAL_ERROR.getService(), ResponseCode.GENERAL_ERROR.getDesc()));
                    return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
                }
            } else {
                oneServiceResponse.setStatus(new TmbStatus(ResponseCode.DATA_NOT_FOUND_ERROR.getCode(),
                        ResponseCode.DATA_NOT_FOUND_ERROR.getMessage(), ResponseCode.DATA_NOT_FOUND_ERROR.getService(),
                        ResponseCode.DATA_NOT_FOUND_ERROR.getDesc()));
                return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
            }
        } catch (Exception e) {
            throw new TMBCommonException(ResponseCode.GENERAL_ERROR.getCode(), ResponseCode.GENERAL_ERROR.getMessage(),
                    ResponseCode.GENERAL_ERROR.getService(), HttpStatus.BAD_REQUEST, null);
        }

    }
}


