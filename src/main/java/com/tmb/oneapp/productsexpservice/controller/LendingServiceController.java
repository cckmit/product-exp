package com.tmb.oneapp.productsexpservice.controller;

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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.HEADER_X_CORRELATION_ID;
import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.HEADER_X_CRM_ID;

@RestController
@RequiredArgsConstructor
@Api(tags = "Lending Service")
public class LendingServiceController {
    private static final TMBLogger<LendingServiceController> logger = new TMBLogger<>(LendingServiceController.class);
    private final LendingServiceClient lendingServiceClient;
    private final LoanService loanService;

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
            ResponseEntity<TmbOneServiceResponse<Object>> getLoanProductsResp = lendingServiceClient.getLoanProducts(xCorrelationId, request);
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(getLoanProductsResp.getBody());
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
            ResponseEntity<TmbOneServiceResponse<ProductDetailResponse>> getProductOrientationResp = loanService
                    .fetchProductOrientation(xCorrelationId, crmId, request);
            logger.info(
                    "Success while calling POST /apis/lending-service/loan/product-orientation. response code:{} body :{}",
                    getProductOrientationResp.getStatusCode(), getProductOrientationResp.getBody().getData().toString());
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(getProductOrientationResp.getBody());

        } catch (FeignException e) {
            TmbOneServiceErrorResponse response = mapTmbOneServiceErrorResponse(e.responseBody());
            if (response != null && response.getStatus() != null) {
                logger.info(
                        "Error while calling POST /apis/lending-service/loan/product-orientation. crmId: {}, code:{}, errMsg:{}",
                        crmId, response.getStatus().getCode(), response.getStatus().getMessage());
                throw new TMBCommonException(response.getStatus().getCode(), response.getStatus().getMessage(),
                        response.getStatus().getService(), HttpStatus.BAD_REQUEST, null);
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
            throw new TMBCommonException(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(), HttpStatus.BAD_REQUEST, null);
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
            ResponseEntity<TmbOneServiceResponse<com.tmb.common.model.legacy.rsl.ws.instant.transfer.response.ResponseTransfer>> transferApplicationResp = lendingServiceClient.transferApplication(xCorrelationId, crmId, body);
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(transferApplicationResp.getBody());
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
            ResponseEntity<TmbOneServiceResponse<UploadDocumentResponse>> uploadDocumentResp = lendingServiceClient.uploadDocument(xCorrelationId, crmId, request);
            logger.info(
                    "Success while calling POST /apis/lending-service/document/upload. response code:{} body :{}",
                    uploadDocumentResp.getStatusCode(), uploadDocumentResp.getBody().getData().toString());
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(uploadDocumentResp.getBody());

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
            ResponseEntity<TmbOneServiceResponse<SubmitDocumentResponse>> submitDocumentResp = lendingServiceClient.submitDocument(xCorrelationId, crmId, request);
            logger.info(
                    "Success while calling POST /apis/lending-service/document/submit. response code:{} body :{}",
                    submitDocumentResp.getStatusCode(), submitDocumentResp.getBody().getData().toString());
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(submitDocumentResp.getBody());
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
            ResponseEntity<TmbOneServiceResponse<SubmitDocumentResponse>> submitMoreDocumentResp = lendingServiceClient.submitMoreDocument(xCorrelationId, crmId, request);
            logger.info(
                    "Success while calling POST /apis/lending-service/document/submit/more. response code:{} body :{}",
                    submitMoreDocumentResp.getStatusCode(), submitMoreDocumentResp.getBody().getData().toString());
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(submitMoreDocumentResp.getBody());
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
            ResponseEntity<TmbOneServiceResponse<DeleteDocumentResponse>> deleteDocumentResp = lendingServiceClient.deleteDocument(xCorrelationId, crmId, caId, docCode, fileType, fileName);
            logger.info(
                    "Success while calling DELETE /apis/lending-service/document. response code:{} body :{}",
                    deleteDocumentResp.getStatusCode(), deleteDocumentResp.getBody().getData().toString());
            return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(deleteDocumentResp.getBody());
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
