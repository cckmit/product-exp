package com.tmb.oneapp.productsexpservice.service.productexperience.alternative;

import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.enums.AlternativeBuySellSwitchDcaErrorEnums;
import com.tmb.oneapp.productsexpservice.enums.AlternativeOpenPortfolioErrorEnums;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertEquals;

@ExtendWith(MockitoExtension.class)
class AlternativeFatcaServiceTest {

    @InjectMocks
    public AlternativeService alternativeService;

    @Test
    void should_return_status_code_2000032_when_call_validate_fatca_flag_not_valid_given_validate_customer_not_fill_fatca_form() {
        // Given
        // When
        TmbStatus actual = alternativeService.validateFatcaFlagNotValid("0", TmbStatusUtil.successStatus(), "OPEN_PORTFOLIO");

        // Then
        assertEquals(AlternativeOpenPortfolioErrorEnums.NOT_COMPLETED_FATCA_FORM.getCode(), actual.getCode());
        assertEquals(AlternativeOpenPortfolioErrorEnums.NOT_COMPLETED_FATCA_FORM.getMessage(), actual.getMessage());
        assertEquals(AlternativeOpenPortfolioErrorEnums.NOT_COMPLETED_FATCA_FORM.getDescription(), actual.getDescription());
    }

    @Test
    void should_return_status_code_2000034_when_call_validate_fatca_flag_not_valid_given_fatca_flag_8() {
        // Given
        // When
        TmbStatus actual = alternativeService.validateFatcaFlagNotValid("8", TmbStatusUtil.successStatus(), "OPEN_PORTFOLIO");

        // Then
        assertEquals(AlternativeOpenPortfolioErrorEnums.DID_NOT_PASS_FATCA_FORM.getCode(), actual.getCode());
        assertEquals(AlternativeOpenPortfolioErrorEnums.DID_NOT_PASS_FATCA_FORM.getMessage(), actual.getMessage());
        assertEquals(AlternativeOpenPortfolioErrorEnums.DID_NOT_PASS_FATCA_FORM.getDescription(), actual.getDescription());
    }

    @Test
    void should_return_status_code_2000034_when_call_validate_fatca_flag_not_valid_given_fatca_flag_9() {
        // Given
        // When
        TmbStatus actual = alternativeService.validateFatcaFlagNotValid("9", TmbStatusUtil.successStatus(), "OPEN_PORTFOLIO");

        // Then
        assertEquals(AlternativeOpenPortfolioErrorEnums.DID_NOT_PASS_FATCA_FORM.getCode(), actual.getCode());
        assertEquals(AlternativeOpenPortfolioErrorEnums.DID_NOT_PASS_FATCA_FORM.getMessage(), actual.getMessage());
        assertEquals(AlternativeOpenPortfolioErrorEnums.DID_NOT_PASS_FATCA_FORM.getDescription(), actual.getDescription());
    }

    @Test
    void should_return_status_code_success_when_call_validate_fatca_flag_not_valid_given_fatca_flag_N() {
        // Given
        // When
        TmbStatus actual = alternativeService.validateFatcaFlagNotValid("N", TmbStatusUtil.successStatus(), "OPEN_PORTFOLIO");

        // Then
        assertEquals(ProductsExpServiceConstant.SUCCESS_CODE, actual.getCode());
        assertEquals(ProductsExpServiceConstant.SUCCESS_MESSAGE, actual.getMessage());
        assertEquals(ProductsExpServiceConstant.SUCCESS_MESSAGE, actual.getDescription());
    }

    @Test
    void should_return_status_code_success_when_call_validate_fatca_flag_not_valid_given_fatca_flag_I() {
        // Given
        // When
        TmbStatus actual = alternativeService.validateFatcaFlagNotValid("I", TmbStatusUtil.successStatus(), "OPEN_PORTFOLIO");

        // Then
        assertEquals(ProductsExpServiceConstant.SUCCESS_CODE, actual.getCode());
        assertEquals(ProductsExpServiceConstant.SUCCESS_MESSAGE, actual.getMessage());
        assertEquals(ProductsExpServiceConstant.SUCCESS_MESSAGE, actual.getDescription());
    }

    @Test
    void should_return_status_code_success_when_call_validate_fatca_flag_not_valid_given_fatca_flag_U() {
        // Given
        // When
        TmbStatus actual = alternativeService.validateFatcaFlagNotValid("U", TmbStatusUtil.successStatus(), "OPEN_PORTFOLIO");

        // Then
        assertEquals(ProductsExpServiceConstant.SUCCESS_CODE, actual.getCode());
        assertEquals(ProductsExpServiceConstant.SUCCESS_MESSAGE, actual.getMessage());
        assertEquals(ProductsExpServiceConstant.SUCCESS_MESSAGE, actual.getDescription());
    }

    @Test
    void should_return_status_code_2000018_when_call_validate_fatca_flag_not_valid_given_validate_process_open_portfolio_and_fatca_flag_P_or_R() {
        // Given
        // When
        TmbStatus actual = alternativeService.validateFatcaFlagNotValid("P", TmbStatusUtil.successStatus(), "OPEN_PORTFOLIO");

        // Then
        assertEquals(AlternativeOpenPortfolioErrorEnums.CAN_NOT_OPEN_ACCOUNT_FOR_FATCA.getCode(), actual.getCode());
        assertEquals(AlternativeOpenPortfolioErrorEnums.CAN_NOT_OPEN_ACCOUNT_FOR_FATCA.getMessage(), actual.getMessage());
        assertEquals(AlternativeOpenPortfolioErrorEnums.CAN_NOT_OPEN_ACCOUNT_FOR_FATCA.getDescription(), actual.getDescription());
    }

    @Test
    void should_return_status_code_2000002_when_call_validate_fatca_flag_not_valid_given_validate_process_first_trade_and_fatca_flag_P_or_R() {
        // Given
        // When
        TmbStatus actual = alternativeService.validateFatcaFlagNotValid("P", TmbStatusUtil.successStatus(), "FIRST_TRADE");

        // Then
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.NOT_ALLOW_PROCESS_TO_BE_PROCEEDED.getCode(), actual.getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.NOT_ALLOW_PROCESS_TO_BE_PROCEEDED.getMessage(), actual.getMessage());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.NOT_ALLOW_PROCESS_TO_BE_PROCEEDED.getDescription(), actual.getDescription());
    }
}