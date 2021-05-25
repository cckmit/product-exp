package com.tmb.oneapp.productsexpservice.feignclients.loansubmission;

import java.rmi.RemoteException;
import java.util.UUID;

import javax.xml.rpc.ServiceException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tmb.common.model.legacy.rsl.ws.instant.eligible.customer.request.RequestInstantLoanGetCustInfo;
import com.tmb.common.model.legacy.rsl.ws.instant.eligible.customer.response.ResponseInstantLoanGetCustInfo;
import com.tmb.common.model.legacy.rsl.ws.loan.submission.LoanSubmissionInstantLoanGetCustomerInfoServiceLocator;
import com.tmb.common.model.legacy.rsl.ws.loan.submission.LoanSubmissionInstantLoanGetCustomerInfoSoapBindingStub;

@Service
public class LoanInstantGetCustomerInfoClient {

	@Value("${loan-submission-instance-profile-info.url}")
	private String instanctLoandCustomerInfoUrl;

	LoanSubmissionInstantLoanGetCustomerInfoServiceLocator locator = new LoanSubmissionInstantLoanGetCustomerInfoServiceLocator();

	private static final String CHANNEL = "MIB";
	private static final String MODULE = "3";

	public void setLocator(LoanSubmissionInstantLoanGetCustomerInfoServiceLocator locator) {
		this.locator = locator;
	}

	public ResponseInstantLoanGetCustInfo getInstantCustomerInfo(String categoryCode)
			throws RemoteException, ServiceException {
		locator.setLoanSubmissionInstantLoanGetCustomerInfoEndpointAddress(instanctLoandCustomerInfoUrl);

		LoanSubmissionInstantLoanGetCustomerInfoSoapBindingStub stub = (LoanSubmissionInstantLoanGetCustomerInfoSoapBindingStub) locator
				.getLoanSubmissionInstantLoanGetCustomerInfo();
		RequestInstantLoanGetCustInfo request = new RequestInstantLoanGetCustInfo();
		com.tmb.common.model.legacy.rsl.ws.instant.eligible.customer.request.Header header = new com.tmb.common.model.legacy.rsl.ws.instant.eligible.customer.request.Header();
		header.setChannel(CHANNEL);
		header.setModule(MODULE);
		header.setRequestID(UUID.randomUUID().toString());
		com.tmb.common.model.legacy.rsl.ws.instant.eligible.customer.request.Body body = new com.tmb.common.model.legacy.rsl.ws.instant.eligible.customer.request.Body();
		request.setBody(body);
		request.setHeader(header);

		return stub.getInstantCustomerInfo(request);
	}

}
