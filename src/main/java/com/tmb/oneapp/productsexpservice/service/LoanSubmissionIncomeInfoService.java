package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.legacy.rsl.ws.incomemodel.response.ResponseIncomeModel;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionGetIncomeModelInfoClient;
import com.tmb.oneapp.productsexpservice.model.response.IncomeInfo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.xml.rpc.ServiceException;
import java.math.BigDecimal;
import java.rmi.RemoteException;

@Service
@AllArgsConstructor
public class LoanSubmissionIncomeInfoService {
    private static final TMBLogger<LoanSubmissionIncomeInfoService> logger = new TMBLogger<>(LoanSubmissionIncomeInfoService.class);
    private final LoanSubmissionGetIncomeModelInfoClient incomeModelInfoClient;


    public IncomeInfo getIncomeInfoByRmId(String rmId)throws ServiceException, RemoteException {
        try {

            ResponseIncomeModel responseIncomeModel = incomeModelInfoClient.getIncomeInfo(rmId);
            IncomeInfo incomeInfo = new IncomeInfo();
            incomeInfo.setIncomeAmount(responseIncomeModel.getBody().getIncomeModelAmt());
            return incomeInfo;
        } catch (Exception e) {
            logger.error("getIncomeInfoByRmId got exception:{}", e);
            throw e;
        }
    }
}
