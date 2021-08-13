package com.tmb.oneapp.productsexpservice.service.productexperience.alternative;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.model.CommonData;
import com.tmb.common.model.CommonTime;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.enums.AlternativeOpenPortfolioErrorEnums;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.AddressWithPhone;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundfactsheet.FundResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.DepositAccount;
import com.tmb.oneapp.productsexpservice.service.ProductsExpService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AlternativeServiceTest {

    @Mock
    public ProductsExpService productsExpService;

    @Mock
    public CommonServiceClient commonServiceClient;

    @Mock
    public CustomerServiceClient customerServiceClient;

    @InjectMocks
    public AlternativeService alternativeService;

    private final String correlationId = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";

    private void mockCommonConfig() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<CommonData> list = new ArrayList<>();
        TmbOneServiceResponse<List<CommonData>> response = new TmbOneServiceResponse<List<CommonData>>();
        list.add(objectMapper.readValue(Paths.get("src/test/resources/investment/common/investment_config.json").toFile(), CommonData.class));
        response.setData(list);
        when(commonServiceClient.getCommonConfig(any(), any())).thenReturn(ResponseEntity.ok(response));
    }

    private void mockNotPassServiceHour() {
        TmbOneServiceResponse<List<CommonData>> responseCommon = new TmbOneServiceResponse<>();
        FundResponse fundResponse = new FundResponse();
        fundResponse.setError(true);
        CommonData commonData = new CommonData();
        CommonTime commonTime = new CommonTime();
        List<CommonData> commonDataList = new ArrayList<>();

        commonTime.setStart("00:00");
        commonTime.setEnd("00:00");
        commonData.setNoneServiceHour(commonTime);
        commonDataList.add(commonData);

        responseCommon.setData(commonDataList);
        responseCommon.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
                ProductsExpServiceConstant.SUCCESS_MESSAGE,
                ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));

        when(commonServiceClient.getCommonConfigByModule(anyString(), anyString())).thenReturn(ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(responseCommon));    }

    @Test
    void should_return_status_code_2000001_when_call_validate_service_hour() throws Exception {
        // Given
        mockNotPassServiceHour();
        // When
        TmbStatus actual = alternativeService.validateServiceHour(correlationId, TmbStatusUtil.successStatus());

        // Then
        assertEquals(AlternativeOpenPortfolioErrorEnums.NOT_IN_SERVICE_HOUR.getCode(), actual.getCode());
        assertEquals(AlternativeOpenPortfolioErrorEnums.NOT_IN_SERVICE_HOUR.getMsg(), actual.getMessage());
        assertEquals(AlternativeOpenPortfolioErrorEnums.NOT_IN_SERVICE_HOUR.getDesc(), actual.getDescription());
    }

    @Test
    void should_return_status_code_2000025_when_call_validate_age_is_not_over_twenty() throws Exception {
        // When
        TmbStatus actual = alternativeService.validateDateNotOverTwentyYearOld("2010-07-08", TmbStatusUtil.successStatus());

        // Then
        assertEquals(AlternativeOpenPortfolioErrorEnums.AGE_NOT_OVER_TWENTY.getCode(), actual.getCode());
        assertEquals(AlternativeOpenPortfolioErrorEnums.AGE_NOT_OVER_TWENTY.getMsg(), actual.getMessage());
        assertEquals(AlternativeOpenPortfolioErrorEnums.AGE_NOT_OVER_TWENTY.getDesc(), actual.getDescription());
    }

    @Test
    void should_return_status_code_2000019_when_call_validate_no_casa_active() throws Exception {
        // Given
        DepositAccount depositAccount = new DepositAccount();
        depositAccount.setProductNameTH("บัญชีออลล์ฟรี");
        depositAccount.setProductNameEN("TMB All Free Account");
        depositAccount.setAccountNumber("1102416367");
        depositAccount.setAccountStatus("ACTIVE");
        depositAccount.setAccountType("S");
        depositAccount.setAccountTypeShort("SDA");
        depositAccount.setAccountStatusCode(ProductsExpServiceConstant.DORMANT_STATUS_CODE);
        depositAccount.setAvailableBalance(new BigDecimal("1033583777.38"));

        // When
        TmbStatus actual = alternativeService.validateCasaAccountActiveOnce(newArrayList(depositAccount), TmbStatusUtil.successStatus());

        // Then
        assertEquals(AlternativeOpenPortfolioErrorEnums.NO_ACTIVE_CASA_ACCOUNT.getCode(), actual.getCode());
        assertEquals(AlternativeOpenPortfolioErrorEnums.NO_ACTIVE_CASA_ACCOUNT.getMsg(), actual.getMessage());
        assertEquals(AlternativeOpenPortfolioErrorEnums.NO_ACTIVE_CASA_ACCOUNT.getDesc(), actual.getDescription());
    }

    @Test
    void should_return_status_code_2000018_when_call_validate_risk_level_not_valid() throws Exception {
        //given
        TmbOneServiceResponse<String> response = new TmbOneServiceResponse<>();
        response.setData("B3");
        when(customerServiceClient.customerEkycRiskCalculate(any(),any())).thenReturn(ResponseEntity.ok(response));

        // When
        CustomerSearchResponse customerSearchResponse = CustomerSearchResponse
                .builder()
                .businessTypeCode("22")
                .officeAddressData(AddressWithPhone.builder().build())
                .registeredAddressData(AddressWithPhone.builder().build())
                .primaryAddressData(AddressWithPhone.builder().build())
                .build();
        TmbStatus actual = alternativeService.validateCustomerRiskLevel(correlationId,customerSearchResponse,  TmbStatusUtil.successStatus());

        // Then
        assertEquals(AlternativeOpenPortfolioErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getCode(), actual.getCode());
        assertEquals(AlternativeOpenPortfolioErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getMsg(), actual.getMessage());
        assertEquals(AlternativeOpenPortfolioErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getDesc(), actual.getDescription());
    }

    @Test
    void should_return_success_code_when_call_validate_risk_level_not_valid() throws Exception {
        //given
        when(customerServiceClient.customerEkycRiskCalculate(any(),any())).thenThrow(MockitoException.class);

        // When
        CustomerSearchResponse customerSearchResponse = CustomerSearchResponse
                .builder()
                .businessTypeCode("22")
                .officeAddressData(AddressWithPhone.builder().build())
                .registeredAddressData(AddressWithPhone.builder().build())
                .primaryAddressData(AddressWithPhone.builder().build())
                .build();
        TmbStatus actual = alternativeService.validateCustomerRiskLevel(correlationId,customerSearchResponse,  TmbStatusUtil.successStatus());

        // Then
        assertEquals(ProductsExpServiceConstant.SUCCESS_CODE, actual.getCode());
    }

    @Test
    void should_return_status_code_2000018_when_call_validate_customer_assurance_level() throws Exception {

        // When
        TmbStatus actual = alternativeService.validateIdentityAssuranceLevel("100",  TmbStatusUtil.successStatus());

        // Then
        assertEquals(AlternativeOpenPortfolioErrorEnums.CUSTOMER_IDENTIFY_ASSURANCE_LEVEL.getCode(), actual.getCode());
        assertEquals(AlternativeOpenPortfolioErrorEnums.CUSTOMER_IDENTIFY_ASSURANCE_LEVEL.getMsg(), actual.getMessage());
        assertEquals(AlternativeOpenPortfolioErrorEnums.CUSTOMER_IDENTIFY_ASSURANCE_LEVEL.getDesc(), actual.getDescription());
    }

    @Test
    void should_return_status_code_2000018_when_call_validate_customer_nationality() throws Exception {

        // given
        mockCommonConfig();

        // When
        TmbStatus actual = alternativeService.validateNationality(correlationId,"TH","US",TmbStatusUtil.successStatus());

        // Then
        assertEquals(AlternativeOpenPortfolioErrorEnums.CUSTOMER_HAS_US_NATIONALITY_OR_OTHER_THIRTY_RESTRICTED.getCode(), actual.getCode());
        assertEquals(AlternativeOpenPortfolioErrorEnums.CUSTOMER_HAS_US_NATIONALITY_OR_OTHER_THIRTY_RESTRICTED.getMsg(), actual.getMessage());
        assertEquals(AlternativeOpenPortfolioErrorEnums.CUSTOMER_HAS_US_NATIONALITY_OR_OTHER_THIRTY_RESTRICTED.getDesc(), actual.getDescription());
    }

    @Test
    void should_return_status_code_2000034_when_call_validateOpenPortfolioService_validate_customer_not_fill_fatca_form() throws Exception {
        // When
        TmbStatus actual = alternativeService.validateFatcaFlagNotValid("0",TmbStatusUtil.successStatus());

        // Then
        assertEquals(AlternativeOpenPortfolioErrorEnums.CUSTOMER_NOT_FILL_FATCA_FORM.getCode(), actual.getCode());
        assertEquals(AlternativeOpenPortfolioErrorEnums.CUSTOMER_NOT_FILL_FATCA_FORM.getMsg(), actual.getMessage());
        assertEquals(AlternativeOpenPortfolioErrorEnums.CUSTOMER_NOT_FILL_FATCA_FORM.getDesc(), actual.getDescription());
    }

    @Test
    void should_return_status_code_2000022_when_call_validateOpenPortfolioService_validate_kyc_and_id_card_expired() throws Exception {
        // When
        TmbStatus actual = alternativeService.validateKycAndIdCardExpire("0","2021-07-08",TmbStatusUtil.successStatus());

        // Then
        assertEquals(AlternativeOpenPortfolioErrorEnums.FAILED_VERIFY_KYC.getCode(), actual.getCode());
        assertEquals(AlternativeOpenPortfolioErrorEnums.FAILED_VERIFY_KYC.getMsg(), actual.getMessage());
        assertEquals(AlternativeOpenPortfolioErrorEnums.FAILED_VERIFY_KYC.getDesc(), actual.getDescription());
    }


}
