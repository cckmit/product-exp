package com.tmb.oneapp.productsexpservice.feignclients.loansubmission;

import com.tmb.common.model.legacy.rsl.ws.application.request.Body;
import com.tmb.common.model.legacy.rsl.ws.application.request.Header;
import com.tmb.common.model.legacy.rsl.ws.application.request.RequestApplication;
import com.tmb.common.model.legacy.rsl.ws.application.response.ResponseApplication;
import com.tmb.common.model.legacy.rsl.ws.loan.submission.LoanSubmissionGetApplicationInfoServiceLocator;
import com.tmb.common.model.legacy.rsl.ws.loan.submission.LoanSubmissionGetApplicationInfoSoapBindingStub;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import java.util.UUID;

@Service
public class LoanSubmissionGetApplicationInfoClient {

	@Value("${loan-submission-get-application-info.url}")
	private String getApplicationInfoUrl;

	LoanSubmissionGetApplicationInfoServiceLocator locator = new LoanSubmissionGetApplicationInfoServiceLocator();

	private static final String CHANNEL = "MIB";
	private static final String MODULE = "3";

	public void setLocator(LoanSubmissionGetApplicationInfoServiceLocator locator) {
		this.locator = locator;
	}

	public ResponseApplication getApplicationInfo(long caID)
			throws RemoteException, ServiceException {
		locator.setLoanSubmissionGetApplicationInfoEndpointAddress(getApplicationInfoUrl);

		LoanSubmissionGetApplicationInfoSoapBindingStub stub = (LoanSubmissionGetApplicationInfoSoapBindingStub) locator
				.getLoanSubmissionGetApplicationInfo();
		RequestApplication request = new RequestApplication();
		Header header = new Header();
		header.setChannel(CHANNEL);
		header.setModule(MODULE);
		header.setRequestID(UUID.randomUUID().toString());
		Body body = new Body();
		body.setCaID(caID);
		request.setBody(body);
		request.setHeader(header);

		return stub.searchApplicationInfoByCaID(request);
	}

}
