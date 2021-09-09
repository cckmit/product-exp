package com.tmb.oneapp.productsexpservice.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.loan.stagingbar.LoanStagingbar;
import com.tmb.common.model.loan.stagingbar.StagingDetails;
import com.tmb.oneapp.productsexpservice.model.lending.loan.LoanStagingbarRequest;
import com.tmb.oneapp.productsexpservice.service.LoanStagingBarService;

@RunWith(JUnit4.class)
public class LoanStagingBarControllerTest {

	LoanStagingBarController loanStagingBarController;

	@Mock
	LoanStagingBarService loanStagingBarService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		loanStagingBarController = new LoanStagingBarController(loanStagingBarService);
	}

	@Test
	public void fetchLoanStagingBarSuccess() throws TMBCommonException {
		LoanStagingbarRequest loanStagingbarReq = new LoanStagingbarRequest();
		loanStagingbarReq.setLoanType("flexi");
		loanStagingbarReq.setProductHeaderKey("apply-personal-loan");
		LoanStagingbar loanStagingbar = new LoanStagingbar();
		loanStagingbar.setLoanType("flexi");
		loanStagingbar.setProductHeaderKey("apply-personal-loan");
		loanStagingbar.setProductHeaderTh("สมัครสินเชื่อบุคคล");
		List<StagingDetails> stagingDetailsList = new ArrayList<>();
		StagingDetails stagingDetails = new StagingDetails();
		stagingDetails.setStageNo("1");
		stagingDetails.setStageKey("loan-cal");
		stagingDetails.setStageTh("วงเงินสินเชื่อและระยะเวลาผ่อน");
		stagingDetailsList.add(stagingDetails);
		loanStagingbar.setStagingDetails(stagingDetailsList);
		loanStagingbar.setStagesCount("1");

		when(loanStagingBarService.fetchLoanStagingBar(any())).thenReturn(loanStagingbar);

		ResponseEntity<TmbOneServiceResponse<LoanStagingbar>> result = loanStagingBarController
				.fetchLoanStagingBar(loanStagingbarReq);
		assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());

	}

	@Test
	public void fetchLoanStagingBarFailKeyNull() throws TMBCommonException {
		LoanStagingbarRequest loanStagingbarReq = new LoanStagingbarRequest();
		loanStagingbarReq.setLoanType(null);
		loanStagingbarReq.setProductHeaderKey(null);
		LoanStagingbar loanStagingbar = new LoanStagingbar();
		loanStagingbar.setLoanType(null);
		loanStagingbar.setProductHeaderKey(null);
		loanStagingbar.setProductHeaderTh("สมัครสินเชื่อบุคคล");
		List<StagingDetails> stagingDetailsList = new ArrayList<>();
		StagingDetails stagingDetails = new StagingDetails();
		stagingDetails.setStageNo("1");
		stagingDetails.setStageKey("loan-cal");
		stagingDetails.setStageTh("วงเงินสินเชื่อและระยะเวลาผ่อน");
		stagingDetailsList.add(stagingDetails);
		loanStagingbar.setStagingDetails(stagingDetailsList);
		loanStagingbar.setStagesCount("1");
		try {
			loanStagingBarController.fetchLoanStagingBar(loanStagingbarReq);
			Assertions.fail("Should have TMBCommonException");
		} catch (TMBCommonException e) {
		}
	}

	@Test
	public void fetchLoanStagingBarFail() throws TMBCommonException {
		LoanStagingbarRequest loanStagingbarReq = new LoanStagingbarRequest();
		loanStagingbarReq.setLoanType("flexi");
		loanStagingbarReq.setProductHeaderKey("apply-personal-loan");
		LoanStagingbar loanStagingbar = new LoanStagingbar();
		loanStagingbar.setLoanType("flexi");
		loanStagingbar.setProductHeaderKey("apply-personal-loan");
		loanStagingbar.setProductHeaderTh("สมัครสินเชื่อบุคคล");
		List<StagingDetails> stagingDetailsList = new ArrayList<>();
		StagingDetails stagingDetails = new StagingDetails();
		stagingDetails.setStageNo("1");
		stagingDetails.setStageKey("loan-cal");
		stagingDetails.setStageTh("วงเงินสินเชื่อและระยะเวลาผ่อน");
		stagingDetailsList.add(stagingDetails);
		loanStagingbar.setStagingDetails(stagingDetailsList);
		loanStagingbar.setStagesCount("1");

		when(loanStagingBarService.fetchLoanStagingBar(any())).thenThrow(new TMBCommonException(""));

		try {
			loanStagingBarController.fetchLoanStagingBar(loanStagingbarReq);
			Assertions.fail("Should have TMBCommonException");
		} catch (TMBCommonException e) {
		}
	}

}