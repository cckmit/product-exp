package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.legacy.rsl.ws.instant.calculate.uw.request.RequestInstantLoanCalUW;
import com.tmb.common.model.legacy.rsl.ws.instant.calculate.uw.response.ResponseInstantLoanCalUW;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionInstantLoanCalUWClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;


@Service
@AllArgsConstructor
public class LoanSubmissionInstantLoanCalUWService {
    private static final TMBLogger<LoanSubmissionInstantLoanCalUWService> logger = new TMBLogger<>(LoanSubmissionInstantLoanCalUWService.class);

    private final LoanSubmissionInstantLoanCalUWClient loanCalUWClient;

    public ResponseInstantLoanCalUW checkCalculateUnderwriting(RequestInstantLoanCalUW request) throws ServiceException, RemoteException {
        return checkCalculateUnderwriting(request);
    }

    private ResponseInstantLoanCalUW pareCalculateUnderwriting(ResponseInstantLoanCalUW response) {

        ResponseInstantLoanCalUW responseCalUW = new ResponseInstantLoanCalUW();


        return responseCalUW;
    }


    private ResponseInstantLoanCalUW calculateUnderwriting() throws ServiceException, RemoteException {
        try {
          return loanCalUWClient.getCalculateUnderwriting();
        }catch (Exception e) {
            logger.error("calculateUnderwriting got exception:{}", e);
            throw e;
        }
    }
}
