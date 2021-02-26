package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.ActivateCardResponse;
import com.tmb.oneapp.productsexpservice.model.response.buildstatement.BilledStatementResponse;
import io.swagger.annotations.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Api(tags = "Controller for build and unbilled statement")
public class BilledStatementController {

    private final CreditCardClient creditCardClient;
    private static final TMBLogger<CreditCardController> logger = new TMBLogger<>(
            CreditCardController.class);

    public BilledStatementController(CreditCardClient creditCardClient) {
        this.creditCardClient = creditCardClient;
    }

    public ResponseEntity<TmbOneServiceResponse<BilledStatementResponse>> getBilledStatement(
            @ApiParam(value = "Correlation ID", defaultValue = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da", required = true) @RequestHeader("X-Correlation-ID") String correlationId,
            @ApiParam(value = "ACCOUNT_ID", defaultValue = "0000000050078680472000929", required = true) @PathVariable(value = "ACCOUNT_ID") String accountId) throws TMBCommonException
    {
            logger.info("Get Verify Cvv Details for correlationId: {}", correlationId);
            BilledStatementResponse response = new BilledStatementResponse();
            TmbOneServiceResponse<BilledStatementResponse> oneServiceResponse = new TmbOneServiceResponse<>();
            HttpHeaders responseHeaders = new HttpHeaders();


            return ResponseEntity.ok().headers(responseHeaders).body(oneServiceResponse);
    }

}