package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.NotificationServiceClient;
import com.tmb.oneapp.productsexpservice.model.CustomerFirstUsage;
import com.tmb.oneapp.productsexpservice.model.request.notification.EmailChannel;
import com.tmb.oneapp.productsexpservice.model.request.notification.NotificationRecord;
import com.tmb.oneapp.productsexpservice.model.request.notification.NotificationRequest;
import com.tmb.oneapp.productsexpservice.model.response.ncb.NcbPaymentConfirmResponse;
import com.tmb.oneapp.productsexpservice.model.response.notification.NotificationResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.*;

/**
 * ApplicationStatusService process application status information
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
     * Get get HP and RSL application status
     *
     * @param requestHeaders correlatinoId, crmId, deviceId, accept-language
     * @param serviceTypeId  service type for this endpoint
     * @return CustomerProfileResponseData customer data
     */
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
            if(firstnameTh.isEmpty() && lastnameTh.isEmpty()) {
                params.put("custFullNameTH", "");
            } else {
                params.put("custFullNameTH", (firstnameTh + " " + lastnameTh));
            }
            if(firstnameEn.isEmpty() && lastnameEn.isEmpty()) {
                params.put("custFullNameEN", "");
            } else {
                params.put("custFullNameEN", (firstnameEn + " " + lastnameEn));
            }
            if(deliveryMethod.equals("email")) {
                params.put("DeliveryMethodTH", "ทางอีเมล");
                params.put("DeliveryMethodEN", "By e-mail");
                params.put("CustDeliveryDetail", email);
            } else if(deliveryMethod.equals("post")) {
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

            createNcbCase(crmId, correlationId, firstnameTh, lastnameTh, firstnameEn, lastnameEn);

            // === Check First Time Usage ===
            //GET /apis/customers/firstTimeUsage
            CustomerFirstUsage customerFirstUsage = getFirstTimeUsage(crmId, deviceId, serviceTypeId);
            logger.info("GET /apis/customers/firstTimeUsage response: {}", customerFirstUsage);

            //PUT /apis/customers/firstTimeUsage
            if (customerFirstUsage != null) {
                putFirstTimeUsage(crmId, deviceId, serviceTypeId);
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

    public boolean sendEmail(String correlationId, List<NotificationRecord> notificationRecord) {
        try {
            NotificationRequest notificationRequest = new NotificationRequest();
            notificationRequest.setRecords(notificationRecord);
            TmbOneServiceResponse<NotificationResponse> response = notificationServiceClient.sendMessage(correlationId, notificationRequest);

            if (response.getData().isSuccess()) {
                logger.info("Send Email success");
                return true;
            }
        } catch(Exception e) {
            logger.error("Send Email error: {}", e);
        }

        return false;
    }

    public String createNcbCase(String crmId, String correlationId, String firstnameTh, String lastnameTh, String firstnameEn, String lastnameEn) {
        try {
            String firstname = (!firstnameTh.isEmpty())? firstnameTh : firstnameEn;
            String lastname = (!lastnameEn.isEmpty())? lastnameTh : lastnameEn;

            ResponseEntity<TmbOneServiceResponse<String>> response =
                    customerServiceClient.submitNcbCustomerCase(crmId, correlationId, firstname, lastname);
            return response.getBody().getData(); //NOSONAR lightweight logging
        } catch (Exception e) {
            logger.error("Unexpected error occured : {}", e);
            return "";
        }
    }

    /**
     * Get customer first time use
     *
     * @param crmId    customer Id
     * @param deviceId device Id
     * @param serviceTypeId serviceType Id
     */
    public CustomerFirstUsage getFirstTimeUsage(String crmId, String deviceId, String serviceTypeId) {
        try {
            logger.info("Calling PUT /apis/customers/firstTimeUsage.");

            ResponseEntity<TmbOneServiceResponse<CustomerFirstUsage>> response =
                    customerServiceClient.getFirstTimeUsage(crmId, deviceId, serviceTypeId);

            if (response != null && response.getStatusCode() == HttpStatus.OK) {
                logger.info("call to update first time usage completed successfully. " +
                        "crmId: {}, deviceId: {}", crmId, deviceId);
            }
        } catch (Exception e) {
            logger.error("call to update first time usage failed. " +
                    "crmId: {}, deviceId: {}, error: {}", crmId, deviceId, e);
        }

        return null;
    }

    /**
     * Insert customer first time use
     *
     * @param crmId    customer Id
     * @param deviceId device Id
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
