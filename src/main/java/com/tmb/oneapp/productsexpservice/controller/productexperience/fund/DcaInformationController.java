package com.tmb.oneapp.productsexpservice.controller.productexperience.fund;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.LogAround;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.dto.fund.dca.information.DcaInformationDto;
import com.tmb.oneapp.productsexpservice.service.productexperience.fund.DcaInformationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.tmb.oneapp.productsexpservice.util.TmbStatusUtil.notFoundStatus;

@Api(tags = "Get fund list for make dca")
@RequestMapping("/funds")
@RestController
public class DcaInformationController {

    private final DcaInformationService dcaInformationService;

    @Autowired
    public DcaInformationController(DcaInformationService dcaInformationService) {
        this.dcaInformationService = dcaInformationService;
    }

    /**
     * Description:- method get dca list
     *
     * @headerparam correlationId        the correlation id
     * @headerparam crmId  the user crmid
     * @return return dca list
     */
    @ApiOperation(value = "Get dca allow aip flag list to frontend")
    @LogAround
    @PostMapping(value = "/dca/info")
    public ResponseEntity<TmbOneServiceResponse<DcaInformationDto>> getDcaInformation(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_CORRELATION_ID_DESC, defaultValue = ProductsExpServiceConstant.X_COR_ID_DEFAULT, required = true)
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId) throws TMBCommonException {
        TmbOneServiceResponse<DcaInformationDto> oneServiceResponse = dcaInformationService.getDcaInformation(correlationId, crmId);
        if (!StringUtils.isEmpty(oneServiceResponse.getStatus())) {
            return ResponseEntity.ok(oneServiceResponse);
        } else {
            oneServiceResponse.setStatus(notFoundStatus());
            return new ResponseEntity(oneServiceResponse, HttpStatus.NOT_FOUND);
        }
    }
}