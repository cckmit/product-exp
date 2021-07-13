package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.model.legacy.rsl.common.ob.application.InstantApplication;
import com.tmb.common.model.legacy.rsl.common.ob.creditcard.InstantCreditCard;
import com.tmb.common.model.legacy.rsl.common.ob.facility.InstantFacility;
import com.tmb.common.model.legacy.rsl.ob.individual.InstantIndividual;
import com.tmb.common.model.legacy.rsl.ws.instant.application.create.response.ResponseInstantLoanCreateApplication;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionCreateApplicationClient;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanSubmitRegisterRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;

@Service
@AllArgsConstructor
public class LoanSubmissionCreateApplicationService {
    private final LoanSubmissionCreateApplicationClient createApplicationClient;


    public ResponseInstantLoanCreateApplication submitRegisterApplication(LoanSubmitRegisterRequest request, String transactionType) throws ServiceException, RemoteException {

        InstantApplication instantApplication = new InstantApplication();
        instantApplication.setNatureOfRequest(request.getNatureOfRequest());
        instantApplication.setAppType(request.getAppType());
        instantApplication.setBranchCode(request.getBranchCode());
        instantApplication.setBookBranchCode(request.getBookBranchCode());
        instantApplication.setSaleChannel(request.getSaleChannel());
        instantApplication.setAuthenCode(request.getAuthenCode());
        instantApplication.setNcbConsentFlag(request.getNcbConsentFlag());
        instantApplication.setNatureOfRequest(request.getNatureOfRequest());

        if (request.getAppType().equals("CC")) {
            instantApplication = mapProductTypeCC(request, instantApplication);
        } else {
            instantApplication = mapProductTypeNoneCC(request, instantApplication);
        }

        ResponseInstantLoanCreateApplication loanCreateApplication = createApplication(instantApplication, transactionType);
        return loanCreateApplication;

    }

    private InstantApplication mapProductTypeCC(LoanSubmitRegisterRequest loanSubmitRegisterRequest, InstantApplication instantApplication) {
        var individuals = new InstantIndividual[1];
        individuals[0] = mapIndividual(loanSubmitRegisterRequest);
        var creditCards = new InstantCreditCard[1];
        creditCards[0] = mapCreditCard(loanSubmitRegisterRequest);
        individuals[0].setCreditCards(creditCards);
        instantApplication.setIndividuals(individuals);
        return instantApplication;
    }

    private InstantApplication mapProductTypeNoneCC(LoanSubmitRegisterRequest loanSubmitRegisterRequest, InstantApplication instantApplication) {
        var individuals = new InstantIndividual[1];
        individuals[0] = mapIndividual(loanSubmitRegisterRequest);
        var facilities = new InstantFacility[1];
        facilities[0] = mapFacility(loanSubmitRegisterRequest);
        instantApplication.setFacilities(facilities);
        instantApplication.setIndividuals(individuals);
        return instantApplication;
    }

    private InstantCreditCard mapCreditCard(LoanSubmitRegisterRequest loanSubmitRegisterRequest) {
        InstantCreditCard creditCard = new InstantCreditCard();
        creditCard.setCardInd(loanSubmitRegisterRequest.getCardInd());
        creditCard.setProductType(loanSubmitRegisterRequest.getProductType());
        creditCard.setCardBrand(loanSubmitRegisterRequest.getCardBrand());
        creditCard.setCampaignCode((loanSubmitRegisterRequest.getCampaignCode()));
        creditCard.setRequestCreditLimit(loanSubmitRegisterRequest.getRequestCreditLimit());
        creditCard.setPaymentMethod((loanSubmitRegisterRequest.getPaymentMethod()));
        creditCard.setMailPreference(loanSubmitRegisterRequest.getMailPreference());
        creditCard.setCardDeliveryAddress(loanSubmitRegisterRequest.getCardDelivery());
        creditCard.setCardBrand(loanSubmitRegisterRequest.getCardBrand());
        creditCard.setCardInd(loanSubmitRegisterRequest.getCardInd());
        return creditCard;
    }

    private InstantFacility mapFacility(LoanSubmitRegisterRequest loanSubmitRegisterRequest) {
        InstantFacility facility = new InstantFacility();
        facility.setFacilityCode(loanSubmitRegisterRequest.getFacilityCode());
        facility.setProductCode(loanSubmitRegisterRequest.getProductCode());
        facility.setCaCampaignCode(loanSubmitRegisterRequest.getCaCampaignCode());
        facility.setLimitApplied(loanSubmitRegisterRequest.getLimitApplied());
        facility.setMonthlyInstallment(loanSubmitRegisterRequest.getMonthlyInstallment());
        facility.setTenure(loanSubmitRegisterRequest.getTenure());
        facility.setInterestRate(loanSubmitRegisterRequest.getInterestRate());
        facility.setPaymentDueDate(loanSubmitRegisterRequest.getPaymentDueDate());
        facility.setFirstPaymentDueDate(loanSubmitRegisterRequest.getFirstPaymentDueDate());
        facility.setLoanWithOtherBank(loanSubmitRegisterRequest.getLoanWithOtherBank());
        facility.setConsiderLoanWithOtherBank(loanSubmitRegisterRequest.getConsiderLoanWithOtherBank());
        facility.setDisburstBankName(loanSubmitRegisterRequest.getDisburstBankName());
        facility.setDisburstAccountName(loanSubmitRegisterRequest.getDisburstAccountName());
        facility.setDisburstAccountNo(loanSubmitRegisterRequest.getDisburstAccountNo());
        facility.setPaymentMethod(loanSubmitRegisterRequest.getPaymentMethod());
        facility.setPaymentAccountName(loanSubmitRegisterRequest.getPaymentAccountName());
        facility.setPaymentAccountNo(loanSubmitRegisterRequest.getPaymentAccountNo());
        facility.setMailingPreference(loanSubmitRegisterRequest.getMailingPreference());
        facility.setMailingPreference(loanSubmitRegisterRequest.getMailingPreference());
        return facility;
    }

    private InstantIndividual mapIndividual(LoanSubmitRegisterRequest loanSubmitRegisterRequest) {
        InstantIndividual individual = new InstantIndividual();
        individual.setCifRelCode(loanSubmitRegisterRequest.getCifRelCode());
        individual.setIdType1(loanSubmitRegisterRequest.getIdType1());
        individual.setIdNo1(loanSubmitRegisterRequest.getIdNo1());
        individual.setHostCifNo(loanSubmitRegisterRequest.getHostCifNo());
        individual.setThaiName(loanSubmitRegisterRequest.getThaiName());
        individual.setThaiSurName(loanSubmitRegisterRequest.getThaiSurName());
        individual.setMobileNo(loanSubmitRegisterRequest.getMobileNo());
        individual.setBirthDate(loanSubmitRegisterRequest.getBirthDate());
        individual.setNcbConsentFlag(loanSubmitRegisterRequest.getNcbConsentFlag());
        individual.setNationality(loanSubmitRegisterRequest.getNationality());
        individual.setEmploymentStatus(loanSubmitRegisterRequest.getEmploymentStatus());
        individual.setIncomeType(loanSubmitRegisterRequest.getIncomeType());
        return individual;
    }

    private ResponseInstantLoanCreateApplication createApplication(InstantApplication instantApplication, String transactionType) throws ServiceException, RemoteException {
        try {
            return createApplicationClient.submitRegister(instantApplication, transactionType);
        } catch (Exception e) {
            throw e;
        }
    }
}
