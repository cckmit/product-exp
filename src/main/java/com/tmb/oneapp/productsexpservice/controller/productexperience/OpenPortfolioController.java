package com.tmb.oneapp.productsexpservice.controller.productexperience;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.common.teramandcondition.response.TermAndConditionResponseBody;
import com.tmb.oneapp.productsexpservice.model.openportfolio.request.OpenPortfolioRequest;
import com.tmb.oneapp.productsexpservice.service.productexperience.OpenPortfolioService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    @LogAround
    @PostMapping(value = "/open/portfolio", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<TermAndConditionResponseBody>> getFundAccountDetail(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_CORRELATION_ID_DESC, defaultValue = ProductsExpServiceConstant.X_COR_ID_DEFAULT, required = true)
            @Valid @RequestHeader(ProductsExpServiceConstant.X_CORRELATION_ID) String correlationId,
            @Valid @RequestBody OpenPortfolioRequest openPortfolioRequest) {
        return openPortfolioService.validateOpenPortfolio(correlationId, openPortfolioRequest);
    }
}
