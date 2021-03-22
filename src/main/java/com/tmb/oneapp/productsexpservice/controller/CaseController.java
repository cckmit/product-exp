package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.model.response.statustracking.CaseStatusResponse;
import com.tmb.oneapp.productsexpservice.service.CaseService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.*;

/**
 * CaseController request mapping will handle apis call
 * and then navigate to respective method
 */
@RestController
public class CaseController {
    private static final TMBLogger<CaseController> logger = new TMBLogger<>(
            CaseController.class);

    private final CaseService caseService;

    @Autowired
    public CaseController(CaseService caseService) {
        super();
        this.caseService = caseService;
    }

    /**
     * Check customer first time use
     *
     * @return CustomerFirstUsage information of first time use
     */
    @LogAround
    @ApiOperation(value = "Get Case status data")
    @GetMapping(value = "/case/status")
    @ApiImplicitParams({
            @ApiImplicitParam(name = X_CORRELATION_ID, defaultValue = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", required = true, paramType = "header"),
            @ApiImplicitParam(name = X_CRMID, defaultValue = "001100000000000000000001184383", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = DEVICE_ID, defaultValue = "34cec72b26b7a30ae0a3eaa48d45d82bc2f69728472d9145d57565885", required = true)
    })
    public ResponseEntity<TmbOneServiceResponse<CaseStatusResponse>> getCaseStatus(
            @RequestHeader Map<String, String> requestHeaders,
            @RequestParam("service_type_id") String serviceTypeId
    ) {
        TmbOneServiceResponse<CaseStatusResponse> response = new TmbOneServiceResponse<>();

        if (!requestHeaders.containsKey(X_CORRELATION_ID) ||
                !requestHeaders.containsKey(X_CRMID) ||
                !requestHeaders.containsKey(DEVICE_ID)) {
            response.setStatus(new TmbStatus(ResponseCode.GENERAL_ERROR.getCode(),
                    ResponseCode.GENERAL_ERROR.getMessage(), ResponseCode.GENERAL_ERROR.getService()));
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(response);
        }

        try {
            CaseStatusResponse caseStatusResponse = caseService.getCaseStatus(requestHeaders, serviceTypeId);

            response.setData(caseStatusResponse);
            response.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                    ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));

            return ResponseEntity.status(HttpStatus.OK)
                    .headers(TMBUtils.getResponseHeaders())
                    .body(response);

        } catch (Exception e) {
            logger.error("Error calling GET /case/status : {}", e);
            response.setStatus(new TmbStatus(ResponseCode.GENERAL_ERROR.getCode(),
                    ResponseCode.GENERAL_ERROR.getMessage(), ResponseCode.GENERAL_ERROR.getService()));
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(response);
        }

    }


}
