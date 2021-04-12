package com.tmb.oneapp.productsexpservice.controller;

import com.google.common.base.Strings;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.loan.InstallmentRateRequest;
import com.tmb.oneapp.productsexpservice.model.loan.InstallmentRateResponse;
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
public class InstallmentRateController {
    private static final TMBLogger<InstallmentRateController> logger = new TMBLogger<>(InstallmentRateController.class);
    private final CreditCardClient creditCardClient;
    @Autowired
    public InstallmentRateController(CreditCardClient creditCardClient) {
        this.creditCardClient = creditCardClient;
    }

    /**
     * @param correlationId
     * @param requestBody
     * @return
     */
    @LogAround
    @PostMapping(value = "/installment/get-installment-rate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<InstallmentRateResponse>> getLoanAccountDetail(
            @ApiParam(value = "Correlation ID", defaultValue = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", required = true)  @RequestHeader String correlationId,
            @ApiParam(value = "Account ID , start date, end date", defaultValue = "00016109738001", required = true) @RequestBody InstallmentRateRequest requestBody) {

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
        TmbOneServiceResponse<InstallmentRateResponse> oneServiceResponse = new TmbOneServiceResponse<>();


        try {

            String groupAccountId = requestBody.getGroupAccountId();
            String disbursementDate = requestBody.getDisbursementDate();

            if (!Strings.isNullOrEmpty(groupAccountId) && !Strings.isNullOrEmpty(disbursementDate))
             {
                ResponseEntity<TmbOneServiceResponse<InstallmentRateResponse>> loanResponse = creditCardClient.getInstallmentRate(correlationId,requestBody);
                int statusCodeValue = loanResponse.getStatusCodeValue();
                HttpStatus statusCode = loanResponse.getStatusCode();

                if (loanResponse.getBody() != null && statusCodeValue == 200 && statusCode == HttpStatus.OK) {

                    InstallmentRateResponse loanDetails = loanResponse.getBody().getData();

                    oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                            ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
                    oneServiceResponse.setData(loanDetails);
                    return ResponseEntity.ok().headers(responseHeaders).body(oneServiceResponse);
                } else {
                    return getTmbOneServiceResponseResponseEntity(responseHeaders, oneServiceResponse);

                }
            } else {
                return getTmbOneServiceResponseResponseEntity(responseHeaders, oneServiceResponse);
            }

        } catch (Exception e) {
            logger.error("Error while getting installment rate controller: {}", e);
            oneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService()));
            return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
        }

    }

    private ResponseEntity<TmbOneServiceResponse<InstallmentRateResponse>> getTmbOneServiceResponseResponseEntity(HttpHeaders responseHeaders, TmbOneServiceResponse<InstallmentRateResponse> serviceResponse) {
        serviceResponse.setStatus(new TmbStatus(ResponseCode.DATA_NOT_FOUND_ERROR.getCode(),
                ResponseCode.DATA_NOT_FOUND_ERROR.getMessage(), ResponseCode.DATA_NOT_FOUND_ERROR.getService(),
                ResponseCode.DATA_NOT_FOUND_ERROR.getDesc()));
        return ResponseEntity.badRequest().headers(responseHeaders).body(serviceResponse);
    }
}
