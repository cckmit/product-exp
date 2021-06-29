package com.tmb.oneapp.productsexpservice.feignclients.loansubmission;

import com.tmb.common.model.legacy.rsl.common.ob.facility.Facility;
import com.tmb.common.model.legacy.rsl.ws.facility.update.request.Body;
import com.tmb.common.model.legacy.rsl.ws.facility.update.request.Header;
import com.tmb.common.model.legacy.rsl.ws.facility.update.request.RequestFacility;
import com.tmb.common.model.legacy.rsl.ws.facility.update.response.ResponseFacility;
import com.tmb.common.model.legacy.rsl.ws.loan.submission.LoanSubmissionUpdateFacilityServiceLocator;
import com.tmb.common.model.legacy.rsl.ws.loan.submission.LoanSubmissionUpdateFacilitySoapBindingStub;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.rpc.ServiceException;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.UUID;

@Service
public class LoanSubmissionUpdateFacilityInfoClient {

    @Value("${loan-submission-update-facility-info.url}")
    private String updateFacilityInfoUrl;

     LoanSubmissionUpdateFacilityServiceLocator locator = new LoanSubmissionUpdateFacilityServiceLocator();

    private static final String CHANNEL = "MIB";
    private static final String MODULE = "3";

    public void setLocator(LoanSubmissionUpdateFacilityServiceLocator locator) {
        this.locator = locator;
    }


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
        facility.setTenure(BigDecimal.valueOf(facility.getFeature().getTenure()));
        body.setFacility(facility);
        req.setBody(body);

        return stub.updateFacilityInfo(req);
    }


}
