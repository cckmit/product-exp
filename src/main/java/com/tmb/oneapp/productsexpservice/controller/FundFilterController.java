package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.FundListBySuitScoreBody;
import com.tmb.oneapp.productsexpservice.model.FundListBySuitScoreRequest;
import com.tmb.oneapp.productsexpservice.service.FundFilterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;

/**
 * FundFilterController request will handle to call apis for filtering the List Fund Data from Investment Service
 */
@Api(tags=" Get FilteredFund List based on SuitScore")
@RestController
public class  FundFilterController{

    private static final TMBLogger<FundFilterController> logger = new TMBLogger<>(FundFilterController.class);

    private final FundFilterService fundFilterService;

    /**
     * Instantiates a new Fund Filter Controller.
     * @param fundFilterService the Fund Filter Service
     */
    @Autowired
    public FundFilterController(FundFilterService fundFilterService) {
        this.fundFilterService = fundFilterService;
    }

    /**
     * Description:- Inquiry MF Service
     *
     * @param correlationId      the correlation id
     * @param fundListRq the fund List Rq
     * @return return fund list based on suitability score
     */
    @LogAround
    @ApiOperation(value = "Fetch Fund List based on suitability score")
    @PostMapping(value = "/fundsBySuitScore", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<FundListBySuitScoreBody>> getFundListBySuitScore(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_CORRELATION_ID_DESC,
                    defaultValue = ProductsExpServiceConstant.X_COR_ID_DEFAULT, required = true)
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @Valid @RequestBody FundListBySuitScoreRequest fundListRq) {
        TmbOneServiceResponse<FundListBySuitScoreBody> oneServiceResponse= new TmbOneServiceResponse<>();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
        FundListBySuitScoreBody fundListResponse = fundFilterService.getFundListBySuitScore(correlationId, fundListRq);
        boolean y= fundListResponse.getFundClassList().isEmpty();
        if (!y) {
            oneServiceResponse.setData(fundListResponse);
            oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse);
        } else {
            oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.DATA_NOT_FOUND_CODE,
                    ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE));
            oneServiceResponse.setData(null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse);
        }

    }
}
