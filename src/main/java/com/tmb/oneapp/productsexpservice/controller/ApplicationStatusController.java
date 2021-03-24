package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.model.response.statustracking.ApplicationStatusResponse;
import com.tmb.oneapp.productsexpservice.service.ApplicationStatusService;
import io.swagger.annotations.*;
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
 * ApplicationStatusController request mapping will handle apis call and then navigate
 * to respective method
 */
@RestController
@Api(tags = "Application Status Controller")
public class ApplicationStatusController {
    private static final TMBLogger<ApplicationStatusController> logger = new TMBLogger<>(
            ApplicationStatusController.class);

    private final ApplicationStatusService applicationStatusService;

    @Autowired
    public ApplicationStatusController(ApplicationStatusService applicationStatusService) {
        super();
        this.applicationStatusService = applicationStatusService;
    }

    @LogAround
    @ApiOperation(value = "Get Application Status")
    @GetMapping(value = "/application/status")
    @ApiImplicitParams({
            @ApiImplicitParam(name = X_CORRELATION_ID, defaultValue = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", required = true, paramType = "header"),
            @ApiImplicitParam(name = X_CRMID, defaultValue = "001100000000000000000001184383", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = DEVICE_ID, defaultValue = "34cec72b26b7a30ae0a3eaa48d45d82bc2f69728472d9145d57565885", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = ACCEPT_LANGUAGE, defaultValue = "en", required = true, paramType = "header"),
    })
    public ResponseEntity<TmbOneServiceResponse<ApplicationStatusResponse>> getApplicationStatus(
            @ApiParam(hidden = true) @RequestHeader Map<String, String> requestHeaders,
            @ApiParam(value = "Service Type Id", defaultValue = "AST", required = true)
            @RequestParam("service_type_id") String serviceTypeId) {

        TmbOneServiceResponse<ApplicationStatusResponse> response = new TmbOneServiceResponse<>();

        if (!requestHeaders.containsKey(X_CORRELATION_ID) ||
                !requestHeaders.containsKey(X_CRMID) ||
                !requestHeaders.containsKey(DEVICE_ID)) {
            response.setStatus(new TmbStatus(ResponseCode.GENERAL_ERROR.getCode(),
                    ResponseCode.GENERAL_ERROR.getMessage(), ResponseCode.GENERAL_ERROR.getService()));
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(response);
        }

        try {
            ApplicationStatusResponse applicationStatusResponse =
                    applicationStatusService.getApplicationStatus(requestHeaders, serviceTypeId);

            response.setData(applicationStatusResponse);

            if (1 == applicationStatusResponse.getHpStatus() &&
                    1 == applicationStatusResponse.getRslStatus()) { //AST_0004
                logger.info("Error retrieving data from RSL and HP.");
                response.setStatus(new TmbStatus(ResponseCode.HP_RSL_ERROR.getCode(),
                        ResponseCode.HP_RSL_ERROR.getMessage(), ResponseCode.HP_RSL_ERROR.getService()));
            } else if (1 == applicationStatusResponse.getHpStatus()) { //AST_0003
                logger.info("Error retrieving data from HP.");
                response.setStatus(new TmbStatus(ResponseCode.HP_ERROR_CODE.getCode(),
                        ResponseCode.HP_ERROR_CODE.getMessage(), ResponseCode.HP_ERROR_CODE.getService()));
            } else if (1 == applicationStatusResponse.getRslStatus()) { //AST_0002
                logger.info("Error retrieving data from RSL.");
                response.setStatus(new TmbStatus(ResponseCode.RSL_ERROR_CODE.getCode(),
                        ResponseCode.RSL_ERROR_CODE.getMessage(), ResponseCode.RSL_ERROR_CODE.getService()));
            } else if (2 == applicationStatusResponse.getHpStatus() ||
                    2 == applicationStatusResponse.getRslStatus()) { // AST_0001
                response.setStatus(new TmbStatus(ResponseCode.HP_RSL_DATA_NOT_FOUND.getCode(),
                        ResponseCode.HP_RSL_DATA_NOT_FOUND.getMessage(), ResponseCode.HP_RSL_DATA_NOT_FOUND.getService()));
            } else { //AST_0000
                response.setStatus(new TmbStatus(ResponseCode.HP_RSL_SUCCESS.getCode(),
                        ResponseCode.HP_RSL_SUCCESS.getMessage(), ResponseCode.HP_RSL_SUCCESS.getService()));
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .headers(TMBUtils.getResponseHeaders())
                    .body(response);

        } catch (Exception e) {
            logger.error("Error calling GET /application/status : {}", e);
            response.setStatus(new TmbStatus(ResponseCode.GENERAL_ERROR.getCode(),
                    ResponseCode.GENERAL_ERROR.getMessage(), ResponseCode.GENERAL_ERROR.getService()));
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(response);
        }

    }

}
