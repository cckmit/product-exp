package com.tmb.oneapp.productsexpservice.service.productexperience.Account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.CommonData;
import com.tmb.oneapp.productsexpservice.feignclients.AccountRequestClient;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.DepositAccount;
import com.tmb.oneapp.productsexpservice.service.ProductExpAsynService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EligibleDepositAccountServiceTest {

    @Mock
    private TMBLogger<EligibleDepositAccountServiceTest> logger;

    @Mock
    private ProductExpAsynService productExpAsynService;

    @Mock
    private AccountRequestClient accountRequestClient;

    @InjectMocks
    private EligibleDepositAccountService eligibleDepositAccountService;

    private void mockAccountResponse() throws IOException, TMBCommonException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<CommonData> commonData = new ArrayList<>();
        commonData.add(objectMapper.readValue(Paths.get("src/test/resources/investment/common/investment_config.json").toFile(), CommonData.class));
        String accountList = "{\"status\":{\"code\":\"0000\",\"message\":\"success\",\"service\":\"accounts-service\",\"description\":{\"en\":\"success\",\"th\":\"success\"}},\"data\":[{\"product_name_Eng\":\"TMB All Free Account\",\"product_name_TH\":\"บัญชีออลล์ฟรี\",\"product_code\":\"225\",\"balance_currency\":\"THB\",\"current_balance\":\"1033583777.38\",\"account_number\":\"00001102416367\",\"relationship_code\":\"PRIIND\",\"account_status_code\":\"0\",\"account_status_text\":\"ACTIVE\",\"product_group_code\":\"SDA\",\"icon_id\":\"/product/logo/icon_01.png\",\"sort_order\":\"10001\",\"allow_transfer_from_account\":\"1\",\"allow_transfer_other_account\":\"1\",\"transfer_own_tmb\":[\"DDA\",\"SDA\",\"CDA\"],\"transfer_other_tmb\":[\"DDA\",\"SDA\"],\"personalized_acct_nickname_EN\":\"TMB All Free Account\",\"personalized_acct_nickname_TH\":\"บัญชีออลล์ฟรี\",\"account_name\":\"MIBITSIE01 LMIB1\",\"isRegisterPromptpay\":\"1\",\"account_number_display\":\"1102416367\",\"allow_transfer_to_promptpay\":\"1\",\"waive_fee_for_promptpay\":\"1\",\"waive_fee_for_promptpay_account\":\"1\"},{\"product_name_Eng\":\"No Fixed Account\",\"product_name_TH\":\"บัญชีโนฟิกซ์\",\"product_code\":\"221\",\"balance_currency\":\"THB\",\"current_balance\":\"922963.66\",\"account_number\":\"00001102416458\",\"relationship_code\":\"PRIIND\",\"account_status_code\":\"0\",\"account_status_text\":\"ACTIVE\",\"product_group_code\":\"SDA\",\"icon_id\":\"/product/logo/icon_03.png\",\"sort_order\":\"10023\",\"allow_transfer_from_account\":\"1\",\"allow_transfer_other_account\":\"0\",\"transfer_own_tmb\":[\"DDA\",\"SDA\",\"CDA\"],\"transfer_other_tmb\":[\"DDA\",\"SDA\"],\"personalized_acct_nickname_EN\":\"No Fixed Account\",\"personalized_acct_nickname_TH\":\"บัญชีโนฟิกซ์\",\"account_name\":\"นาย MIBITSIE01 LMIB1\",\"isRegisterPromptpay\":\"0\",\"account_number_display\":\"1102416458\",\"allow_transfer_to_promptpay\":\"1\",\"waive_fee_for_promptpay\":\"0\",\"waive_fee_for_promptpay_account\":\"0\"},{\"product_name_Eng\":\"Savings Care\",\"product_name_TH\":\"Savings Care\",\"product_code\":\"211\",\"balance_currency\":\"THB\",\"current_balance\":\"5000.00\",\"account_number\":\"00001102416524\",\"relationship_code\":\"PRIIND\",\"account_status_code\":\"0\",\"account_status_text\":\"ACTIVE\",\"product_group_code\":\"SDA\",\"icon_id\":\"/product/logo/icon_05.png\",\"sort_order\":\"10024\",\"allow_transfer_from_account\":\"1\",\"allow_transfer_other_account\":\"0\",\"transfer_own_tmb\":[\"DDA\",\"SDA\",\"CDA\"],\"transfer_other_tmb\":[\"DDA\",\"SDA\"],\"personalized_acct_nickname_EN\":\"Savings Care\",\"personalized_acct_nickname_TH\":\"Savings Care\",\"account_name\":\"นาย MIBITSIE01 LMIB1\",\"isRegisterPromptpay\":\"0\",\"account_number_display\":\"1102416524\",\"allow_transfer_to_promptpay\":\"0\",\"waive_fee_for_promptpay\":\"0\",\"waive_fee_for_promptpay_account\":\"0\"},{\"product_name_Eng\":\"Quick Interest Account 12 Months\",\"product_name_TH\":\"บัญชีดอกเบี้ยด่วน 12 เดือน\",\"product_code\":\"664\",\"balance_currency\":\"THB\",\"current_balance\":\"10000.00\",\"account_number\":\"1103318497\",\"relationship_code\":\"PRIIND\",\"account_status_code\":\"0\",\"account_status_text\":\"ACTIVE\",\"product_group_code\":\"CDA\",\"icon_id\":\"/product/logo/icon_06.png\",\"sort_order\":\"10027\",\"allow_transfer_from_account\":\"1\",\"allow_transfer_other_account\":\"0\",\"transfer_own_tmb\":[\"DDA\",\"SDA\"],\"transfer_other_tmb\":[],\"personalized_acct_nickname_EN\":\"Quick Interest Account 12 Months\",\"personalized_acct_nickname_TH\":\"บัญชีดอกเบี้ยด่วน 12 เดือน\",\"account_name\":\"นาย MIBITSIE01 LMIB1\",\"isRegisterPromptpay\":\"0\",\"account_number_display\":\"1103318497\",\"allow_transfer_to_promptpay\":\"0\",\"waive_fee_for_promptpay\":\"0\",\"waive_fee_for_promptpay_account\":\"0\"}]}";
        when(productExpAsynService.fetchCommonConfigByModule(any(), any())).thenReturn(CompletableFuture.completedFuture(commonData));
        when(accountRequestClient.callCustomerExpService(any(), any())).thenReturn(accountList);
    }

    @Test
    void should_return_deposit_account_list_when_call_get_eligible_deposit_account_given_correlation_id_and_crm_id() throws Exception {
        // Given
        mockAccountResponse();

        // When
        List<DepositAccount> actual = eligibleDepositAccountService.getEligibleDepositAccounts("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", "00000000002914");

        // Then
        assertNotNull(actual);
    }

    @Test
    void should_return_null_when_call_get_eligible_deposit_account_given_throw_exception_from_api() throws Exception {
        // Given
        when(productExpAsynService.fetchCommonConfigByModule(any(), any())).thenThrow(RuntimeException.class);

        // When
        List<DepositAccount> actual = eligibleDepositAccountService.getEligibleDepositAccounts("32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", "00000000002914");

        // Then
        assertNull(actual);
    }
}