package com.tmb.oneapp.productsexpservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.kafka.service.KafkaProducerService;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.BaseEvent;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.model.CustomerFirstUsage;
import com.tmb.oneapp.productsexpservice.model.activitylog.CustomerServiceActivity;
import com.tmb.oneapp.productsexpservice.model.response.statustracking.CaseStatusCase;
import com.tmb.oneapp.productsexpservice.model.response.statustracking.CaseStatusResponse;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.*;
import static com.tmb.oneapp.productsexpservice.constant.ResponseCode.DATA_NOT_FOUND_ERROR;

@Service
public class CaseService {
    private static final TMBLogger<CaseService> logger = new TMBLogger<>(CaseService.class);
    private final CustomerServiceClient customerServiceClient;
    private final String topicName;
    private final KafkaProducerService kafkaProducerService;

    public CaseService(CustomerServiceClient customerServiceClient,
                       KafkaProducerService kafkaProducerService,
                       @Value("${com.tmb.oneapp.service.activity.topic.name}") final String topicName) {
        this.customerServiceClient = customerServiceClient;
        this.kafkaProducerService = kafkaProducerService;
        this.topicName = topicName;
    }

    /**
     * Get case status and first time use info for customer
     *
     * @param requestHeaders crmId deviceId correlationId
     * @return CaseStatusResponse all case statuses belonging to customer Id
     */
    public CaseStatusResponse getCaseStatus(Map<String, String> requestHeaders, String serviceTypeId) throws TMBCommonException {

        try {
            String correlationId = requestHeaders.get(X_CORRELATION_ID);
            String crmId = requestHeaders.get(X_CRMID);
            String deviceId = requestHeaders.get(DEVICE_ID);

            //GET /apis/customers/firstTimeUsage
            logger.info("Calling GET /apis/customers/firstTimeUsage.");
            CustomerFirstUsage customerFirstUsage = getFirstTimeUsage(requestHeaders, serviceTypeId);
            logger.info("GET /apis/customers/firstTimeUsage response: {}", customerFirstUsage);

            //GET /apis/customer/case/status/{CRM_ID}.
            logger.info("Calling GET /apis/customer/case/status/{CRM_ID}");
            List<CaseStatusCase> caseStatusList = getCaseStatus(requestHeaders);
            logger.info("GET /apis/customer/case/status/{CRM_ID} response: {}", caseStatusList);

            //Separate According to Status
            List<CaseStatusCase> inProgress = new ArrayList<>();
            List<CaseStatusCase> completed = new ArrayList<>();

            caseStatusList.forEach(caseStatusCase -> {
                if (caseStatusCase.getStatus().equalsIgnoreCase(CASE_STATUS_IN_PROGRESS)) {
                    inProgress.add(caseStatusCase);
                } else if (caseStatusCase.getStatus().equalsIgnoreCase(CASE_STATUS_CLOSED)) {
                    completed.add(caseStatusCase);
                }
            });

            //Send Activity Log
            if (customerFirstUsage == null) {
                //101500201
                logActivityCST(new CustomerServiceActivity(correlationId,
                                String.valueOf(System.currentTimeMillis()),
                                CASE_TRACKING_TUTORIAL_ACTIVITY_ID)
                                .setScreenName(ACTIVITY_SCREEN_NAME_TUTORIAL_CST),
                        requestHeaders,
                        ACTIVITY_LOG_SUCCESS,
                        "");
            }

            //POST /apis/customers/firstTimeUsage
            if (customerFirstUsage == null) {
                logger.info("Calling POST /apis/customers/firstTimeUsage.");
                asyncPostFirstTime(crmId, deviceId, serviceTypeId);
            }
            return new CaseStatusResponse()
                    .setServiceTypeId(serviceTypeId)
                    .setFirstUsageExperience(customerFirstUsage == null)
                    .setInProgress(inProgress)
                    .setCompleted(completed);

        } catch (Exception e) {
            logger.error("Error calling GET /case/status : {}", e);
            throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                    ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(), HttpStatus.BAD_REQUEST, null);
        }

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
            TmbOneServiceResponse response = mapTmbOneServiceResponse(e.responseBody());

