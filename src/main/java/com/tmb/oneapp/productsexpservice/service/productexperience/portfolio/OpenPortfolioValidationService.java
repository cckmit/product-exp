package com.tmb.oneapp.productsexpservice.service.productexperience.portfolio;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.CommonData;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.enums.OpenPortfolioErrorEnums;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.mapper.customer.CustomerInfoMapper;
import com.tmb.oneapp.productsexpservice.model.common.teramandcondition.response.TermAndConditionResponseBody;
import com.tmb.oneapp.productsexpservice.model.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.model.portfolio.request.OpenPortfolioValidationRequest;
import com.tmb.oneapp.productsexpservice.model.portfolio.response.ValidateOpenPortfolioResponse;
import com.tmb.oneapp.productsexpservice.model.request.crm.CrmSearchBody;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FundResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.DepositAccount;
import com.tmb.oneapp.productsexpservice.service.ProductsExpService;
import com.tmb.oneapp.productsexpservice.service.productexperience.account.EligibleDepositAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import static com.tmb.oneapp.productsexpservice.util.ExceptionUtil.throwTmbException;
import static com.tmb.oneapp.productsexpservice.util.TmbStatusUtil.successStatus;

@Service
public class OpenPortfolioValidationService {

    private static final TMBLogger<OpenPortfolioValidationService> logger = new TMBLogger<>(OpenPortfolioValidationService.class);

    private CustomerServiceClient customerServiceClient;

    private CommonServiceClient commonServiceClient;

    private ProductsExpService productsExpService;

    private EligibleDepositAccountService eligibleDepositAccountService;

    private CustomerInfoMapper customerInfoMapper;

    @Autowired
    public OpenPortfolioValidationService(CustomerServiceClient customerServiceClient, CommonServiceClient commonServiceClient, ProductsExpService productsExpService, EligibleDepositAccountService eligibleDepositAccountService, CustomerInfoMapper customerInfoMapper) {
        this.customerServiceClient = customerServiceClient;
        this.commonServiceClient = commonServiceClient;
        this.productsExpService = productsExpService;
        this.eligibleDepositAccountService = eligibleDepositAccountService;
        this.customerInfoMapper = customerInfoMapper;
    }

