package com.tmb.oneapp.productsexpservice.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.model.loan.stagingbar.LoanStagingbar;
import com.tmb.common.model.loan.stagingbar.StagingDetails;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.model.lending.loan.LoanStagingbarRequest;

@RunWith(JUnit4.class)
public class LoanStagingBarServiceTest {

	@Mock
	CommonServiceClient commonServiceFeignClient;

	LoanStagingBarService loanStagingBarService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void fetchLoanStagingBarSuccess() throws TMBCommonException {
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
		TmbOneServiceResponse<LoanStagingbar> loanStagingbarRes = new TmbOneServiceResponse<LoanStagingbar>();
		loanStagingbarRes.setData(loanStagingbar);
		loanStagingbarRes.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "", ""));
		Mockito.when(commonServiceFeignClient.fetchLoanStagingBar(loanStagingbarReq)).thenReturn(loanStagingbarRes);
		loanStagingBarService = new LoanStagingBarService(commonServiceFeignClient);

		LoanStagingbar actual = loanStagingBarService.fetchLoanStagingBar(loanStagingbarReq);

		Assertions.assertNotNull(actual);
		Assertions.assertEquals(loanStagingbar.getLoanType(), actual.getLoanType());
		Assertions.assertEquals(loanStagingbar.getProductHeaderKey(), actual.getProductHeaderKey());
	}

	@Test
	void fetchLoanStagingBarFail() throws TMBCommonException {
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
		TmbOneServiceResponse<LoanStagingbar> loanStagingbarRes = new TmbOneServiceResponse<LoanStagingbar>();
		loanStagingbarRes.setData(loanStagingbar);
		loanStagingbarRes.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), "", ""));
		Mockito.when(commonServiceFeignClient.fetchLoanStagingBar(loanStagingbarReq)).thenReturn(loanStagingbarRes);
		loanStagingBarService = new LoanStagingBarService(commonServiceFeignClient);

		try {
			loanStagingBarService.fetchLoanStagingBar(loanStagingbarReq);
			Assertions.fail("Should have TMBCommonException");
		} catch (TMBCommonException e) {
		}
	}
}
