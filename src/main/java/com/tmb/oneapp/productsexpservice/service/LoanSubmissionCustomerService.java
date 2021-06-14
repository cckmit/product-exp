package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.model.legacy.rsl.common.ob.application.InstantApplication;
import com.tmb.common.model.legacy.rsl.common.ob.facility.Facility;
import com.tmb.common.model.legacy.rsl.ws.facility.response.ResponseFacility;
import com.tmb.common.model.legacy.rsl.ws.instant.application.create.response.ResponseInstantLoanCreateApplication;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionCreateApplicationClient;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionGetFacilityInfoClient;
import com.tmb.oneapp.productsexpservice.model.loan.LoanSubmissionResponse;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanSubmitRegisterRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;

@Service
@AllArgsConstructor
public class LoanSubmissionCustomerService {

    private final LoanSubmissionGetFacilityInfoClient getFacilityInfoClient;
    private final LoanSubmissionCreateApplicationClient createApplicationClient;


    public LoanSubmissionResponse getCustomerInfo(Long caID) throws ServiceException, RemoteException {
        LoanSubmissionResponse response = new LoanSubmissionResponse();

        Facility facilityInfo = getFacility(caID);

        response.setAccountNo(facilityInfo.getDisburstAccountNo());
        response.setAccountName(facilityInfo.getAccountName());
        response.setTenure(facilityInfo.getTenure());

        return response;

    }

    public void submitRegisterApplication(LoanSubmitRegisterRequest request, String transactionType) throws ServiceException, RemoteException{
       // LoanSubmitRegisterRequest loanSubmitRegisterRequest = new LoanSubmitRegisterRequest();

        InstantApplication instantApplication = new InstantApplication();
//        instantApplication.se(request.get);

        ResponseInstantLoanCreateApplication loanCreateApplication = createApplication(instantApplication,transactionType);

//        loanSubmitRegisterRequest.setBonus(loanSubmitRegisterRequest.getBonus());
//        loanSubmitRegisterRequest.setApproveAmount(loanSubmitRegisterRequest.getApproveAmount());
//        loanSubmitRegisterRequest.setFeatureType(loanSubmitRegisterRequest.getFeatureType());
//        loanSubmitRegisterRequest.setRequestAmount(loanSubmitRegisterRequest.getRequestAmount());
//        loanSubmitRegisterRequest.setSummary(loanSubmitRegisterRequest.getSummary());
//        loanSubmitRegisterRequest.setDisburstAccountName(loanSubmitRegisterRequest.getDisburstAccountName());
//        loanSubmitRegisterRequest.setDisburstAccountNo(loanSubmitRegisterRequest.getDisburstAccountNo());
//        loanSubmitRegisterRequest.setTenure(loanSubmitRegisterRequest.getTenure());
//        loanSubmitRegisterRequest.setMonthlyPayment(loanSubmitRegisterRequest.getMonthlyPayment());
//        loanSubmitRegisterRequest.setStatusWorking(loanSubmitRegisterRequest.getStatusWorking());


    }

    private Facility getFacility(Long caID) throws ServiceException, RemoteException {
        try {
            ResponseFacility getFacilityResp = getFacilityInfoClient.searchFacilityInfoByCaID(caID);
            return getFacilityResp.getBody().getFacilities()[0];
        } catch (Exception e) {
            throw e;
        }
    }

    private ResponseInstantLoanCreateApplication createApplication(InstantApplication instantApplication, String transactionType) throws ServiceException, RemoteException {
        try {
           return createApplicationClient.submitRegister(instantApplication, transactionType);
        } catch (Exception e) {
            throw e;
        }
    }
}
