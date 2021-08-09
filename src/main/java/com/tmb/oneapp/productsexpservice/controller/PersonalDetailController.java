package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.model.legacy.rsl.ws.individual.update.response.ResponseIndividual;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.model.personaldetail.PersonalDetailRequest;
import com.tmb.oneapp.productsexpservice.model.personaldetail.PersonalDetailResponse;
import com.tmb.oneapp.productsexpservice.model.personaldetail.PersonalDetailSaveInfoRequest;
import com.tmb.oneapp.productsexpservice.service.PersonalDetailSaveInfoService;
import com.tmb.oneapp.productsexpservice.service.PersonalDetailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;

@RestController
@RequiredArgsConstructor
@RequestMapping("/loanSubmission")
@Api(tags = "Personal Detail")
public class PersonalDetailController {
    private static final TMBLogger<ProductsVerifyCvvController> logger = new TMBLogger<>(ProductsVerifyCvvController.class);
    private static final HttpHeaders responseHeaders = new HttpHeaders();
    private final PersonalDetailService personalDetailService;
    private final PersonalDetailSaveInfoService personalDetailSaveInfoService;

    @GetMapping(value = "/personalDetail")
    @LogAround
    @ApiOperation("Get Personal Detail")
    @ApiImplicitParams({
            @ApiImplicitParam(name = ProductsExpServiceConstant.HEADER_X_CRM_ID, defaultValue = "001100000000000000000018593707", required = true, dataType = "string", paramType = "header") })
    public ResponseEntity<TmbOneServiceResponse<PersonalDetailResponse>> getPersonalDetail(
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmid,
            @Valid PersonalDetailRequest request) {
        TmbOneServiceResponse<PersonalDetailResponse> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();
        try {
            PersonalDetailResponse personalDetailResponse = personalDetailService.getPersonalDetailInfo(crmid, request);
            oneTmbOneServiceResponse.setData(personalDetailResponse);
            oneTmbOneServiceResponse.setStatus(getStatusSuccess());
            setHeader();
            return ResponseEntity.ok().body(oneTmbOneServiceResponse);
        } catch (Exception e) {
            logger.error("error while get personal detail: {}", e);
            oneTmbOneServiceResponse.setStatus(getStatusFailed());
            return ResponseEntity.badRequest().headers(responseHeaders).body(oneTmbOneServiceResponse);
        }
    }

    @PostMapping(value = "/savePersonalDetail", produces = MediaType.APPLICATION_JSON_VALUE)
    @LogAround
    @ApiOperation("Update Personal Detail")
    public ResponseEntity<TmbOneServiceResponse<ResponseIndividual>> savePersonalDetail(
            @RequestBody PersonalDetailSaveInfoRequest request) {
        TmbOneServiceResponse<ResponseIndividual> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();
        try {
            personalDetailSaveInfoService.updatePersonalDetailInfo(request);
            oneTmbOneServiceResponse.setStatus(getStatusSuccess());
            setHeader();
            return ResponseEntity.ok().body(oneTmbOneServiceResponse);
        } catch (Exception e) {
            logger.error("error while update personal customer detail: {}", e);
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
