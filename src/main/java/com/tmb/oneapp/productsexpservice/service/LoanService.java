package com.tmb.oneapp.productsexpservice.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.loan.stagingbar.LoanStagingbar;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;
import com.tmb.oneapp.productsexpservice.model.lending.loan.FlowType;
import com.tmb.oneapp.productsexpservice.model.lending.loan.LoanStagingbarRequest;
import com.tmb.oneapp.productsexpservice.model.lending.loan.ProductDetailRequest;
import com.tmb.oneapp.productsexpservice.model.lending.loan.ProductDetailResponse;

import lombok.AllArgsConstructor;

/**
 * Provides service to fetchProductOrientation
 */
@Service
@AllArgsConstructor
public class LoanService {

	private static final TMBLogger<LoanService> logger = new TMBLogger<>(LoanService.class);
	private final LendingServiceClient lendingServiceClient;
	private final LoanStagingBarService loanStagingBarService;

	public ResponseEntity<TmbOneServiceResponse<ProductDetailResponse>> fetchProductOrientation(String correlationId,
			String crmId, ProductDetailRequest request) throws TMBCommonException {
		try {
			ResponseEntity<TmbOneServiceResponse<ProductDetailResponse>> productDetailResponse = lendingServiceClient
					.fetchProductOrientation(correlationId, crmId, request);

			if (productDetailResponse.getBody().getData() == null) {
				String errorMessage = String.format("[%s] %s", productDetailResponse.getBody().getStatus().getCode(),
						productDetailResponse.getBody().getStatus().getMessage());
				throw new TMBCommonException(ResponseCode.DATA_NOT_FOUND_ERROR.getCode(), errorMessage,
						ResponseCode.DATA_NOT_FOUND_ERROR.getService(), HttpStatus.INTERNAL_SERVER_ERROR, null);
			}

			LoanStagingbar loanStagingbarRes = processMappingStagingBarResponse(correlationId, crmId,
					productDetailResponse.getBody().getData());
			productDetailResponse.getBody().getData().setLoanStagingBar(loanStagingbarRes);
			return productDetailResponse;

		} catch (Exception e) {
			logger.error(e.toString(), e);
			throw e;
		}
	}

	private LoanStagingbar processMappingStagingBarResponse(String correlationId, String crmId,
			ProductDetailResponse data) throws TMBCommonException {
		LoanStagingbarRequest request = new LoanStagingbarRequest();
		if (FlowType.FLEXI == data.getFlowType()) {
			request.setLoanType(ProductsExpServiceConstant.FLEXI);
		} else {
			request.setLoanType(ProductsExpServiceConstant.LOAN_SUBMISSION);
		}
		String lowerCaseProductCode = data.getProductCode().toLowerCase();
		if (lowerCaseProductCode.contains(ProductsExpServiceConstant.C2G)) {
			request.setProductHeaderKey(ProductsExpServiceConstant.APPLY_PERSONAL_LOAN);
		} else if (lowerCaseProductCode.contains(ProductsExpServiceConstant.RC)) {
			request.setProductHeaderKey(ProductsExpServiceConstant.APPLY_FLASH_CARD);
		} else {
			request.setProductHeaderKey(ProductsExpServiceConstant.APPLY_CREDIT_CARD);
		}

		LoanStagingbar loanStagingbarRes;
		loanStagingbarRes = loanStagingBarService.fetchLoanStagingBar(correlationId, crmId, request);
		if (loanStagingbarRes == null) {
			loanStagingbarRes = new LoanStagingbar();
		}
		if (data.getContinueApplyNextStep() != null) {
			switch (data.getContinueApplyNextStep()) {
			case PERSONAL:
				loanStagingbarRes.setCurrentStep(ProductsExpServiceConstant.PERSONAL_DETAIL);
				break;
			case WORKING:
			case INCOME:
				loanStagingbarRes.setCurrentStep(ProductsExpServiceConstant.WORK_DETAIL);
				break;
			case UPLOAD_DOC:
				loanStagingbarRes.setCurrentStep(ProductsExpServiceConstant.UPLOAD_DOCUMENTS);
				break;
			case CASH_TRANSFER_DAY1:
				loanStagingbarRes.setCurrentStep(ProductsExpServiceConstant.LOAN_DAY1);
				break;
			case FINAL_APPROVE_LOAN_CONFIRMATION:
				loanStagingbarRes.setCurrentStep(ProductsExpServiceConstant.FINAL_APPROVAL);
				break;
			case CONFIRMATION:
				loanStagingbarRes.setCurrentStep(ProductsExpServiceConstant.CONFIRM_APPLICATION);
				break;
			default:
				loanStagingbarRes.setCurrentStep(ProductsExpServiceConstant.LOAN_CAL);
			}
		} else {
			loanStagingbarRes.setCurrentStep(ProductsExpServiceConstant.LOAN_CAL);
		}
		return loanStagingbarRes;
	}

}
