package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.model.personaldetail.ChecklistRequest;
import com.tmb.oneapp.productsexpservice.model.personaldetail.ChecklistResponse;
import com.tmb.oneapp.productsexpservice.service.ChecklistService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.Instant;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/loanSubmission")
@Api(tags = "Checklist")
public class ChecklistController {
    private static final TMBLogger<ProductsVerifyCvvController> logger = new TMBLogger<>(ProductsVerifyCvvController.class);
    private static final HttpHeaders responseHeaders = new HttpHeaders();
    private final ChecklistService checklistService;

    @GetMapping(value = "/documents")
    @LogAround
    @ApiOperation("Checklist Document")
    @ApiImplicitParams({
            @ApiImplicitParam(name = ProductsExpServiceConstant.HEADER_X_CRM_ID, defaultValue = "001100000000000000000018593707", required = true, dataType = "string", paramType = "header") })
    public ResponseEntity<TmbOneServiceResponse<List<ChecklistResponse>>> getDocuments(
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmid,
            @Valid ChecklistRequest request) {
        TmbOneServiceResponse<List<ChecklistResponse>> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();
        try {
            List<ChecklistResponse> checklistResponses = checklistService.getDocuments(crmid, request.getCaId());
            oneTmbOneServiceResponse.setData(checklistResponses);
            oneTmbOneServiceResponse.setStatus(getStatusSuccess());
            setHeader();
            return ResponseEntity.ok().body(oneTmbOneServiceResponse);
        } catch (Exception e) {
            logger.error("error while get checklist : {}", e);
            oneTmbOneServiceResponse.setStatus(getStatusFailed());
            return ResponseEntity.badRequest().headers(responseHeaders).body(oneTmbOneServiceResponse);
        }
    }

    private TmbStatus getStatusFailed() {
        return new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                ResponseCode.FAILED.getService());
    }


    private TmbStatus getStatusSuccess() {
        return new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                ProductsExpServiceConstant.SUCCESS_MESSAGE,
                ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE);
    }

    private void setHeader() {
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
    }
}
