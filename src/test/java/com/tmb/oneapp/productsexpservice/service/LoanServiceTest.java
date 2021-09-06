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
import org.springframework.http.ResponseEntity;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.model.loan.stagingbar.LoanStagingbar;
import com.tmb.common.model.loan.stagingbar.StagingDetails;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;
import com.tmb.oneapp.productsexpservice.model.lending.loan.ContinueApplyNextScreen;
import com.tmb.oneapp.productsexpservice.model.lending.loan.FlowType;
import com.tmb.oneapp.productsexpservice.model.lending.loan.LoanStagingbarRequest;
import com.tmb.oneapp.productsexpservice.model.lending.loan.ProductDetailRequest;
import com.tmb.oneapp.productsexpservice.model.lending.loan.ProductDetailResponse;

@RunWith(JUnit4.class)
public class LoanServiceTest {

	@Mock
	LendingServiceClient lendingServiceClient;

	@Mock
	LoanStagingBarService loanStagingBarService;

	LoanService loanService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void fetchProductOrientationSuccessPLFlexiComfirmation() throws TMBCommonException {
		String correlationId = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da";
		String crmId = "001100000000000000000018593707";
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
		Mockito.when(loanStagingBarService.fetchLoanStagingBar(correlationId, crmId, loanStagingbarReq))
				.thenReturn(loanStagingbar);
		ProductDetailRequest request = new ProductDetailRequest();
		request.setProductCode("c2g");
		ProductDetailResponse dataProductDetailResponse = new ProductDetailResponse();
		dataProductDetailResponse.setProductCode("c2g01");
		dataProductDetailResponse.setFlowType(FlowType.FLEXI);
		dataProductDetailResponse.setContinueApplyNextStep(ContinueApplyNextScreen.CONFIRMATION);
		TmbOneServiceResponse<ProductDetailResponse> productDetailResponse = new TmbOneServiceResponse<ProductDetailResponse>();
		productDetailResponse.setData(dataProductDetailResponse);
		productDetailResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "", ""));
		Mockito.when(lendingServiceClient.fetchProductOrientation(correlationId, crmId, request))
				.thenReturn(ResponseEntity.ok(productDetailResponse));

		loanService = new LoanService(lendingServiceClient, loanStagingBarService);

		ResponseEntity<TmbOneServiceResponse<ProductDetailResponse>> actual = loanService
				.fetchProductOrientation(correlationId, crmId, request);

		Assertions.assertNotNull(actual);
		Assertions.assertEquals(ProductsExpServiceConstant.CONFIRM_APPLICATION,
				actual.getBody().getData().getLoanStagingBar().getCurrentStep());
	}

	@Test
	void fetchProductOrientationFail() throws TMBCommonException {
		String correlationId = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da";
		String crmId = "001100000000000000000018593707";
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
		Mockito.when(loanStagingBarService.fetchLoanStagingBar(correlationId, crmId, loanStagingbarReq))
				.thenReturn(loanStagingbar);
		ProductDetailRequest request = new ProductDetailRequest();
		request.setProductCode("c2g");
		ProductDetailResponse dataProductDetailResponse = new ProductDetailResponse();
		dataProductDetailResponse.setProductCode("c2g01");
		dataProductDetailResponse.setFlowType(FlowType.FLEXI);
		dataProductDetailResponse.setContinueApplyNextStep(ContinueApplyNextScreen.CONFIRMATION);
		TmbOneServiceResponse<ProductDetailResponse> productDetailResponse = new TmbOneServiceResponse<ProductDetailResponse>();
		productDetailResponse.setData(null);
		productDetailResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), "", ""));
		Mockito.when(lendingServiceClient.fetchProductOrientation(correlationId, crmId, request))
				.thenReturn(ResponseEntity.ok(productDetailResponse));

		loanService = new LoanService(lendingServiceClient, loanStagingBarService);
		try {
			loanService.fetchProductOrientation(correlationId, crmId, request);
			Assertions.fail("Should have TMBCommonException");
		} catch (TMBCommonException e) {
		}
	}

	@Test
	void fetchProductOrientationSuccessFCLoanPersonal() throws TMBCommonException {
		String correlationId = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da";
		String crmId = "001100000000000000000018593707";
		LoanStagingbarRequest loanStagingbarReq = new LoanStagingbarRequest();
		loanStagingbarReq.setLoanType("loan-submission");
		loanStagingbarReq.setProductHeaderKey("apply-flash-card");
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
		Mockito.when(loanStagingBarService.fetchLoanStagingBar(correlationId, crmId, loanStagingbarReq))
				.thenReturn(loanStagingbar);
		ProductDetailRequest request = new ProductDetailRequest();
		request.setProductCode("rc");
		ProductDetailResponse dataProductDetailResponse = new ProductDetailResponse();
		dataProductDetailResponse.setProductCode("rc01");
		dataProductDetailResponse.setFlowType(FlowType.LOAN_SUBMISSION);
		dataProductDetailResponse.setContinueApplyNextStep(ContinueApplyNextScreen.PERSONAL);
		TmbOneServiceResponse<ProductDetailResponse> productDetailResponse = new TmbOneServiceResponse<ProductDetailResponse>();
		productDetailResponse.setData(dataProductDetailResponse);
		productDetailResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "", ""));
		Mockito.when(lendingServiceClient.fetchProductOrientation(correlationId, crmId, request))
				.thenReturn(ResponseEntity.ok(productDetailResponse));

		loanService = new LoanService(lendingServiceClient, loanStagingBarService);

		ResponseEntity<TmbOneServiceResponse<ProductDetailResponse>> actual = loanService
				.fetchProductOrientation(correlationId, crmId, request);

		Assertions.assertNotNull(actual);
		Assertions.assertEquals(ProductsExpServiceConstant.PERSONAL_DETAIL,
				actual.getBody().getData().getLoanStagingBar().getCurrentStep());
	}
	
	@Test
	void fetchProductOrientationSuccessCCLoanWork() throws TMBCommonException {
		String correlationId = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da";
		String crmId = "001100000000000000000018593707";
		LoanStagingbarRequest loanStagingbarReq = new LoanStagingbarRequest();
		loanStagingbarReq.setLoanType("loan-submission");
		loanStagingbarReq.setProductHeaderKey("apply-credit-card");
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
		Mockito.when(loanStagingBarService.fetchLoanStagingBar(correlationId, crmId, loanStagingbarReq))
				.thenReturn(loanStagingbar);
		ProductDetailRequest request = new ProductDetailRequest();
		request.setProductCode("vi");
		ProductDetailResponse dataProductDetailResponse = new ProductDetailResponse();
		dataProductDetailResponse.setProductCode("vi");
		dataProductDetailResponse.setFlowType(FlowType.LOAN_SUBMISSION);
		dataProductDetailResponse.setContinueApplyNextStep(ContinueApplyNextScreen.WORKING);
		TmbOneServiceResponse<ProductDetailResponse> productDetailResponse = new TmbOneServiceResponse<ProductDetailResponse>();
		productDetailResponse.setData(dataProductDetailResponse);
		productDetailResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "", ""));
		Mockito.when(lendingServiceClient.fetchProductOrientation(correlationId, crmId, request))
				.thenReturn(ResponseEntity.ok(productDetailResponse));

		loanService = new LoanService(lendingServiceClient, loanStagingBarService);

		ResponseEntity<TmbOneServiceResponse<ProductDetailResponse>> actual = loanService
				.fetchProductOrientation(correlationId, crmId, request);

		Assertions.assertNotNull(actual);
		Assertions.assertEquals(ProductsExpServiceConstant.WORK_DETAIL,
				actual.getBody().getData().getLoanStagingBar().getCurrentStep());
	}
	
	@Test
	void fetchProductOrientationSuccessCCLoanUpload() throws TMBCommonException {
		String correlationId = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da";
		String crmId = "001100000000000000000018593707";
		LoanStagingbarRequest loanStagingbarReq = new LoanStagingbarRequest();
		loanStagingbarReq.setLoanType("loan-submission");
		loanStagingbarReq.setProductHeaderKey("apply-credit-card");
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
		Mockito.when(loanStagingBarService.fetchLoanStagingBar(correlationId, crmId, loanStagingbarReq))
				.thenReturn(loanStagingbar);
		ProductDetailRequest request = new ProductDetailRequest();
		request.setProductCode("vi");
		ProductDetailResponse dataProductDetailResponse = new ProductDetailResponse();
		dataProductDetailResponse.setProductCode("vi");
		dataProductDetailResponse.setFlowType(FlowType.LOAN_SUBMISSION);
		dataProductDetailResponse.setContinueApplyNextStep(ContinueApplyNextScreen.UPLOAD_DOC);
		TmbOneServiceResponse<ProductDetailResponse> productDetailResponse = new TmbOneServiceResponse<ProductDetailResponse>();
		productDetailResponse.setData(dataProductDetailResponse);
		productDetailResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "", ""));
		Mockito.when(lendingServiceClient.fetchProductOrientation(correlationId, crmId, request))
				.thenReturn(ResponseEntity.ok(productDetailResponse));

		loanService = new LoanService(lendingServiceClient, loanStagingBarService);

		ResponseEntity<TmbOneServiceResponse<ProductDetailResponse>> actual = loanService
				.fetchProductOrientation(correlationId, crmId, request);

		Assertions.assertNotNull(actual);
		Assertions.assertEquals(ProductsExpServiceConstant.UPLOAD_DOCUMENTS,
				actual.getBody().getData().getLoanStagingBar().getCurrentStep());
	}

	@Test
	void fetchProductOrientationSuccessCCLoanDay1() throws TMBCommonException {
		String correlationId = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da";
		String crmId = "001100000000000000000018593707";
		LoanStagingbarRequest loanStagingbarReq = new LoanStagingbarRequest();
		loanStagingbarReq.setLoanType("loan-submission");
		loanStagingbarReq.setProductHeaderKey("apply-credit-card");
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
		Mockito.when(loanStagingBarService.fetchLoanStagingBar(correlationId, crmId, loanStagingbarReq))
				.thenReturn(loanStagingbar);
		ProductDetailRequest request = new ProductDetailRequest();
		request.setProductCode("vi");
		ProductDetailResponse dataProductDetailResponse = new ProductDetailResponse();
		dataProductDetailResponse.setProductCode("vi");
		dataProductDetailResponse.setFlowType(FlowType.LOAN_SUBMISSION);
		dataProductDetailResponse.setContinueApplyNextStep(ContinueApplyNextScreen.CASH_TRANSFER_DAY1);
		TmbOneServiceResponse<ProductDetailResponse> productDetailResponse = new TmbOneServiceResponse<ProductDetailResponse>();
		productDetailResponse.setData(dataProductDetailResponse);
		productDetailResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "", ""));
		Mockito.when(lendingServiceClient.fetchProductOrientation(correlationId, crmId, request))
				.thenReturn(ResponseEntity.ok(productDetailResponse));

		loanService = new LoanService(lendingServiceClient, loanStagingBarService);

		ResponseEntity<TmbOneServiceResponse<ProductDetailResponse>> actual = loanService
				.fetchProductOrientation(correlationId, crmId, request);

		Assertions.assertNotNull(actual);
		Assertions.assertEquals(ProductsExpServiceConstant.LOAN_DAY1,
				actual.getBody().getData().getLoanStagingBar().getCurrentStep());
	}
	
	@Test
	void fetchProductOrientationSuccessCCLoanFinalApprove() throws TMBCommonException {
		String correlationId = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da";
		String crmId = "001100000000000000000018593707";
		LoanStagingbarRequest loanStagingbarReq = new LoanStagingbarRequest();
		loanStagingbarReq.setLoanType("loan-submission");
		loanStagingbarReq.setProductHeaderKey("apply-credit-card");
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
		Mockito.when(loanStagingBarService.fetchLoanStagingBar(correlationId, crmId, loanStagingbarReq))
				.thenReturn(loanStagingbar);
		ProductDetailRequest request = new ProductDetailRequest();
		request.setProductCode("vi");
		ProductDetailResponse dataProductDetailResponse = new ProductDetailResponse();
		dataProductDetailResponse.setProductCode("vi");
		dataProductDetailResponse.setFlowType(FlowType.LOAN_SUBMISSION);
		dataProductDetailResponse.setContinueApplyNextStep(ContinueApplyNextScreen.FINAL_APPROVE_LOAN_CONFIRMATION);
		TmbOneServiceResponse<ProductDetailResponse> productDetailResponse = new TmbOneServiceResponse<ProductDetailResponse>();
		productDetailResponse.setData(dataProductDetailResponse);
		productDetailResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "", ""));
		Mockito.when(lendingServiceClient.fetchProductOrientation(correlationId, crmId, request))
				.thenReturn(ResponseEntity.ok(productDetailResponse));

		loanService = new LoanService(lendingServiceClient, loanStagingBarService);

		ResponseEntity<TmbOneServiceResponse<ProductDetailResponse>> actual = loanService
				.fetchProductOrientation(correlationId, crmId, request);

		Assertions.assertNotNull(actual);
		Assertions.assertEquals(ProductsExpServiceConstant.FINAL_APPROVAL,
				actual.getBody().getData().getLoanStagingBar().getCurrentStep());
	}
	
	@Test
	void fetchProductOrientationSuccessCCLoanCal() throws TMBCommonException {
		String correlationId = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da";
		String crmId = "001100000000000000000018593707";
		LoanStagingbarRequest loanStagingbarReq = new LoanStagingbarRequest();
		loanStagingbarReq.setLoanType("loan-submission");
		loanStagingbarReq.setProductHeaderKey("apply-credit-card");
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
		Mockito.when(loanStagingBarService.fetchLoanStagingBar(correlationId, crmId, loanStagingbarReq))
				.thenReturn(loanStagingbar);
		ProductDetailRequest request = new ProductDetailRequest();
		request.setProductCode("vi");
		ProductDetailResponse dataProductDetailResponse = new ProductDetailResponse();
		dataProductDetailResponse.setProductCode("vi");
		dataProductDetailResponse.setFlowType(FlowType.LOAN_SUBMISSION);
		dataProductDetailResponse.setContinueApplyNextStep(ContinueApplyNextScreen.UNKNOWN);
		TmbOneServiceResponse<ProductDetailResponse> productDetailResponse = new TmbOneServiceResponse<ProductDetailResponse>();
		productDetailResponse.setData(dataProductDetailResponse);
		productDetailResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "", ""));
		Mockito.when(lendingServiceClient.fetchProductOrientation(correlationId, crmId, request))
				.thenReturn(ResponseEntity.ok(productDetailResponse));

		loanService = new LoanService(lendingServiceClient, loanStagingBarService);

		ResponseEntity<TmbOneServiceResponse<ProductDetailResponse>> actual = loanService
				.fetchProductOrientation(correlationId, crmId, request);

		Assertions.assertNotNull(actual);
		Assertions.assertEquals(ProductsExpServiceConstant.LOAN_CAL,
				actual.getBody().getData().getLoanStagingBar().getCurrentStep());
	}
	
	@Test
	void fetchProductOrientationSuccessCCLoanCalCaseNull() throws TMBCommonException {
		String correlationId = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da";
		String crmId = "001100000000000000000018593707";
		LoanStagingbarRequest loanStagingbarReq = new LoanStagingbarRequest();
		loanStagingbarReq.setLoanType("loan-submission");
		loanStagingbarReq.setProductHeaderKey("apply-credit-card");
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
		Mockito.when(loanStagingBarService.fetchLoanStagingBar(correlationId, crmId, loanStagingbarReq))
				.thenReturn(loanStagingbar);
		ProductDetailRequest request = new ProductDetailRequest();
		request.setProductCode("vi");
		ProductDetailResponse dataProductDetailResponse = new ProductDetailResponse();
		dataProductDetailResponse.setProductCode("vi");
		dataProductDetailResponse.setFlowType(FlowType.LOAN_SUBMISSION);
		dataProductDetailResponse.setContinueApplyNextStep(null);
		TmbOneServiceResponse<ProductDetailResponse> productDetailResponse = new TmbOneServiceResponse<ProductDetailResponse>();
		productDetailResponse.setData(dataProductDetailResponse);
		productDetailResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "", ""));
		Mockito.when(lendingServiceClient.fetchProductOrientation(correlationId, crmId, request))
				.thenReturn(ResponseEntity.ok(productDetailResponse));

		loanService = new LoanService(lendingServiceClient, loanStagingBarService);

		ResponseEntity<TmbOneServiceResponse<ProductDetailResponse>> actual = loanService
				.fetchProductOrientation(correlationId, crmId, request);

		Assertions.assertNotNull(actual);
		Assertions.assertEquals(ProductsExpServiceConstant.LOAN_CAL,
				actual.getBody().getData().getLoanStagingBar().getCurrentStep());
	}
	
	@Test
	void fetchProductOrientationSuccessCCLoanIncome() throws TMBCommonException {
		String correlationId = "32fbd3b2-3f97-4a89-ar39-b4f628fbc8da";
		String crmId = "001100000000000000000018593707";
		LoanStagingbarRequest loanStagingbarReq = new LoanStagingbarRequest();
		loanStagingbarReq.setLoanType("loan-submission");
		loanStagingbarReq.setProductHeaderKey("apply-credit-card");
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
		Mockito.when(loanStagingBarService.fetchLoanStagingBar(correlationId, crmId, loanStagingbarReq))
				.thenReturn(loanStagingbar);
		ProductDetailRequest request = new ProductDetailRequest();
		request.setProductCode("vi");
		ProductDetailResponse dataProductDetailResponse = new ProductDetailResponse();
		dataProductDetailResponse.setProductCode("vi");
		dataProductDetailResponse.setFlowType(FlowType.LOAN_SUBMISSION);
		dataProductDetailResponse.setContinueApplyNextStep(ContinueApplyNextScreen.INCOME);
		TmbOneServiceResponse<ProductDetailResponse> productDetailResponse = new TmbOneServiceResponse<ProductDetailResponse>();
		productDetailResponse.setData(dataProductDetailResponse);
		productDetailResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), "", ""));
		Mockito.when(lendingServiceClient.fetchProductOrientation(correlationId, crmId, request))
				.thenReturn(ResponseEntity.ok(productDetailResponse));

		loanService = new LoanService(lendingServiceClient, loanStagingBarService);

		ResponseEntity<TmbOneServiceResponse<ProductDetailResponse>> actual = loanService
				.fetchProductOrientation(correlationId, crmId, request);

		Assertions.assertNotNull(actual);
		Assertions.assertEquals(ProductsExpServiceConstant.WORK_DETAIL,
				actual.getBody().getData().getLoanStagingBar().getCurrentStep());
	}
}
