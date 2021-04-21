package com.tmb.oneapp.productsexpservice.controller;

import com.google.common.base.Strings;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.loan.EligibleLeadRequest;
import com.tmb.oneapp.productsexpservice.model.loan.EligibleLeadResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@Api(tags = "Credit Card-Cash For You")
public class EligibleLeadController {
    private static final TMBLogger<EligibleLeadController> logger = new TMBLogger<>(EligibleLeadController.class);

    private final CreditCardClient creditCardClient;

    @Autowired
    public EligibleLeadController(CreditCardClient creditCardClient) {
        this.creditCardClient = creditCardClient;
    }

    /**
     * @param correlationId
     * @param requestBody
     * @return
     */
    @LogAround
    @PostMapping(value = "/loan/get-eligible-lead", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<EligibleLeadResponse>> getLoanAccountDetail(
            @ApiParam(value = "Correlation ID", defaultValue = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", required = true) @RequestHeader String correlationId,
            @ApiParam(value = "Account ID , start date, end date", defaultValue = "00016109738001", required = true) @RequestBody EligibleLeadRequest requestBody) {

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
        TmbOneServiceResponse<EligibleLeadResponse> serviceResponse = new TmbOneServiceResponse<>();


        try {

            String groupAccountId = requestBody.getGroupAccountId();
            String disbursementDate = requestBody.getDisbursementDate();

            if (!Strings.isNullOrEmpty(groupAccountId) && !Strings.isNullOrEmpty(disbursementDate)) {
                ResponseEntity<TmbOneServiceResponse<EligibleLeadResponse>> loanResponse = creditCardClient.getEligibleLeads(correlationId, requestBody);
                int statusCodeValue = loanResponse.getStatusCodeValue();
                HttpStatus statusCode = loanResponse.getStatusCode();

                if (loanResponse.getBody() != null && statusCodeValue == 200 && statusCode == HttpStatus.OK) {

                    EligibleLeadResponse loanDetails = loanResponse.getBody().getData();

                    serviceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                            ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
                    serviceResponse.setData(loanDetails);
                    return ResponseEntity.ok().headers(responseHeaders).body(serviceResponse);
                } else {
                    return getTmbOneServiceResponseResponseEntity(responseHeaders, serviceResponse);

                }
            } else {
                return getTmbOneServiceResponseResponseEntity(responseHeaders, serviceResponse);
            }

        } catch (Exception e) {
            logger.error("Error while getting eligible lead controller: {}", e);
            serviceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService()));
            return ResponseEntity.badRequest().headers(responseHeaders).body(serviceResponse);
        }

    }

    /**
     * @param responseHeaders
     * @param serviceResponse
     * @return
     */
    private ResponseEntity<TmbOneServiceResponse<EligibleLeadResponse>> getTmbOneServiceResponseResponseEntity(HttpHeaders responseHeaders, TmbOneServiceResponse<EligibleLeadResponse> serviceResponse) {
        serviceResponse.setStatus(new TmbStatus(ResponseCode.DATA_NOT_FOUND_ERROR.getCode(),
                ResponseCode.DATA_NOT_FOUND_ERROR.getMessage(), ResponseCode.DATA_NOT_FOUND_ERROR.getService(),
                ResponseCode.DATA_NOT_FOUND_ERROR.getDesc()));
        return ResponseEntity.badRequest().headers(responseHeaders).body(serviceResponse);
    }
}
