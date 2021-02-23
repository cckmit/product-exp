package com.tmb.oneapp.productsexpservice.constant;

/**
 * Constant class for Application
 *
 */
public class ProductsExpServiceConstant {

    /**
     * created private constructor so that no body can create object of this call
     */
    private ProductsExpServiceConstant() {
    }

    public static final String HEADER_TIMESTAMP = "Timestamp";
    public static final String CONTENT_TYPE = "content-type";
    public static final String INITIALIZE_SSL_CONTEXT = "[initializeSSLContext] ";
    public static final String EXCEPTION_OCCURED = "Exception occured : {}";
    public static final String HEADER_CORRELATION_ID = "X-Correlation-ID";
    public static final String HEADER_CORRELATION_ID_DESC = "Correlation ID";
    public static final String X_COR_ID_DEFAULT  = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
    public static final String INVESTMENT_SERVICE_RESPONSE = "Response from investment service : {}";
    public static final String CUSTOMER_EXP_SERVICE_RESPONSE = "Response from customer-exp service : {}";
    public static final String SUCCESS_CODE = "0000";
    public static final String SUCCESS_MESSAGE = "success";
    public static final String SERVICE_NAME = "product-exp-service";
    public static final String FUND_CODE_ACCDETAIL = "TMB50";
    public static final String FUND_CODE_RULE = "TESEQDSSFX";
    public static final String FUND_HOUSE_CODE_RULE = "TFUND";
    public static final String DATA_NOT_FOUND_CODE  = "0009";
    public static final String DATA_NOT_FOUND_MESSAGE = "DATA NOT FOUND";
    public static final String X_CORRELATION_ID = "x-correlation-id";
    public static final String ACCOUNT_ID = "account-id";
    public static final int ZERO = 0;
    public static final String EMPTY = "";
    public static final String CVV= "cvv";
    public static final String CARD_EXPIRY="card-expiry";
    public static final String REDIS_KEY_PORTLIST = "invesment-portlist" ;
    public static final String REDIS_KEY_FUND_SUMMARY = "invesment-fundsummary" ;
    public static final String ACC_TYPE_SDA = "SDA";
    public static final String ACC_TYPE_DDA = "DDA";
    public static final String ACC_TYPE_SAVING = "S";
    public static final String ACC_TYPE_CURRENT = "A";
    public static final String FUND_RULE_TRANS_TYPE = "1";
    public static final String FAILED_MESSAGE = "failed";

    public static final String MF_TIME_HHMM = "HHmm";
    public static final String PAGE_SIZE = "pageSize";
    public static final String PAGE_NO = "pageNo";

    /* SERVICE OUR CLOSE */
    public static final String SERVICE_OUR_CLOSE  = "MSG#01";
    public static final String SERVICE_OUR_CLOSE_MESSAGE = "NOT ALLOW TO PROCESSING";
    public static final String SERVICE_OUR_CLOSE_DESC = "Error";

    /* OF SHELF */
    public static final String OF_SHELF_FUND_CODE  = "MSG#05";
    public static final String OF_SHELF_FUND_MESSAGE = "NOT ALLOW TO PURCHASE";
    public static final String OF_SHELF_FUND_DESC = "FUND DO NOT ALLOW TO PURCHASE";

    /* FUND RULE RETURN NOT FOUND */
    public static final String SERVICE_NOT_FOUND  = "MSG999";
    public static final String SERVICE_NOT_FOUND_MESSAGE = "DATA NOT FOUND";
    public static final String SERVICE_NOT_FOUND_DESC = "FUND LIST INFO NOT FOUND";
    public static final String FUND_RULE_NOT_FOUND_DESC = "FUND RULE NOT FOUND";

    /* BUSINESS OUR CLOSE */
    public static final String BUSINESS_HOURS_CLOSE_CODE  = "MSG#06";
    public static final String BUSINESS_HOURS_CLOSE_MESSAGE = "NOT ALLOW TO DO";
    public static final String BUSINESS_HOURS_CLOSE_DESC = "TRANSACTION DO NOT ALLOW TO PROCESS";

    /* CASA DORMANT OUR CLOSE */
    public static final String CASA_DORMANT_ACCOUNT_CODE  = "MSG#03";
    public static final String CASA_DORMANT_ACCOUNT_MESSAGE = "ACCOUNT NOT FOUND";
    public static final String CASA_DORMANT_ACCOUNT_DESC = "ACCOUNT NOT FOUND OR NOT READY TO DO THE TRANSACTION";

    public static final String ACTIVITY_ID_INVESTMENT_STATUS_TRACKING = "101000101";
    public static final String ACTIVITY_TYPE_INVESTMENT_STATUS_TRACKING = "Buy holding fund ";
    public static final String ACTIVITY_LOG_CHANNEL= "mb";
    public static final String ACTIVITY_LOG_APP_VERSION = "1.0.0";
    public static final String ACTIVITY_LOG_SUCCESS = "Success";
    public static final String ACTIVITY_LOG_FAILURE = "Off business hour";
    public static final String ACTIVITY_LOG_VERIFY_FLAG = "Verify flag = ";
    public static final String ACTIVITY_LOG_REASON = "Reason = ";
    public static final String ACTIVITY_LOG_VERIFY_FLAG_ID = "verify_flag";
    public static final String ACTIVITY_LOG_REASON_ID = "reason";

    public static final String ACTIVE_STATUS = "ACTIVE";
    public static final String INACTIVE_STATUS = "INACTIVE";

    public static final String PROCESS_FLAG_Y = "Y";
}

