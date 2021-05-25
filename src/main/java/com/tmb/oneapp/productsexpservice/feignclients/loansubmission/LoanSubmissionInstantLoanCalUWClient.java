package com.tmb.oneapp.productsexpservice.feignclients.loansubmission;

import com.tmb.common.model.legacy.rsl.ws.instant.calculate.uw.request.Body;
import com.tmb.common.model.legacy.rsl.ws.instant.calculate.uw.request.Header;
import com.tmb.common.model.legacy.rsl.ws.instant.calculate.uw.request.RequestInstantLoanCalUW;
import com.tmb.common.model.legacy.rsl.ws.instant.calculate.uw.response.ResponseInstantLoanCalUW;
import com.tmb.common.model.legacy.rsl.ws.loan.submission.LoanSubmissionInstantLoanCalUWServiceLocator;
import com.tmb.common.model.legacy.rsl.ws.loan.submission.LoanSubmissionInstantLoanCalUWSoapBindingStub;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.xml.rpc.ServiceException;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.UUID;

@Service
public class LoanSubmissionInstantLoanCalUWClient {
    @Value("${loan-submission-instant-loan-cal-uw.url}")
    LoanSubmissionInstantLoanCalUWServiceLocator locator = new LoanSubmissionInstantLoanCalUWServiceLocator();

    private static final String CHANNEL = "MIB";
    private static final String MODULE = "3";
    private void setLocator(LoanSubmissionInstantLoanCalUWServiceLocator locator) {
        this.locator = locator;
    }

    public ResponseInstantLoanCalUW getCalculateUnderwriting() throws RemoteException, ServiceException {
        LoanSubmissionInstantLoanCalUWSoapBindingStub stub = (LoanSubmissionInstantLoanCalUWSoapBindingStub) locator.getLoanSubmissionInstantLoanCalUW();

        RequestInstantLoanCalUW req = new RequestInstantLoanCalUW();

        Header header = new Header();
        header.setChannel(CHANNEL);
        header.setModule(MODULE);
        header.setRequestID(UUID.randomUUID().toString());
        req.setHeader(header);
//
//        Body body = new Body();
//        body.setCaId(caID);
//        req.setBody(body);

        return stub.calculateUnderwriting(req);
    }
}
