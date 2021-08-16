package com.tmb.oneapp.productsexpservice.controller.productexperience.alternative;

import com.tmb.common.logger.LogAround;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.dto.fund.dca.validation.DcaValidationDto;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.dcavalidation.DcaValidationRequest;
import com.tmb.oneapp.productsexpservice.service.productexperience.fund.DcaValidationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.tmb.oneapp.productsexpservice.util.TmbStatusUtil.notFoundStatus;

@Api(tags = "for dca fund validation")
@RequestMapping("/funds")
@RestController
public class DcaValidationController {

    private final DcaValidationService dcaValidationService;

    public DcaValidationController(DcaValidationService dcaValidationService) {
        this.dcaValidationService = dcaValidationService;
    }

    /**
     * Description:- method get fund fact sheet data
     *
     * @headerParam correlationId        the correlation id
     * @headerParam crmif                the unique id for customer
     * @bodyParam dcaRuleRequest  the user crmid
     * @return return dca list
     */
    @ApiOperation(value = "Get fund factsheet to frontend")
    @LogAround
    @PostMapping(value = "/dca/rule")
    public ResponseEntity<TmbOneServiceResponse<DcaValidationDto>> getFundFactSheetWithValidation(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_CORRELATION_ID_DESC, defaultValue = ProductsExpServiceConstant.X_COR_ID_DEFAULT, required = true)
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId,
            @Valid @RequestBody DcaValidationRequest dcaValidationRequest) {
        TmbOneServiceResponse<DcaValidationDto> oneServiceResponse = dcaValidationService.dcaValidation(correlationId, crmId,dcaValidationRequest);
        if (!StringUtils.isEmpty(oneServiceResponse.getStatus())) {
            if (!oneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
                return ResponseEntity.badRequest().body(oneServiceResponse);
            }
            return ResponseEntity.ok(oneServiceResponse);
        } else {
            oneServiceResponse.setStatus(notFoundStatus());
            return new ResponseEntity(oneServiceResponse, HttpStatus.NOT_FOUND);
        }
    }

}
