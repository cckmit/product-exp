package com.tmb.oneapp.productsexpservice.controller;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.HEADER_X_CORRELATION_ID;
import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.HEADER_X_CRM_ID;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.legacy.rsl.ws.instant.transfer.request.Body;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;
import com.tmb.oneapp.productsexpservice.model.lending.document.*;
import com.tmb.oneapp.productsexpservice.model.lending.loan.ProductDetailRequest;
import com.tmb.oneapp.productsexpservice.model.lending.loan.ProductDetailResponse;
import com.tmb.oneapp.productsexpservice.model.lending.loan.ProductRequest;
import com.tmb.oneapp.productsexpservice.model.lending.loan.TmbOneServiceErrorResponse;
import com.tmb.oneapp.productsexpservice.model.request.TransferApplicationRequest;
import com.tmb.oneapp.productsexpservice.service.LoanService;
import feign.FeignException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "Lending Service")
public class LendingServiceController {
    private static final TMBLogger<LendingServiceController> logger =
            new TMBLogger<>(LendingServiceController.class);
    private final LendingServiceClient lendingServiceClient;
    private final LoanService loanService;

    public LendingServiceController(LendingServiceClient lendingServiceClient, LoanService loanService) {
        this.lendingServiceClient = lendingServiceClient;
        this.loanService = loanService;
    }

