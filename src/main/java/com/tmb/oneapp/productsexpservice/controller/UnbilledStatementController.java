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
import com.tmb.oneapp.productsexpservice.model.request.buildstatement.GetUnbilledStatementQuery;
import com.tmb.oneapp.productsexpservice.model.request.buildstatement.StatementTransaction;
import com.tmb.oneapp.productsexpservice.model.response.buildstatement.BilledStatementResponse;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


@RestController
@Api(tags = "Controller for billed statement")
public class UnbilledStatementController {
    @Autowired
    private final CreditCardClient creditCardClient;


    private static final TMBLogger<UnbilledStatementController> logger = new TMBLogger<>(
            UnbilledStatementController.class);

    public UnbilledStatementController(CreditCardClient creditCardClient) {
        this.creditCardClient = creditCardClient;
    }

    /**
     * @param correlationId
     * @param requestBody
     * @return
     */
    @LogAround
    @ApiOperation(value = "get unbilled statement ")
    @PostMapping(value = "credit-card/statement/get-unbilled-statement")
    @ApiImplicitParams({
            @ApiImplicitParam(name = ProductsExpServiceConstant.X_CORRELATION_ID, value = "Correlation Id", required = true, dataType = "string", paramType = "header", example = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da"),
    })
    public ResponseEntity<TmbOneServiceResponse<BilledStatementResponse>> getUnBilledStatement(
            @ApiParam(value = "Correlation ID", defaultValue = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da", required = true) @RequestHeader("X-Correlation-ID") String correlationId,
            @RequestBody GetUnbilledStatementQuery requestBody) {

        TmbOneServiceResponse<BilledStatementResponse> oneServiceResponse = new TmbOneServiceResponse<>();
        HttpHeaders responseHeaders = new HttpHeaders();
        try {
            String accountId = requestBody.getAccountId();

            oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                    ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
            oneServiceResponse.setData(oneServiceResponse.getData());
            if (!Strings.isNullOrEmpty(accountId) && !Strings.isNullOrEmpty(correlationId)) {
                ResponseEntity<BilledStatementResponse> billedStatementRes = creditCardClient
                        .getUnBilledStatement(correlationId, requestBody);


                if (billedStatementRes != null && billedStatementRes.getStatusCode() == HttpStatus.OK
                        && billedStatementRes.getBody().getStatus().getStatusCode() == ProductsExpServiceConstant.ZERO) {
                    return getTmbOneServiceResponseResponse(oneServiceResponse, responseHeaders, billedStatementRes);

                } else {

                    return this.handlingFailedResponse(oneServiceResponse, responseHeaders);
                }

            } else {
                this.handlingFailedResponse(oneServiceResponse, responseHeaders);
            }

        } catch (Exception e) {
            logger.error("Unable to fetch unbilled statement for this accountId : {}", e);
            oneServiceResponse.setStatus(new TmbStatus(ResponseCode.GENERAL_ERROR.getCode(),
                    ResponseCode.GENERAL_ERROR.getMessage(), ResponseCode.GENERAL_ERROR.getService()));

            return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
        }
        return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
    }

    /**
     * @param oneServiceResponse
     * @param responseHeaders
     * @param billedStatementRes
     * @return
     */
    ResponseEntity<TmbOneServiceResponse<BilledStatementResponse>> getTmbOneServiceResponseResponse(TmbOneServiceResponse<BilledStatementResponse> oneServiceResponse, HttpHeaders responseHeaders, ResponseEntity<BilledStatementResponse> billedStatementRes) {
        BigDecimal totalUnbilledAmounts = billedStatementRes.getBody().getCardStatement().getTotalUnbilledAmounts();
        if (totalUnbilledAmounts == null) {
            CardStatement cardStatement = billedStatementRes.getBody().getCardStatement();
            List<BigDecimal> items;
            items = Arrays.asList(cardStatement.getMinPaymentAmounts(), cardStatement.getTotalAmountDue(), cardStatement.getMinimumDue(), cardStatement.getInterests(), cardStatement.getCashAdvanceFee());

            totalUnbilledAmounts = items.stream().filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
            cardStatement.setTotalUnbilledAmounts(totalUnbilledAmounts);


        }
        return handlingResponseData(billedStatementRes, oneServiceResponse,
                responseHeaders);
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
     * @param response
     * @param oneServiceResponse
     * @param responseHeaders
     * @return
     */
    public ResponseEntity<TmbOneServiceResponse<BilledStatementResponse>> handlingResponseData(
            ResponseEntity<BilledStatementResponse> response,
            TmbOneServiceResponse<BilledStatementResponse> oneServiceResponse,
            HttpHeaders responseHeaders) {
        BilledStatementResponse responseBody = response.getBody();


        String moreRecords = responseBody.getCardStatement() != null ? responseBody.getMoreRecords()
                : ProductsExpServiceConstant.EMPTY;
        String searchKeys = responseBody.getCardStatement() != null
                ? responseBody.getSearchKeys()
                : ProductsExpServiceConstant.EMPTY;
        Integer totalRecords = responseBody.getCardStatement() != null ? responseBody.getTotalRecords()
                : ProductsExpServiceConstant.ZERO;
        Integer maxRecords = responseBody.getCardStatement() != null ? responseBody.getMaxRecords()
                : ProductsExpServiceConstant.ZERO;
        CardStatement cardStatement = responseBody.getCardStatement();
        responseBody.setCardStatement(cardStatement);
        responseBody.setMaxRecords(maxRecords);
        responseBody.setMoreRecords(moreRecords);
        responseBody.setSearchKeys(searchKeys);
        responseBody.setTotalRecords(totalRecords);
        List<StatementTransaction> statementTransactions = responseBody.getCardStatement().getStatementTransactions();
        statementTransactions.sort((StatementTransaction s1, StatementTransaction s2) -> s2.getTransactionDate().compareTo(s1.getTransactionDate()));
        responseBody.getCardStatement().setStatementTransactions(statementTransactions);
        oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
        oneServiceResponse.setData(responseBody);
        return ResponseEntity.ok().headers(responseHeaders).body(oneServiceResponse);
    }

}
