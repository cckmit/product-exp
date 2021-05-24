package com.tmb.oneapp.productsexpservice.controller;


import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundSummaryBody;
import com.tmb.oneapp.productsexpservice.model.request.accdetail.FundAccountRq;
import com.tmb.oneapp.productsexpservice.model.request.alternative.AlternativeRq;
import com.tmb.oneapp.productsexpservice.model.request.fundffs.FfsRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fundlist.FundListRq;
import com.tmb.oneapp.productsexpservice.model.request.fundpayment.FundPaymentDetailRq;
import com.tmb.oneapp.productsexpservice.model.request.fundsummary.FundSummaryRq;
import com.tmb.oneapp.productsexpservice.model.response.accdetail.FundAccountRs;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FfsResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FfsRsAndValidation;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FundResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundClassListInfo;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.FundPaymentDetailRs;
import com.tmb.oneapp.productsexpservice.service.ProductsExpService;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
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
import java.util.List;


/**
 * ProductExpServiceController request mapping will handle apis call and
 * then navigate to respective method to get MF account Detail
 */
@RequestMapping("/funds")
@RestController
@Api(tags = "Get fund detail and fund rule than return to front-end")
public class ProductExpServiceController {

    private static final TMBLogger<ProductExpServiceController> logger = new TMBLogger<>(ProductExpServiceController.class);
    private final ProductsExpService productsExpService;


    /**
     * Instantiates a new Product exp service controller.
     *
     * @param productsExpService the products exp service
     */
    @Autowired
    public ProductExpServiceController(ProductsExpService productsExpService) {
        this.productsExpService = productsExpService;
    }

