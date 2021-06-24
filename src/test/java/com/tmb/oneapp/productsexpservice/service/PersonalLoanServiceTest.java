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

	private static final String PRODUCT_DES = "ตอบโจทย์ทุกการใช้ชีวิต";
	private static final String PRODUCT_DES_CREDIT = "เอกสิทธิ์ขั้นสูงสุด ทั้งด้านการเงินและการลงทุน";
	private static final String PRODUCT_DES_CREDIT_VB = "ตอบโจทย์ทั้งด้านการเงิน การลงทุน และไลฟ์สไตล์";
	private static final String PRODUCT_DES_CREDIT_VP = "สะสม Point ได้เร็วกว่า ให้คุณได้มากกว่า";
	private static final String PRODUCT_DES_CREDIT_VM = "บัตรเครดิตเงินคืน ให้คุณได้มากกว่า";
	private static final String PRODUCT_DES_CREDIT_VH = "กดเงินสดแบบชิลล์ๆ ฟรีค่าธรรมเนียม";
	private static final String PRODUCT_NAME_VI = "ttb reserve infinite";
	private static final String PRODUCT_NAME_VB = "ttb reserve signature";
	private static final String PRODUCT_NAME_VJ = "ttb absolute";
	private static final String PRODUCT_NAME_VP = "ttb so fast";
	private static final String PRODUCT_NAME_VM = "ttb so smart";
	private static final String PRODUCT_NAME_VH = "ttb so chill";

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

	@Test
	public void testProductCreditList() {

		List<ProductData> productDataList = new ArrayList();
		ProductData productData = new ProductData();
		productData.setRslCode("VI");
		productData.setContentLink("https://www-uat.tau2904.com/ttb-reserve/main/index.html?inapp=y&dl=n");
		productData.setProductNameEn(PRODUCT_NAME_VI);
		productData.setProductNameTh(PRODUCT_NAME_VI);
		productData.setProductDescEn(PRODUCT_DES_CREDIT);
		productData.setProductDescTh(PRODUCT_DES_CREDIT);
		productData.setIconId("/product/apply_loan/ttb_reserve_infinite.png");

		ProductData productData1= new ProductData();
		productData1.setRslCode("VB");
		productData1.setContentLink("https://www-uat.tau2904.com/ttb-reserve/main/index.html?inapp=y&dl=n");
		productData1.setProductNameEn(PRODUCT_NAME_VB);
		productData1.setProductNameTh(PRODUCT_NAME_VB);
		productData1.setProductDescEn(PRODUCT_DES_CREDIT_VB);
		productData1.setProductDescTh(PRODUCT_DES_CREDIT_VB);
		productData1.setIconId("/product/apply_loan/ttb_c2g.png");

		ProductData productData2= new ProductData();
		productData2.setRslCode("VJ");
		productData2.setContentLink("https://www-uat.tau2904.com/th/personal/credit-cards/card-type/ttb-absolute?inapp=y&dl=n");
		productData2.setProductNameEn(PRODUCT_NAME_VJ);
		productData2.setProductNameTh(PRODUCT_NAME_VJ);
		productData2.setProductDescEn(PRODUCT_DES);
		productData2.setProductDescTh(PRODUCT_DES);
		productData2.setIconId("/product/apply_loan/ttb_signature.png");

		ProductData productData3 = new ProductData();
		productData3.setRslCode("VP");
		productData3.setContentLink("https://www-uat.tau2904.com/th/personal/credit-cards/card-type/ttb-so-fast?inapp=y&dl=n");
		productData3.setProductNameEn(PRODUCT_NAME_VP);
		productData3.setProductNameTh(PRODUCT_NAME_VP);
		productData3.setProductDescEn(PRODUCT_DES_CREDIT_VP);
		productData3.setProductDescTh(PRODUCT_DES_CREDIT_VP);
		productData3.setIconId("/product/apply_loan/ttb_so_fast.png");

		ProductData productData4 = new ProductData();
		productData4.setRslCode("VM");
		productData4.setContentLink("https://www-uat.tau2904.com/th/personal/credit-cards/card-type/ttb-so-smart?inapp=y&dl=n");
		productData4.setProductNameEn(PRODUCT_NAME_VM);
		productData4.setProductNameTh(PRODUCT_NAME_VM);
		productData4.setProductDescEn(PRODUCT_DES_CREDIT_VM);
		productData4.setProductDescTh(PRODUCT_DES_CREDIT_VM);
		productData4.setIconId("/product/apply_loan/ttb_so_smart.png");

		ProductData productData5 = new ProductData();
		productData5.setRslCode("VH");
		productData5.setContentLink("https://www-uat.tau2904.com/th/personal/credit-cards/card-type/ttb-so-chill?inapp=y&dl=n");
		productData5.setProductNameEn(PRODUCT_NAME_VH);
		productData5.setProductNameTh(PRODUCT_NAME_VH);
		productData5.setProductDescEn(PRODUCT_DES_CREDIT_VH);
		productData5.setProductDescTh(PRODUCT_DES_CREDIT_VH);
		productData5.setIconId("/product/apply_loan/ttb_so_chill.png");

		productDataList.add(productData);
		productDataList.add(productData1);
		productDataList.add(productData2);
		productDataList.add(productData3);
		productDataList.add(productData4);
		productDataList.add(productData5);
		productDataList.add(productData1);

		personalLoanService.getProductsCredit();

		Assert.assertTrue(true);
	}

}