    /**
     * Method validateOpenPortfolio
     *
     * @param correlationId
     * @param openPortfolioValidateRequest
     */
    public TmbOneServiceResponse<ValidateOpenPortfolioResponse> validateOpenPortfolioService(String correlationId, OpenPortfolioValidationRequest openPortfolioValidateRequest) {
        TmbOneServiceResponse<ValidateOpenPortfolioResponse> tmbOneServiceResponse = new TmbOneServiceResponse();
        try {
            String crmID = openPortfolioValidateRequest.getCrmId();

            ResponseEntity<TmbOneServiceResponse<List<CustomerSearchResponse>>> customerInfoFuture =
                    customerServiceClient.customerSearch(crmID, correlationId, CrmSearchBody.builder().searchType(ProductsExpServiceConstant.SEARCH_TYPE).searchValue(crmID).build());
            validateCustomerService(customerInfoFuture);
            CustomerSearchResponse customerInfo = customerInfoFuture.getBody().getData().get(0);

            List<DepositAccount> depositAccountList = null;
            if (!openPortfolioValidateRequest.isExistingCustomer()) {
                depositAccountList = eligibleDepositAccountService.getEligibleDepositAccounts(correlationId, crmID);
            }

            tmbOneServiceResponse = validateAlternativeCase(correlationId,customerInfo,depositAccountList,tmbOneServiceResponse);
            if (!tmbOneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)){
                return tmbOneServiceResponse;
            }

            ResponseEntity<TmbOneServiceResponse<TermAndConditionResponseBody>> termAndCondition = commonServiceClient.getTermAndConditionByServiceCodeAndChannel(
                    correlationId, ProductsExpServiceConstant.SERVICE_CODE_OPEN_PORTFOLIO, ProductsExpServiceConstant.CHANNEL_MOBILE_BANKING);
            if (!termAndCondition.getStatusCode().equals(HttpStatus.OK) || StringUtils.isEmpty(termAndCondition.getBody().getData())) {
                throwTmbException("========== failed get termandcondition service ==========");
            }


            mappingOpenPortFolioValidationResponse(tmbOneServiceResponse, customerInfo, termAndCondition.getBody().getData(), depositAccountList);
            return tmbOneServiceResponse;
        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, ex);
            tmbOneServiceResponse.setStatus(null);
            tmbOneServiceResponse.setData(null);
            return tmbOneServiceResponse;
        }
    }

    private TmbOneServiceResponse<ValidateOpenPortfolioResponse> validateAlternativeCase(
            String correlationId,
            CustomerSearchResponse customerInfo,
            List<DepositAccount> depositAccountList,
            TmbOneServiceResponse<ValidateOpenPortfolioResponse> tmbOneServiceResponse) throws ParseException {

        TmbStatus status = new TmbStatus();
        // validate service hour
        FundResponse fundResponse = new FundResponse();
        fundResponse = productsExpService.isServiceHour(correlationId, fundResponse);
        if (fundResponse.isError()) {
            status.setCode(OpenPortfolioErrorEnums.NOT_IN_SERVICE_HOUR.getCode());
            status.setDescription(OpenPortfolioErrorEnums.NOT_IN_SERVICE_HOUR.getDesc());
            status.setMessage(OpenPortfolioErrorEnums.NOT_IN_SERVICE_HOUR.getMsg());
            status.setService(ProductsExpServiceConstant.SERVICE_NAME);
            tmbOneServiceResponse.setStatus(status);
            return tmbOneServiceResponse;
        }

        // validate age should > 20
        TmbOneServiceResponse<ValidateOpenPortfolioResponse> validateAgeNotOverTwentyResponse =
                validateDateNotOverTwentyYearOld(customerInfo.getBirthDate(), tmbOneServiceResponse, status);
        if (validateAgeNotOverTwentyResponse != null){
            return validateAgeNotOverTwentyResponse;
        }

        // validate account active only once
        if(depositAccountList != null) {
            boolean isAccountActiveOnce = false;
            for (DepositAccount depositAccount :
                    depositAccountList) {
                if (depositAccount.getAccountStatusCode().equals(ProductsExpServiceConstant.ACTIVE_STATUS_CODE)) {
                    isAccountActiveOnce = true;
                }
            }
            if (!isAccountActiveOnce || depositAccountList.size() == 0) {
                status.setCode(OpenPortfolioErrorEnums.NO_ACTIVE_CASA_ACCOUNT.getCode());
                status.setDescription(OpenPortfolioErrorEnums.NO_ACTIVE_CASA_ACCOUNT.getDesc());
                status.setMessage(OpenPortfolioErrorEnums.NO_ACTIVE_CASA_ACCOUNT.getMsg());
                status.setService(ProductsExpServiceConstant.SERVICE_NAME);
                tmbOneServiceResponse.setStatus(status);
                return tmbOneServiceResponse;
            }
        }
        // validate customer pass kyc (U,Blank) allow  and id card has not expired
        String kycLimitFalg = customerInfo.getKycLimitedFlag();
        String expireDate = customerInfo.getExpiryDate();
        boolean isKycAndIdCardExpiredValid = false;
        if(kycLimitFalg != null && expireDate != null){
            if(kycLimitFalg.equalsIgnoreCase("U") || kycLimitFalg.isBlank()){
                if(isExpiredDateOccurAfterCurrentDate(expireDate)){
                    isKycAndIdCardExpiredValid = true;
                }
            }
        }

        if(!isKycAndIdCardExpiredValid){
            status.setCode(OpenPortfolioErrorEnums.FAILED_VERIFY_KYC.getCode());
            status.setDescription(OpenPortfolioErrorEnums.FAILED_VERIFY_KYC.getDesc());
            status.setMessage(OpenPortfolioErrorEnums.FAILED_VERIFY_KYC.getMsg());
            status.setService(ProductsExpServiceConstant.SERVICE_NAME);
            tmbOneServiceResponse.setStatus(status);
            return tmbOneServiceResponse;
        }

        // validate customer assurange level
        boolean isAssuranceLevelValid = false;
        String ekycIdentifyAssuranceLevel = customerInfo.getEkycIdentifyAssuranceLevel();
        if(ekycIdentifyAssuranceLevel != null) {
            if (validateAssuranceLevel(ekycIdentifyAssuranceLevel)) {
                isAssuranceLevelValid = true;
            }
        }

        if(!isAssuranceLevelValid ){
            status.setCode(OpenPortfolioErrorEnums.CUSTOMER_IDENTIFY_ASSURANCE_LEVEL.getCode());
            status.setDescription(OpenPortfolioErrorEnums.CUSTOMER_IDENTIFY_ASSURANCE_LEVEL.getDesc());
            status.setMessage(OpenPortfolioErrorEnums.CUSTOMER_IDENTIFY_ASSURANCE_LEVEL.getMsg());
            status.setService(ProductsExpServiceConstant.SERVICE_NAME);
            tmbOneServiceResponse.setStatus(status);
            return tmbOneServiceResponse;
        }

        // validate customer not us and not restriced in 30 nationality
        boolean isNationalValid = false;
        String mainNationality = customerInfo.getNationality();
        String secondNationality = customerInfo.getNationalitySecond();
        if(!StringUtils.isEmpty(customerInfo.getNationality())){
            if(validateNationality(correlationId,mainNationality,secondNationality)){
                isNationalValid = true;
            }
        }

        if(!isNationalValid ){
            status.setCode(OpenPortfolioErrorEnums.CUSTOMER_HAS_US_NATIONALITY_OR_OTHER_THIRTY_RESTRICTED.getCode());
            status.setDescription(OpenPortfolioErrorEnums.CUSTOMER_HAS_US_NATIONALITY_OR_OTHER_THIRTY_RESTRICTED.getDesc());
            status.setMessage(OpenPortfolioErrorEnums.CUSTOMER_HAS_US_NATIONALITY_OR_OTHER_THIRTY_RESTRICTED.getMsg());
            status.setService(ProductsExpServiceConstant.SERVICE_NAME);
            tmbOneServiceResponse.setStatus(status);
            return tmbOneServiceResponse;
        }

        // validate complete flatca form
        boolean isFatcaFlagNotValid = false;
        if(!StringUtils.isEmpty(customerInfo.getFatcaFlag())){
            if(customerInfo.getFatcaFlag().equals("0")){
                isFatcaFlagNotValid = true;
            }
        }

        if(isFatcaFlagNotValid){
            status.setCode(OpenPortfolioErrorEnums.CUSTOMER_NOT_FILL_FATCA_FORM.getCode());
            status.setDescription(OpenPortfolioErrorEnums.CUSTOMER_NOT_FILL_FATCA_FORM.getDesc());
            status.setMessage(OpenPortfolioErrorEnums.CUSTOMER_NOT_FILL_FATCA_FORM.getMsg());
            status.setService(ProductsExpServiceConstant.SERVICE_NAME);
            tmbOneServiceResponse.setStatus(status);
            return tmbOneServiceResponse;
        }

        // validate customer risk level <> {"C3","B3"}
        boolean isCustomerRiskLevelNotValid = false;
        if(!StringUtils.isEmpty(customerInfo.getCustomerRiskLevel())){
            String[] values = {"C3","B3"};
            if(Arrays.stream(values).anyMatch(customerInfo.getCustomerRiskLevel()::equals)){
                isCustomerRiskLevelNotValid = true;
            }
        }

        if(isCustomerRiskLevelNotValid) {
            status.setCode(OpenPortfolioErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getCode());
            status.setDescription(OpenPortfolioErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getDesc());
            status.setMessage(OpenPortfolioErrorEnums.CUSTOMER_IN_LEVEL_C3_AND_B3.getMsg());
            status.setService(ProductsExpServiceConstant.SERVICE_NAME);
            tmbOneServiceResponse.setStatus(status);
            return tmbOneServiceResponse;
        }
        tmbOneServiceResponse.setStatus(successStatus());
        return tmbOneServiceResponse;
    }

    private boolean validateNationality(String correlationId,String mainNationality, String secondNationality) {
        ResponseEntity<TmbOneServiceResponse<List<CommonData>>> commonConfig =
                commonServiceClient.getCommonConfig(correlationId, ProductsExpServiceConstant.INVESTMENT_MODULE_VALUE);
        List<CommonData> commonDataList = commonConfig.getBody().getData();
        List<String> blackList = commonDataList.get(0).getNationalBlackList();
        if(blackList.stream().anyMatch(mainNationality::equals)){
            return false;
        }

        if(!StringUtils.isEmpty(secondNationality)) {
            if (blackList.stream().anyMatch(secondNationality::equals)) {
                return false;
            }
        }

        return true;
    }

    private boolean validateAssuranceLevel(String ekycIdentifyAssuranceLevel) {
        try {
            int ekycIdentifyAssuranceLevelInt = Integer.parseInt(ekycIdentifyAssuranceLevel);
            return ekycIdentifyAssuranceLevelInt >= 210;
        }catch (NumberFormatException ex){
            logger.info("ekycIdentifyAssuranceLevel is not number : "+ekycIdentifyAssuranceLevel);
            return false;
        }
    }

    private TmbOneServiceResponse<ValidateOpenPortfolioResponse> validateDateNotOverTwentyYearOld(
            String birthDate,
            TmbOneServiceResponse<ValidateOpenPortfolioResponse> tmbOneServiceResponse,
            TmbStatus status) throws ParseException {

        status.setCode(OpenPortfolioErrorEnums.AGE_NOT_OVER_TWENTY.getCode());
        status.setDescription(OpenPortfolioErrorEnums.AGE_NOT_OVER_TWENTY.getDesc());
        status.setMessage(OpenPortfolioErrorEnums.AGE_NOT_OVER_TWENTY.getMsg());
        status.setService(ProductsExpServiceConstant.SERVICE_NAME);
        tmbOneServiceResponse.setStatus(status);

        if(birthDate == null){
            return tmbOneServiceResponse;
        }

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
        if(diff.getYears() < 20){
            return tmbOneServiceResponse;
        }
        return null;
    }

    private boolean isExpiredDateOccurAfterCurrentDate(String expireDate) throws ParseException {
        try{
            SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
            Date d1 = sdformat.parse(expireDate);
            Date d2 = Calendar.getInstance().getTime();
            if(d1.compareTo(d2) > 0) {
                return true;
            }
        }catch (ParseException ex){

        }
        return false;
    }

    private void mappingOpenPortFolioValidationResponse(TmbOneServiceResponse<ValidateOpenPortfolioResponse> tmbOneServiceResponse, CustomerSearchResponse customerInfo, TermAndConditionResponseBody termAndCondition, List<DepositAccount> depositAccountList) {
        tmbOneServiceResponse.setData(ValidateOpenPortfolioResponse.builder()
                .termsConditions(termAndCondition)
                .customerInfo(customerInfoMapper.map(customerInfo))
                .depositAccountList(depositAccountList)
                .build());
    }

    private void validateCustomerService(ResponseEntity<TmbOneServiceResponse<List<CustomerSearchResponse>>> customerInfo) throws TMBCommonException {
        if (!customerInfo.getStatusCode().equals(HttpStatus.OK) || StringUtils.isEmpty(customerInfo.getBody().getData())) {
            throwTmbException("========== failed customer search service ==========");
        }
    }
}