    /**
     * Maps tmb one app error response
     *
     * @param optionalResponse
     * @return
     */
    public static TmbOneServiceErrorResponse mapTmbOneServiceErrorResponse(Optional<ByteBuffer> optionalResponse) {
        try {
            if (optionalResponse.isEmpty()) {
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
    public ResponseEntity<TmbOneServiceResponse<Object>> getProducts(@RequestHeader(HEADER_X_CORRELATION_ID) String xCorrelationId, @RequestBody ProductRequest request) throws TMBCommonException {
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

    @PostMapping(value = "/lending/get-product-orientation", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<ProductDetailResponse>> getProductOrientation(
            @RequestHeader(HEADER_X_CORRELATION_ID) String xCorrelationId, @RequestHeader(HEADER_X_CRM_ID) String crmId,
            @RequestBody ProductDetailRequest request) throws TMBCommonException {
        try {
			ResponseEntity<TmbOneServiceResponse<ProductDetailResponse>> response = loanService
					.fetchProductOrientation(xCorrelationId, crmId, request);
			logger.info("Success while calling POST /apis/lending-service/loan/product-orientation. :{}", response);
			return response;
        } catch (FeignException e) {
            TmbOneServiceErrorResponse response = mapTmbOneServiceErrorResponse(e.responseBody());
            if (response != null && response.getStatus() != null) {
                logger.info(
                        "Error while calling POST /apis/lending-service/loan/product-orientation. crmId: {}, code:{}, errMsg:{}",
                        crmId, response.getStatus().getCode(), response.getStatus().getMessage());
                throw new TMBCommonException(response.getStatus().getCode(), response.getStatus().getMessage(),
                        response.getStatus().getService(), HttpStatus.BAD_REQUEST, null);
            }
        }
        throw new TMBCommonException(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                ResponseCode.FAILED.getService(), HttpStatus.BAD_REQUEST, null);
    }

    @PostMapping(value = "/lending/transfer-application", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<com.tmb.common.model.legacy.rsl.ws.instant.transfer.response.ResponseTransfer>> transferApplication(
            @RequestHeader(HEADER_X_CORRELATION_ID) String xCorrelationId, @RequestHeader(HEADER_X_CRM_ID) String crmId,
            @RequestBody TransferApplicationRequest request) throws TMBCommonException {
        try {
            com.tmb.common.model.legacy.rsl.ws.instant.transfer.request.Body body = new Body();
            body.setCaId(new BigDecimal(request.getCaId()));
            return lendingServiceClient.transferApplication(xCorrelationId, crmId, body);
        } catch (FeignException e) {
            TmbOneServiceErrorResponse response = mapTmbOneServiceErrorResponse(e.responseBody());
            if (response != null && response.getStatus() != null) {
                logger.info(
                        "Error while calling POST /apis/lending-service/rsl/LoanSubmissionInstantLoanTransferApplication. crmId: {}, code:{}, errMsg:{}",
                        crmId, response.getStatus().getCode(), response.getStatus().getMessage());
                throw new TMBCommonException(response.getStatus().getCode(), response.getStatus().getMessage(),
                        response.getStatus().getService(), HttpStatus.BAD_REQUEST, null);
            }
        }
        throw new TMBCommonException(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                ResponseCode.FAILED.getService(), HttpStatus.BAD_REQUEST, null);
    }

    @PostMapping(value = "/lending/document/upload")
    public ResponseEntity<TmbOneServiceResponse<UploadDocumentResponse>> uploadDocument(
            @ApiParam(value = HEADER_X_CORRELATION_ID, defaultValue = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da", required = true)
            @RequestHeader(HEADER_X_CORRELATION_ID) String xCorrelationId,
            @ApiParam(value = HEADER_X_CRM_ID, defaultValue = "001100000000000000000018593707", required = true)
            @RequestHeader(HEADER_X_CRM_ID) String crmId,
            @Valid @RequestBody UploadDocumentRequest request) throws TMBCommonException {
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

    @PostMapping(value = "/lending/document/submit")
    public ResponseEntity<TmbOneServiceResponse<SubmitDocumentResponse>> submitDocument(
            @ApiParam(value = HEADER_X_CORRELATION_ID, defaultValue = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da", required = true)
            @RequestHeader(HEADER_X_CORRELATION_ID) String xCorrelationId,
            @ApiParam(value = HEADER_X_CRM_ID, defaultValue = "001100000000000000000018593707", required = true)
            @RequestHeader(HEADER_X_CRM_ID) String crmId,
            @Valid @RequestBody SubmitDocumentRequest request) throws TMBCommonException {
        try {
            return lendingServiceClient.submitDocument(xCorrelationId, crmId, request);
        } catch (FeignException e) {
            TmbOneServiceErrorResponse response = mapTmbOneServiceErrorResponse(e.responseBody());
            if (response != null && response.getStatus() != null) {
                logger.info("Error while calling POST /apis/lending-service/document/submit. crmId: {}, code:{}, errMsg:{}",
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

    @PostMapping(value = "/lending/document/submit/more")
    public ResponseEntity<TmbOneServiceResponse<SubmitDocumentResponse>> submitMoreDocument(
            @ApiParam(value = HEADER_X_CORRELATION_ID, defaultValue = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da", required = true)
            @RequestHeader(HEADER_X_CORRELATION_ID) String xCorrelationId,
            @ApiParam(value = HEADER_X_CRM_ID, defaultValue = "001100000000000000000018593707", required = true)
            @RequestHeader(HEADER_X_CRM_ID) String crmId,
            @Valid @RequestBody SubmitDocumentRequest request) throws TMBCommonException {
        try {
            return lendingServiceClient.submitMoreDocument(xCorrelationId, crmId, request);
        } catch (FeignException e) {
            TmbOneServiceErrorResponse response = mapTmbOneServiceErrorResponse(e.responseBody());
            if (response != null && response.getStatus() != null) {
                logger.info("Error while calling POST /apis/lending-service/document/submit/more. crmId: {}, code:{}, errMsg:{}",
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

    @DeleteMapping(value = "/lending/document/{caId}/{docCode}/{fileType}/{fileName}")
    public ResponseEntity<TmbOneServiceResponse<DeleteDocumentResponse>> deleteDocument(
            @ApiParam(value = HEADER_X_CORRELATION_ID, defaultValue = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da", required = true)
            @RequestHeader(HEADER_X_CORRELATION_ID) String xCorrelationId,
            @ApiParam(value = HEADER_X_CRM_ID, defaultValue = "001100000000000000000018593707", required = true)
            @RequestHeader(HEADER_X_CRM_ID) String crmId,
            @ApiParam(value = "caId", required = true)
            @Valid @PathVariable("caId") String caId,
            @ApiParam(value = "docCode", required = true)
            @Valid @PathVariable("docCode") String docCode,
            @ApiParam(value = "fileType", required = true)
            @Valid @PathVariable("fileType") String fileType,
            @ApiParam(value = "fileName", required = true)
            @Valid @PathVariable("fileName") String fileName) throws TMBCommonException {
        try {
            return lendingServiceClient.deleteDocument(xCorrelationId, crmId, caId, docCode, fileType, fileName);
        } catch (FeignException e) {
            TmbOneServiceErrorResponse response = mapTmbOneServiceErrorResponse(e.responseBody());
            if (response != null && response.getStatus() != null) {
                logger.info("Error while calling DELETE /apis/lending-service/document. crmId: {}, code:{}, errMsg:{}",
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
