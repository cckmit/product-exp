package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.model.LoanOnlineInterestRate;
import com.tmb.common.model.LoanOnlineRangeIncome;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.legacy.rsl.common.ob.facility.Facility;
import com.tmb.common.model.legacy.rsl.ws.facility.response.ResponseFacility;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionCreateApplicationClient;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionGetFacilityInfoClient;
import com.tmb.oneapp.productsexpservice.model.loan.LoanSubmissionResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class LoanSubmissionCustomerService {

    private final LoanSubmissionGetFacilityInfoClient getFacilityInfoClient;
    private final LoanSubmissionCreateApplicationClient createApplicationClient;
    private final CommonServiceClient commonServiceClient;


    public List<LoanSubmissionResponse> getCustomerInfo(Long caId) throws ServiceException, RemoteException {
        LoanSubmissionResponse response = new LoanSubmissionResponse();
        List<LoanSubmissionResponse> responseList = new ArrayList<>();

        Facility facilityInfo = getFacility(caId);
        TmbOneServiceResponse<List<LoanOnlineInterestRate>> interestRateAll = getInterestRateAll();
        TmbOneServiceResponse<List<LoanOnlineRangeIncome>> rangeIncomeAll = getRangeIncomeAll();

        response.setAccountNo(facilityInfo.getDisburstAccountNo());
        response.setAccountName(facilityInfo.getAccountName());
        response.setTenure(facilityInfo.getTenure());


        response.setMonthlyIncome(interestRateAll.getData().get(0).getSalary());
        response.setMaxAmount(rangeIncomeAll.getData().get(0).getRangeIncomeMaz());
        response.setMinAmount(rangeIncomeAll.getData().get(0).getRangeIncomeMin());



        //response.setInterestRate(applyProductData.getData().get(0).getApplyCreditCards());

        responseList.add(response);
        return responseList;

    }



    private Facility getFacility(Long caID) throws ServiceException, RemoteException {
        try {
            ResponseFacility getFacilityResp = getFacilityInfoClient.searchFacilityInfoByCaID(caID);
            return getFacilityResp.getBody().getFacilities()[0];
        } catch (Exception e) {
            throw e;
        }
    }

    public TmbOneServiceResponse<List<LoanOnlineInterestRate>> getInterestRateAll() {
        TmbOneServiceResponse<List<LoanOnlineInterestRate>> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();

        try {
            ResponseEntity<TmbOneServiceResponse<List<LoanOnlineInterestRate>>> nodeTextResponse = commonServiceClient.getInterestRateAll();
            oneTmbOneServiceResponse.setData(nodeTextResponse.getBody().getData());

        } catch (Exception e) {
            throw e;
        }

        return oneTmbOneServiceResponse;
    }

    public TmbOneServiceResponse<List<LoanOnlineRangeIncome>> getRangeIncomeAll() {
        TmbOneServiceResponse<List<LoanOnlineRangeIncome>> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();

        try {
            ResponseEntity<TmbOneServiceResponse<List<LoanOnlineRangeIncome>>> nodeTextResponse = commonServiceClient.getRangeIncomeAll();
            oneTmbOneServiceResponse.setData(nodeTextResponse.getBody().getData());

        } catch (Exception e) {
            throw e;
        }

        return oneTmbOneServiceResponse;
    }
}
