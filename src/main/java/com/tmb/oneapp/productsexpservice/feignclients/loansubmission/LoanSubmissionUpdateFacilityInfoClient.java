package com.tmb.oneapp.productsexpservice.feignclients.loansubmission;

import com.tmb.common.model.legacy.rsl.common.ob.facility.Facility;
import com.tmb.common.model.legacy.rsl.ws.facility.update.request.Body;
import com.tmb.common.model.legacy.rsl.ws.facility.update.request.Header;
import com.tmb.common.model.legacy.rsl.ws.facility.update.request.RequestFacility;
import com.tmb.common.model.legacy.rsl.ws.facility.update.response.ResponseFacility;
import com.tmb.common.model.legacy.rsl.ws.loan.submission.LoanSubmissionUpdateFacilityServiceLocator;
import com.tmb.common.model.legacy.rsl.ws.loan.submission.LoanSubmissionUpdateFacilitySoapBindingStub;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import java.util.UUID;

@Service
@AllArgsConstructor
public class LoanSubmissionUpdateFacilityInfoClient {

    @Value("${loan-submission-update-facility-info.url}")
    private String updateFacilityInfoUrl;

     LoanSubmissionUpdateFacilityServiceLocator locator = new LoanSubmissionUpdateFacilityServiceLocator();

    private static final String CHANNEL = "MIB";
    private static final String MODULE = "3";

    public ResponseFacility updateFacilityInfo(Facility facility) throws RemoteException, ServiceException {
        locator.setLoanSubmissionUpdateFacilityEndpointAddress(updateFacilityInfoUrl);

        LoanSubmissionUpdateFacilitySoapBindingStub stub = (LoanSubmissionUpdateFacilitySoapBindingStub) locator.getLoanSubmissionUpdateFacility();

        RequestFacility req = new RequestFacility();

        Header header = new Header();
        header.setChannel(CHANNEL);
        header.setModule(MODULE);
        header.setRequestID(UUID.randomUUID().toString());
        req.setHeader(header);

        Body body = new Body();
        body.setFacility(facility);
        req.setBody(body);

        return stub.updateFacilityInfo(req);
    }


}
