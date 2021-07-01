package com.tmb.oneapp.productsexpservice.controller.productexperience;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.common.teramandcondition.response.TermAndConditionResponseBody;
import com.tmb.oneapp.productsexpservice.model.customer.request.CustomerRequestBody;
import com.tmb.oneapp.productsexpservice.model.openportfolio.request.OpenPortfolioRequest;
import com.tmb.oneapp.productsexpservice.model.openportfolio.response.OpenPortfolioResponse;
import com.tmb.oneapp.productsexpservice.model.openportfolio.response.ValidateOpenPortfolioResponse;
import com.tmb.oneapp.productsexpservice.service.productexperience.OpenPortfolioService;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * OpenPortfolioController will handle to call apis for open portfolio validation
 */
@RestController
@Api(tags = "Get portfolio data than return it back")
public class OpenPortfolioController {

    private static final TMBLogger<OpenPortfolioController> logger = new TMBLogger<>(OpenPortfolioController.class);

    private OpenPortfolioService openPortfolioService;

    @Autowired
    public OpenPortfolioController(OpenPortfolioService openPortfolioService) {
        this.openPortfolioService = openPortfolioService;
    }

    /**
     * Description:- method validation to handle cases of open portfolio validation
     *
     * @param correlationId        the correlation id
     * @param openPortfolioRequest the open portfolio request
     * @return return term and condition with status
     */
    @ApiOperation(value = "Get term and condition with open portfolio status")
    @PostMapping(value = "/open/portfolio", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<ValidateOpenPortfolioResponse>> validateOpenPortfolio(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_CORRELATION_ID_DESC, defaultValue = ProductsExpServiceConstant.X_COR_ID_DEFAULT, required = true)
            @Valid @RequestHeader(ProductsExpServiceConstant.X_CORRELATION_ID) String correlationId,
            @Valid @RequestBody OpenPortfolioRequest openPortfolioRequest) {
        TmbOneServiceResponse<ValidateOpenPortfolioResponse> oneServiceResponse = openPortfolioService.validateOpenPortfolio(correlationId, openPortfolioRequest);
        if(!StringUtils.isEmpty(oneServiceResponse.getData())){
            return ResponseEntity.ok(oneServiceResponse);
        }else{
            oneServiceResponse.setStatus(notFoundStatus());
            return new ResponseEntity(oneServiceResponse,HttpStatus.NOT_FOUND);
        }
    }

    private TmbStatus notFoundStatus() {
        TmbStatus status = new TmbStatus();
        status.setCode(ProductsExpServiceConstant.DATA_NOT_FOUND_CODE);
        status.setDescription(ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE);
        status.setMessage(ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE);
        status.setService(ProductsExpServiceConstant.SERVICE_NAME);
        return status;
    }


    /**
     * Description:- method call to MF service to open portfolio
     *
     * @param correlationId       the correlation id
     * @param customerRequestBody the customer request body
     * @return return status of open portfolio
     */
    @ApiOperation(value = "Get term and condition with open portfolio status")
    @LogAround
    @PostMapping(value = "/info/openportfolio", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<OpenPortfolioResponse>> createCustomer(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_CORRELATION_ID_DESC, defaultValue = ProductsExpServiceConstant.X_COR_ID_DEFAULT, required = true)
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_CORRELATION_ID) String correlationId,
            @Valid @RequestBody CustomerRequestBody customerRequestBody) {

        TmbOneServiceResponse<OpenPortfolioResponse> oneServiceResponse = new TmbOneServiceResponse<>();
        try {
            OpenPortfolioResponse openPortfolioResponse = openPortfolioService.createCustomer(correlationId, customerRequestBody);
            if (!StringUtils.isEmpty(openPortfolioResponse)) {
                return getTmbOneServiceResponseEntity(oneServiceResponse, openPortfolioResponse, ProductsExpServiceConstant.SUCCESS_CODE, ProductsExpServiceConstant.SUCCESS_MESSAGE, ResponseEntity.ok());
            } else {
                return getTmbOneServiceResponseEntity(oneServiceResponse, null, ProductsExpServiceConstant.DATA_NOT_FOUND_CODE, ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE, ResponseEntity.status(HttpStatus.NOT_FOUND));
            }
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            return getTmbOneServiceResponseEntity(oneServiceResponse, null, ProductsExpServiceConstant.DATA_NOT_FOUND_CODE, ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE, ResponseEntity.status(HttpStatus.NOT_FOUND));
        }
    }

    private ResponseEntity<TmbOneServiceResponse<OpenPortfolioResponse>> getTmbOneServiceResponseEntity(TmbOneServiceResponse<OpenPortfolioResponse> oneServiceResponse, OpenPortfolioResponse openPortfolioResponse, String statusCode, String statusMessage, ResponseEntity.BodyBuilder status) {
        oneServiceResponse.setData(openPortfolioResponse);
        oneServiceResponse.setStatus(new TmbStatus(statusCode, statusMessage, ProductsExpServiceConstant.SERVICE_NAME, statusMessage));
        return status.headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse);
    }
}
