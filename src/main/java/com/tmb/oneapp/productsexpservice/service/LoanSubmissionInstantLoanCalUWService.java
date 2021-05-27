package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.legacy.rsl.ws.instant.calculate.uw.request.Body;
import com.tmb.common.model.legacy.rsl.ws.instant.calculate.uw.request.RequestInstantLoanCalUW;
import com.tmb.common.model.legacy.rsl.ws.instant.calculate.uw.response.ResponseInstantLoanCalUW;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionInstantLoanCalUWClient;
import com.tmb.oneapp.productsexpservice.model.loan.InstantLoanCalUWResponse;
import com.tmb.oneapp.productsexpservice.model.request.loan.InstantLoanCalUWRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;


@Service
@AllArgsConstructor
public class LoanSubmissionInstantLoanCalUWService {
    private static final TMBLogger<LoanSubmissionInstantLoanCalUWService> logger = new TMBLogger<>(LoanSubmissionInstantLoanCalUWService.class);

    private final LoanSubmissionInstantLoanCalUWClient loanCalUWClient;
    static final String APPROVE = "APPROVE";
    static final String REJECT = "REJECT";

    public InstantLoanCalUWResponse checkCalculateUnderwriting(InstantLoanCalUWRequest request) throws ServiceException, RemoteException {
        RequestInstantLoanCalUW requestInstantLoanCalUW = new RequestInstantLoanCalUW();
        Body body = new Body();
        body.setCaId(request.getCaId());
        body.setTriggerFlag(request.getTriggerFlag());
        requestInstantLoanCalUW.setBody(body);
        ResponseInstantLoanCalUW responseInstantLoanCalUW = calculateUnderwriting(requestInstantLoanCalUW);
        return pareCalculateUnderwriting(responseInstantLoanCalUW);
    }

    private InstantLoanCalUWResponse pareCalculateUnderwriting(ResponseInstantLoanCalUW response) {

        String underWriting = response.getBody().getUnderwritingResult();
        InstantLoanCalUWResponse instantLoanCalUWResponse = new InstantLoanCalUWResponse();
        instantLoanCalUWResponse.setStatus(underWriting);
        if (underWriting.equals(APPROVE)) {
            instantLoanCalUWResponse.setApprovalMemoCreditCards(response.getBody().getApprovalMemoCreditCards());
            instantLoanCalUWResponse.setApprovalMemoFacilities(response.getBody().getApprovalMemoFacilities());
        } else if (underWriting.equals(REJECT)) {
            instantLoanCalUWResponse.setApprovalMemoCreditCards(null);
            instantLoanCalUWResponse.setApprovalMemoFacilities(null);
        }

        return instantLoanCalUWResponse;
    }


    private ResponseInstantLoanCalUW calculateUnderwriting(RequestInstantLoanCalUW request) throws ServiceException, RemoteException {
        try {
            return loanCalUWClient.getCalculateUnderwriting(request);
        } catch (Exception e) {
            logger.error("calculateUnderwriting got exception:{}", e);
            throw e;
        }
    }
}
