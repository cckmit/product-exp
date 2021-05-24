package com.tmb.oneapp.productsexpservice.feignclients.loansubmission;

import com.tmb.common.model.legacy.rsl.ws.dropdown.request.Body;
import com.tmb.common.model.legacy.rsl.ws.dropdown.request.RequestDropdown;
import com.tmb.common.model.legacy.rsl.ws.dropdown.request.Header;
import com.tmb.common.model.legacy.rsl.ws.dropdown.response.ResponseDropdown;
import com.tmb.common.model.legacy.rsl.ws.loan.submission.LoanSubmissionGetDropdownListServiceLocator;
import com.tmb.common.model.legacy.rsl.ws.loan.submission.LoanSubmissionGetDropdownListSoapBindingStub;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import java.util.UUID;

@Service
public class LoanSubmissionGetDropdownListClient {
	@Value("${loan-submission-get-dropdown-list.url}")
	private String getDropdownListUrl;

	LoanSubmissionGetDropdownListServiceLocator locator = new LoanSubmissionGetDropdownListServiceLocator();

	private static final String CHANNEL = "MIB";
	private static final String MODULE = "3";

	public void setLocator(LoanSubmissionGetDropdownListServiceLocator locator) {
		this.locator = locator;
	}

	public ResponseDropdown getDropdownList(String categoryCode) throws RemoteException, ServiceException {
		locator.setLoanSubmissionGetDropdownListEndpointAddress(getDropdownListUrl);

		LoanSubmissionGetDropdownListSoapBindingStub stub = (LoanSubmissionGetDropdownListSoapBindingStub) locator
				.getLoanSubmissionGetDropdownList();

		RequestDropdown req = new RequestDropdown();

		Header header = new Header();
		header.setChannel(CHANNEL);
		header.setModule(MODULE);
		header.setRequestID(UUID.randomUUID().toString());
		req.setHeader(header);

		Body body = new Body();
		body.setCategoryCode(categoryCode);
		req.setBody(body);

		return stub.getDropDownListByCode(req);
	}

}
