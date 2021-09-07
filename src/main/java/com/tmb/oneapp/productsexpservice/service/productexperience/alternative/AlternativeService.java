package com.tmb.oneapp.productsexpservice.service.productexperience.alternative;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.*;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.enums.AlternativeBuySellSwitchDcaErrorEnums;
import com.tmb.oneapp.productsexpservice.enums.AlternativeOpenPortfolioErrorEnums;
import com.tmb.oneapp.productsexpservice.feignclients.AccountRequestClient;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.customer.calculaterisk.request.AddressModel;
import com.tmb.oneapp.productsexpservice.model.customer.calculaterisk.request.EkycRiskCalculateRequest;
import com.tmb.oneapp.productsexpservice.model.customer.calculaterisk.response.EkycRiskCalculateResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.alternative.response.servicehour.ValidateServiceHourResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.DepositAccount;
import com.tmb.oneapp.productsexpservice.model.response.suitability.SuitabilityInfo;
import com.tmb.oneapp.productsexpservice.service.ProductExpAsyncService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import feign.FeignException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * AlternativeService class will handle all of alternative of investment
 */
@Service
public class AlternativeService {

    private static final TMBLogger<AlternativeService> logger = new TMBLogger<>(AlternativeService.class);

    private final CommonServiceClient commonServiceClient;

    private final CustomerServiceClient customerServiceClient;

    private final AccountRequestClient accountRequestClient;

    private final InvestmentRequestClient investmentRequestClient;

    private final ProductExpAsyncService productExpAsyncService;

    @Autowired
    public AlternativeService(
            CommonServiceClient commonServiceClient,
            CustomerServiceClient customerServiceClient,
            AccountRequestClient accountRequestClient,
            InvestmentRequestClient investmentRequestClient,
            ProductExpAsyncService productExpAsyncService
    ) {
        this.commonServiceClient = commonServiceClient;
        this.customerServiceClient = customerServiceClient;
        this.accountRequestClient = accountRequestClient;
        this.investmentRequestClient = investmentRequestClient;
        this.productExpAsyncService = productExpAsyncService;
    }

