package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;
import com.tmb.oneapp.productsexpservice.model.lending.document.UploadDocumentRequest;
import com.tmb.oneapp.productsexpservice.model.lending.document.UploadDocumentResponse;
import com.tmb.oneapp.productsexpservice.model.lending.loan.ProductRequest;
import com.tmb.oneapp.productsexpservice.model.lending.loan.TmbOneServiceErrorResponse;
import feign.FeignException;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.HEADER_X_CORRELATION_ID;
import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.HEADER_X_CRM_ID;

@RestController
@Api(tags = "Lending Service")
public class LendingServiceController {
    private static final TMBLogger<LendingServiceController> logger =
            new TMBLogger<>(LendingServiceController.class);
    private final LendingServiceClient lendingServiceClient;

    public LendingServiceController(LendingServiceClient lendingServiceClient) {
        this.lendingServiceClient = lendingServiceClient;
    }

    /**
     * Maps tmb one app error response
     *
     * @param optionalResponse
     * @return
     */
    public static TmbOneServiceErrorResponse mapTmbOneServiceErrorResponse(Optional<ByteBuffer> optionalResponse) {
        try {
            if (!optionalResponse.isPresent()) {
                return null;
            }

            String respBody = StandardCharsets.UTF_8.decode(optionalResponse.get()).toString();
            return (TmbOneServiceErrorResponse) TMBUtils.convertStringToJavaObj(respBody, TmbOneServiceErrorResponse.class);
        } catch (Exception e) {
            logger.error("Unexpected error received, cannot parse.");
            return null;
        }
    }

    @PostMapping(value = "/lending/get-preload-data", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<Object>> getProducts(@RequestHeader("X-Correlation-ID") String xCorrelationId, @RequestBody ProductRequest request) throws TMBCommonException {
        try {
            return lendingServiceClient.getLoanProducts(xCorrelationId, request);
        } catch (FeignException e) {
            TmbOneServiceErrorResponse response = mapTmbOneServiceErrorResponse(e.responseBody()); // NO SONAR
            if (response != null && response.getStatus() != null) {
                logger.info("Error while calling GET /apis/lending-service/loan/products. crmId: {}, code:{}, errMsg:{}",
                        request.getCrmId(), response.getStatus().getCode(), response.getStatus().getMessage());
                throw new TMBCommonException(response.getStatus().getCode(),
                        response.getStatus().getMessage(),
                        response.getStatus().getService(), HttpStatus.BAD_REQUEST, null);
            }
        }
        throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                ResponseCode.FAILED.getMessage(),
                ResponseCode.FAILED.getService(), HttpStatus.BAD_REQUEST, null);
    }

    @PostMapping(value = "/lending/document/upload", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<UploadDocumentResponse>> uploadDocument(
            @RequestHeader(HEADER_X_CORRELATION_ID) String xCorrelationId,
            @RequestHeader(HEADER_X_CRM_ID) String crmId,
            @ModelAttribute UploadDocumentRequest request) throws TMBCommonException {
        try {
            return lendingServiceClient.uploadDocument(xCorrelationId, crmId, request);
        } catch (FeignException e) {
            TmbOneServiceErrorResponse response = mapTmbOneServiceErrorResponse(e.responseBody());
            if (response != null && response.getStatus() != null) {
                logger.info("Error while calling POST /apis/lending-service/document/upload. crmId: {}, code:{}, errMsg:{}",
                        crmId, response.getStatus().getCode(), response.getStatus().getMessage());
                throw new TMBCommonException(response.getStatus().getCode(),
                        response.getStatus().getMessage(),
                        response.getStatus().getService(), HttpStatus.BAD_REQUEST, null);
            }
        }
        throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                ResponseCode.FAILED.getMessage(),
                ResponseCode.FAILED.getService(), HttpStatus.BAD_REQUEST, null);
    }
}
