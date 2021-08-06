package com.tmb.oneapp.productsexpservice.service.productexperience.alternative;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.CommonData;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.enums.OpenPortfolioErrorEnums;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.model.response.fundfactsheet.FundResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.DepositAccount;
import com.tmb.oneapp.productsexpservice.service.ProductsExpService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class AlternativeService {

    private static final TMBLogger<AlternativeService> logger = new TMBLogger<>(AlternativeService.class);

    private ProductsExpService productsExpService;

    private CommonServiceClient commonServiceClient;

    @Autowired
    public AlternativeService(ProductsExpService productsExpService, CommonServiceClient commonServiceClient) {
        this.productsExpService = productsExpService;
        this.commonServiceClient = commonServiceClient;
    }

    // validate service hour
    public TmbStatus validateServiceHour(String correlationId, TmbStatus status) {
        FundResponse fundResponse = new FundResponse();
        fundResponse = productsExpService.isServiceHour(correlationId, fundResponse);
        if (fundResponse.isError()) {
            status.setCode(OpenPortfolioErrorEnums.NOT_IN_SERVICE_HOUR.getCode());
            status.setDescription(OpenPortfolioErrorEnums.NOT_IN_SERVICE_HOUR.getDesc());
            status.setMessage(OpenPortfolioErrorEnums.NOT_IN_SERVICE_HOUR.getMsg());
            status.setService(ProductsExpServiceConstant.SERVICE_NAME);
            return status;
        }
        return status;
    }

    // validate age should > 20
    public TmbStatus validateDateNotOverTwentyYearOld(String birthDate, TmbStatus status) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date d = sdf.parse(birthDate);
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH) + 1;
            int date = c.get(Calendar.DATE);
            LocalDate birthDateLocalDate = LocalDate.of(year, month, date);
            LocalDate now = LocalDate.now();
            Period diff = Period.between(birthDateLocalDate, now);
            if (diff.getYears() < 20) {
                status.setCode(OpenPortfolioErrorEnums.AGE_NOT_OVER_TWENTY.getCode());
                status.setDescription(OpenPortfolioErrorEnums.AGE_NOT_OVER_TWENTY.getDesc());
                status.setMessage(OpenPortfolioErrorEnums.AGE_NOT_OVER_TWENTY.getMsg());
                status.setService(ProductsExpServiceConstant.SERVICE_NAME);
                return status;
            }
            return status;
        } catch (ParseException ex) {
            logger.info("birthdate is invalid format");
            return TmbStatusUtil.notFoundStatus();
        }
    }

    // validate account active at least one
    public TmbStatus validateCasaAccountActiveOnce(List<DepositAccount> depositAccountList, TmbStatus status) {
        if (depositAccountList != null) {
            boolean isAccountActiveOnce = false;
            for (DepositAccount depositAccount :
                    depositAccountList) {
                if (depositAccount.getAccountStatusCode().equals(ProductsExpServiceConstant.ACTIVE_STATUS_CODE)) {
                    isAccountActiveOnce = true;
                }
            }
            if (!isAccountActiveOnce || depositAccountList.isEmpty()) {
                status.setCode(OpenPortfolioErrorEnums.NO_ACTIVE_CASA_ACCOUNT.getCode());
                status.setDescription(OpenPortfolioErrorEnums.NO_ACTIVE_CASA_ACCOUNT.getDesc());
                status.setMessage(OpenPortfolioErrorEnums.NO_ACTIVE_CASA_ACCOUNT.getMsg());
                status.setService(ProductsExpServiceConstant.SERVICE_NAME);
                return status;
            }
        }
        return status;
    }

    // validate complete flatca form
    public TmbStatus validateFatcaFlagNotValid(String fatcaFlag, TmbStatus status) {
        boolean isFatcaFlagValid = false;
        if (!StringUtils.isEmpty(fatcaFlag) && !fatcaFlag.equals("0")) {
            isFatcaFlagValid = true;
        }

        if (!isFatcaFlagValid) {
            status.setCode(OpenPortfolioErrorEnums.CUSTOMER_NOT_FILL_FATCA_FORM.getCode());
            status.setDescription(OpenPortfolioErrorEnums.CUSTOMER_NOT_FILL_FATCA_FORM.getDesc());
            status.setMessage(OpenPortfolioErrorEnums.CUSTOMER_NOT_FILL_FATCA_FORM.getMsg());
            status.setService(ProductsExpServiceConstant.SERVICE_NAME);
            return status;
        }
        return status;
    }

    // validate customer pass kyc (U,Blank) allow  and id card has not expired
    public TmbStatus validateKycAndIdCardExpire(String kycLimitFlag, String expireDate, TmbStatus status) {
        boolean isKycAndIdCardExpiredValid = false;
        if ((kycLimitFlag != null && expireDate != null) &&
                (kycLimitFlag.equalsIgnoreCase("U") ||
                        kycLimitFlag.isBlank()) && isExpiredDateOccurAfterCurrentDate(expireDate)) {
            isKycAndIdCardExpiredValid = true;
        }

        if (!isKycAndIdCardExpiredValid) {
            status.setCode(OpenPortfolioErrorEnums.FAILED_VERIFY_KYC.getCode());
            status.setDescription(OpenPortfolioErrorEnums.FAILED_VERIFY_KYC.getDesc());
            status.setMessage(OpenPortfolioErrorEnums.FAILED_VERIFY_KYC.getMsg());
            status.setService(ProductsExpServiceConstant.SERVICE_NAME);
            return status;
        }
        return status;
    }

    private boolean isExpiredDateOccurAfterCurrentDate(String expireDate) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date expire = format.parse(expireDate);
            Date current = Calendar.getInstance().getTime();
            if (expire.compareTo(current) > 0) {
                return true;
            }
        } catch (ParseException ex) {
            logger.info("isExpiredDateOccurAfterCurrentDate :: Error ParseException");
        }
        return false;
    }

    // validate customer assurange level
    public TmbStatus validateIdentityAssuranceLevel(String ekycIdentifyAssuranceLevel, TmbStatus status) {
        boolean isAssuranceLevelValid = false;

        if (ekycIdentifyAssuranceLevel != null && validateAssuranceLevel(ekycIdentifyAssuranceLevel)) {
            isAssuranceLevelValid = true;
        }

        if (!isAssuranceLevelValid) {
            status.setCode(OpenPortfolioErrorEnums.CUSTOMER_IDENTIFY_ASSURANCE_LEVEL.getCode());
            status.setDescription(OpenPortfolioErrorEnums.CUSTOMER_IDENTIFY_ASSURANCE_LEVEL.getDesc());
            status.setMessage(OpenPortfolioErrorEnums.CUSTOMER_IDENTIFY_ASSURANCE_LEVEL.getMsg());
            status.setService(ProductsExpServiceConstant.SERVICE_NAME);
            return status;
        }
        return status;
    }

    private boolean validateAssuranceLevel(String ekycIdentifyAssuranceLevel) {
        try {
            int ekycIdentifyAssuranceLevelInt = Integer.parseInt(ekycIdentifyAssuranceLevel);
            return ekycIdentifyAssuranceLevelInt >= 210;
        } catch (NumberFormatException ex) {
            logger.info("ekycIdentifyAssuranceLevel is not number : " + ekycIdentifyAssuranceLevel);
            return false;
        }
    }

    // validate customer not us and not restriced in 30 nationality
    public TmbStatus validateNationality(String correlationId, String mainNationality, String secondNationality, TmbStatus status) {
        ResponseEntity<TmbOneServiceResponse<List<CommonData>>> commonConfig =
                commonServiceClient.getCommonConfig(correlationId, ProductsExpServiceConstant.INVESTMENT_MODULE_VALUE);

        List<CommonData> commonDataList = commonConfig.getBody().getData();
        List<String> blackList = commonDataList.get(0).getNationalBlackList();

        if (StringUtils.isEmpty(mainNationality) ||
                blackList.stream().anyMatch(mainNationality::equals) ||
                !StringUtils.isEmpty(secondNationality) && blackList.stream().anyMatch(secondNationality::equals)) {
            status.setCode(OpenPortfolioErrorEnums.CUSTOMER_HAS_US_NATIONALITY_OR_OTHER_THIRTY_RESTRICTED.getCode());
            status.setDescription(OpenPortfolioErrorEnums.CUSTOMER_HAS_US_NATIONALITY_OR_OTHER_THIRTY_RESTRICTED.getDesc());
            status.setMessage(OpenPortfolioErrorEnums.CUSTOMER_HAS_US_NATIONALITY_OR_OTHER_THIRTY_RESTRICTED.getMsg());
            status.setService(ProductsExpServiceConstant.SERVICE_NAME);
            return status;
        }
        return status;
    }

    // validate customer risk level
    public TmbStatus validateCustomerRiskLevel(String customerRiskLevel, TmbStatus status) {
        boolean isCustomerRiskLevelNotValid = false;
        if (!StringUtils.isEmpty(customerRiskLevel)) {
            String[] values = {"C3", "B3"};
            if (Arrays.stream(values).anyMatch(customerRiskLevel::equals)) {
                isCustomerRiskLevelNotValid = true;
            }
        }

        if (isCustomerRiskLevelNotValid) {
            status.setCode(OpenPortfolioErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getCode());
            status.setDescription(OpenPortfolioErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getDesc());
            status.setMessage(OpenPortfolioErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getMsg());
            status.setService(ProductsExpServiceConstant.SERVICE_NAME);
            return status;
        }
        return status;
    }

}
