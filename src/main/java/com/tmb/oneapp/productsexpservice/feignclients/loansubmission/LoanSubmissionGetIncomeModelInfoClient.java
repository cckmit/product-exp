package com.tmb.oneapp.productsexpservice.feignclients.loansubmission;

import com.tmb.common.model.legacy.rsl.ws.incomemodel.request.Body;
import com.tmb.common.model.legacy.rsl.ws.incomemodel.request.Header;
import com.tmb.common.model.legacy.rsl.ws.incomemodel.request.RequestIncomeModel;
import com.tmb.common.model.legacy.rsl.ws.incomemodel.response.ResponseIncomeModel;
import com.tmb.common.model.legacy.rsl.ws.loan.submission.LoanSubmissionGetIncomeModelInfoServiceLocator;
import com.tmb.common.model.legacy.rsl.ws.loan.submission.LoanSubmissionGetIncomeModelInfoSoapBindingStub;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import java.util.UUID;

@Service
public class LoanSubmissionGetIncomeModelInfoClient {
    @Value("${loan-submission-get-income-model-info.url}")
    private String incomeInfoUrl;

    private static final String CHANNEL = "MIB";
    private static final String MODULE = "3";
    LoanSubmissionGetIncomeModelInfoServiceLocator locator = new LoanSubmissionGetIncomeModelInfoServiceLocator();
    public void setLocator(LoanSubmissionGetIncomeModelInfoServiceLocator locator)
    {
        this.locator = locator;
    }

    public ResponseIncomeModel getIncomeInfo(String rmNo) throws RemoteException, ServiceException {
       locator.setLoanSubmissionGetIncomeModelInfoEndpointAddress(incomeInfoUrl);

        LoanSubmissionGetIncomeModelInfoSoapBindingStub stub = (LoanSubmissionGetIncomeModelInfoSoapBindingStub) locator
                .getLoanSubmissionGetIncomeModelInfo();

        RequestIncomeModel req = new RequestIncomeModel();


        var header = new Header();
        header.setChannel(CHANNEL);
        header.setModule(MODULE);
        header.setRequestID(UUID.randomUUID().toString());
        req.setHeader(header);

        Body body = new Body();
        body.setRmNo(rmNo);
        req.setBody(body);

        return stub.getIncomeModelInfo(req);
    }

}
