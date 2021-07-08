package com.tmb.oneapp.productsexpservice.feignclients.loansubmission;

import com.tmb.common.model.legacy.rsl.ws.instant.submit.request.Body;
import com.tmb.common.model.legacy.rsl.ws.instant.submit.request.Header;
import com.tmb.common.model.legacy.rsl.ws.instant.submit.request.RequestInstantLoanSubmit;
import com.tmb.common.model.legacy.rsl.ws.instant.submit.response.ResponseInstantLoanSubmit;
import com.tmb.common.model.legacy.rsl.ws.loan.submission.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.rpc.ServiceException;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.UUID;

@Service
public class LoanSubmissionInstantLoanSubmitApplicationClient {

	@Value("${loan-submission-submit-application.url}")
	private String submitApplicationUrl;

	LoanSubmissionInstantLoanSubmitApplicationServiceLocator locator = new LoanSubmissionInstantLoanSubmitApplicationServiceLocator();

	private static final String CHANNEL = "MIB";
	private static final String MODULE = "3";

	public void setLocator(LoanSubmissionInstantLoanSubmitApplicationServiceLocator locator) {
		this.locator = locator;
	}

	public ResponseInstantLoanSubmit submitApplication(BigDecimal caID, String submitFlag)
			throws RemoteException, ServiceException {
		locator.setLoanSubmissionInstantLoanSubmitApplicationEndpointAddress(submitApplicationUrl);

		LoanSubmissionInstantLoanSubmitApplicationSoapBindingStub stub = (LoanSubmissionInstantLoanSubmitApplicationSoapBindingStub) locator
				.getLoanSubmissionInstantLoanSubmitApplication();
		RequestInstantLoanSubmit request = new RequestInstantLoanSubmit();
		Header header = new Header();
		header.setChannel(CHANNEL);
		header.setModule(MODULE);
		header.setRequestID(UUID.randomUUID().toString());
		Body body = new Body();
		body.setCaId(caID);
		body.setSubmittedFlag(submitFlag);
		request.setBody(body);
		request.setHeader(header);

		return stub.submitInstantLoanApplication(request);
	}

}
