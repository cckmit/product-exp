package com.tmb.oneapp.productsexpservice.controller.productexperience.dca;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.dto.aip.validation.DcaValidationDto;
import com.tmb.oneapp.productsexpservice.model.productexperience.dca.validation.request.DcaValidationRequest;
import com.tmb.oneapp.productsexpservice.service.productexperience.dca.DcaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;

/**
 * DcaController request will handle to call apis for combining the data from transaction validation and aip validation of investment
 */
@Api(tags = "Get transaction validation and aip validation than return to front-end")
@RequestMapping("/dca")
@RestController
public class DcaController {

    private static final TMBLogger<DcaController> logger = new TMBLogger<>(DcaController.class);

    private DcaService dcaService;

    @Autowired
    public DcaController(DcaService dcaService) {
        this.dcaService = dcaService;
    }

    /**
     * Description:- Inquiry MF Service
     *
     * @param correlationId        the correlation id
     * @param dcaValidationRequest the dca validation request body
     * @return return dca validation response data
     */
    @ApiOperation(value = "Fetch dca validation from MF Service, then return to front-end")
    @LogAround
    @PostMapping(value = "/confirm/screen", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<DcaValidationDto>> getValidation(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_CORRELATION_ID_DESC, defaultValue = ProductsExpServiceConstant.X_COR_ID_DEFAULT, required = true)
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId,
            @Valid @RequestBody DcaValidationRequest dcaValidationRequest) {

        TmbOneServiceResponse<DcaValidationDto> oneServiceResponse = new TmbOneServiceResponse<>();
        try {
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));

            DcaValidationDto dcaValidationDto = dcaService.getValidation(correlationId, crmId, dcaValidationRequest);
            if (!StringUtils.isEmpty(dcaValidationDto)) {
                return getTmbOneServiceResponseEntity(oneServiceResponse, dcaValidationDto, ProductsExpServiceConstant.SUCCESS_CODE, ProductsExpServiceConstant.SUCCESS_MESSAGE, ResponseEntity.ok());
            } else {
                return getTmbOneServiceResponseEntity(oneServiceResponse, null, ProductsExpServiceConstant.DATA_NOT_FOUND_CODE, ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE, ResponseEntity.status(HttpStatus.NOT_FOUND));
            }
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            return getTmbOneServiceResponseEntity(oneServiceResponse, null, ProductsExpServiceConstant.DATA_NOT_FOUND_CODE, ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE, ResponseEntity.status(HttpStatus.NOT_FOUND));
        }
    }

    private ResponseEntity<TmbOneServiceResponse<DcaValidationDto>> getTmbOneServiceResponseEntity(TmbOneServiceResponse<DcaValidationDto> oneServiceResponse, DcaValidationDto dcaValidationDto, String statusCode, String statusMessage, ResponseEntity.BodyBuilder status) {
        oneServiceResponse.setData(dcaValidationDto);
        oneServiceResponse.setStatus(new TmbStatus(statusCode, statusMessage, ProductsExpServiceConstant.SERVICE_NAME, statusMessage));
        return status.headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse);
    }
}
