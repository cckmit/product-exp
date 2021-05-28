package com.tmb.oneapp.productsexpservice.feignclients.loansubmission;

import com.tmb.common.model.legacy.rsl.ws.instant.eligible.customer.request.Body;
import com.tmb.common.model.legacy.rsl.ws.instant.eligible.customer.request.Header;
import com.tmb.common.model.legacy.rsl.ws.instant.eligible.customer.request.RequestInstantLoanGetCustInfo;
import com.tmb.common.model.legacy.rsl.ws.instant.eligible.customer.response.ResponseInstantLoanGetCustInfo;
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

	LoanSubmissionInstantLoanGetCustomerInfoServiceLocator locator = new LoanSubmissionInstantLoanGetCustomerInfoServiceLocator();

	private static final String CHANNEL = "MIB";
	private static final String MODULE = "3";

	public void setLocator(LoanSubmissionInstantLoanGetCustomerInfoServiceLocator locator) {
		this.locator = locator;
	}

	public ResponseInstantLoanGetCustInfo searchCustomerInfoByCaID(String caID) throws RemoteException, ServiceException {
		locator.setLoanSubmissionInstantLoanGetCustomerInfoEndpointAddress(getCustomerInfoUrl);

		LoanSubmissionInstantLoanGetCustomerInfoSoapBindingStub stub = (LoanSubmissionInstantLoanGetCustomerInfoSoapBindingStub) locator
				.getLoanSubmissionInstantLoanGetCustomerInfo();

		RequestInstantLoanGetCustInfo req = new RequestInstantLoanGetCustInfo();

		Header header = new Header();
		header.setChannel(CHANNEL);
		header.setModule(MODULE);
		header.setRequestID(UUID.randomUUID().toString());
		req.setHeader(header);

		Body body = new Body();
		body.setRmNo(caID);
		req.setBody(body);

		return stub.getInstantCustomerInfo(req);
	}

}
