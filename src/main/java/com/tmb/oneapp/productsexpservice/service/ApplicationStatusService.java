package com.tmb.oneapp.productsexpservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.kafka.service.KafkaProducerService;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.BaseEvent;
import com.tmb.common.model.CustomerProfileResponseData;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ApplicationStatusEnum;
import com.tmb.oneapp.productsexpservice.constant.RSLProductCodeEnum;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.model.CustomerFirstUsage;
import com.tmb.oneapp.productsexpservice.model.LoanDetails;
import com.tmb.oneapp.productsexpservice.model.activitylog.CustomerServiceActivity;
import com.tmb.oneapp.productsexpservice.model.response.NodeDetails;
import com.tmb.oneapp.productsexpservice.model.response.statustracking.ApplicationStatusApplication;
import com.tmb.oneapp.productsexpservice.model.response.statustracking.ApplicationStatusResponse;
import com.tmb.oneapp.productsexpservice.model.response.statustracking.LendingRslStatusResponse;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.tmb.oneapp.productsexpservice.constant.ApplicationStatusEnum.SD2;
import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.*;
import static com.tmb.oneapp.productsexpservice.constant.ResponseCode.DATA_NOT_FOUND_ERROR;

/**
 * ApplicationStatusService process application status information
 */
@Service
public class ApplicationStatusService {
    private static final TMBLogger<ApplicationStatusService> logger = new TMBLogger<>(ApplicationStatusService.class);

    private final CustomerServiceClient customerServiceClient;
    private final AsyncApplicationStatusService asyncApplicationStatusService;
    private final CommonServiceClient commonServiceClient;
    private final KafkaProducerService kafkaProducerService;
    private final String topicName;

    public ApplicationStatusService(CustomerServiceClient customerServiceClient,
                                    AsyncApplicationStatusService asyncApplicationStatusService,
                                    CommonServiceClient commonServiceClient,
                                    KafkaProducerService kafkaProducerService,
                                    @Value("${com.tmb.oneapp.service.activity.topic.name}") final String topicName) {
        this.customerServiceClient = customerServiceClient;
        this.asyncApplicationStatusService = asyncApplicationStatusService;
        this.commonServiceClient = commonServiceClient;
        this.kafkaProducerService = kafkaProducerService;
        this.topicName = topicName;
    }

