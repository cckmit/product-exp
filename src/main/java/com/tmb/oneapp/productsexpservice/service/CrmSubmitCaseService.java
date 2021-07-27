package com.tmb.oneapp.productsexpservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.kafka.service.KafkaProducerService;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.BaseEvent;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.model.activitylog.CaseSubmitPwaActivity;
import com.tmb.oneapp.productsexpservice.model.request.crm.CustomerCaseSubmitBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.*;

/**
 * CrmSubmitCaseService submit crm case
 */
@Service
public class CrmSubmitCaseService {
    private static final TMBLogger<CrmSubmitCaseService> logger = new TMBLogger<>(CrmSubmitCaseService.class);

    private final CustomerServiceClient customerServiceClient;
    private final String topicName;
    private final KafkaProducerService kafkaProducerService;

    public CrmSubmitCaseService(CustomerServiceClient customerServiceClient,
                                KafkaProducerService kafkaProducerService,
                                @Value("${com.tmb.oneapp.service.activity.topic.name}") final String topicName) {
        this.customerServiceClient = customerServiceClient;
        this.kafkaProducerService = kafkaProducerService;
        this.topicName = topicName;
    }

    /**
     * confirm payment of NCB
     *
     * @param requestHeaders crmId deviceId correlationId
     * @param firstnameTh  firstnameTh
     * @param lastnameTh  lastnameTh
     * @param firstnameEn  firstnameEn
     * @param lastnameEn  lastnameEn
     * @param serviceTypeMatrixCode serviceTypeMatrixCode
     * @return NcbPaymentConfirmResponse NcbPaymentConfirmResponse
     */
    @LogAround
    public Map<String, String> createCrmCase(Map<String, String> requestHeaders, String firstnameTh, String lastnameTh, String firstnameEn, String lastnameEn, String serviceTypeMatrixCode, String note) throws TMBCommonException { //NOSONAR lightweight logging
        String correlationId = requestHeaders.get(HEADER_X_CORRELATION_ID);
        String crmId = requestHeaders.get(X_CRMID);
        String activityId = "";
        if(serviceTypeMatrixCode.equals(SERVICE_TYPE_MATRIC_CODE_PWA_SEND_EMAIL_TO_ADVISOR)) {
            activityId = CASE_SUBMIT_PWA_BY_EMAIL_ACTIVITY_ID;
        } else if(serviceTypeMatrixCode.equals(SERVICE_TYPE_MATRIC_CODE_PWA_CALL_TO_ADVISOR)) {
            activityId = CASE_SUBMIT_PWA_BY_CALL_ACTIVITY_ID;
        } else if(serviceTypeMatrixCode.equals(SERVICE_TYPE_MATRIC_CODE_PWA_SEND_MESSAGE_TO_ADVISOR)) {
            activityId = CASE_SUBMIT_PWA_BY_LEAVE_MSG_ACTIVITY_ID;
        }

        try {
            String firstname = (!firstnameTh.isEmpty()) ? firstnameTh : firstnameEn;
            String lastname = (!lastnameEn.isEmpty()) ? lastnameTh : lastnameEn;

            byte[] bytesFirstname = firstname.getBytes(StandardCharsets.UTF_8);
            firstname = new String(bytesFirstname, StandardCharsets.UTF_8);
            byte[] bytesLastname = lastname.getBytes(StandardCharsets.UTF_8);
            lastname = new String(bytesLastname, StandardCharsets.UTF_8);

            CustomerCaseSubmitBody customerCaseSubmitBody = new CustomerCaseSubmitBody(firstname, lastname, serviceTypeMatrixCode, note);

            ResponseEntity<TmbOneServiceResponse<Map<String, String>>> response =
                    customerServiceClient.submitCustomerCase(crmId, correlationId, customerCaseSubmitBody);

            String caseNumber = response.getBody().getData().get(ProductsExpServiceConstant.CASE_NUMBER);  //NOSONAR lightweight logging
            String requestDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").format(new Date());

            Map<String, String> result = new HashMap<>();
            result.put(ProductsExpServiceConstant.CASE_NUMBER, caseNumber);  //NOSONAR lightweight logging
            result.put(ProductsExpServiceConstant.TRANSACTION_DATE, requestDate);  //NOSONAR lightweight logging

            //101500503 - email, 101500504 - call, 101500505 - leave msg
            logActivityCST(new CaseSubmitPwaActivity(correlationId,
                            String.valueOf(System.currentTimeMillis()),
                            activityId)
                            .setCaseNumber(caseNumber)
                            .setRequestDate(requestDate),
                    requestHeaders,
                    SUCCESS,
                    "");

            return result; //NOSONAR lightweight logging
        } catch (Exception e) {
            logger.error("createNcbCase error : {}", e);
            logActivityCST(new CaseSubmitPwaActivity(correlationId,
                            String.valueOf(System.currentTimeMillis()),
                            activityId)
                            .setCaseNumber("")
                            .setRequestDate(""),
                    requestHeaders,
                    FAILURE,
                    "Feign Error occured when calling GET /apis/customers/case/submit : " + e.toString());

            throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                    ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(), HttpStatus.BAD_REQUEST, null);
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
            baseEvent.setCorrelationId(requestHeaders.get(HEADER_X_CORRELATION_ID));
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
