package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.service.CrmSubmitCaseService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;


/**
 * CrmCaseController request mapping will handle apis call
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
    public ResponseEntity<TmbOneServiceResponse<Map<String, String>>> submitCaseStatus(
            @ApiParam(value = "Crm ID", defaultValue = "001100000000000000000001184383", required = true) @Valid @RequestHeader(ProductsExpServiceConstant.X_CRMID) String crmId,
            @ApiParam(value = "Correlation ID", defaultValue = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da", required = true) @Valid @RequestHeader(ProductsExpServiceConstant.X_CORRELATION_ID) String correlationId,
            @ApiParam(value = "Firstname TH", defaultValue = "NAME", required = true) @Valid @RequestHeader("firstname_th") String firstnameTh,
            @ApiParam(value = "Lastname TH", defaultValue = "TEST", required = true) @Valid @RequestHeader("lastname_th") String lastnameTh,
            @ApiParam(value = "Firstname EN", defaultValue = "NAME", required = true) @Valid @RequestHeader("firstname_en") String firstnameEn,
            @ApiParam(value = "Lastname EN", defaultValue = "TEST", required = true) @Valid @RequestHeader("lastname_en") String lastnameEn,
            @ApiParam(value = "Service Type Matrix Code", defaultValue = "O0001", required = true) @Valid @RequestHeader("service_type_matrix_code") String serviceTypeMatrixCode) {

        logger.info("product-exp-service submitCaseStatus method start Time : {} ", System.currentTimeMillis());

        TmbOneServiceResponse<Map<String, String>> caseStatusTrackingResponse = new TmbOneServiceResponse<>();

        try {
            final Map<String, String> caseStatusTracking = crmSubmitCaseService.createNcbCase(crmId, correlationId, firstnameTh, lastnameTh, firstnameEn, lastnameEn, serviceTypeMatrixCode);

            if (caseStatusTracking.isEmpty()) {
                caseStatusTrackingResponse.setStatus(new TmbStatus("0009",
                        ResponseCode.DATA_NOT_FOUND_ERROR.getMessage(), ResponseCode.DATA_NOT_FOUND_ERROR.getService()));
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .headers(TMBUtils.getResponseHeaders())
                        .body(caseStatusTrackingResponse);
            }

            caseStatusTrackingResponse.setData(caseStatusTracking);
            caseStatusTrackingResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                    ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
            return ResponseEntity.status(HttpStatus.OK)
                    .headers(TMBUtils.getResponseHeaders())
                    .body(caseStatusTrackingResponse);
        } catch (Exception e) {
            logger.error("Unable to getCaseStatusTracking data : {} ", e);
            caseStatusTrackingResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(), ResponseCode.FAILED.getDesc()));

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .headers(TMBUtils.getResponseHeaders())
                    .body(caseStatusTrackingResponse);
        }
    }
}
