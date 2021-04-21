package com.tmb.oneapp.productsexpservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.ProductConfig;
import feign.FeignException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * FetchProductConfigListController request mapping will handle apis call and then
 * navigate to respective method
 */
@RestController
@Api(tags = "Fetch Product Config Api")
public class FetchProductConfigController {
    private static final TMBLogger<FetchProductConfigController> logger = new TMBLogger<>(FetchProductConfigController.class);
    private final CommonServiceClient commonServiceClient;

    /**
     * Constructor
     *
     * @param commonServiceClient
     */
    @Autowired
    public FetchProductConfigController(CommonServiceClient commonServiceClient) {
        this.commonServiceClient = commonServiceClient;
    }

    /**
     * @param correlationId
     * @return ProductConfig list from mongo db
     */
    @LogAround
    @GetMapping(value = "/fetch/product-config")
    @ApiImplicitParams({
            @ApiImplicitParam(name = ProductsExpServiceConstant.X_CORRELATION_ID, value = "Correlation Id", required = true, dataType = "string", paramType = "header", example = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da")})

    public ResponseEntity<TmbOneServiceResponse<List<ProductConfig>>> getProductConfigList(
            @RequestHeader(ProductsExpServiceConstant.X_CORRELATION_ID) final String correlationId) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
        TmbOneServiceResponse<List<ProductConfig>> oneServiceResponse = new TmbOneServiceResponse<>();
        try {


            ResponseEntity<TmbOneServiceResponse<List<ProductConfig>>> fetchProductConfigList = commonServiceClient
                    .getProductConfig(correlationId);
            if (fetchProductConfigList != null && fetchProductConfigList.getBody() != null) {

                oneServiceResponse.setData(fetchProductConfigList.getBody().getData());
                oneServiceResponse
                        .setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                                ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
                return ResponseEntity.ok().headers(responseHeaders).body(oneServiceResponse);
            } else {
                oneServiceResponse.setStatus(new TmbStatus(ResponseCode.GENERAL_ERROR.getCode(),
                        ResponseCode.GENERAL_ERROR.getMessage(), ResponseCode.GENERAL_ERROR.getService()));

                return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
            }
        } catch (Exception e) {
            logger.error("Unable to fetch reason list : {}", e);
            oneServiceResponse.setStatus(new TmbStatus(ResponseCode.GENERAL_ERROR.getCode(),
                    ResponseCode.GENERAL_ERROR.getMessage(), ResponseCode.GENERAL_ERROR.getService()));
            return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
        }

    }

    /**
     * @param correlationId
     * @param ekycFlag
     * @return ProductConfig list from mongo db
     * @throws TMBCommonException
     * @throws JsonProcessingException
     * @throws UnsupportedEncodingException
     */
    @LogAround
    @GetMapping(value = "/filter/{ekycFlag}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = ProductsExpServiceConstant.X_CORRELATION_ID, value = "Correlation Id", required = true, dataType = "string", paramType = "header", example = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da"),
            @ApiImplicitParam(name = "Open eKyc filter", value = "eKyc flag", required = true, dataType = "string", paramType = "param", example = "1")})

    public ResponseEntity<TmbOneServiceResponse<List<ProductConfig>>> getProductConfigListByEKYCFilter(
            @RequestHeader(ProductsExpServiceConstant.X_CORRELATION_ID) final String correlationId,
            @PathVariable String ekycFlag
    ) throws  JsonProcessingException, TMBCommonException {
        TmbOneServiceResponse<List<ProductConfig>> oneServiceResponse = new TmbOneServiceResponse<>();
        HttpHeaders responseHeaders = this.getResponseHeaders();
        logStartController("/filter/ekycFlag", correlationId);
        logger.info("Request param {}", ekycFlag);
        try {


            List<ProductConfig> productConfigList = getProductConfig(correlationId);
            Predicate<ProductConfig> byEKYC = product -> product.getOpenEKyc() != null && product.getOpenEKyc().equals(ekycFlag);
            var result = productConfigList.stream().filter(byEKYC)
                    .collect(Collectors.toList());
            oneServiceResponse.setData(result);
            oneServiceResponse
                    .setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                            ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
        } catch (FeignException e) {
            throw handleFeignException(e);
        } catch (Exception e) {
            oneServiceResponse.setStatus(getResponseFail(e));
            return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
        } finally {
            logFinallyController("/filter/ekycFlag", oneServiceResponse);
        }
        return ResponseEntity.ok().headers(responseHeaders).body(oneServiceResponse);

    }

    private void logStartController(String route, String correlationid) {
        logger.info("########### Start api name : {} ###########", route);
        logger.info("Header correlationid: {}", correlationid);
    }

    private <T> void logFinallyController(String route, TmbOneServiceResponse<T> response) {
        logger.info("Response {} : {} ", route, response);
        logger.info("########### End api name : {} ###########", route);
    }

    @SuppressWarnings("unchecked")
    private <T> TmbServiceResponse<T> exceptionHandling(final FeignException ex)
            throws JsonProcessingException {
        TmbServiceResponse<T> data = new TmbServiceResponse<>();
        Optional<ByteBuffer> response = ex.responseBody();
        if (response.isPresent()) {
            ByteBuffer responseBuffer = response.get();
            String responseObj = new String(responseBuffer.array(), StandardCharsets.UTF_8);
            logger.info("response fail {}", responseObj);
            data = ((TmbServiceResponse<T>) TMBUtils.convertStringToJavaObj(responseObj,
                    TmbServiceResponse.class));
        }
        return data;

    }

    /**
     * @param e
     * @return
     * @throws UnsupportedEncodingException
     * @throws JsonProcessingException
     */
    private TMBCommonException handleFeignException(FeignException e) throws  JsonProcessingException {
        logger.error("Exception in {} :{}", e.getClass().getName(), e.toString());
        if (e instanceof FeignException.BadRequest) {
            TmbServiceResponse<String> body = exceptionHandling(e);
            return new TMBCommonException(
                    body.getStatus().getCode(),
                    body.getStatus().getMessage(),
                    ResponseCode.FAILED.getService(), HttpStatus.BAD_REQUEST, null);
        }

        return new TMBCommonException(ResponseCode.ETE_SERVICE_ERROR.getCode(),
                ResponseCode.ETE_SERVICE_ERROR.getMessage(), ResponseCode.ETE_SERVICE_ERROR.getService(),
                HttpStatus.BAD_REQUEST, null);
    }

    /**
     * @param e
     * @return
     */
    private TmbStatus getResponseFail(Exception e) {
        logger.error("Exception :{}", e.toString());
        return new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                ResponseCode.FAILED.getService());
    }

    /**
     * @return
     */
    private HttpHeaders getResponseHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
        return responseHeaders;
    }

    /**
     * @param correlationId
     * @return
     */
    private List<ProductConfig> getProductConfig(String correlationId) {
        logger.info("========== Start call service name : common service ==========");
        try {
            logger.info("Request:");
            var data = commonServiceClient.getProductConfig(correlationId);
            logger.info("Response product count : {} ", data.getBody().getData().size());
            return data.getBody().getData();
        } catch (Exception e) {
            logger.error("Exception in common service :{}", e.toString());
            throw e;
        } finally {
            logger.info("========== End call service name : common service ==========");
        }

    }
}