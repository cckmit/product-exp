package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.model.legacy.rsl.ws.creditcard.response.ResponseCreditcard;
import com.tmb.common.model.legacy.rsl.ws.facility.response.ResponseFacility;
import com.tmb.common.model.legacy.rsl.ws.instant.eligible.customer.response.ResponseInstantLoanGetCustInfo;
import com.tmb.common.model.legacy.rsl.ws.loan.submission.LoanSubmissionGetCreditcardInfo;
import com.tmb.common.model.legacy.rsl.ws.loan.submission.LoanSubmissionInstantLoanGetCustomerInfo;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionGetCreditcardInfoClient;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionGetCustomerInfoClient;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionGetFacilityInfoClient;
import com.tmb.oneapp.productsexpservice.model.request.flexiloan.SubmissionInfoRequest;
import com.tmb.oneapp.productsexpservice.model.request.loan.LoanCustomerSubmissionRequest;
import com.tmb.oneapp.productsexpservice.model.response.flexiloan.SubmissionInfoResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;

@Service
@AllArgsConstructor
public class FlexiLoanService {

    private final LoanSubmissionGetFacilityInfoClient getFacilityInfoClient;
    private final LoanSubmissionGetCustomerInfoClient getCustomerInfoClient;
    private final LoanSubmissionGetCreditcardInfoClient getCreditcardInfoClient;

    public SubmissionInfoResponse getSubmissionInfo(String correlationId, SubmissionInfoRequest request) throws ServiceException, RemoteException {
        ResponseFacility facilityInfo = getFacilityInfoClient.searchFacilityInfoByCaID(request.getCaID());
        ResponseInstantLoanGetCustInfo customerInfo = getCustomerInfoClient.searchCustomerInfoByCaID(request.getCaID().toString());
        ResponseCreditcard creditcardInfo = getCreditcardInfoClient.searchCreditcardInfoByCaID(request.getCaID());
        return parseSubmissionInfoResponse(facilityInfo, customerInfo, creditcardInfo);
    }

    private SubmissionInfoResponse parseSubmissionInfoResponse(ResponseFacility facilityInfo,
                                                               ResponseInstantLoanGetCustInfo customerInfo,
                                                               ResponseCreditcard creditcardInfo) {
        SubmissionInfoResponse response = new SubmissionInfoResponse();
        return response;
    }

}
