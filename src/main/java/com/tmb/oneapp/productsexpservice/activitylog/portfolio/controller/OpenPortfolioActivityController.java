package com.tmb.oneapp.productsexpservice.activitylog.portfolio.controller;

import com.tmb.common.logger.LogAround;
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
     * Description:- method to save activity service when click confirm button
     *
     * @param correlationId                   the correlation id
     * @param crmId                           the crm id
     * @param ipAddress                       the ip address
     * @param openPortfolioActivityLogRequest the open portfolio activity log request
     * @return return success status
     */
    @ApiOperation(value = "Response success status of saving activity log")
    @PostMapping(value = "/open/portfolio/click-confirm")
    public ResponseEntity<TmbOneServiceResponse<String>> clickConfirm(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_CORRELATION_ID_DESC, defaultValue = ProductsExpServiceConstant.X_COR_ID_DEFAULT, required = true)
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId,
            @Valid @RequestHeader(ProductsExpServiceConstant.X_FORWARD_FOR) String ipAddress,
            @Valid @RequestBody OpenPortfolioActivityLogRequest openPortfolioActivityLogRequest) {
        TmbOneServiceResponse<String> oneServiceResponse = new TmbOneServiceResponse<>();

        openPortfolioActivityLogService.clickConfirm(correlationId, crmId, ipAddress, openPortfolioActivityLogRequest);
        oneServiceResponse.setStatus(buildStatusResponse(ProductsExpServiceConstant.SUCCESS_CODE, ProductsExpServiceConstant.SUCCESS_MESSAGE));
        oneServiceResponse.setData("Update activity log is successfully");
        return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse);
    }

    @LogAround
    private TmbStatus buildStatusResponse(String successCode, String successMessage) {
        return new TmbStatus(successCode, successMessage, ProductsExpServiceConstant.SERVICE_NAME, successMessage);
    }
}
