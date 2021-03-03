package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.model.response.CaseStatusResponse;
import com.tmb.oneapp.productsexpservice.service.CaseService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.DEVICE_ID;
import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.X_CRMID;

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

    @LogAround
    @ApiOperation(value = "Get Case status data")
    @GetMapping(value = "/case/status")
    @ApiImplicitParams({
            @ApiImplicitParam(name = X_CRMID, value = "Crm Id", defaultValue = "001100000000000000000001184383", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = DEVICE_ID, value = "device_Id", defaultValue = "34cec72b26b7a30ae0a3eaa48d45d82bc2f69728472d9145d57565885", required = true)
    })
    public ResponseEntity<TmbOneServiceResponse<CaseStatusResponse>> getCaseStatus(
            @RequestHeader(X_CRMID) String crmId,
            @RequestHeader(DEVICE_ID) String deviceId
    ) {
        TmbOneServiceResponse<CaseStatusResponse> response = new TmbOneServiceResponse<>();

        try {
            CaseStatusResponse caseStatusResponse = caseService.getCaseStatus(crmId, deviceId);

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
