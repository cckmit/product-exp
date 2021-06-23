package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.legacy.rsl.common.ob.apprmemo.facility.ApprovalMemoFacility;
import com.tmb.common.model.legacy.rsl.common.ob.pricing.Pricing;
import com.tmb.common.model.legacy.rsl.ws.facility.response.ResponseFacility;
import com.tmb.common.model.legacy.rsl.ws.instant.calculate.uw.request.Body;
import com.tmb.common.model.legacy.rsl.ws.instant.calculate.uw.request.RequestInstantLoanCalUW;
import com.tmb.common.model.legacy.rsl.ws.instant.calculate.uw.response.ResponseInstantLoanCalUW;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionGetFacilityInfoClient;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionInstantLoanCalUWClient;
import com.tmb.oneapp.productsexpservice.model.flexiloan.InstantLoanCalUWResponse;
import com.tmb.oneapp.productsexpservice.model.request.loan.InstantLoanCalUWRequest;
import com.tmb.oneapp.productsexpservice.model.response.loan.LoanCustomerPricing;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.xml.rpc.ServiceException;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;


@Service
@AllArgsConstructor
public class LoanSubmissionInstantLoanCalUWService {
    private static final TMBLogger<LoanSubmissionInstantLoanCalUWService> logger = new TMBLogger<>(LoanSubmissionInstantLoanCalUWService.class);

    private final LoanSubmissionInstantLoanCalUWClient loanCalUWClient;
    private final LoanSubmissionGetFacilityInfoClient getFacilityInfoClient;

    static final String APPROVE = "APPROVE";
    static final String FLASH = "RC01";
    static final String C2G02 = "C2G02";

    public InstantLoanCalUWResponse checkCalculateUnderwriting(InstantLoanCalUWRequest request) throws ServiceException, RemoteException {

        RequestInstantLoanCalUW requestInstantLoanCalUW = new RequestInstantLoanCalUW();
        Body body = new Body();
        body.setCaId(request.getCaId());
        body.setTriggerFlag(request.getTriggerFlag());
        requestInstantLoanCalUW.setBody(body);

        return calculateUnderwriting(requestInstantLoanCalUW, request.getProduct());
    }

    private InstantLoanCalUWResponse parseResponse(ResponseFacility facilityInfo,
                                                   ResponseInstantLoanCalUW loanCalUWResponse,
                                                   String productCode
    ) {
        InstantLoanCalUWResponse response = new InstantLoanCalUWResponse();
        String underWriting = loanCalUWResponse.getBody().getUnderwritingResult();

        response.setStatus(underWriting);
        response.setProduct(productCode);

        if (underWriting.equals(APPROVE)) {
            if (productCode.equals(FLASH) && facilityInfo.getBody().getFacilities() != null) {
                response.setRequestAmount(facilityInfo.getBody().getFacilities()[0].getFeature().getRequestAmount());
                Pricing[] pricings = facilityInfo.getBody().getFacilities()[0].getPricings();
                List<LoanCustomerPricing> pricingList = new ArrayList<>();

                for (Pricing p : pricings) {
                    LoanCustomerPricing pricing = new LoanCustomerPricing();
                    if (p.getPricingType().equals("S")) {
                        pricing.setMonthFrom(p.getMonthFrom());
                        pricing.setMonthTo(p.getMonthTo());
                        pricing.setRateVariance(p.getRateVaraince().multiply(BigDecimal.valueOf(100)));
                        pricing.setYearTo(p.getYearTo());
                        pricing.setYearFrom(p.getYearFrom());
                        pricingList.add(pricing);
                    }
                }
                response.setPricings(pricingList);
            }else if (productCode.equals(C2G02) && loanCalUWResponse.getBody().getApprovalMemoFacilities() != null){
                response.setRequestAmount(loanCalUWResponse.getBody().getApprovalMemoFacilities()[0].getOutstandingBalance());
            }


            if(loanCalUWResponse.getBody().getApprovalMemoFacilities()!=null){
                ApprovalMemoFacility approvalMemoFacility = loanCalUWResponse.getBody().getApprovalMemoFacilities()[0];

                response.setTenor(approvalMemoFacility.getTenor());
                response.setPayDate(approvalMemoFacility.getPayDate());
                response.setInterestRate(approvalMemoFacility.getInterestRate());
                response.setDisburstAccountNo(approvalMemoFacility.getDisburstAccountNo());
                response.setCreditLimit(approvalMemoFacility.getCreditLimit());

                response.setFirstPaymentDueDate(approvalMemoFacility.getFirstPaymentDueDate());
                response.setLoanContractDate(approvalMemoFacility.getLoanContractDate());
                response.setInstallmentAmount(approvalMemoFacility.getInstallmentAmount());
                response.setRateType(approvalMemoFacility.getRateType());
                response.setRateTypePercent(approvalMemoFacility.getRateTypePercent());
            }
        }

        return response;
    }


    private InstantLoanCalUWResponse calculateUnderwriting(RequestInstantLoanCalUW request, String productCode) throws ServiceException, RemoteException {
        try {
            ResponseInstantLoanCalUW responseInstantLoanCalUW = loanCalUWClient.getCalculateUnderwriting(request);
            ResponseFacility facilityInfo = new ResponseFacility();

            if (productCode.equals(FLASH)) {
                facilityInfo = getFacilityInfoClient.searchFacilityInfoByCaID(request.getBody().getCaId().longValue());
            }

            return parseResponse(facilityInfo, responseInstantLoanCalUW, productCode);
        } catch (Exception e) {
            logger.error("calculateUnderwriting got exception:{}", e);
            throw e;
        }
    }
}
