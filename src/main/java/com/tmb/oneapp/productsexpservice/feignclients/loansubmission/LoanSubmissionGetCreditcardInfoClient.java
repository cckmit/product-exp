package com.tmb.oneapp.productsexpservice.feignclients.loansubmission;

import com.tmb.common.model.legacy.rsl.ws.creditcard.request.Body;
import com.tmb.common.model.legacy.rsl.ws.creditcard.request.Header;
import com.tmb.common.model.legacy.rsl.ws.creditcard.request.RequestCreditcard;
import com.tmb.common.model.legacy.rsl.ws.creditcard.response.ResponseCreditcard;
import com.tmb.common.model.legacy.rsl.ws.loan.submission.LoanSubmissionGetCreditcardInfoServiceLocator;
import com.tmb.common.model.legacy.rsl.ws.loan.submission.LoanSubmissionGetCreditcardInfoSoapBindingStub;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import java.util.UUID;

@Service
public class LoanSubmissionGetCreditcardInfoClient {
	@Value("${loan-submission-get-creditcard-info.url}")
	private String getCreditCardInfoUrl;

	LoanSubmissionGetCreditcardInfoServiceLocator locator = new LoanSubmissionGetCreditcardInfoServiceLocator();

	private static final String CHANNEL = "MIB";
	private static final String MODULE = "3";

	public void setLocator(LoanSubmissionGetCreditcardInfoServiceLocator locator) {
		this.locator = locator;
	}

	public ResponseCreditcard searchCreditcardInfoByCaID(Long caID) throws RemoteException, ServiceException {
		locator.setLoanSubmissionGetCreditcardInfoEndpointAddress(getCreditCardInfoUrl);

		LoanSubmissionGetCreditcardInfoSoapBindingStub stub = (LoanSubmissionGetCreditcardInfoSoapBindingStub) locator
				.getLoanSubmissionGetCreditcardInfo();

		RequestCreditcard req = new RequestCreditcard();

		Header header = new Header();
		header.setChannel(CHANNEL);
		header.setModule(MODULE);
		header.setRequestID(UUID.randomUUID().toString());
		req.setHeader(header);

		Body body = new Body();
		body.setCaID(caID);
		req.setBody(body);

		return stub.searchCreditcardInfoByCaID(req);
	}

}
