package com.tmb.oneapp.productsexpservice.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.model.loan.stagingbar.LoanStagingbar;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.model.lending.loan.LoanStagingbarRequest;
import com.tmb.oneapp.productsexpservice.service.LoanStagingBarService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Api(tags = "LoanStagingBar")
@RequestMapping("/loan")
@RestController
public class LoanStagingBarController {
	private static final TMBLogger<LoanStagingBarController> logger = new TMBLogger<>(LoanStagingBarController.class);
	private final LoanStagingBarService loanStagingBarService;

	@ApiOperation("Loan Staging Bar")
	@PostMapping(value = "/get-staging-bar", produces = MediaType.APPLICATION_JSON_VALUE)
	@LogAround
	public ResponseEntity<TmbOneServiceResponse<LoanStagingbar>> fetchLoanStagingBar(
			@Valid @RequestBody LoanStagingbarRequest request) throws TMBCommonException {
		TmbOneServiceResponse<LoanStagingbar> response = new TmbOneServiceResponse<>();
		if (request.getLoanType() == null || request.getProductHeaderKey() == null) {
			logger.error("error exception fetch loan staging bar : key value is null");
			throw new TMBCommonException(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
					ResponseCode.FAILED.getService(), HttpStatus.BAD_REQUEST, null);
		}
		try {
			LoanStagingbar loanStagingbarRes = loanStagingBarService.fetchLoanStagingBar(request);
			response.setData(loanStagingbarRes);
			response.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
					ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
			return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(response);

		} catch (Exception e) {
			throw new TMBCommonException(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
					ResponseCode.FAILED.getService(), HttpStatus.INTERNAL_SERVER_ERROR, e);
		}
	}

}