    /**
     * Description:- Inquiry MF Service
     *
     * @param correlationId the correlation id
     * @param fundAccountRq the fund account rq
     * @return return account full details
     */
    @ApiOperation(value = "Fetch Fund Detail based on Unit Holder No, Fund House Code And FundCode")
    @LogAround
    @PostMapping(value = "/account/detail", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<FundAccountRs>> getFundAccountDetail(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_CORRELATION_ID_DESC, defaultValue = ProductsExpServiceConstant.X_COR_ID_DEFAULT, required = true)
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_CORRELATION_ID) String correlationId,
            @Valid @RequestBody FundAccountRq fundAccountRq) {

        TmbOneServiceResponse<FundAccountRs> oneServiceResponse = new TmbOneServiceResponse<>();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
        FundAccountRs fundAccountRs = productsExpService.getFundAccountDetail(correlationId, fundAccountRq);
        if (!StringUtils.isEmpty(fundAccountRs)) {
            oneServiceResponse.setData(fundAccountRs);
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

    /**
     * Gets fund summary.
     *
     * @param correlationId the correlation id
     * @param fundSummaryRq the fund summary rq
     * @return the fund summary
     */
    @ApiOperation(value = "Fetch Fund Summary and Port List based on Unit Holder No and CRMID")
    @LogAround
    @PostMapping(value = "/summary", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<FundSummaryBody>> getFundSummary(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_CORRELATION_ID_DESC, defaultValue = ProductsExpServiceConstant.X_COR_ID_DEFAULT, required = true)
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_CORRELATION_ID) String correlationId,
            @Valid @RequestBody FundSummaryRq fundSummaryRq) {

        TmbOneServiceResponse<FundSummaryBody> oneServiceResponse = new TmbOneServiceResponse<>();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));

        FundSummaryBody fundSummaryResponse = productsExpService.getFundSummary(correlationId, fundSummaryRq);
        if (fundSummaryResponse != null) {
            oneServiceResponse.setData(fundSummaryResponse);
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

    /**
     * Description:- Inquiry MF Service
     *
     * @param correlationId       the correlation id
     * @param fundPaymentDetailRq the fund account rq
     * @return return  list of port, list of account, fund rule and list of holiday
     */
    @ApiOperation(value = "Get all payment detail info than return list of port, list of account, fund rule and list of holiday")
    @LogAround
    @PostMapping(value = "/paymentDetails", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<FundPaymentDetailRs>> getFundPrePaymentDetail(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_CORRELATION_ID_DESC, defaultValue = ProductsExpServiceConstant.X_COR_ID_DEFAULT, required = true)
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_CORRELATION_ID) String correlationId,
            @Valid @RequestBody FundPaymentDetailRq fundPaymentDetailRq) {
        TmbOneServiceResponse<FundPaymentDetailRs> oneServiceResponse = new TmbOneServiceResponse<>();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
        FundPaymentDetailRs fundPaymentDetailRs = productsExpService.getFundPrePaymentDetail(correlationId, fundPaymentDetailRq);
        if (fundPaymentDetailRs!=null) {
            oneServiceResponse.setData(fundPaymentDetailRs);
            oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                    ProductsExpServiceConstant.SUCCESS_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse);
        } else {
            return dataNotFoundError(oneServiceResponse);
        }
    }

    /**
     * @param oneServiceResponse
     * @return
     */
    ResponseEntity<TmbOneServiceResponse<FundPaymentDetailRs>> dataNotFoundError(TmbOneServiceResponse<FundPaymentDetailRs> oneServiceResponse) {
        oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.DATA_NOT_FOUND_CODE,
                ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE,
                ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE));
        oneServiceResponse.setData(null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse);
    }

    /**
     * Description:- Inquiry MF Service
     *
     * @param correlationId  the correlation id
     * @param ffsRequestBody the fund account rq
     * @return return fund sheet
     */
    @ApiOperation(value = "Validation alternative case, then return fund sheet")
    @LogAround
    @PostMapping(value = "/alternative/validation", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<FfsResponse>> getFundFFSAndValidation(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_CORRELATION_ID_DESC, defaultValue = ProductsExpServiceConstant.X_COR_ID_DEFAULT, required = true)
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_CORRELATION_ID) String correlationId,
            @Valid @RequestBody FfsRequestBody ffsRequestBody) {
        TmbOneServiceResponse<FfsResponse> oneServiceResponse = new TmbOneServiceResponse<>();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
        FfsRsAndValidation ffsRsAndValidation = null;
        try {
            String trackingStatus = ProductsExpServiceConstant.ACTIVITY_ID_INVESTMENT_STATUS_TRACKING;
            AlternativeRq alternativeRq = UtilMap.mappingRequestAlternative(ffsRequestBody);
            if (ProductsExpServiceConstant.PROCESS_FLAG_Y.equals(ffsRequestBody.getProcessFlag())) {
                ffsRsAndValidation = productsExpService.getFundFFSAndValidation(correlationId, ffsRequestBody);
                if (ffsRsAndValidation.isError()) {
                    productsExpService.logactivity(productsExpService.constructActivityLogDataForBuyHoldingFund(correlationId,
                            ProductsExpServiceConstant.ACTIVITY_TYPE_INVESTMENT_STATUS_TRACKING,
                            trackingStatus, alternativeRq));

                    oneServiceResponse.setStatus(new TmbStatus(ffsRsAndValidation.getErrorCode(),
                            ffsRsAndValidation.getErrorMsg(),
                            ProductsExpServiceConstant.SERVICE_NAME, ffsRsAndValidation.getErrorDesc()));
                    oneServiceResponse.setData(null);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse);
                } else {
                    productsExpService.logactivity(productsExpService.constructActivityLogDataForBuyHoldingFund(correlationId,
                            ProductsExpServiceConstant.ACTIVITY_TYPE_INVESTMENT_STATUS_TRACKING, trackingStatus, alternativeRq));

                    FfsResponse ffsResponse = new FfsResponse();
                    ffsResponse.setBody(ffsRsAndValidation.getBody());
                    oneServiceResponse.setData(ffsResponse);
                    oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                            ProductsExpServiceConstant.SUCCESS_MESSAGE,
                            ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
                    return ResponseEntity.status(HttpStatus.OK).headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse);
                }
            } else {
                productsExpService.logactivity(productsExpService.constructActivityLogDataForBuyHoldingFund(correlationId,
                        ProductsExpServiceConstant.ACTIVITY_TYPE_INVESTMENT_STATUS_TRACKING, trackingStatus, alternativeRq));

                oneServiceResponse.setData(null);
                oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.BUSINESS_HOURS_CLOSE_CODE,
                        ProductsExpServiceConstant.BUSINESS_HOURS_CLOSE_MESSAGE,
                        ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.BUSINESS_HOURS_CLOSE_DESC));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse);
            }
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            oneServiceResponse.setData(null);
            oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.DATA_NOT_FOUND_CODE,
                    ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse);

        }
    }

    /**
     * Description:- Inquiry MF Service
     *
     * @param correlationId the correlation id
     * @param alternativeRq the fund alternative case rq
     * @return return fund sheet
     */
    @ApiOperation(value = "Validation alternative case for Sale and Switch")
    @LogAround
    @PostMapping(value = "/alternative/sellAndSwitch", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<FundResponse>> validateAlternativeSellAndSwitch(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_CORRELATION_ID_DESC, defaultValue = ProductsExpServiceConstant.X_COR_ID_DEFAULT, required = true)
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_CORRELATION_ID) String correlationId,
            @Valid @RequestBody AlternativeRq alternativeRq) {
        TmbOneServiceResponse<FundResponse> oneServiceResponse = new TmbOneServiceResponse<>();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
        FundResponse fundResponse = null;
        try {
            fundResponse = productsExpService.validateAlternativeSellAndSwitch(correlationId, alternativeRq);
            if (fundResponse.isError()) {

                return errorResponse(oneServiceResponse, fundResponse);
            } else {

                oneServiceResponse.setData(fundResponse);
                oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                        ProductsExpServiceConstant.SUCCESS_MESSAGE,
                        ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
                return ResponseEntity.status(HttpStatus.OK).headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse);
            }

        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            oneServiceResponse.setData(null);
            oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.DATA_NOT_FOUND_CODE,
                    ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse);

        }
    }



    ResponseEntity<TmbOneServiceResponse<FundResponse>> errorResponse(TmbOneServiceResponse<FundResponse> oneServiceResponse, FundResponse fundResponse) {
        oneServiceResponse.setStatus(new TmbStatus(fundResponse.getErrorCode(),
                fundResponse.getErrorMsg(),
                ProductsExpServiceConstant.SERVICE_NAME, fundResponse.getErrorDesc()));
        oneServiceResponse.setData(null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse);
    }

    /**
     * Description:- Inquiry MF Service
     *
     * @param correlationId the correlation id
     * @return return fund list info
     */
    @ApiOperation(value = "Fetch Fund list from MF Service and add more flag, then return to front-end")
    @LogAround
    @PostMapping(value = "/search/fundList", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<List<FundClassListInfo>>> getFundListInfo(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_CORRELATION_ID_DESC, defaultValue = ProductsExpServiceConstant.X_COR_ID_DEFAULT, required = true)
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_CORRELATION_ID) String correlationId,
            @Valid @RequestBody FundListRq fundListRq) {

        TmbOneServiceResponse<List<FundClassListInfo>> oneServiceResponse = new TmbOneServiceResponse<>();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
        try {
            List<FundClassListInfo> fundAccountRs = productsExpService.getFundList(correlationId, fundListRq);
            if (!StringUtils.isEmpty(fundAccountRs)) {
                oneServiceResponse.setData(fundAccountRs);
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

        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            oneServiceResponse.setData(null);
            oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.DATA_NOT_FOUND_CODE,
                    ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE,
                    ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse);

        }
    }


}
