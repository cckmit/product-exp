package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.model.request.crm.CrmSubmitCaseBody;
import com.tmb.oneapp.productsexpservice.service.CrmSubmitCaseService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.*;


/**
 * CrmSubmitCaseController request mapping will handle apis call
 * and then navigate to respective method
 */
@RestController
public class CrmSubmitCaseController {
    private static final TMBLogger<CrmSubmitCaseController> logger = new TMBLogger<>(
            CrmSubmitCaseController.class);

    private final CrmSubmitCaseService crmSubmitCaseService;

    @Autowired
    public CrmSubmitCaseController(CrmSubmitCaseService crmSubmitCaseService) {
        super();
        this.crmSubmitCaseService = crmSubmitCaseService;
    }

    /**
     * Submit Crm Case Status
     *
     * @return case_reference
     */
    @ApiOperation(value = "Submit Case status data")
    @PostMapping(value = "/crm/submitCase")
    @LogAround
    @ApiImplicitParams({
            @ApiImplicitParam(name = X_CORRELATION_ID, defaultValue = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", required = true, paramType = "header"),
            @ApiImplicitParam(name = X_CRMID, defaultValue = "001100000000000000000000051187", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseEntity<TmbOneServiceResponse<Map<String, String>>> submitCaseStatus(
            @ApiParam(hidden = true) @RequestHeader Map<String, String> requestHeaders,
            @RequestBody CrmSubmitCaseBody requestBody) {

        TmbOneServiceResponse<Map<String, String>> caseStatusTrackingResponse = new TmbOneServiceResponse<>();

        try {
            final Map<String, String> caseCreateResult = crmSubmitCaseService.createCrmCase(requestHeaders.get(X_CRMID), requestHeaders.get(X_CORRELATION_ID), requestBody.getFirstnameTh(), requestBody.getLastnameTh(), requestBody.getFirstnameEn(), requestBody.getLastnameEn(), requestBody.getServiceTypeMatrixCode(), requestBody.getNote());

            if (caseCreateResult == null) {
                caseStatusTrackingResponse.setStatus(new TmbStatus("0009",
                        ResponseCode.DATA_NOT_FOUND_ERROR.getMessage(), ResponseCode.DATA_NOT_FOUND_ERROR.getService()));
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .headers(TMBUtils.getResponseHeaders())
                        .body(caseStatusTrackingResponse);
            }

            caseStatusTrackingResponse.setData(caseCreateResult);
            caseStatusTrackingResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                    ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
            return ResponseEntity.status(HttpStatus.OK)
                    .headers(TMBUtils.getResponseHeaders())
                    .body(caseStatusTrackingResponse);
        } catch (Exception e) {
            logger.error("submitCaseStatus error : {} ", e);
            caseStatusTrackingResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(), ResponseCode.FAILED.getDesc()));

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .headers(TMBUtils.getResponseHeaders())
                    .body(caseStatusTrackingResponse);
        }
    }
}
