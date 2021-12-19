package com.tmb.oneapp.productsexpservice.controller.productexperience.customer;

import com.tmb.common.logger.LogAround;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.fatca.request.FatcaRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.fatca.response.FatcaResponseBody;
import com.tmb.oneapp.productsexpservice.service.productexperience.customer.FatcaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.DATA_NOT_FOUND_CODE;
import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.SUCCESS_CODE;

/**
 * CustomerController will handle to call apis for creating fatca form
 */
@Api(tags = "Create fatca form for customer")
@RequestMapping("/customer")
@RestController
public class FatcaController {

    private final FatcaService fatcaService;

    @Autowired
    public FatcaController(FatcaService fatcaService) {
        this.fatcaService = fatcaService;
    }

    /**
     * Description:- Create fatca from by calling customer-exp service
     *
     * @param correlationId the correlation id
     * @param crmId         the crm id
     * @param ipAddress     the ip address
     * @param fatcaRequest  the fatca request
     * @return return status of creating fatca
     */
    @ApiOperation(value = "Create fatca form by calling customer-exp service, then filter it with type to front-end")
    @LogAround
    @PostMapping(value = "/fatca/creation")
    public ResponseEntity<TmbOneServiceResponse<FatcaResponseBody>> createFatcaForm(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_CORRELATION_ID_DESC, defaultValue = ProductsExpServiceConstant.X_COR_ID_DEFAULT, required = true)
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId,
            @Valid @RequestHeader(ProductsExpServiceConstant.X_FORWARD_FOR) String ipAddress,
            @Valid @RequestBody FatcaRequest fatcaRequest) {

        TmbOneServiceResponse<FatcaResponseBody> oneServiceResponse = fatcaService.createFatcaForm(correlationId, crmId, ipAddress, fatcaRequest);
        if (SUCCESS_CODE.equals(oneServiceResponse.getStatus().getCode())) {
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse);
        } else if (DATA_NOT_FOUND_CODE.equals(oneServiceResponse.getStatus().getCode())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(oneServiceResponse);
        }
    }
}
