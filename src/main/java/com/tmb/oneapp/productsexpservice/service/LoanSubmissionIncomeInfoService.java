package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.CustGeneralProfileResponse;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.legacy.rsl.ws.dropdown.response.ResponseDropdown;
import com.tmb.common.model.legacy.rsl.ws.incomemodel.response.ResponseIncomeModel;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionGetDropdownListClient;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionGetIncomeModelInfoClient;
import com.tmb.oneapp.productsexpservice.model.response.IncomeInfo;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import java.util.Objects;

@Service
@AllArgsConstructor
public class LoanSubmissionIncomeInfoService {
    private static final TMBLogger<LoanSubmissionIncomeInfoService> logger = new TMBLogger<>(LoanSubmissionIncomeInfoService.class);
    private final LoanSubmissionGetIncomeModelInfoClient incomeModelInfoClient;
    private CustomerServiceClient customerServiceClient;
    private final LoanSubmissionGetDropdownListClient getDropdownListClient;


    public IncomeInfo getIncomeInfoByRmId(String rmId) throws ServiceException, RemoteException {
        try {

            ResponseIncomeModel responseIncomeModel = incomeModelInfoClient.getIncomeInfo(StringUtils.right(rmId, 14));
            if (Objects.isNull(responseIncomeModel) || Objects.isNull(responseIncomeModel.getBody())) {
                return null;
            }

            IncomeInfo incomeInfo = new IncomeInfo();
            incomeInfo.setIncomeAmount(responseIncomeModel.getBody().getIncomeModelAmt());
            incomeInfo.setStatusWorking(getWorkingStatus(rmId));

            return incomeInfo;
        } catch (Exception e) {
            logger.error("getIncomeInfoByRmId got exception:{}", e);
            throw e;
        }
    }

    private String getWorkingStatus(String rmId) throws ServiceException, RemoteException {
        ResponseEntity<TmbOneServiceResponse<CustGeneralProfileResponse>> responseWorkingProfileInfo = customerServiceClient
                .getCustomerProfile(rmId);
        if (Objects.nonNull(responseWorkingProfileInfo)
                && Objects.nonNull(responseWorkingProfileInfo.getBody())
                && Objects.nonNull(responseWorkingProfileInfo.getBody().getData())) {
            return mapWorkingStatus(responseWorkingProfileInfo.getBody().getData().getOccupationCode());
        }
        return null;
    }

    private String mapWorkingStatus(String occupation_code) throws ServiceException, RemoteException {
        ResponseDropdown getDropdownListResp = getDropdownListClient.getDropdownList("RM_OCCUPATION");

        var list = getDropdownListResp.getBody().getCommonCodeEntries();
        for (var item : list) {
            if (item.getEntryCode().equals(occupation_code)) {
                if (item.getExtValue1().equals("01")) {
                    return "salary";
                }
                if (item.getExtValue1().equals("02")) {
                    return "self_employed";
                }
            }
        }
        return null;
    }
}
