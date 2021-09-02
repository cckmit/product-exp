package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.loan.stagingbar.LoanStagingbar;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;
import com.tmb.oneapp.productsexpservice.model.lending.document.UploadDocumentResponse;
import com.tmb.oneapp.productsexpservice.model.lending.loan.LoanStagingbarRequest;
import com.tmb.oneapp.productsexpservice.model.lending.loan.ProductDetailRequest;
import com.tmb.oneapp.productsexpservice.model.lending.loan.ProductDetailResponse;
import com.tmb.oneapp.productsexpservice.model.lending.loan.ProductRequest;
import com.tmb.oneapp.productsexpservice.model.lending.loan.TmbOneServiceErrorResponse;
import feign.FeignException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
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

    @PostMapping(value = "/lending/document/upload")
    public ResponseEntity<TmbOneServiceResponse<UploadDocumentResponse>> uploadDocument(
            @ApiParam(value = HEADER_X_CORRELATION_ID, defaultValue = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da", required = true)
            @RequestHeader(HEADER_X_CORRELATION_ID) String xCorrelationId,
            @ApiParam(value = HEADER_X_CRM_ID, defaultValue = "001100000000000000000018593707", required = true)
            @RequestHeader(HEADER_X_CRM_ID) String crmId,
            @ApiParam(value = "file", required = true) @Valid @RequestPart MultipartFile file,
            @ApiParam(value = "caId", required = true) @Valid @RequestPart String caId,
            @ApiParam(value = "docCode", required = true) @Valid @RequestPart String docCode) throws TMBCommonException {
        try {
            return lendingServiceClient.uploadDocument(xCorrelationId, crmId, file, caId, docCode);
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
    
	@PostMapping(value = "/lending/get-product-orientation", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TmbOneServiceResponse<ProductDetailResponse>> getProductOrientation(
			@RequestHeader(HEADER_X_CORRELATION_ID) String xCorrelationId, @RequestHeader(HEADER_X_CRM_ID) String crmId,
			@RequestBody ProductDetailRequest request) throws TMBCommonException {
		try {
			return lendingServiceClient.fetchProductOrientation(xCorrelationId, crmId, request);
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
	
	@PostMapping(value = "/lending/loan/get-staging-bar", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TmbOneServiceResponse<LoanStagingbar>> fetchLoanStagingBar(
			@RequestHeader(HEADER_X_CORRELATION_ID) String xCorrelationId, @RequestHeader(HEADER_X_CRM_ID) String crmId,
			@RequestBody LoanStagingbarRequest request) throws TMBCommonException {
		try {
			return lendingServiceClient.fetchLoanStagingBar(xCorrelationId, crmId, request);
		} catch (FeignException e) {
			TmbOneServiceErrorResponse response = mapTmbOneServiceErrorResponse(e.responseBody());
			if (response != null && response.getStatus() != null) {
				logger.info(
						"Error while calling POST /apis/lending-service/loan/get-staging-bar crmId: {}, code:{}, errMsg:{}",
						crmId, response.getStatus().getCode(), response.getStatus().getMessage());
				throw new TMBCommonException(response.getStatus().getCode(), response.getStatus().getMessage(),
						response.getStatus().getService(), HttpStatus.BAD_REQUEST, null);
			}
		}
		throw new TMBCommonException(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
				ResponseCode.FAILED.getService(), HttpStatus.BAD_REQUEST, null);
	}
}
