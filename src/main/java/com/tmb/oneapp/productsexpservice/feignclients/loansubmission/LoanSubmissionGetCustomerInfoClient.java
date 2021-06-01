package com.tmb.oneapp.productsexpservice.feignclients.loansubmission;

import com.tmb.common.model.legacy.rsl.ws.individual.request.Body;
import com.tmb.common.model.legacy.rsl.ws.individual.request.Header;
import com.tmb.common.model.legacy.rsl.ws.individual.request.RequestIndividual;
import com.tmb.common.model.legacy.rsl.ws.individual.response.ResponseIndividual;
import com.tmb.common.model.legacy.rsl.ws.loan.submission.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import java.util.UUID;

@Service
public class LoanSubmissionGetCustomerInfoClient {
    @Value("${loan-submission-get-customer-info.url}")
    private String getCustomerInfoUrl;

    LoanSubmissionGetCustomerInfoServiceLocator locator = new LoanSubmissionGetCustomerInfoServiceLocator();

    private static final String CHANNEL = "MIB";
    private static final String MODULE = "3";

    public void setLocator(LoanSubmissionGetCustomerInfoServiceLocator locator) {
        this.locator = locator;
    }

    public ResponseIndividual searchCustomerInfoByCaID(long caID) throws ServiceException, RemoteException, ServiceException {
        locator.setLoanSubmissionGetCustomerInfoEndpointAddress(getCustomerInfoUrl);

        LoanSubmissionGetCustomerInfoSoapBindingStub stub = (LoanSubmissionGetCustomerInfoSoapBindingStub) locator.
                getLoanSubmissionGetCustomerInfo();

        RequestIndividual req = new RequestIndividual();

        Header header = new Header();
        header.setChannel(CHANNEL);
        header.setModule(MODULE);
        header.setRequestID(UUID.randomUUID().toString());
        req.setHeader(header);

        Body body = new Body();
        body.setCaID(caID);
        req.setBody(body);

        return stub.searchCustomerInfoByCaID(req);
    }

}