    /**
     * Method validateServiceHour method  validate working hour for customer
     *
     * @param correlationId
     * @param status
     * @return TmbStatusWithTime
     */
    @LogAround
    public ValidateServiceHourResponse validateServiceHour(String correlationId, TmbStatus status) {
        ValidateServiceHourResponse statusWithTime = new ValidateServiceHourResponse();
        try {
            BeanUtils.copyProperties(status, statusWithTime);

            ResponseEntity<TmbOneServiceResponse<List<CommonData>>> responseCommon = commonServiceClient
                    .getCommonConfigByModule(correlationId, ProductsExpServiceConstant.INVESTMENT_MODULE_VALUE);

            logger.info(ProductsExpServiceConstant.CUSTOMER_EXP_SERVICE_RESPONSE, responseCommon);

            if (!StringUtils.isEmpty(responseCommon)) {
                List<CommonData> commonDataList = responseCommon.getBody().getData();
                CommonData commonData = commonDataList.get(0);
                CommonTime noneServiceHour = commonData.getNoneServiceHour();
                String startTime = noneServiceHour.getStart();
                String endTime = noneServiceHour.getEnd();

                if (UtilMap.isBusinessClose(startTime, endTime)) {
                    statusWithTime.setCode(AlternativeOpenPortfolioErrorEnums.NOT_IN_SERVICE_HOUR.getCode());
                    statusWithTime.setDescription(AlternativeOpenPortfolioErrorEnums.NOT_IN_SERVICE_HOUR.getDesc());
                    statusWithTime.setMessage(AlternativeOpenPortfolioErrorEnums.NOT_IN_SERVICE_HOUR.getMsg());
                    statusWithTime.setService(ProductsExpServiceConstant.SERVICE_NAME);
                    statusWithTime.setStartTime(startTime);
                    statusWithTime.setEndTime(endTime);
                }
            }
            return statusWithTime;
        } catch (Exception e) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, e);
            statusWithTime.setCode(ProductsExpServiceConstant.SERVICE_NOT_READY);
            statusWithTime.setMessage(ProductsExpServiceConstant.SERVICE_NOT_READY_MESSAGE);
            statusWithTime.setDescription(String.format(ProductsExpServiceConstant.SERVICE_NOT_READY_DESC_MESSAGE, "validateServiceHour failed"));
            statusWithTime.setService(ProductsExpServiceConstant.SERVICE_NAME);
            return statusWithTime;
        }
    }

    /**
     * Method validateDateNotOverTwentyYearOld method vaidate age of customer
     *
     * @param birthDate
     * @param status
     * @return TmbStatus
     */
    @LogAround
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
     * Method isCASADormant get Customer account and check dormant status
     *
     * @param correlationId
     * @param crmId
     * @return TmbStatus
     */
    @LogAround
    public TmbStatus validateCASADormant(String correlationId, String crmId, TmbStatus status) {
        try {
            Map<String, String> invHeaderReqParameter = UtilMap.createHeader(correlationId);
            String responseCustomerExp = accountRequestClient.getAccountList(invHeaderReqParameter, UtilMap.halfCrmIdFormat(crmId));
            logger.info(ProductsExpServiceConstant.CUSTOMER_EXP_SERVICE_RESPONSE, responseCustomerExp);
            if (UtilMap.isCASADormant(responseCustomerExp)) {
                status.setCode(AlternativeBuySellSwitchDcaErrorEnums.CASA_DORMANT.getCode());
                status.setDescription(AlternativeBuySellSwitchDcaErrorEnums.CASA_DORMANT.getDesc());
                status.setMessage(AlternativeBuySellSwitchDcaErrorEnums.CASA_DORMANT.getMsg());
                status.setService(ProductsExpServiceConstant.SERVICE_NAME);
            }
        } catch (Exception e) {
            logger.error("========== accountRequestClient error ==========");
            status.setCode(ProductsExpServiceConstant.SERVICE_NOT_READY);
            status.setDescription(String.format(ProductsExpServiceConstant.SERVICE_NOT_READY_DESC_MESSAGE, "validateCASADormant failed"));
            status.setMessage(ProductsExpServiceConstant.SERVICE_NOT_READY_DESC);
            status.setService(ProductsExpServiceConstant.SERVICE_NAME);
        }
        return status;
    }

    /**
     * Method isSuitabilityExpired Call MF service to check suitability is expire.
     *
     * @param correlationId
     * @param crmId
     * @return TmbStatus
     */
    @LogAround
    public TmbStatus validateSuitabilityExpired(String correlationId, String crmId, TmbStatus status) {
        try {
            Map<String, String> investmentHeaderRequest = UtilMap.createHeader(correlationId);
            ResponseEntity<TmbOneServiceResponse<SuitabilityInfo>> responseResponseEntity = investmentRequestClient.callInvestmentFundSuitabilityService(investmentHeaderRequest, UtilMap.halfCrmIdFormat(crmId));
            logger.info(ProductsExpServiceConstant.INVESTMENT_SERVICE_RESPONSE, responseResponseEntity);
            if (UtilMap.isSuitabilityExpire(responseResponseEntity.getBody().getData())) {
                status.setCode(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_SUIT_EXPIRED.getCode());
                status.setDescription(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_SUIT_EXPIRED.getDesc());
                status.setMessage(AlternativeBuySellSwitchDcaErrorEnums.CUSTOMER_SUIT_EXPIRED.getMsg());
                status.setService(ProductsExpServiceConstant.SERVICE_NAME);
            }
        } catch (Exception e) {
            logger.error("========== investment callInvestmentFundSuitabilityService error ==========");
            status.setCode(ProductsExpServiceConstant.SERVICE_NOT_READY);
            status.setDescription(String.format(ProductsExpServiceConstant.SERVICE_NOT_READY_DESC_MESSAGE, "validateSuitabilityExpired failed"));
            status.setMessage(ProductsExpServiceConstant.SERVICE_NOT_READY_DESC);
            status.setService(ProductsExpServiceConstant.SERVICE_NAME);
        }
        return status;
    }

    /**
     * Method validateCustomerIdExpired call to customer-info and get id_expire_date to verify with current date
     *
     * @param crmId
     * @return TmbStatus
     */
    @LogAround
    public TmbStatus validateIdCardExpired(String crmId, TmbStatus status) {
        CompletableFuture<CustGeneralProfileResponse> responseResponseEntity;
        try {
            responseResponseEntity = productExpAsyncService.fetchCustomerProfile(UtilMap.halfCrmIdFormat(crmId));
            CompletableFuture.allOf(responseResponseEntity);
            CustGeneralProfileResponse responseData = responseResponseEntity.get();
            logger.info(ProductsExpServiceConstant.INVESTMENT_SERVICE_RESPONSE, responseData);
            if (UtilMap.isCustIdExpired(responseData)) {
                status.setCode(AlternativeBuySellSwitchDcaErrorEnums.ID_CARD_EXPIRED.getCode());
                status.setDescription(AlternativeBuySellSwitchDcaErrorEnums.ID_CARD_EXPIRED.getDesc());
                status.setMessage(AlternativeBuySellSwitchDcaErrorEnums.ID_CARD_EXPIRED.getMsg());
                status.setService(ProductsExpServiceConstant.SERVICE_NAME);
            }
        } catch (Exception e) {
            logger.error("========== investment callInvestmentFundSuitabilityService error ==========");
            status.setCode(ProductsExpServiceConstant.SERVICE_NOT_READY);
            status.setDescription(String.format(ProductsExpServiceConstant.SERVICE_NOT_READY_DESC_MESSAGE, "validateIdCardExpired failed"));
            status.setService(ProductsExpServiceConstant.SERVICE_NAME);
        }
        return status;
    }

    /**
     * Method validateCasaAccountActiveOnce method validate customer active casa account
     *
     * @param depositAccountList
     * @param status
     * @return TmbStatus
     */
    @LogAround
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
     *
     * @param fatcaFlag
     * @param status
     * @return TmbStatus
     */
    @LogAround
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
     *
     * @param kycLimitFlag
     * @param expireDate
     * @param status
     * @return TmbStatus
     */
    @LogAround
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

    @LogAround
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
     *
     * @param ekycIdentifyAssuranceLevel
     * @param status
     * @return TmbStatus
     */
    @LogAround
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

    @LogAround
    private boolean validateAssuranceLevel(String ekycIdentifyAssuranceLevel) {
        try {
            int ekycIdentifyAssuranceLevelInt = Integer.parseInt(ekycIdentifyAssuranceLevel);
            return ekycIdentifyAssuranceLevelInt >= 210;
        } catch (NumberFormatException ex) {
            logger.error("ekycIdentifyAssuranceLevel is not number : " + ekycIdentifyAssuranceLevel);
            return false;
        }
    }

    /**
     * Method validateNationality method validate customer nationality
     *
     * @param correlationId
     * @param mainNationality
     * @param secondNationality
     * @param status
     * @return TmbStatus
     */
    @LogAround
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
     *
     * @param correlationId
     * @param customerInfo
     * @param status
     * @return TmbStatus
     */
    @LogAround
    public TmbStatus validateCustomerRiskLevel(String correlationId, CustomerSearchResponse customerInfo, TmbStatus status, boolean isBuyFlow, boolean isFirstTrade) {
        EkycRiskCalculateResponse customerRiskLevel = fetchApiculateRiskLevel(correlationId, customerInfo);
        boolean isCustomerRiskLevelNotValid = false;
        if (!StringUtils.isEmpty(customerRiskLevel)) {
            String[] values = new String[2];
            values[0] = "C3";
            if (isBuyFlow && isFirstTrade) {
                values[1] = "B3";
            }

            if (Arrays.stream(values).anyMatch(customerRiskLevel.getMaxRiskRM()::equals)) {
                isCustomerRiskLevelNotValid = true;
            }

        } else {
            status.setCode(ProductsExpServiceConstant.SERVICE_NOT_READY);
            status.setMessage(ProductsExpServiceConstant.SERVICE_NOT_READY_MESSAGE);
            status.setDescription(String.format(ProductsExpServiceConstant.SERVICE_NOT_READY_DESC_MESSAGE, "Customer Cal Risk"));
            status.setService(ProductsExpServiceConstant.SERVICE_NAME);
            return status;
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

    @LogAround
    private EkycRiskCalculateResponse fetchApiculateRiskLevel(String correlationId, CustomerSearchResponse customerInfo) {
        try {
            EkycRiskCalculateRequest ekycRiskCalculateRequest = mappingFieldToRequestEkycRiskCalculate(customerInfo);
            ResponseEntity<TmbServiceResponse<EkycRiskCalculateResponse>> customerRiskResponse = customerServiceClient.customerEkycRiskCalculate(correlationId, ekycRiskCalculateRequest);
            return customerRiskResponse.getBody().getData();
        } catch (FeignException feignException) {
            if (feignException.status() == HttpStatus.BAD_REQUEST.value()) {
                try {
                    TmbServiceResponse<String> body = getResponseFromBadRequest(feignException);
                    if (!StringUtils.isEmpty(body.getData())) {
                        ObjectMapper obj = new ObjectMapper();
                        return obj.convertValue(body.getData(), EkycRiskCalculateResponse.class);
                    }
                    logger.info("========== no data risk return from customer cal risk  ========== : {}", body);
                } catch (JsonProcessingException e) {
                    logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, e);
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    <T> TmbServiceResponse<T> getResponseFromBadRequest(final FeignException ex)
            throws JsonProcessingException {
        TmbServiceResponse<T> response = new TmbServiceResponse<>();
        Optional<ByteBuffer> responseBody = ex.responseBody();
        if (responseBody.isPresent()) {
            ByteBuffer responseBuffer = responseBody.get();
            String responseObj = new String(responseBuffer.array(), StandardCharsets.UTF_8);
            logger.info("response msg fail {}", responseObj);
            response = ((TmbServiceResponse<T>) TMBUtils.convertStringToJavaObj(responseObj,
                    TmbServiceResponse.class));
        }
        return response;
    }

    private EkycRiskCalculateRequest mappingFieldToRequestEkycRiskCalculate(CustomerSearchResponse customerInfo) {
        return EkycRiskCalculateRequest.builder()
                .businessCode(customerInfo.getBusinessTypeCode())
                .cardId(customerInfo.getIdNumber())
                .dob(customerInfo.getBirthDate())
                .dobCountry(customerInfo.getNationality())
                .firstName(customerInfo.getCustomerThaiFirstName())
                .firstNameEng(customerInfo.getCustomerEnglishFirstName())
                .incomeSourceCountry(customerInfo.getCountryOfIncome())
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