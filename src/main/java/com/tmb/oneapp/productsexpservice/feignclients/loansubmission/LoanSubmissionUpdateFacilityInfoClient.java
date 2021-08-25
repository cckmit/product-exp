package com.tmb.oneapp.productsexpservice.feignclients.loansubmission;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.legacy.rsl.common.ob.facility.Facility;
import com.tmb.common.model.legacy.rsl.ws.facility.update.request.Body;
import com.tmb.common.model.legacy.rsl.ws.facility.update.request.Header;
import com.tmb.common.model.legacy.rsl.ws.facility.update.request.RequestFacility;
import com.tmb.common.model.legacy.rsl.ws.facility.update.response.ResponseFacility;
import com.tmb.common.model.legacy.rsl.ws.loan.submission.LoanSubmissionUpdateFacilityServiceLocator;
import com.tmb.common.model.legacy.rsl.ws.loan.submission.LoanSubmissionUpdateFacilitySoapBindingStub;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import java.util.UUID;

@Service
public class LoanSubmissionUpdateFacilityInfoClient {

    @Value("${loan-submission-update-facility-info.url}")
    private String updateFacilityInfoUrl;

     LoanSubmissionUpdateFacilityServiceLocator locator = new LoanSubmissionUpdateFacilityServiceLocator();
    private final ObjectMapper mapper;
    private static final TMBLogger<LoanSubmissionUpdateFacilityInfoClient> logger = new TMBLogger<>(LoanSubmissionUpdateFacilityInfoClient.class);


    private static final String CHANNEL = "MIB";
    private static final String MODULE = "3";

    public void setLocator(LoanSubmissionUpdateFacilityServiceLocator locator) {
        this.locator = locator;
    }

    public LoanSubmissionUpdateFacilityInfoClient(ObjectMapper mapper) {
        this.mapper = mapper;
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

    }

    public ResponseFacility updateFacilityInfo(Facility facility) throws ServiceException, TMBCommonException {
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


        try {
            ResponseFacility responseFacility = stub.updateFacilityInfo(req);
            logger.info("LoanSubmissionUpdateCustomer Response: {}",mapper.writeValueAsString(responseFacility));
            return responseFacility;
        }catch (RemoteException | JsonProcessingException e) {
            throw new TMBCommonException("RSL0001", "rsl connection error", "rsl-service", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }


}
