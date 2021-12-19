package com.tmb.oneapp.productsexpservice.controller.productexperience.fund;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.portfolio.response.PortfolioResponse;
import com.tmb.oneapp.productsexpservice.service.productexperience.fund.PortfolioService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * PortfolioController will handle to call apis for getting portfolio list
 */
@Api(tags = "Get portfolio data list then return it back")
@RequestMapping("/funds")
@RestController
public class PortfolioController {

    private static final TMBLogger<PortfolioController> logger = new TMBLogger<>(PortfolioController.class);

    private PortfolioService portfolioService;

    @Autowired
    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    /**
     * Description:- Inquiry portfolio from MF Service
     *
     * @param correlationId the correlation id
     * @param crmId         the crm id
     * @param type          the type
     * @return return portfolio list
     */
    @ApiOperation(value = "Fetch portfolio list from MF Service, then filter it with type to front-end")
    @LogAround
    @GetMapping(value = "/portfolio")
    public ResponseEntity<TmbOneServiceResponse<PortfolioResponse>> getPortfolioList(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_CORRELATION_ID_DESC, defaultValue = ProductsExpServiceConstant.X_COR_ID_DEFAULT, required = true)
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId,
            @Valid @RequestParam String type) throws TMBCommonException {

        TmbOneServiceResponse<PortfolioResponse> oneServiceResponse = new TmbOneServiceResponse<>();

        PortfolioResponse portfolioResponse = portfolioService.getPortfolioList(correlationId, crmId, type);
        if (!StringUtils.isEmpty(portfolioResponse)) {
            return getTmbOneServiceResponseEntity(oneServiceResponse, portfolioResponse, ProductsExpServiceConstant.SUCCESS_CODE, ProductsExpServiceConstant.SUCCESS_MESSAGE, ResponseEntity.ok());
        } else {
            return getTmbOneServiceResponseEntity(oneServiceResponse, null, ProductsExpServiceConstant.DATA_NOT_FOUND_CODE, ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE, ResponseEntity.status(HttpStatus.NOT_FOUND));
        }
    }

    private ResponseEntity<TmbOneServiceResponse<PortfolioResponse>> getTmbOneServiceResponseEntity(TmbOneServiceResponse<PortfolioResponse> oneServiceResponse, PortfolioResponse portfolioResponse, String statusCode, String statusMessage, ResponseEntity.BodyBuilder status) {
        oneServiceResponse.setData(portfolioResponse);
        oneServiceResponse.setStatus(new TmbStatus(statusCode, statusMessage, ProductsExpServiceConstant.SERVICE_NAME, statusMessage));
        return status.headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse);
    }
}
