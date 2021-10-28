package com.tmb.oneapp.productsexpservice.feignclients.loansubmission;

import java.rmi.RemoteException;
import java.util.UUID;

import javax.xml.rpc.ServiceException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.legacy.rsl.ws.instant.eligible.customer.request.RequestInstantLoanGetCustInfo;
import com.tmb.common.model.legacy.rsl.ws.instant.eligible.customer.response.ResponseInstantLoanGetCustInfo;
import com.tmb.common.model.legacy.rsl.ws.loan.submission.LoanSubmissionInstantLoanGetCustomerInfoServiceLocator;
import com.tmb.common.model.legacy.rsl.ws.loan.submission.LoanSubmissionInstantLoanGetCustomerInfoSoapBindingStub;

@Service
public class LoanInstantGetCustomerInfoClient {
	
	private static final TMBLogger<LoanInstantGetCustomerInfoClient> logger = new TMBLogger<>(LoanInstantGetCustomerInfoClient.class);
	private final ObjectMapper mapper;
	
	@Value("${loan-submission-instance-profile-info.url}")
	private String instanctLoandCustomerInfoUrl;

	LoanSubmissionInstantLoanGetCustomerInfoServiceLocator locator = new LoanSubmissionInstantLoanGetCustomerInfoServiceLocator();

	private static final String CHANNEL = "MIB";
	private static final String MODULE = "3";

	public LoanInstantGetCustomerInfoClient(ObjectMapper mapper) {
		this.mapper = mapper;
		mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
	}
	 
	public void setLocator(LoanSubmissionInstantLoanGetCustomerInfoServiceLocator locator) {
		this.locator = locator;
	}
	
	/**
	 * Get instant customer information
	 * @param rmNo
	 * @return
	 * @throws RemoteException
	 * @throws ServiceException
	 * @throws JsonProcessingException 
	 */
	public ResponseInstantLoanGetCustInfo getInstantCustomerInfo(String rmNo)
			throws RemoteException, ServiceException, JsonProcessingException {
		logger.info("getInstantCustomerInfo by rm no.: {}", rmNo);
		locator.setLoanSubmissionInstantLoanGetCustomerInfoEndpointAddress(instanctLoandCustomerInfoUrl);

		LoanSubmissionInstantLoanGetCustomerInfoSoapBindingStub stub = (LoanSubmissionInstantLoanGetCustomerInfoSoapBindingStub) locator
				.getLoanSubmissionInstantLoanGetCustomerInfo();
		RequestInstantLoanGetCustInfo request = new RequestInstantLoanGetCustInfo();
		com.tmb.common.model.legacy.rsl.ws.instant.eligible.customer.request.Header header = new com.tmb.common.model.legacy.rsl.ws.instant.eligible.customer.request.Header();
		header.setChannel(CHANNEL);
		header.setModule(MODULE);
		header.setRequestID(UUID.randomUUID().toString());
		com.tmb.common.model.legacy.rsl.ws.instant.eligible.customer.request.Body body = new com.tmb.common.model.legacy.rsl.ws.instant.eligible.customer.request.Body();
		body.setRmNo(rmNo);
		request.setBody(body);
		request.setHeader(header);
		try {
            ResponseInstantLoanGetCustInfo response = stub.getInstantCustomerInfo(request);
            logger.info("LoanSubmissionGetCustomerInfo Response: {}", mapper.writeValueAsString(response));
            return response;
		} catch (Exception e) {
        	throw e;
		}
	}

}
