package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.NotificationServiceClient;
import com.tmb.oneapp.productsexpservice.model.CustomerFirstUsage;
import com.tmb.oneapp.productsexpservice.model.request.crm.CustomerCaseSubmitBody;
import com.tmb.common.model.request.notification.EmailChannel;
import com.tmb.common.model.request.notification.NotificationRecord;
import com.tmb.common.model.request.notification.NotificationRequest;
import com.tmb.oneapp.productsexpservice.model.response.ncb.NcbPaymentConfirmResponse;
import com.tmb.common.model.response.notification.NotificationResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.*;

/**
 * NcbPaymentConfirmService confirm payment of NCB
 */
@Service
public class NcbPaymentConfirmService {
    private static final TMBLogger<NcbPaymentConfirmService> logger = new TMBLogger<>(NcbPaymentConfirmService.class);

    private final CustomerServiceClient customerServiceClient;
    private final NotificationServiceClient notificationServiceClient;

    public NcbPaymentConfirmService(CustomerServiceClient customerServiceClient,
                                    NotificationServiceClient notificationServiceClient) {
        this.customerServiceClient = customerServiceClient;
        this.notificationServiceClient = notificationServiceClient;
    }

    /**
     * confirm payment of NCB
     *
     * @param requestHeaders correlationId, crmId, deviceId, accept-language
     * @param serviceTypeId  serviceTypeId
     * @param firstnameTh    firstnameTh
     * @param lastnameTh     lastnameTh
     * @param firstnameEn    firstnameEn
     * @param lastnameEn     lastnameEn
     * @param email          email
     * @param address        address
     * @param deliveryMethod deliveryMethod
     * @param accountNumber  accountNumber
     * @return NcbPaymentConfirmResponse NcbPaymentConfirmResponse
     */
    @LogAround
    public NcbPaymentConfirmResponse confirmNcbPayment(Map<String, String> requestHeaders, //NOSONAR lightweight logging
                                                       String serviceTypeId, String firstnameTh, String lastnameTh, String firstnameEn,
                                                       String lastnameEn, String email, String address, String deliveryMethod, String accountNumber) throws TMBCommonException {
        try {
            NcbPaymentConfirmResponse response = new NcbPaymentConfirmResponse();

            String correlationId = requestHeaders.get(X_CORRELATION_ID);
            String crmId = requestHeaders.get(X_CRMID);
            String deviceId = requestHeaders.get(DEVICE_ID);

            String currentTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").format(new Date());

            Map<String, Object> params = new LinkedHashMap<>();
            params.put("template_name", "ncb_payment_completed");
            if (firstnameTh.isEmpty() && lastnameTh.isEmpty()) {
                params.put("custFullNameTH", "");
            } else {
                params.put("custFullNameTH", (firstnameTh + " " + lastnameTh));
            }
            if (firstnameEn.isEmpty() && lastnameEn.isEmpty()) {
                params.put("custFullNameEN", "");
            } else {
                params.put("custFullNameEN", (firstnameEn + " " + lastnameEn));
            }
            if (deliveryMethod.equals("by email")) {
                params.put("DeliveryMethodTH", "ทางอีเมล");
                params.put("DeliveryMethodEN", "By e-mail");
                params.put("CustDeliveryDetail", email);
            } else if (deliveryMethod.equals("by post")) {
                params.put("DeliveryMethodTH", "ทางไปรษณีย์");
                params.put("DeliveryMethodEN", "By post");
                params.put("CustDeliveryDetail", address);
            }
            params.put("toAcctNumber", accountNumber);
            params.put("successDateTime", currentTime);
            params.put("ReferenceID", "1234567890");

            EmailChannel emailChannel = new EmailChannel();
            emailChannel.setCc("");
            emailChannel.setEmailEndpoint(email);
            emailChannel.setEmailSearch(false);

            NotificationRecord notificationRecord = new NotificationRecord();
            notificationRecord.setCrmId(crmId);
            notificationRecord.setParams(params);
            notificationRecord.setEmail(emailChannel);

            List<NotificationRecord> notificationRecordList = new ArrayList<>();
            notificationRecordList.add(notificationRecord);

            sendEmail(correlationId, notificationRecordList);

            createNcbCase(crmId, correlationId, firstnameTh, lastnameTh, firstnameEn, lastnameEn, deliveryMethod);

            CustomerFirstUsage customerFirstUsage = getFirstTimeUsage(crmId, deviceId, serviceTypeId);

            if (customerFirstUsage != null) {
                putFirstTimeUsage(crmId, deviceId, serviceTypeId);
            } else {
                postFirstTimeUsage(crmId, deviceId, serviceTypeId);
            }

            return response
                    .setTransactionDate(currentTime)
                    .setReferenceNo("1234567890");

        } catch (Exception e) {
            logger.error("Error calling GET /application/status : {}", e);
            throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                    ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(), HttpStatus.BAD_REQUEST, null);
        }
    }

    /**
     * Send email for confirm payment
     *
     * @param correlationId      correlationId
     * @param notificationRecord notificationRecord
     * @return boolean status of email sending
     */
    @LogAround
    public boolean sendEmail(String correlationId, List<NotificationRecord> notificationRecord) {
        try {
            NotificationRequest notificationRequest = new NotificationRequest();
            notificationRequest.setRecords(notificationRecord);
            TmbOneServiceResponse<NotificationResponse> response = notificationServiceClient.sendMessage(correlationId, notificationRequest);

            if (response.getData().isSuccess()) {
                logger.info("Send Email success");
                return true;
            }
        } catch (Exception e) {
            logger.error("Send Email error: {}", e);
        }

        return false;
    }

    /**
     * Create CRM case for NCB
     *
     * @param crmId          crmId
     * @param correlationId  correlationId
     * @param firstnameTh    firstnameTh
     * @param lastnameTh     lastnameTh
     * @param firstnameEn    firstnameEn
     * @param lastnameEn     lastnameEn
     * @param deliveryMethod deliveryMethod
     * @return Map of result
     */
    @LogAround
    public Map<String, String> createNcbCase(String crmId, String correlationId, String firstnameTh, String lastnameTh, String firstnameEn, String lastnameEn, String deliveryMethod) {
        try {
            String firstname = (!firstnameTh.isEmpty()) ? firstnameTh : firstnameEn;
            String lastname = (!lastnameEn.isEmpty()) ? lastnameTh : lastnameEn;

            byte[] bytesFirstname = firstname.getBytes(StandardCharsets.UTF_8);
            firstname = new String(bytesFirstname, StandardCharsets.UTF_8);
            byte[] bytesLastname = lastname.getBytes(StandardCharsets.UTF_8);
            lastname = new String(bytesLastname, StandardCharsets.UTF_8);

            String serviceTypeMatrixCode = SERVICE_TYPE_MATRIX_CODE_NCB_BY_EMAIL;

            if (deliveryMethod.equals("by post")) {
                serviceTypeMatrixCode = SERVICE_TYPE_MATRIX_CODE_NCB_BY_POST;
            }

            CustomerCaseSubmitBody customerCaseSubmitBody = new CustomerCaseSubmitBody(firstname, lastname, serviceTypeMatrixCode, "");

            ResponseEntity<TmbOneServiceResponse<Map<String, String>>> response =
                    customerServiceClient.submitCustomerCase(crmId, correlationId, customerCaseSubmitBody);
            return response.getBody().getData(); //NOSONAR lightweight logging
        } catch (Exception e) {
            logger.error("createNcbCase error : {}", e);
            return new HashMap<>();
        }
    }

    /**
     * Get customer first time use
     *
     * @param crmId         customer Id
     * @param deviceId      device Id
     * @param serviceTypeId serviceType Id
     */
    public CustomerFirstUsage getFirstTimeUsage(String crmId, String deviceId, String serviceTypeId) {
        try {
            logger.info("Calling GET /apis/customers/firstTimeUsage.");

            ResponseEntity<TmbOneServiceResponse<CustomerFirstUsage>> response =
                    customerServiceClient.getFirstTimeUsage(crmId, deviceId, serviceTypeId);

            if (response != null && response.getStatusCode() == HttpStatus.OK) {
                logger.info("call get first time usage completed successfully. " +
                        "crmId: {}, deviceId: {}", crmId, deviceId);  //NOSONAR lightweight logging

                return response.getBody().getData();  //NOSONAR lightweight logging
            }
        } catch (Exception e) {
            logger.error("call to get first time usage failed. " +
                    "crmId: {}, deviceId: {}, error: {}", crmId, deviceId, e);  //NOSONAR lightweight logging
        }

        return null;
    }

    /**
     * Insert customer first time use
     *
     * @param crmId         customer Id
     * @param deviceId      device Id
     * @param serviceTypeId serviceType Id
     */
    public String postFirstTimeUsage(String crmId, String deviceId, String serviceTypeId) {
        try {
            logger.info("Calling POST /apis/customers/firstTimeUsage.");

            ResponseEntity<TmbOneServiceResponse<String>> response =
                    customerServiceClient.postFirstTimeUsage(crmId, deviceId, serviceTypeId);

            if (response != null && response.getStatusCode() == HttpStatus.OK) {
                logger.info("call to insert first time usage completed successfully. " +
                        "crmId: {}, deviceId: {}", crmId, deviceId);
                return "success";
            }
        } catch (Exception e) {
            logger.error("call to insert first time usage failed. " +
                    "crmId: {}, deviceId: {}, error: {}", crmId, deviceId, e);
        }

        return "";
    }

    /**
     * Update customer first time use
     *
     * @param crmId         customer Id
     * @param deviceId      device Id
     * @param serviceTypeId serviceType Id
     */
    public String putFirstTimeUsage(String crmId, String deviceId, String serviceTypeId) {
        try {
            logger.info("Calling PUT /apis/customers/firstTimeUsage.");

            ResponseEntity<TmbOneServiceResponse<String>> response =
                    customerServiceClient.putFirstTimeUsage(crmId, deviceId, serviceTypeId);

            if (response != null && response.getStatusCode() == HttpStatus.OK) {
                logger.info("call to update first time usage completed successfully. " +
                        "crmId: {}, deviceId: {}", crmId, deviceId);
                return "success";
            }
        } catch (Exception e) {
            logger.error("call to update first time usage failed. " +
                    "crmId: {}, deviceId: {}, error: {}", crmId, deviceId, e);
        }

        return "";
    }
}
