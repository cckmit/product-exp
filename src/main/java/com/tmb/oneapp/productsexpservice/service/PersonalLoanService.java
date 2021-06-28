package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.*;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanPreloadRequest;
import com.tmb.oneapp.productsexpservice.model.response.LoanPreloadResponse;
import com.tmb.oneapp.productsexpservice.model.response.loan.ApplyPersonalLoan;
import com.tmb.oneapp.productsexpservice.model.response.loan.ProductData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PersonalLoanService {

    private static final TMBLogger<PersonalLoanService> logger = new TMBLogger(PersonalLoanService.class);
    private final CommonServiceClient commonServiceClient;

    @Autowired
    public PersonalLoanService(CommonServiceClient commonServiceClient) {
        this.commonServiceClient = commonServiceClient;
    }

    public LoanPreloadResponse checkPreload(String correlationId, LoanPreloadRequest loanPreloadRequest) {

        LoanPreloadResponse loanPreloadResponse = new LoanPreloadResponse();
        TmbOneServiceResponse<List<CommonData>> configs = getAllConfig(correlationId, loanPreloadRequest.getSearch());
        loanPreloadResponse.setFlagePreload(checkPreloadConfig(configs, loanPreloadRequest));

        return loanPreloadResponse;
    }

    public ApplyPersonalLoan getProductsLoan(String correlationId, String search) {
        ProductData productLoanData = new ProductData();
        ProductData productCreditData = new ProductData();
        TmbOneServiceResponse<List<CommonData>> productList = getAllConfig(correlationId, search);

        ApplyPersonalLoan applyPersonalLoans = new ApplyPersonalLoan();
        ApplyPersonalLoan personalCreditLoan = new ApplyPersonalLoan();
        ApplyPersonalLoan personalFlashLoan = new ApplyPersonalLoan();

        for (CommonData commonData : productList.getData()) {
            Optional<ApplyProductData> personalLoans = commonData.getApplyPersonalLoans().stream().filter(a -> a.getRslCode().equals("RC")).findAny();
            if (personalLoans.isPresent()) {
                productLoanData.setRslCode(personalLoans.get().getRslCode());
                productLoanData.setContentLink(personalLoans.get().getContentLink());
                productLoanData.setProductNameEn(personalLoans.get().getProductNameEn());
                productLoanData.setProductNameTh(personalLoans.get().getProductNameTh());
                productLoanData.setProductDescEn(personalLoans.get().getProductDescEn());
                productLoanData.setProductDescTh(personalLoans.get().getProductDescTh());
                productLoanData.setIconId(personalLoans.get().getIconId());
            }

            Optional<ApplyProductData> personalLoansCredit = commonData.getApplyPersonalLoans().stream().filter(a -> a.getRslCode().equals("C2G")).findAny();
            if (personalLoansCredit.isPresent()) {
                productCreditData.setRslCode(personalLoansCredit.get().getRslCode());
                productCreditData.setContentLink(personalLoansCredit.get().getContentLink());
                productCreditData.setProductNameEn(personalLoansCredit.get().getProductNameEn());
                productCreditData.setProductNameTh(personalLoansCredit.get().getProductNameTh());
                productCreditData.setProductDescEn(personalLoansCredit.get().getProductDescEn());
                productCreditData.setProductDescTh(personalLoansCredit.get().getProductDescTh());
                productCreditData.setIconId(personalLoansCredit.get().getIconId());

            }

        }

        List<ProductData> productDataCreditList = new ArrayList();
        productDataCreditList.add(productCreditData);

        List<ProductData> productDataFlashList = new ArrayList();
        productDataFlashList.add(productLoanData);

        personalCreditLoan.setProductCreditList(productDataCreditList);
        personalFlashLoan.setProductFlashList(productDataFlashList);

        applyPersonalLoans.setProductFlashList(productDataFlashList);
        applyPersonalLoans.setProductCreditList(productDataCreditList);

        return applyPersonalLoans;
    }

    public List<ProductData> getProductsCredit(String correlationId, String search) {
        List<ProductData> productDataList = new ArrayList();

        TmbOneServiceResponse<List<CommonData>> productList = getAllConfig(correlationId, search);

        for (CommonData commonData : productList.getData()) {
            List<ApplyProductData> personalLoans = commonData.getApplyCreditCards();
            for (ApplyProductData applyCreditCards : personalLoans) {
                ProductData productData = new ProductData();
                productData.setRslCode(applyCreditCards.getRslCode());
                productData.setContentLink(applyCreditCards.getContentLink());
                productData.setProductNameEn(applyCreditCards.getProductNameEn());
                productData.setProductNameTh(applyCreditCards.getProductNameTh());
                productData.setProductDescEn(applyCreditCards.getProductDescEn());
                productData.setProductDescTh(applyCreditCards.getProductDescTh());
                productData.setIconId(applyCreditCards.getIconId());
                productDataList.add(productData);
            }
        }

        return productDataList;
    }


    public TmbOneServiceResponse<List<CommonData>> getAllConfig(String correlationId, String search) {
        TmbOneServiceResponse<List<CommonData>> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();

        try {
            ResponseEntity<TmbOneServiceResponse<List<CommonData>>> nodeTextResponse = commonServiceClient.getCommonConfig(correlationId, search);
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
