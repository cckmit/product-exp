package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.model.cardinstallment.InstallmentPlan;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

/**
 * FetchReasonListController request mapping will handle apis call and then
 * navigate to respective method
 *
 */
@RestController
@Api(tags = "Fetch Credit Card Reason List Api")
public class FetchInstallmentPlanController{
    private static final TMBLogger<com.tmb.oneapp.productsexpservice.controller.FetchReasonListController> logger = new TMBLogger<>(com.tmb.oneapp.productsexpservice.controller.FetchReasonListController.class);
    private final CreditCardClient creditCardClient;

    /**
     * Constructor
     *
     * @param creditCardClient
     */
    @Autowired
    public FetchInstallmentPlanController(CreditCardClient creditCardClient) {
        this.creditCardClient = creditCardClient;
    }

    /**
     * Fetch Credit Card Reason List
     *
     * @param correlationId
     * @return reason list from mongo db
     */
    @LogAround
    @GetMapping(value = "/fetch-installment-plan")
    @ApiImplicitParams({
            @ApiImplicitParam(name = ProductsExpServiceConstant.X_CORRELATION_ID, value = "Correlation Id", required = true, dataType = "string", paramType = "header", example = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da") })
    public ResponseEntity<TmbOneServiceResponse<List<InstallmentPlan>>> getInstallmentPlan(
            @RequestHeader(ProductsExpServiceConstant.X_CORRELATION_ID) final String correlationId) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
        TmbOneServiceResponse<List<InstallmentPlan>> oneServiceResponse = new TmbOneServiceResponse<>();
        try {
            ResponseEntity<TmbOneServiceResponse<List<InstallmentPlan>>> installmentPlan = creditCardClient
                    .getInstallmentPlan(correlationId);
            if (installmentPlan != null && installmentPlan.getBody() != null) {
                oneServiceResponse.setData(installmentPlan.getBody().getData());
                oneServiceResponse
                        .setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                                ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
                return ResponseEntity.ok().headers(responseHeaders).body(oneServiceResponse);
            } else {
                oneServiceResponse.setStatus(new TmbStatus(ResponseCode.DATA_NOT_FOUND_ERROR.getCode(),
                        ResponseCode.DATA_NOT_FOUND_ERROR.getMessage(), ResponseCode.DATA_NOT_FOUND_ERROR.getService(),
                        ResponseCode.DATA_NOT_FOUND_ERROR.getDesc()));

                return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
            }
        } catch (Exception e) {
            logger.error("Unable to fetch reason list : {}", e);
            oneServiceResponse.setStatus(new TmbStatus(ResponseCode.GENERAL_ERROR.getCode(),
                    ResponseCode.GENERAL_ERROR.getMessage(), ResponseCode.GENERAL_ERROR.getService()));
            return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
        }

    }

}