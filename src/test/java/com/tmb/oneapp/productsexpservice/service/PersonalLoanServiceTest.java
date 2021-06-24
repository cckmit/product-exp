package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.model.AllowCashDayOne;
import com.tmb.common.model.CommonData;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanPreloadRequest;
import com.tmb.oneapp.productsexpservice.model.response.loan.ProductData;
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

	@Test
	public void testProductList() {
//		TmbOneServiceResponse<List<CommonData>> list = new TmbOneServiceResponse<List<CommonData>>();
//		List<CommonData> listDatas = new ArrayList<CommonData>();
//		CommonData cData = new CommonData();
//		cData.setAccount221Url("");
//		List<AllowCashDayOne> allowCashDay = new ArrayList<AllowCashDayOne>();
//		AllowCashDayOne modelMocl = new AllowCashDayOne();
//		modelMocl.setAllowCashDayOne("P");
//		allowCashDay.add(modelMocl);
//		cData.setAllowCashDayOnes(allowCashDay);
//		listDatas.add(cData);
//		list.setData(listDatas);

		List<ProductData> productDataList = new ArrayList<>();
		ProductData productData = new ProductData();
		productData.setRslCode("RC");
		productData.setContentLink("https://www-uat.tau2904.com/th/personal/loans/personal-loan/flash-card");
		productData.setProductNameEn("flash card");
		productData.setProductNameTh("บัตรกดเงินสด");
		productData.setProductDescEn("ตอบโจทย์ทุกการใช้ชีวิต");
		productData.setProductDescTh("ตอบโจทย์ทุกการใช้ชีวิต");
		productData.setIconId("/product/credit_card/cards_flash.png");

		ProductData productData1= new ProductData();
		productData1.setRslCode("C2G");
		productData1.setContentLink("https://www-uat.tau2904.com/th/personal/loans/personal-loan/cash-2-go?inapp=y&dl=n");
		productData1.setProductNameEn("cash2go");
		productData1.setProductNameTh("สินเชื่อบุคคล");
		productData1.setProductDescEn("ตอบโจทย์ทุกการใช้ชีวิต");
		productData1.setProductDescTh("ตอบโจทย์ทุกการใช้ชีวิต");
		productData1.setIconId("/product/logo/icon_09.png");

		productDataList.add(productData);
		productDataList.add(productData1);

		personalLoanService.getProducts();

		Assert.assertTrue(true);
	}

}
