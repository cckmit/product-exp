package com.tmb.oneapp.productsexpservice.activitylog.portfolio.controller;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.activitylog.portfolio.request.OpenPortfolioActivityLogRequest;
import com.tmb.oneapp.productsexpservice.activitylog.portfolio.service.OpenPortfolioActivityLogService;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.*;

@Api(tags = "Update the activity log that belong to open portfolio")
@RequestMapping("/activities")
@RestController
public class OpenPortfolioActivityController {

    private OpenPortfolioActivityLogService openPortfolioActivityLogService;

    @Autowired
    public OpenPortfolioActivityController(OpenPortfolioActivityLogService openPortfolioActivityLogService) {
        this.openPortfolioActivityLogService = openPortfolioActivityLogService;
    }

    /**
     * Description:- Inquiry activity service
     *
     * @param correlationId                   the correlation id
     * @param correlationId                   the crm id
     * @param openPortfolioActivityLogRequest the open portfolio activity log request
     * @return return success status
     */
    @ApiOperation(value = "Response success status of saving activity log")
    @PostMapping(value = "/open/portfolio/click-confirm")
    public ResponseEntity<TmbOneServiceResponse<String>> clickConfirm(
            @ApiParam(value = HEADER_CORRELATION_ID_DESC, defaultValue = ProductsExpServiceConstant.X_COR_ID_DEFAULT, required = true)
            @Valid @RequestHeader(X_CORRELATION_ID) String correlationId,
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_CRM_ID) String crmId,
            @Valid @RequestBody OpenPortfolioActivityLogRequest openPortfolioActivityLogRequest) {
        TmbOneServiceResponse<String> oneServiceResponse = new TmbOneServiceResponse<>();

        openPortfolioActivityLogService.clickConfirm(correlationId, crmId, openPortfolioActivityLogRequest);
        oneServiceResponse.setStatus(buildStatusResponse(SUCCESS_CODE, SUCCESS_MESSAGE));
        oneServiceResponse.setData("Update activity log is successfully");
        return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse);
    }

    private TmbStatus buildStatusResponse(String successCode, String successMessage) {
        return new TmbStatus(successCode, successMessage, ProductsExpServiceConstant.SERVICE_NAME, successMessage);
    }
}
