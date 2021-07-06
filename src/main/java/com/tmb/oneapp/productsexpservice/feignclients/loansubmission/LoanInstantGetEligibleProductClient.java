package com.tmb.oneapp.productsexpservice.feignclients.loansubmission;

import java.rmi.RemoteException;
import java.util.UUID;

import javax.xml.rpc.ServiceException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.legacy.rsl.ws.instant.eligible.product.request.Body;
import com.tmb.common.model.legacy.rsl.ws.instant.eligible.product.request.Header;
import com.tmb.common.model.legacy.rsl.ws.instant.eligible.product.request.RequestInstantLoanGetEligibleProduct;
import com.tmb.common.model.legacy.rsl.ws.instant.eligible.product.response.ResponseInstantLoanGetEligibleProduct;
import com.tmb.common.model.legacy.rsl.ws.loan.submission.LoanSubmissionInstantLoanGetEligibleProductServiceLocator;
import com.tmb.common.model.legacy.rsl.ws.loan.submission.LoanSubmissionInstantLoanGetEligibleProductSoapBindingStub;

@Service
public class LoanInstantGetEligibleProductClient {

	private static final TMBLogger<LoanInstantGetEligibleProductClient> logger = new TMBLogger<>(
			LoanInstantGetEligibleProductClient.class);

	@Value("${loan-submission-get-eligible-product}")
	private String loanSubmissionInstantLoanGetEligibleProductUrl;

	private LoanSubmissionInstantLoanGetEligibleProductServiceLocator locator = new LoanSubmissionInstantLoanGetEligibleProductServiceLocator();

	public void setLocator(LoanSubmissionInstantLoanGetEligibleProductServiceLocator locator) {
		this.locator = locator;
	}

	public ResponseInstantLoanGetEligibleProduct getEligibleProduct(String crmId)
			throws RemoteException, ServiceException {

		locator.setLoanSubmissionInstantLoanGetEligibleProductEndpointAddress(
				loanSubmissionInstantLoanGetEligibleProductUrl);
		LoanSubmissionInstantLoanGetEligibleProductSoapBindingStub stub = (LoanSubmissionInstantLoanGetEligibleProductSoapBindingStub) locator
				.getLoanSubmissionInstantLoanGetEligibleProduct();
		RequestInstantLoanGetEligibleProduct req = new RequestInstantLoanGetEligibleProduct();

		Header header = new Header();
		header.setChannel("MIB");
		header.setModule("");
		header.setRequestID(UUID.randomUUID().toString());

		req.setHeader(header);
		Body body = new Body();
		try {
			String crmId14Digit = new StringBuilder(new StringBuilder(crmId).reverse().substring(0, 14)).reverse()
					.toString();
			body.setRmNo(crmId14Digit);
			req.setBody(body);
			return stub.getEligibleProduct(req);
		} catch (StringIndexOutOfBoundsException e) {
			logger.error("getEligibleProduct got wrong format crmId:{}", crmId);
			throw e;
		}
	}

}
