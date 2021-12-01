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
class AlternativeAssuranceLevelServiceTest {

    @InjectMocks
    public AlternativeService alternativeService;

    @Test
    void should_return_status_code_success_when_call_validate_customer_assurance_level_given_assurance_level_210() {
        // Given
        // When
        TmbStatus actual = alternativeService.validateIdentityAssuranceLevel("210", TmbStatusUtil.successStatus(), "OPEN_PORTFOLIO");

        // Then
        assertEquals(ProductsExpServiceConstant.SUCCESS_CODE, actual.getCode());
        assertEquals(ProductsExpServiceConstant.SUCCESS_MESSAGE, actual.getMessage());
        assertEquals(ProductsExpServiceConstant.SUCCESS_MESSAGE, actual.getDescription());
    }

    @Test
    void should_return_status_code_2000018_when_call_validate_customer_assurance_level_given_assurance_level_100_and_process_open_portfolio() {
        // Given
        // When
        TmbStatus actual = alternativeService.validateIdentityAssuranceLevel("100", TmbStatusUtil.successStatus(), "OPEN_PORTFOLIO");

        // Then
        assertEquals(AlternativeOpenPortfolioErrorEnums.CUSTOMER_IDENTIFY_ASSURANCE_LEVEL.getCode(), actual.getCode());
        assertEquals(AlternativeOpenPortfolioErrorEnums.CUSTOMER_IDENTIFY_ASSURANCE_LEVEL.getMessage(), actual.getMessage());
        assertEquals(AlternativeOpenPortfolioErrorEnums.CUSTOMER_IDENTIFY_ASSURANCE_LEVEL.getDescription(), actual.getDescription());
    }

    @Test
    void should_return_status_code_2000002_when_call_validate_customer_assurance_level_given_assurance_level_100_and_process_first_trade() {
        // Given
        // When
        TmbStatus actual = alternativeService.validateIdentityAssuranceLevel("100", TmbStatusUtil.successStatus(), "FIRST_TRADE");

        // Then
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.NOT_ALLOW_PROCESS_TO_BE_PROCEEDED.getCode(), actual.getCode());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.NOT_ALLOW_PROCESS_TO_BE_PROCEEDED.getMessage(), actual.getMessage());
        assertEquals(AlternativeBuySellSwitchDcaErrorEnums.NOT_ALLOW_PROCESS_TO_BE_PROCEEDED.getDescription(), actual.getDescription());
    }
}