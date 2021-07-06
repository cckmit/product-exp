package com.tmb.oneapp.productsexpservice.service.productexperience.account;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.CommonData;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.AccountRequestClient;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.DepositAccount;
import com.tmb.oneapp.productsexpservice.service.ProductExpAsynService;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.google.common.collect.Lists.newArrayList;
import static com.tmb.oneapp.productsexpservice.util.ExceptionUtil.throwTmbException;

@Service
public class EligibleDepositAccountService {

    private static final TMBLogger<EligibleDepositAccountService> logger = new TMBLogger<>(EligibleDepositAccountService.class);

    private ProductExpAsynService productExpAsynService;

    private AccountRequestClient accountRequestClient;

    @Autowired
    public EligibleDepositAccountService(ProductExpAsynService productExpAsynService, AccountRequestClient accountRequestClient) {
        this.productExpAsynService = productExpAsynService;
        this.accountRequestClient = accountRequestClient;
    }

    public List<DepositAccount> getEligibleDepositAccounts(String correlationId, String crmId) {
        List<DepositAccount> depositAccountList;
        try {
            CompletableFuture<List<CommonData>> fetchCommonConfigByModule = productExpAsynService.fetchCommonConfigByModule(correlationId, ProductsExpServiceConstant.INVESTMENT_MODULE_VALUE);
            CompletableFuture<String> accountInfo =
                    CompletableFuture.completedFuture(accountRequestClient.callCustomerExpService(UtilMap.createHeader(correlationId), crmId));
            CompletableFuture.allOf(fetchCommonConfigByModule, accountInfo);

            UtilMap utilMap = new UtilMap();
            depositAccountList = utilMap.mappingAccount(fetchCommonConfigByModule.get(), accountInfo.get());
            validateAccountList(depositAccountList);
            return depositAccountList;
        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, ex);
            return newArrayList();
        }
    }

    private void validateAccountList(List<DepositAccount> depositAccountList) throws TMBCommonException {
        if (depositAccountList.isEmpty()) {
            throwTmbException("========== failed account return 0 in list ==========");
        }
    }
}
