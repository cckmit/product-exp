package com.tmb.oneapp.productsexpservice.feignclients.loansubmission;

import com.tmb.common.model.legacy.rsl.ws.facility.request.Body;
import com.tmb.common.model.legacy.rsl.ws.facility.request.Header;
import com.tmb.common.model.legacy.rsl.ws.facility.request.RequestFacility;
import com.tmb.common.model.legacy.rsl.ws.facility.response.ResponseFacility;
import com.tmb.common.model.legacy.rsl.ws.loan.submission.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import java.util.UUID;

@Service
public class LoanSubmissionGetFacilityInfoClient {
    @Value("${loan-submission-get-facility-info.url}")
    private String getFacilityInfoUrl;

    LoanSubmissionGetFacilityInfoServiceLocator locator = new LoanSubmissionGetFacilityInfoServiceLocator();

    private static final String CHANNEL = "MIB";
    private static final String MODULE = "3";
    private void setLocator( LoanSubmissionGetFacilityInfoServiceLocator locator) {
        this.locator = locator;
    }

    public ResponseFacility searchFacilityInfoByCaID(Long caID) throws RemoteException, ServiceException {
        locator.setLoanSubmissionGetFacilityInfoEndpointAddress(getFacilityInfoUrl);

        LoanSubmissionGetFacilityInfoSoapBindingStub stub = (LoanSubmissionGetFacilityInfoSoapBindingStub) locator.getLoanSubmissionGetFacilityInfo();

        RequestFacility req = new RequestFacility();

        Header header = new Header();
        header.setChannel(CHANNEL);
        header.setModule(MODULE);
        header.setRequestID(UUID.randomUUID().toString());
        req.setHeader(header);

        Body body = new Body();
        body.setCaID(caID);
        req.setBody(body);

        return stub.searchFacilityInfoByCaID(req);
    }

}