            if (response != null && response.getStatus() != null && DATA_NOT_FOUND_ERROR.getCode().equals(response.getStatus().getCode())) {
                return getCustomerFirstUsage(crmId, deviceId);
            } else {
                logger.error("Unexpected error occured : {}", e);
                logActivityCST(new CustomerServiceActivity(correlationId,
                                String.valueOf(System.currentTimeMillis()),
                                CASE_TRACKING_TUTORIAL_ACTIVITY_ID)
                                .setScreenName(ACTIVITY_SCREEN_NAME_TUTORIAL_CST),
                        requestHeaders,
                        FAILURE,
                        "Feign Error occured when calling GET /apis/customers/firstTimeUsage : " + e.toString());
                throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                        ResponseCode.FAILED.getMessage(),
                        ResponseCode.FAILED.getService(), HttpStatus.BAD_REQUEST, null);
            }
        } catch (Exception e) {
            logger.error("Error getting first time usage data. crmId: {}, deviceId {}, error: {}", crmId, deviceId, e);
            logActivityCST(new CustomerServiceActivity(correlationId,
                            String.valueOf(System.currentTimeMillis()),
                            CASE_TRACKING_TUTORIAL_ACTIVITY_ID)
                            .setScreenName(ACTIVITY_SCREEN_NAME_TUTORIAL_CST),
                    requestHeaders,
                    FAILURE,
                    "Unexpected Error occured when calling GET /apis/customers/firstTimeUsage : " + e.toString());
            throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                    ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(), HttpStatus.BAD_REQUEST, null);
        }
    }

    CustomerFirstUsage getCustomerFirstUsage(String crmId, String deviceId) {
        logger.info("Data not found in database. crmId: {}, deviceId {}", crmId, deviceId);
        return null;
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
     * Get customer case information
     *
     * @param crmId customer Id
     * @return list of all case status
     */
    @SuppressWarnings("all")
    public List<CaseStatusCase> getCaseStatus(Map<String, String> requestHeaders) throws TMBCommonException {
        String correlationId = requestHeaders.get(X_CORRELATION_ID);
        String crmId = requestHeaders.get(X_CRMID);

        try {
            ResponseEntity<TmbOneServiceResponse<List<CaseStatusCase>>> getCaseStatusResponse =
                    customerServiceClient.getCaseStatus(correlationId, crmId);

            if (getCaseStatusResponse.getBody() != null &&
                    getCaseStatusResponse.getBody().getStatus() != null &&
                    SUCCESS_CODE.equals(getCaseStatusResponse.getBody().getStatus().getCode())) {
                return getCaseStatusResponse.getBody().getData();
            }
            return new ArrayList<>();
        } catch (FeignException e) {
            TmbOneServiceResponse response = mapTmbOneServiceResponse(e.responseBody());

            if (response != null && response.getStatus() != null && DATA_NOT_FOUND_ERROR.getCode().equals(response.getStatus().getCode())) {
                logger.info("Data not found. crmId: {}", crmId);
                return new ArrayList<>();
            } else {
                logger.error("Unexpected error occured : {}", e);
                throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                        ResponseCode.FAILED.getMessage(),
                        ResponseCode.FAILED.getService(), HttpStatus.BAD_REQUEST, null);
            }
        } catch (Exception e) {
            logger.error("Unexpected error occured : {}", e);
            throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                    ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(), HttpStatus.BAD_REQUEST, null);
        }
    }

    @SuppressWarnings("all")
    public TmbOneServiceResponse mapTmbOneServiceResponse(Optional<ByteBuffer> optionalResponse) {
        try {
            if (!optionalResponse.isPresent()) {
                return null;
            }

            String respBody = StandardCharsets.UTF_8.decode(optionalResponse.get()).toString();
            return (TmbOneServiceResponse) TMBUtils.convertStringToJavaObj(respBody, TmbOneServiceResponse.class);
        } catch (Exception e) {
            logger.error("Unexpected error received, cannot parse.");
            return null;
        }
    }

    /**
     * Method logActivity
     *
     * @param baseEvent base event
     */
    @Async
    @LogAround
    public void logActivityCST(BaseEvent baseEvent, Map<String, String> requestHeaders, String activityStatus, String failReason) {
        try {
            baseEvent.setActivityStatus(activityStatus);
            baseEvent.setFailReason(failReason);

            baseEvent.setActivityDate(String.valueOf(System.currentTimeMillis()));
            baseEvent.setCrmId(requestHeaders.get(X_CRMID));
            baseEvent.setDeviceId(requestHeaders.get(DEVICE_ID));
            baseEvent.setCorrelationId(requestHeaders.get(X_CORRELATION_ID));
            baseEvent.setChannel(requestHeaders.get(CHANNEL));
            baseEvent.setAppVersion(requestHeaders.get(APP_VERSION));
            baseEvent.setIpAddress(requestHeaders.get(X_FORWARD_FOR));
            baseEvent.setDeviceModel(requestHeaders.get(DEVICE_MODEL));
            baseEvent.setOsVersion(requestHeaders.get(OS_VERSION));

            ObjectMapper mapper = new ObjectMapper();
            String output = mapper.writeValueAsString(baseEvent);
            logger.info("Activity Data request is  {} : ", output);
            logger.info("Activity Data request topicName is  {} : ", topicName);
            kafkaProducerService.sendMessageAsync(topicName, output);
            logger.info("callPostEventService -  data posted to activity_service : {}", System.currentTimeMillis());
        } catch (Exception e) {
            logger.info("Unable to log the activity request : {}", e.toString());
        }
    }

}

