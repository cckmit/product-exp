package com.tmb.oneapp.productsexpservice.service.productexperience.alternative;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.CommonData;
import com.tmb.common.model.CommonTime;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.enums.AlternativeOpenPortfolioErrorEnums;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.model.customer.calculaterisk.request.AddressModel;
import com.tmb.oneapp.productsexpservice.model.customer.calculaterisk.request.EkycRiskCalculateRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.DepositAccount;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
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

/**
 * AlternativeService class will handle all of alternative of investment
 */
@Service
public class AlternativeService {

    private static final TMBLogger<AlternativeService> logger = new TMBLogger<>(AlternativeService.class);

    private CommonServiceClient commonServiceClient;

    private CustomerServiceClient customerServiceClient;

    @Autowired
    public AlternativeService(
                              CommonServiceClient commonServiceClient,
                              CustomerServiceClient customerServiceClient
    ) {
        this.commonServiceClient = commonServiceClient;
        this.customerServiceClient = customerServiceClient;
    }

    /**
     * Method validateServiceHour method  validate working hour for customer
     * @param correlationId
     * @param status
     * @return TmbStatus
     */
    public TmbStatus validateServiceHour(String correlationId, TmbStatus status) {
        ResponseEntity<TmbOneServiceResponse<List<CommonData>>> responseCommon = null;
        try {
            responseCommon = commonServiceClient.getCommonConfigByModule(correlationId, ProductsExpServiceConstant.INVESTMENT_MODULE_VALUE);
            logger.info(ProductsExpServiceConstant.CUSTOMER_EXP_SERVICE_RESPONSE, responseCommon);
            if (!StringUtils.isEmpty(responseCommon)) {
                List<CommonData> commonDataList = responseCommon.getBody().getData();
                CommonData commonData = commonDataList.get(0);
                CommonTime noneServiceHour = commonData.getNoneServiceHour();
                if (UtilMap.isBusinessClose(noneServiceHour.getStart(), noneServiceHour.getEnd())) {
                    status.setCode(AlternativeOpenPortfolioErrorEnums.NOT_IN_SERVICE_HOUR.getCode());
                    status.setDescription(AlternativeOpenPortfolioErrorEnums.NOT_IN_SERVICE_HOUR.getDesc());
                    status.setMessage(AlternativeOpenPortfolioErrorEnums.NOT_IN_SERVICE_HOUR.getMsg());
                    status.setService(ProductsExpServiceConstant.SERVICE_NAME);
                }
            }
            return status;
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, e);
            status.setCode(ProductsExpServiceConstant.SERVICE_NOT_READY);
            status.setMessage(ProductsExpServiceConstant.SERVICE_NOT_READY_MESSAGE);
            status.setDescription(ProductsExpServiceConstant.SERVICE_NOT_READY_DESC);
            status.setService(ProductsExpServiceConstant.SERVICE_NAME);
            return status;
        }
    }


    /**
     * Method validateDateNotOverTwentyYearOld method vaidate age of customer
     * @param birthDate
     * @param status
     * @return TmbStatus
     */
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
                status.setCode(AlternativeOpenPortfolioErrorEnums.AGE_NOT_OVER_TWENTY.getCode());
                status.setDescription(AlternativeOpenPortfolioErrorEnums.AGE_NOT_OVER_TWENTY.getDesc());
                status.setMessage(AlternativeOpenPortfolioErrorEnums.AGE_NOT_OVER_TWENTY.getMsg());
                status.setService(ProductsExpServiceConstant.SERVICE_NAME);
                return status;
            }
            return status;
        } catch (ParseException ex) {
            logger.info("birthdate is invalid format");
            return TmbStatusUtil.notFoundStatus();
        }
    }


    /**
     * Method validateCasaAccountActiveOnce method validate customer active casa account
     * @param depositAccountList
     * @param status
     * @return TmbStatus
     */
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
                status.setCode(AlternativeOpenPortfolioErrorEnums.NO_ACTIVE_CASA_ACCOUNT.getCode());
                status.setDescription(AlternativeOpenPortfolioErrorEnums.NO_ACTIVE_CASA_ACCOUNT.getDesc());
                status.setMessage(AlternativeOpenPortfolioErrorEnums.NO_ACTIVE_CASA_ACCOUNT.getMsg());
                status.setService(ProductsExpServiceConstant.SERVICE_NAME);
                return status;
            }
        }
        return status;
    }


    /**
     * Method validateFatcaFlagNotValid method validate customer fatcaFlag
     * @param fatcaFlag
     * @param status
     * @return TmbStatus
     */
    public TmbStatus validateFatcaFlagNotValid(String fatcaFlag, TmbStatus status) {
        boolean isFatcaFlagValid = false;
        if (!StringUtils.isEmpty(fatcaFlag) && !fatcaFlag.equals("0")) {
            isFatcaFlagValid = true;
        }

        if (!isFatcaFlagValid) {
            status.setCode(AlternativeOpenPortfolioErrorEnums.CUSTOMER_NOT_FILL_FATCA_FORM.getCode());
            status.setDescription(AlternativeOpenPortfolioErrorEnums.CUSTOMER_NOT_FILL_FATCA_FORM.getDesc());
            status.setMessage(AlternativeOpenPortfolioErrorEnums.CUSTOMER_NOT_FILL_FATCA_FORM.getMsg());
            status.setService(ProductsExpServiceConstant.SERVICE_NAME);
            return status;
        }
        return status;
    }

    /**
     * Method validateKycAndIdCardExpire method validate ekyc and cardid expired
     * @param kycLimitFlag
     * @param expireDate
     * @param status
     * @return TmbStatus
     */
    public TmbStatus validateKycAndIdCardExpire(String kycLimitFlag, String expireDate, TmbStatus status) {
        boolean isKycAndIdCardExpiredValid = false;
        if ((kycLimitFlag != null && expireDate != null) &&
                (kycLimitFlag.equalsIgnoreCase("U") ||
                        kycLimitFlag.isBlank()) && isExpiredDateOccurAfterCurrentDate(expireDate)) {
            isKycAndIdCardExpiredValid = true;
        }

        if (!isKycAndIdCardExpiredValid) {
            status.setCode(AlternativeOpenPortfolioErrorEnums.FAILED_VERIFY_KYC.getCode());
            status.setDescription(AlternativeOpenPortfolioErrorEnums.FAILED_VERIFY_KYC.getDesc());
            status.setMessage(AlternativeOpenPortfolioErrorEnums.FAILED_VERIFY_KYC.getMsg());
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

    /**
     * Method validateIdentityAssuranceLevel method validate customer assurance level
     * @param ekycIdentifyAssuranceLevel
     * @param status
     * @return TmbStatus
     */
    public TmbStatus validateIdentityAssuranceLevel(String ekycIdentifyAssuranceLevel, TmbStatus status) {
        boolean isAssuranceLevelValid = false;

        if (ekycIdentifyAssuranceLevel != null && validateAssuranceLevel(ekycIdentifyAssuranceLevel)) {
            isAssuranceLevelValid = true;
        }

        if (!isAssuranceLevelValid) {
            status.setCode(AlternativeOpenPortfolioErrorEnums.CUSTOMER_IDENTIFY_ASSURANCE_LEVEL.getCode());
            status.setDescription(AlternativeOpenPortfolioErrorEnums.CUSTOMER_IDENTIFY_ASSURANCE_LEVEL.getDesc());
            status.setMessage(AlternativeOpenPortfolioErrorEnums.CUSTOMER_IDENTIFY_ASSURANCE_LEVEL.getMsg());
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

    /**
     * Method validateNationality method validate customer nationality
     * @param correlationId
     * @param mainNationality
     * @param secondNationality
     * @param status
     * @return TmbStatus
     */
    public TmbStatus validateNationality(String correlationId, String mainNationality, String secondNationality, TmbStatus status) {
        ResponseEntity<TmbOneServiceResponse<List<CommonData>>> commonConfig =
                commonServiceClient.getCommonConfig(correlationId, ProductsExpServiceConstant.INVESTMENT_MODULE_VALUE);

        List<CommonData> commonDataList = commonConfig.getBody().getData();
        List<String> blackList = commonDataList.get(0).getNationalBlackList();

        if (StringUtils.isEmpty(mainNationality) ||
                blackList.stream().anyMatch(mainNationality::equals) ||
                !StringUtils.isEmpty(secondNationality) && blackList.stream().anyMatch(secondNationality::equals)) {
            status.setCode(AlternativeOpenPortfolioErrorEnums.CUSTOMER_HAS_US_NATIONALITY_OR_OTHER_THIRTY_RESTRICTED.getCode());
            status.setDescription(AlternativeOpenPortfolioErrorEnums.CUSTOMER_HAS_US_NATIONALITY_OR_OTHER_THIRTY_RESTRICTED.getDesc());
            status.setMessage(AlternativeOpenPortfolioErrorEnums.CUSTOMER_HAS_US_NATIONALITY_OR_OTHER_THIRTY_RESTRICTED.getMsg());
            status.setService(ProductsExpServiceConstant.SERVICE_NAME);
            return status;
        }
        return status;
    }

    /**
     * Method validateCustomerRiskLevel method validate customer risk level
     * @param correlationId
     * @param customerInfo
     * @param status
     * @return TmbStatus
     */
    public TmbStatus validateCustomerRiskLevel(String correlationId,CustomerSearchResponse customerInfo, TmbStatus status) {
        String customerRiskLevel = fetchApiculateRiskLevel(correlationId,customerInfo);
        boolean isCustomerRiskLevelNotValid = false;
        if (!StringUtils.isEmpty(customerRiskLevel)) {
            String[] values = {"C3", "B3"};
            if (Arrays.stream(values).anyMatch(customerRiskLevel::equals)) {
                isCustomerRiskLevelNotValid = true;
            }
        }

        if (isCustomerRiskLevelNotValid) {
            status.setCode(AlternativeOpenPortfolioErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getCode());
            status.setDescription(AlternativeOpenPortfolioErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getDesc());
            status.setMessage(AlternativeOpenPortfolioErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getMsg());
            status.setService(ProductsExpServiceConstant.SERVICE_NAME);
            return status;
        }
        return status;
    }

    private String fetchApiculateRiskLevel(String correlationId, CustomerSearchResponse customerInfo) {
        try{
            EkycRiskCalculateRequest ekycRiskCalculateRequest = mappingFieldToRequestEkycRiskCalculate(customerInfo);
            ResponseEntity<TmbOneServiceResponse<String>> customerRiskResponse =
                    customerServiceClient.customerEkycRiskCalculate(correlationId, ekycRiskCalculateRequest);
            return customerRiskResponse.getBody().getData();
        }catch (Exception ex){
            logger.error(ProductsExpServiceConstant.CUSTOMER_EXP_SERVICE_RESPONSE,ex);
        }
        return null;
    }
    private EkycRiskCalculateRequest mappingFieldToRequestEkycRiskCalculate(CustomerSearchResponse customerInfo) {

        return EkycRiskCalculateRequest.builder()
                .businessCode(customerInfo.getBusinessTypeCode())
                .cardId(customerInfo.getIdNumber())
                .dob(customerInfo.getBirthDate())
                .dobCountry(customerInfo.getNationality())
                .firstName(customerInfo.getCustomerThaiFirstName())
                .firstNameEng(customerInfo.getCustomerEnglishFirstName())
                .lastName(customerInfo.getCustomerThaiLastName())
                .lastNameEng(customerInfo.getCustomerEnglishLastName())
                .occupationCode(customerInfo.getOccupationCode())
                .officeAddress(
                        AddressModel.builder()
                                .building(customerInfo.getOfficeAddressData().getBuildVillageName())
                                .companyName(customerInfo.getOfficeAddressData().getWorkingPlace())
                                .country(customerInfo.getOfficeAddressData().getCountry())
                                .district(customerInfo.getOfficeAddressData().getDistrict())
                                .moo(customerInfo.getOfficeAddressData().getMoo())
                                .no(customerInfo.getOfficeAddressData().getAddressNo())
                                .phoneExtension(customerInfo.getOfficeAddressData().getPhoneExtension())
                                .phoneNo(customerInfo.getOfficeAddressData().getPhoneNo())
                                .postalCode(customerInfo.getOfficeAddressData().getPostalCode())
                                .province(customerInfo.getOfficeAddressData().getProvince())
                                .road(customerInfo.getOfficeAddressData().getRoad())
                                .soi(customerInfo.getOfficeAddressData().getSoi())
                                .subDistrict(customerInfo.getOfficeAddressData().getSubDistrict())
                                .build()
                )
                .primaryAddress(
                        AddressModel.builder()
                                .building(customerInfo.getPrimaryAddressData().getBuildVillageName())
                                .country(customerInfo.getPrimaryAddressData().getCountry())
                                .district(customerInfo.getPrimaryAddressData().getDistrict())
                                .moo(customerInfo.getPrimaryAddressData().getMoo())
                                .no(customerInfo.getPrimaryAddressData().getAddressNo())
                                .postalCode(customerInfo.getPrimaryAddressData().getPostalCode())
                                .province(customerInfo.getPrimaryAddressData().getProvince())
                                .road(customerInfo.getPrimaryAddressData().getRoad())
                                .soi(customerInfo.getPrimaryAddressData().getSoi())
                                .subDistrict(customerInfo.getPrimaryAddressData().getSubDistrict())
                                .build()
                )
                .registeredAddress(
                        AddressModel.builder()
                                .building(customerInfo.getRegisteredAddressData().getBuildVillageName())
                                .country(customerInfo.getRegisteredAddressData().getCountry())
                                .district(customerInfo.getRegisteredAddressData().getDistrict())
                                .moo(customerInfo.getRegisteredAddressData().getMoo())
                                .no(customerInfo.getRegisteredAddressData().getAddressNo())
                                .postalCode(customerInfo.getRegisteredAddressData().getPostalCode())
                                .province(customerInfo.getRegisteredAddressData().getProvince())
                                .road(customerInfo.getRegisteredAddressData().getRoad())
                                .soi(customerInfo.getRegisteredAddressData().getSoi())
                                .subDistrict(customerInfo.getRegisteredAddressData().getSubDistrict())
                                .build()
                )
                .build();

    }

}