    /**
     * Get get HP and RSL application status
     *
     * @param requestHeaders correlatinoId, crmId, deviceId, accept-language
     * @param serviceTypeId  service type for this endpoint
     * @return CustomerProfileResponseData customer data
     */
    public ApplicationStatusResponse getApplicationStatus(Map<String, String> requestHeaders,
                                                          String serviceTypeId) throws TMBCommonException {
        try {
            ApplicationStatusResponse response = new ApplicationStatusResponse();

            String correlationId = requestHeaders.get(X_CORRELATION_ID);
            String crmId = requestHeaders.get(X_CRMID);
            String deviceId = requestHeaders.get(DEVICE_ID);
            String language = requestHeaders.get(ACCEPT_LANGUAGE);

            //GET /apis/customers/{crmId} CUSTOMER-SERVICE
            CustomerProfileResponseData customerProfileResponseData = getCustomerCrmId(crmId);
            String nationalId = customerProfileResponseData.getIdNo();
            String mobileNo = customerProfileResponseData.getPhoneNoFull();

            // === Get Node Text ===
            List<NodeDetails> nodeTextList = getNodeText();

            // === Hire Purchase ===
            //GET /apis/hpservice/loan-status/application-list
            //GET /apis/hpservice/loan-status/application-detail
            CompletableFuture<List<LoanDetails>> hpResponse =
                    asyncApplicationStatusService.getHpData(correlationId, language, nationalId, mobileNo);

            // === RSL ===
            //GET /apis/lending-service/rsl/status
            CompletableFuture<List<LendingRslStatusResponse>> rslResponse =
                    asyncApplicationStatusService.getRSLData(correlationId, nationalId, mobileNo);

            CompletableFuture.allOf(hpResponse, rslResponse).get();

            List<ApplicationStatusApplication> allApplications = new ArrayList<>();
            mapHPApplications(hpResponse.get(), allApplications, language, nodeTextList);
            mapRSLApplications(rslResponse.get(), allApplications);

            //Combine & Sort
            allApplications.sort(Comparator.comparing(ApplicationStatusApplication::getLastUpdateDate).reversed());

            //Split by status
            List<ApplicationStatusApplication> inProgress = new ArrayList<>();
            List<ApplicationStatusApplication> completed = new ArrayList<>();
            allApplications.forEach(application -> {
                if (APPLICATION_STATUS_APPROVED.equals(application.getStatus())
                        || APPLICATION_STATUS_REJECTED.equals(application.getStatus())
                        || APPLICATION_STATUS_COMPLETED.equals(application.getStatus())) {
                    completed.add(application);
                } else {
                    inProgress.add(application);
                }
            });

            // === Check First Time Usage ===
            //GET /apis/customers/firstTimeUsage
            CustomerFirstUsage customerFirstUsage = getFirstTimeUsage(requestHeaders, serviceTypeId);
            logger.info("GET /apis/customers/firstTimeUsage response: {}", customerFirstUsage);

            //POST /apis/customers/firstTimeUsage
            if (customerFirstUsage == null) {
                asyncPostFirstTime(crmId, deviceId, serviceTypeId);

                //Log tutorial activity
                logASTTutorialActivity(new CustomerServiceActivity(correlationId,
                                String.valueOf(System.currentTimeMillis()),
                                APPLICATION_TRACKING_TUTORIAL_ACTIVITY_ID)
                                .setScreenName(ACTIVITY_SCREEN_NAME_TUTORIAL_AST),
                        requestHeaders,
                        ACTIVITY_LOG_SUCCESS,
                        "");
            }

            return response
                    .setFirstUsageExperience(customerFirstUsage == null)
                    .setServiceTypeId(SERVICE_TYPE_ID_AST)
                    .setInProgress(inProgress)
                    .setCompleted(completed)
                    .setHpStatus(getStatus(hpResponse.get()))
                    .setRslStatus(getStatus(rslResponse.get()));

        } catch (TMBCommonException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error calling GET /application/status : {}", e);
            throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                    ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(), HttpStatus.BAD_REQUEST, null);
        }
    }

    /**
     * get status for responses
     *
     * @return integer 1 = error occurred, 2 = no data found, 0 = success
     */
    <T> int getStatus(List<T> list) {
        if (list == null) {
            return 1;
        } else if (list.isEmpty()) {
            return 2;
        }
        return 0;
    }

    /**
     * Get customerProfileData
     *
     * @param crmId customer Id
     * @return CustomerProfileResponseData customer data
     */
    private CustomerProfileResponseData getCustomerCrmId(String crmId) {
        logger.info("Calling GET /apis/customers/{crmId}");
        ResponseEntity<TmbOneServiceResponse<CustomerProfileResponseData>> customersCrmIdResponse =
                customerServiceClient.getCustomerProfile(new HashMap<>(), crmId);
        logger.info("GET /apis/customers/{crmId} response : {}", customersCrmIdResponse);

        return Objects.requireNonNull(customersCrmIdResponse.getBody()).getData();
    }

    /**
     * Get nodeText from mongoDb
     *
     * @return List of node text
     */
    private List<NodeDetails> getNodeText() {
        ResponseEntity<TmbOneServiceResponse<List<NodeDetails>>> nodeTextResponse =
                commonServiceClient.getProductApplicationRoadMap();

        return Objects.requireNonNull(nodeTextResponse.getBody()).getData();
    }

    /**
     * Map HP Applications
     *
     * @param applicationDetailList HP applications
     * @param allApplications       list of HP and RSL applications
     * @param language              language of HP response
     */
    private void mapHPApplications(List<LoanDetails> applicationDetailList,
                                   List<ApplicationStatusApplication> allApplications,
                                   String language, List<NodeDetails> nodeDetailsList) throws ParseException, TMBCommonException {

        if (applicationDetailList == null) {
            return;
        }

        NodeDetails hpNodeDetails = nodeDetailsList.stream().filter(nodeDetails ->
                HIRE_PURCHASE_HP.equals(nodeDetails.getLoanSystem()))
                .findFirst().orElse(null);
        if (hpNodeDetails == null) {
            logger.error("Unable to retrieve HP node text.");
            throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                    ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(), HttpStatus.BAD_REQUEST, null);
        }

        for (LoanDetails hpApplication : applicationDetailList) {
            logger.info("Processing application: {}", hpApplication);
            String carModel = hpApplication.getCarBrand() + " " + hpApplication.getCarFamily();
            String message = SD2.getHpStatus().equals(hpApplication.getHPAPStatus()) ||
                    hpApplication.getHPAPStatus().startsWith(APPLICATION_STATUS_CC)
                    ? hpApplication.getMsg() : null;

            String date = convertHPToApplicationDateTimeFormat(hpApplication.getStatusDate());

            String formattedStatusCode = hpApplication.getHPAPStatus().replace("+", "");
            ApplicationStatusEnum matchingEnum = ApplicationStatusEnum.valueOf(formattedStatusCode);

            allApplications.add(new ApplicationStatusApplication()
                    .setStatus(matchingEnum.getStatus())
                    .setProductCode(HIRE_PURCHASE_HP)
                    .setProductCategoryTh(HIRE_PURCHASE_TH)
                    .setProductCategoryEn(HIRE_PURCHASE_EN)
                    .setProductTypeTh(HIRE_PURCHASE_TH)
                    .setProductTypeEn(HIRE_PURCHASE_EN)
                    .setProductDetailTh(ACCEPT_LANGUAGE_TH.equals(language) ? carModel : null)
                    .setProductDetailEn(ACCEPT_LANGUAGE_EN.equals(language) ? carModel : null)
                    .setReferenceNo(hpApplication.getAppNo())
                    .setCurrentNode(matchingEnum.getCurrentNode())
                    .setNodeTextTh(hpNodeDetails.getNodeTh())//CONSTANT
                    .setNodeTextEn(hpNodeDetails.getNodeEn())//CONSTANT
                    .setBottomRemarkTh(ACCEPT_LANGUAGE_TH.equals(language) ? message : null)
                    .setBottomRemarkEn(ACCEPT_LANGUAGE_EN.equals(language) ? message : null)
                    .setApplicationDate(date)
                    .setLastUpdateDate(date)
            );
        }

        logger.info("After mapping HP Applications: {}", allApplications);
    }

    /**
     * Get map RSL Data
     *
     * @param rslApplications list of retrieved RSL applications
     * @param allApplications list of mapped data
     */
    private void mapRSLApplications(List<LendingRslStatusResponse> rslApplications,
                                    List<ApplicationStatusApplication> allApplications) {

        if (rslApplications == null) {
            return;
        }

        rslApplications.forEach(rslApplication -> {
                    RSLProductCodeEnum matchingEnum = RSLProductCodeEnum.valueOf(rslApplication.getProductCode());

                    allApplications.add(new ApplicationStatusApplication()
                            .setStatus(rslApplication.getAppStatus())
                            .setProductCode(rslApplication.getProductCode())
                            .setProductCategoryTh(matchingEnum.getProductNameTh())
                            .setProductCategoryEn(matchingEnum.getProductNameEn())
                            .setProductTypeTh(rslApplication.getProductTypeTh())
                            .setProductTypeEn(rslApplication.getProductTypeEn())
                            .setReferenceNo(rslApplication.getReferenceNo())
                            .setImageUrl(rslApplication.getImageUrl())
                            .setCurrentNode(Integer.parseInt(rslApplication.getCurrentNode()))
                            .setTopRemarkEn(rslApplication.getTopRemarkEn())
                            .setTopRemarkEn(rslApplication.getTopRemarkTh())
                            .setBottomRemarkTh(rslApplication.getBottomRemarkTh())
                            .setBottomRemarkEn(rslApplication.getBottomRemarkEn())
                            .setApplicationDate(rslApplication.getApplicationDate())
                            .setLastUpdateDate(RSL_CURRENT_NODE_1.equals(rslApplication.getCurrentNode()) ?
                                    rslApplication.getApplicationDate() :
                                    rslApplication.getLastUpdateDate())
                            .setIsApproved(APPLICATION_STATUS_FLAG_TRUE.equals(rslApplication.getIsApproved()))
                            .setIsRejected(APPLICATION_STATUS_FLAG_TRUE.equals(rslApplication.getIsRejected()))
                    );
                }
        );
        logger.info("After mapping RSL Applications: {}", allApplications);

    }

    /**
     * Get customerProfileData
     *
     * @param hpDate string of date in HP format
     * @return string of date in application status format
     */
    private String convertHPToApplicationDateTimeFormat(String hpDate) throws ParseException {
        SimpleDateFormat applicationDateTimeParser = new SimpleDateFormat(APPLICATION_STATUS_DATETIME_FORMAT);
        SimpleDateFormat hpDateTimeParser = new SimpleDateFormat(HP_DATETIME_FORMAT);

        Date date = hpDateTimeParser.parse(hpDate);

        return applicationDateTimeParser.format(date);
    }


    /**
     * Check customer first time use
     *
     * @param crmId    customer Id
     * @param deviceId device Id
     * @return CustomerFirstUsage information of first time use
     */
    @SuppressWarnings("all")
    public CustomerFirstUsage getFirstTimeUsage(Map<String, String> requestHeaders, String serviceTypeId) throws TMBCommonException {
        logger.info("Calling GET /apis/customers/firstTimeUsage.");
        String correlationId = requestHeaders.get(X_CORRELATION_ID);
        String crmId = requestHeaders.get(X_CRMID);
        String deviceId = requestHeaders.get(DEVICE_ID);

        try {
            ResponseEntity<TmbOneServiceResponse<CustomerFirstUsage>> getFirstTimeUsageResponse =
                    customerServiceClient.getFirstTimeUsage(crmId, deviceId, serviceTypeId);

            if (getFirstTimeUsageResponse.getBody() != null &&
                    getFirstTimeUsageResponse.getBody().getStatus() != null &&
                    SUCCESS_CODE.equals(getFirstTimeUsageResponse.getBody().getStatus().getCode())) {
                return getFirstTimeUsageResponse.getBody().getData();
            }

            return null;
        } catch (FeignException e) {
            TmbOneServiceResponse response = UtilMap.mapTmbOneServiceResponse(e.responseBody());

            if (response != null && response.getStatus() != null && DATA_NOT_FOUND_ERROR.getCode().equals(response.getStatus().getCode())) {
                logger.info("Data not found in database while calling GET /apis/customers/firstTimeUsage. crmId: {}, deviceId {}", crmId, deviceId);
                return null;
            } else {
                logger.error("Unexpected error occured while calling GET /apis/customers/firstTimeUsage: {}", e);
                throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                        ResponseCode.FAILED.getMessage(),
                        ResponseCode.FAILED.getService(), HttpStatus.BAD_REQUEST, null);
            }
        } catch (Exception e) {
            logger.error("Unexpected error occured while calling GET /apis/customers/firstTimeUsage. crmId: {}, deviceId {}, error: {}", crmId, deviceId, e);
            throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                    ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(), HttpStatus.BAD_REQUEST, null);
        }
    }

    /**
     * Insert customer first time use
     *
     * @param crmId    customer Id
     * @param deviceId device Id
     */
    @Async
    public void asyncPostFirstTime(String crmId, String deviceId, String serviceTypeId) {
        try {
            logger.info("Calling POST /apis/customers/firstTimeUsage.");

            ResponseEntity<TmbOneServiceResponse<String>> response =
                    customerServiceClient.postFirstTimeUsage(crmId, deviceId, serviceTypeId);

            if (response != null && response.getStatusCode() == HttpStatus.OK) {
                logger.info("Async call to insert first time usage completed successfully. " +
                        "crmId: {}, deviceId: {}", crmId, deviceId);
            }
        } catch (Exception e) {
            logger.error("Async call to insert first time usage failed. " +
                    "crmId: {}, deviceId: {}, error: {}", crmId, deviceId, e);
        }

    }

    /**
     * Method log activity for application status tracking tutorial
     *
     * @param baseEvent base event
     */
    @Async
    @LogAround
    public void logASTTutorialActivity(BaseEvent baseEvent, Map<String, String> requestHeaders, String activityStatus, String failReason) {
        try {
            baseEvent.setActivityStatus(activityStatus);
            baseEvent.setFailReason(failReason);

            baseEvent.setActivityDate(String.valueOf(System.currentTimeMillis()));
            baseEvent.setCorrelationId(requestHeaders.get(X_CORRELATION_ID));
            baseEvent.setDeviceModel(requestHeaders.get(DEVICE_MODEL));
            baseEvent.setOsVersion(requestHeaders.get(OS_VERSION));
            baseEvent.setChannel(requestHeaders.get(CHANNEL));
            baseEvent.setAppVersion(requestHeaders.get(APP_VERSION));
            baseEvent.setIpAddress(requestHeaders.get(X_FORWARD_FOR));
            baseEvent.setCrmId(requestHeaders.get(X_CRMID));
            baseEvent.setDeviceId(requestHeaders.get(DEVICE_ID));

            ObjectMapper mapper = new ObjectMapper();
            String output = mapper.writeValueAsString(baseEvent);
            logger.info("Activity Data request is  {} : ", output);
            kafkaProducerService.sendMessageAsync(topicName, output);
            logger.info("callPostEventService -  data posted to activity_service : {}", System.currentTimeMillis());
        } catch (Exception e) {
            logger.info("Unable to log the activity request : {}", e.toString());
        }
    }


}
