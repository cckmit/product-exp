package com.tmb.oneapp.productsexpservice.feignclients.loansubmission;

import com.tmb.common.model.legacy.rsl.ws.loan.submission.LoanSubmissionInstantLoanCreateApplicationServiceLocator;
import org.springframework.stereotype.Service;

@Service
public class LoanSubmissionCreateApplicationClient {
//    @Value("${loan-submission-create-application.url}")
//    private String createApplication;

    LoanSubmissionInstantLoanCreateApplicationServiceLocator locator = new LoanSubmissionInstantLoanCreateApplicationServiceLocator();
    private static final String CHANNEL = "MIB";
    private static final String MODULE = "3";

    public void setLocator(LoanSubmissionInstantLoanCreateApplicationServiceLocator locator) {
        this.locator = locator;
    }

//    public ResponseInstantLoanCreateApplication submitRegister(InstantApplication instantApplication, String transactionType) throws RemoteException, ServiceException {
//        locator.setLoanSubmissionInstantLoanCreateApplicationEndpointAddress(createApplication);
//
//        LoanSubmissionInstantLoanCreateApplicationSoapBindingStub stub = (LoanSubmissionInstantLoanCreateApplicationSoapBindingStub) locator.getLoanSubmissionInstantLoanCreateApplication();
//
//        RequestInstantLoanCreateApplication request = new RequestInstantLoanCreateApplication();
//
//        Header header = new Header();
//        header.setChannel(CHANNEL);
//        header.setModule(MODULE);
//        header.setRequestID(UUID.randomUUID().toString());
//        request.setHeader(header);
//
//        Body body = new Body();
//        body.setInstantApplication(instantApplication);
//        body.setTransactionType(transactionType);
//        request.setBody(body);
//
//        return stub.createInstantLoanApplication(request);
//
//    }
}
