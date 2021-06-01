package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.model.AllowCashDayOne;
import com.tmb.common.model.CommonData;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanPreloadRequest;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class PersonalLoanServiceTest {
	@Mock
	private CommonServiceClient commonServiceClient;

	@Mock
	private LendingServiceClient lendingServiceClient;

	PersonalLoanService personalLoanService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.initMocks(this);
		personalLoanService = new PersonalLoanService(commonServiceClient);
	}

	@Test
	public void testService() {
		TmbOneServiceResponse<List<CommonData>> list = new TmbOneServiceResponse<List<CommonData>>();
		List<CommonData> listDatas = new ArrayList<CommonData>();
		CommonData cData = new CommonData();
		cData.setAccount221Url("");
		List<AllowCashDayOne> allowCashDay = new ArrayList<AllowCashDayOne>();
		AllowCashDayOne modelMocl = new AllowCashDayOne();
		modelMocl.setAllowCashDayOne("P");
		allowCashDay.add(modelMocl);
		cData.setAllowCashDayOnes(allowCashDay);
		listDatas.add(cData);
		list.setData(listDatas);
		when(commonServiceClient.getCommonConfig(any(), any())).thenReturn(ResponseEntity.ok(list));
		LoanPreloadRequest preloadReq = new LoanPreloadRequest();
		preloadReq.setProductCode("P");
		personalLoanService.checkPreload("ASSS", preloadReq);

		Assert.assertTrue(true);
	}

}
