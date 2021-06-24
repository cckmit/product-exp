package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.AllowCashDayOne;
import com.tmb.common.model.CommonData;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanPreloadRequest;
import com.tmb.oneapp.productsexpservice.model.response.LoanPreloadResponse;
import com.tmb.oneapp.productsexpservice.model.response.loan.ProductData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PersonalLoanService {

    private static final TMBLogger<PersonalLoanService> logger = new TMBLogger(PersonalLoanService.class);
    private final CommonServiceClient commonServiceClient;
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
    private static final String CONTENT_LINK_FLASH = "https://www-uat.tau2904.com/th/personal/loans/personal-loan/flash-card";
    private static final String CONTENT_LINK_C2G = "https://www-uat.tau2904.com/th/personal/loans/personal-loan/cash-2-go?inapp=y&dl=n";
    private static final String ICON_C2G = "/product/apply_loan/ttb_c2g.png";
    private static final String ICON_FLASH = "/product/apply_loan/ttb_flash.png";
    private static final String ICON_SO_FAST = "/product/apply_loan/ttb_flash.png";
    private static final String ICON_VI = "/product/apply_loan/ttb_reserve_infinite.png";
    private static final String ICON_VJ = "/product/apply_loan/ttb_signature.png";
    private static final String ICON_VM = "/product/apply_loan/ttb_so_smart.png";
    private static final String ICON_VH = "/product/apply_loan/ttb_so_chill.png";
    private static final String C2G = "C2G";
    private static final String RC = "RC";
    private static final String CASH_2_GO = "cash2go";
    private static final String FLASH_CARD = "flash card";
    private static final String CREDIT = "บัตรกดเงินสด";
    private static final String LOAN = "สินเชื่อบุคคล";
    private static final String CONTENT_LINK_VI_VB = "https://www-uat.tau2904.com/ttb-reserve/main/index.html?inapp=y&dl=n";

    @Autowired 
    public PersonalLoanService(CommonServiceClient commonServiceClient) {
    	this.commonServiceClient = commonServiceClient;
    }

    public LoanPreloadResponse checkPreload(String correlationId,LoanPreloadRequest loanPreloadRequest) {

        LoanPreloadResponse loanPreloadResponse = new LoanPreloadResponse();
        TmbOneServiceResponse<List<CommonData>> configs = getAllConfig(correlationId,loanPreloadRequest.getSearch());
        loanPreloadResponse.setFlagePreload(checkPreloadConfig(configs, loanPreloadRequest));

        return loanPreloadResponse;
    }

    public List<ProductData>  getProducts() {
        List<ProductData> productDataList = new ArrayList();
        ProductData productData = new ProductData();
        productData.setRslCode(RC);
        productData.setContentLink(CONTENT_LINK_FLASH);
        productData.setProductNameEn(FLASH_CARD);
        productData.setProductNameTh(CREDIT);
        productData.setProductDescEn(PRODUCT_DES);
        productData.setProductDescTh(PRODUCT_DES);
        productData.setIconId(ICON_FLASH);

        ProductData productData1= new ProductData();
        productData1.setRslCode(C2G);
        productData1.setContentLink(CONTENT_LINK_C2G);
        productData1.setProductNameEn(CASH_2_GO);
        productData1.setProductNameTh(LOAN);
        productData1.setProductDescEn(PRODUCT_DES);
        productData1.setProductDescTh(PRODUCT_DES);
        productData1.setIconId(ICON_C2G);

        ProductData productData2= new ProductData();
        productData2.setRslCode(C2G);
        productData2.setContentLink(CONTENT_LINK_C2G);
        productData2.setProductNameEn(CASH_2_GO);
        productData2.setProductNameTh(LOAN);
        productData2.setProductDescEn(PRODUCT_DES);
        productData2.setProductDescTh(PRODUCT_DES);
        productData2.setIconId(ICON_C2G);

        ProductData productData3= new ProductData();
        productData3.setRslCode(C2G);
        productData3.setContentLink(CONTENT_LINK_C2G);
        productData3.setProductNameEn(CASH_2_GO);
        productData3.setProductNameTh(LOAN);
        productData3.setProductDescEn(PRODUCT_DES);
        productData3.setProductDescTh(PRODUCT_DES);
        productData3.setIconId(ICON_C2G);

        ProductData productData4 = new ProductData();
        productData4.setRslCode(RC);
        productData4.setContentLink(CONTENT_LINK_FLASH);
        productData4.setProductNameEn(FLASH_CARD);
        productData4.setProductNameTh(CREDIT);
        productData4.setProductDescEn(PRODUCT_DES);
        productData4.setProductDescTh(PRODUCT_DES);
        productData4.setIconId(ICON_FLASH);

        ProductData productData5 = new ProductData();
        productData5.setRslCode(RC);
        productData5.setContentLink(CONTENT_LINK_FLASH);
        productData5.setProductNameEn(FLASH_CARD);
        productData5.setProductNameTh(CREDIT);
        productData5.setProductDescEn(PRODUCT_DES);
        productData5.setProductDescTh(PRODUCT_DES);
        productData5.setIconId(ICON_FLASH);

        ProductData productData6 = new ProductData();
        productData6.setRslCode(RC);
        productData6.setContentLink(CONTENT_LINK_FLASH);
        productData6.setProductNameEn(FLASH_CARD);
        productData6.setProductNameTh(CREDIT);
        productData6.setProductDescEn(PRODUCT_DES);
        productData6.setProductDescTh(PRODUCT_DES);
        productData6.setIconId(ICON_FLASH);


        ProductData productData7= new ProductData();
        productData7.setRslCode(RC);
        productData7.setContentLink(CONTENT_LINK_FLASH);
        productData7.setProductNameEn(CASH_2_GO);
        productData7.setProductNameTh(LOAN);
        productData7.setProductDescEn(PRODUCT_DES);
        productData7.setProductDescTh(PRODUCT_DES);
        productData7.setIconId(ICON_C2G);

        productDataList.add(productData);
        productDataList.add(productData1);
        productDataList.add(productData2);
        productDataList.add(productData3);
        productDataList.add(productData4);
        productDataList.add(productData5);
        productDataList.add(productData6);
        productDataList.add(productData7);
        return productDataList;
    }

    public List<ProductData>  getProductsCredit() {
        List<ProductData> productDataList = new ArrayList();
        ProductData productData = new ProductData();
        productData.setRslCode("VI");
        productData.setContentLink(CONTENT_LINK_VI_VB);
        productData.setProductNameEn(PRODUCT_NAME_VI);
        productData.setProductNameTh(PRODUCT_NAME_VI);
        productData.setProductDescEn(PRODUCT_DES_CREDIT);
        productData.setProductDescTh(PRODUCT_DES_CREDIT);
        productData.setIconId(ICON_VI);

        ProductData productData1= new ProductData();
        productData1.setRslCode("VB");
        productData1.setContentLink(CONTENT_LINK_VI_VB);
        productData1.setProductNameEn(PRODUCT_NAME_VB);
        productData1.setProductNameTh(PRODUCT_NAME_VB);
        productData1.setProductDescEn(PRODUCT_DES_CREDIT_VB);
        productData1.setProductDescTh(PRODUCT_DES_CREDIT_VB);
        productData1.setIconId(ICON_C2G);

        ProductData productData2= new ProductData();
        productData2.setRslCode("VJ");
        productData2.setContentLink("https://www-uat.tau2904.com/th/personal/credit-cards/card-type/ttb-absolute?inapp=y&dl=n");
        productData2.setProductNameEn(PRODUCT_NAME_VJ);
        productData2.setProductNameTh(PRODUCT_NAME_VJ);
        productData2.setProductDescEn(PRODUCT_DES);
        productData2.setProductDescTh(PRODUCT_DES);
        productData2.setIconId(ICON_VJ);

        ProductData productData3 = new ProductData();
        productData3.setRslCode("VP");
        productData3.setContentLink("https://www-uat.tau2904.com/th/personal/credit-cards/card-type/ttb-so-fast?inapp=y&dl=n");
        productData3.setProductNameEn(PRODUCT_NAME_VP);
        productData3.setProductNameTh(PRODUCT_NAME_VP);
        productData3.setProductDescEn(PRODUCT_DES_CREDIT_VP);
        productData3.setProductDescTh(PRODUCT_DES_CREDIT_VP);
        productData3.setIconId(ICON_SO_FAST);

        ProductData productData4 = new ProductData();
        productData4.setRslCode("VM");
        productData4.setContentLink("https://www-uat.tau2904.com/th/personal/credit-cards/card-type/ttb-so-smart?inapp=y&dl=n");
        productData4.setProductNameEn(PRODUCT_NAME_VM);
        productData4.setProductNameTh(PRODUCT_NAME_VM);
        productData4.setProductDescEn(PRODUCT_DES_CREDIT_VM);
        productData4.setProductDescTh(PRODUCT_DES_CREDIT_VM);
        productData4.setIconId(ICON_VM);

        ProductData productData5 = new ProductData();
        productData5.setRslCode("VH");
        productData5.setContentLink("https://www-uat.tau2904.com/th/personal/credit-cards/card-type/ttb-so-chill?inapp=y&dl=n");
        productData5.setProductNameEn(PRODUCT_NAME_VH);
        productData5.setProductNameTh(PRODUCT_NAME_VH);
        productData5.setProductDescEn(PRODUCT_DES_CREDIT_VH);
        productData5.setProductDescTh(PRODUCT_DES_CREDIT_VH);
        productData5.setIconId(ICON_VH);

        productDataList.add(productData);
        productDataList.add(productData1);
        productDataList.add(productData2);
        productDataList.add(productData3);
        productDataList.add(productData4);
        productDataList.add(productData5);
        return productDataList;
    }



    public TmbOneServiceResponse<List<CommonData>> getAllConfig(String correlationId,String search) {
        TmbOneServiceResponse<List<CommonData>> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();

        try {
            ResponseEntity<TmbOneServiceResponse<List<CommonData>>> nodeTextResponse = commonServiceClient.getCommonConfig(correlationId,search);
            oneTmbOneServiceResponse.setData(nodeTextResponse.getBody().getData());

        } catch (Exception e) {
            logger.error("get all config fail: ", e);

        }

        return oneTmbOneServiceResponse;
    }

    public Boolean checkPreloadConfig(TmbOneServiceResponse<List<CommonData>> commonDataList, LoanPreloadRequest loanPreloadRequest) {

        List<AllowCashDayOne> allowCashDayOnes = commonDataList.getData().get(0).getAllowCashDayOnes();

        return allowCashDayOnes.get(0).getAllowCashDayOne().contains(loanPreloadRequest.getProductCode());

    }
}
