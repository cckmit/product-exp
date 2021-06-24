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
        productData.setRslCode("RC");
        productData.setContentLink("https://www-uat.tau2904.com/th/personal/loans/personal-loan/flash-card");
        productData.setProductNameEn("flash card");
        productData.setProductNameTh("บัตรกดเงินสด");
        productData.setProductDescEn(PRODUCT_DES);
        productData.setProductDescTh(PRODUCT_DES);
        productData.setIconId("/product/apply_loan/ttb_flash.png");

        ProductData productData1= new ProductData();
        productData1.setRslCode("C2G");
        productData1.setContentLink("https://www-uat.tau2904.com/th/personal/loans/personal-loan/cash-2-go?inapp=y&dl=n");
        productData1.setProductNameEn("cash2go");
        productData1.setProductNameTh("สินเชื่อบุคคล");
        productData1.setProductDescEn(PRODUCT_DES);
        productData1.setProductDescTh(PRODUCT_DES);
        productData1.setIconId("/product/apply_loan/ttb_c2g.png");

        productDataList.add(productData);
        productDataList.add(productData1);
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
