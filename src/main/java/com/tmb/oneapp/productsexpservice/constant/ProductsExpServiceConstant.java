package com.tmb.oneapp.productsexpservice.constant;

import java.util.List;

/**
 * Constant class for Application
 */
public class ProductsExpServiceConstant {
	
    /**
     * created private constructor so that no body can create object of this call
     */
    private ProductsExpServiceConstant() {
    }

    public static final String SERVICE_NAME = "products-exp-service";

    public static final String INVESTMENT_MODULE_VALUE = "investment_module";

    /* RESPONSE MESSAGE FORMAT */
    public static final String INVESTMENT_SERVICE_RESPONSE = "Response from investment service : {}";
    public static final String CUSTOMER_EXP_SERVICE_RESPONSE = "Response from customer-exp service : {}";

    public static final String HEADER_TIMESTAMP = "Timestamp";
    public static final String CONTENT_TYPE = "content-type";
    public static final String HEADER_CORRELATION_ID_DESC = "Correlation ID";
    public static final String HEADER_X_CORRELATION_ID = "x-correlation-id";
    public static final String HEADER_X_CRM_ID = "x-crmid";
    public static final String X_CRMID = "x-crmid";
    public static final String X_COR_ID_DEFAULT = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";

    public static final Integer SILVER_LAKE_SUCCESS_CODE = 0;
    public static final String SUCCESS_CODE = "0000";
    public static final String SUCCESS_MESSAGE = "success";
    public static final String FAILED_MESSAGE = "failed";
    public static final String DATA_NOT_FOUND_CODE = "0009";
    public static final String DATA_NOT_FOUND_MESSAGE = "DATA NOT FOUND";
    public static final String EXCEPTION_OCCURED = "Exception occured : {}";

    public static final int ZERO = 0;
    public static final String STRING_ZERO = "0";
    public static final String ACCOUNT_ID = "account-id";
    public static final String EMPTY = "";
    public static final String CVV = "cvv";
    public static final String CARD_EXPIRY = "card-expiry";
    public static final String ACC_TYPE_SDA = "SDA";
    public static final String ACC_TYPE_DDA = "DDA";
    public static final String ACC_TYPE_SAVING = "S";
    public static final String ACC_TYPE_CURRENT = "A";
    public static final String FUND_RULE_TRANS_TYPE = "1";

    /* DATETIME FORMAT */
    public static final String MF_TIME_HHMM = "HHmm";
    public static final String MF_TIME_WITH_COLON_HHMM = "HH:mm";
    public static final String MF_DATE_YYYYMMDD = "yyyy-MM-dd";
    public static final String APPLICATION_STATUS_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String HP_DATETIME_FORMAT = "dd/MM/yyyy HH:mm:ss";

    /* PAGE */
    public static final String PAGE_SIZE = "pageSize";
    public static final String PAGE_NO = "pageNo";
    public static final String FIXED_START_PAGE = "1";
    public static final String FIXED_END_PAGE = "5";

    /* SERVICE OUR CLOSE */
    public static final String SERVICE_OUR_CLOSE = "msg_alt_01";
    public static final String SERVICE_OUR_CLOSE_MESSAGE = "NOT ALLOW TO PROCESSING";
    public static final String SERVICE_OUR_CLOSE_DESC = "Error";

    /* SUITABILITY EXPIRED */
    public static final String SUITABILITY_EXPIRED_CODE = "msg_alt_04";
    public static final String SUITABILITY_EXPIRED_MESSAGE = "SUITABILITY EXPIRED";
    public static final String SUITABILITY_EXPIRED_DESC = "Error";

    /* ID EXPIRED */
    public static final String ID_EXPIRED_CODE = "msg_alt_09";
    public static final String ID_EXPIRED_MESSAGE = "ID CARD EXPIRED";
    public static final String ID_EXPIRED_DESC = "Error";

    /* CONNECTION FAIL */
    public static final String SERVICE_NOT_READY = "MSG999";
    public static final String SERVICE_NOT_READY_MESSAGE = "INTERNET CONNECTION FAIL";
    public static final String SERVICE_NOT_READY_DESC = "SOMETHING WENT WRONG, TRY AGAIN";
    public static final String SERVICE_NOT_READY_DESC_MESSAGE = "SOMETHING WENT WRONG :%s, TRY AGAIN";

    /* BUSINESS OUR CLOSE */
    public static final String BUSINESS_HOURS_CLOSE_CODE = "msg_alt_06";
    public static final String BUSINESS_HOURS_CLOSE_MESSAGE = "NOT ALLOW TO DO";
    public static final String BUSINESS_HOURS_CLOSE_DESC = "TRANSACTION DO NOT ALLOW TO PROCESS";

    /* CASA DORMANT OUR CLOSE */
    public static final String CASA_DORMANT_ACCOUNT_CODE = "msg_alt_03";
    public static final String CASA_DORMANT_ACCOUNT_MESSAGE = "ACCOUNT NOT FOUND";
    public static final String CASA_DORMANT_ACCOUNT_DESC = "ACCOUNT NOT FOUND OR NOT READY TO DO THE TRANSACTION";

    public static final String ACTIVE_STATUS_CODE = "0";
    public static final String INACTIVE_STATUS_CODE = "1";
    public static final String DORMANT_STATUS_CODE = "2";

    public static final String PROCESS_FLAG_Y = "Y";
    public static final String BUSINESS_HR_CLOSE = "N";
    public static final String ACTIVITY_ID_TEMP = "00700200";
    public static final String ACTIVITY_ID_TEMP_REASON_OF_REQUEST = "00700201";
    public static final String CHANGE_TEMP_COMPLETE_ADJUST_USAGE_LIMIT = "00700203";
    public static final String APPLY_SO_GOOD_ON_CLICK_CONFIRM_BUTTON = "100700701";
    public static final String CHANGE_TYPE_PERMANENT = "change-type-permanent";
    public static final String SUCCESS = "Success";
    public static final String FAILED = "Failed";
    public static final String FAILURE = "Failure";
    public static final String X_FORWARD_FOR = "x-forward-for";
    public static final String OS_VERSION = "os-version";
    public static final String CHANNEL = "channel";
    public static final String APP_VERSION = "app-version";
    public static final String DEVICE_ID = "device-id";
    public static final String DEVICE_MODEL = "device-model";
    public static final String ACCEPT_LANGUAGE = "accept-language";
    public static final String MODE_PERMANENT = "permanent";
    public static final String MODE_TEMPORARY = "temporary";
    public static final String ACTIVITY_ID_VERIFY_CARD_NO = "00700100";
    public static final String ACTIVITY_ID_VIEW_LOAN_LENDING_SCREEN = "00700600";
    public static final String ACTIVITY_ID_LOAD_CARD_DETAILS = "00700800";
    public static final String METHOD = "SCAN/KEY IN";
    public static final String SUITABILITY_EXPIRED = "2";
    public static final String CASE_STATUS_CLOSED = "Closed";

    /* Application Status Tracking */
    public static final String SERVICE_TYPE_ID_AST = "AST";
    public static final String HIRE_PURCHASE_EN = "Hire-purchase";
    public static final String HIRE_PURCHASE_TH = "สินเชื่อเช่าซื้อ";
    public static final String HIRE_PURCHASE_HP = "HP";
    public static final String HIRE_PURCHASE_DATA_NOT_FOUND = "100902102";

    /* In Progress */
    public static final String CASE_STATUS_IN_PROGRESS = "In Progress";
    public static final String APPLICATION_STATUS_IN_PROGRESS = "in_progress";

    /* Completed */
    public static final String APPLICATION_STATUS_REJECTED = "rejected";
    public static final String APPLICATION_STATUS_COMPLETED = "completed";
    public static final String APPLICATION_STATUS_INCOMPLETE = "incomplete";
    public static final String ACCEPT_LANGUAGE_EN = "en";
    public static final String ACCEPT_LANGUAGE_TH = "th";
    public static final String APPLICATION_STATUS_CC = "CC";
    public static final String HEADER_PROSPECTIVE = "prospective";
    public static final String TRUE = "true";
    public static final String APPLICATION_STATUS_FLAG_TRUE = "Y";
    public static final String RSL_CURRENT_NODE_1 = "1";
    public static final String HEADER_CITIZEN_ID = "Citizen-ID";
    public static final String HEADER_MOBILE_NO = "Mobile-No";

    /* Case Status Screen Names */
    public static final String ACTIVITY_SCREEN_NAME_TUTORIAL_CST = "tutorial case tracking";

    public static final String INTERNAL_SERVER = "Internal Server Error";
    public static final String UTF_8 = "utf-8";
    public static final String SET_PIN_ACTIVITY_LOG = "00700302";

    /* Redis */
    public static final String INVESTMENT_CACHE_KEY = "investment_fundlist";
    public static final long INVESTMENT_CACHE_TIME_EXPIRE = 43200;

    /* CODE */
    public static final String SMART_PORT_CODE = "090";
    public static final String SERVICE_TYPE_MATRIX_CODE_NCB_BY_POST = "O0001";
    public static final String SERVICE_TYPE_MATRIX_CODE_NCB_BY_EMAIL = "O0003";

    public static final String CASE_NUMBER = "case_number";
    public static final String PTES_PORT_FOLIO_FLAG = "2";
    public static final String TRANSACTION_DATE = "transaction_date";

    public static final String FATCA_FLAG = "fatca_flag";

    /* Channel */
    public static final String CHANNEL_MOBILE_BANKING = "mb";

    /* Service Code */
    public static final String SERVICE_CODE_OPEN_PORTFOLIO = "INVT_OPENPT";

    public static final List<String> CREDIT_CARDS_CODE = List.of(
            RSLProductCodeEnum.CREDIT_CARD_TTB_ABSOLUTE.getProductCode(),
            RSLProductCodeEnum.CREDIT_CARD_TTB_SO_FAST.getProductCode(),
            RSLProductCodeEnum.CREDIT_CARD_TTB_SO_SMART.getProductCode(),
            RSLProductCodeEnum.CREDIT_CARD_TTB_SO_CHILL.getProductCode(),
            RSLProductCodeEnum.CREDIT_CARD_TTB_RESERVE_INFINITE.getProductCode(),
            RSLProductCodeEnum.CREDIT_CARD_TTB_RESERVE_SIGNATURE.getProductCode());

    /* Service Customer Search */
    public static final String SEARCH_TYPE = "rm-id";
    public static final String MIB_CUSTOMER_STATIC_ID = "D0000000988";

    /* PWA Screen Name */
    public static final String PWA_LEAVE_NOTE_SCREEN_NAME = "Send Note Success";

    /* ACTIVITY IDs */
    public static final String FINISH_BLOCK_CARD_ACTIVITY_ID = "00700402";
    public static final String CASE_TRACKING_TUTORIAL_ACTIVITY_ID = "101500201";
    public static final String ACTIVITY_ID_INVESTMENT_STATUS_TRACKING = "101000101";
    public static final String ACTIVITY_ID_INVESTMENT_STATUS_OPEN_PORTFOLIO = "101000701";
    public static final String ACTIVITY_ID_INVESTMENT_STATUS_OPEN_PORTFOLIO_ACCEPT_TERM_AND_CONDITION = "101000702";
    public static final String ACTIVITY_ID_INVESTMENT_STATUS_OPEN_PORTFOLIO_CLICK_CONFIRM = "101000703";
    public static final String ACTIVITY_ID_INVESTMENT_STATUS_OPEN_PORTFOLIO_ENTER_CORRECT_PIN = "101000704";
    public static final String CASE_SUBMIT_PWA_BY_EMAIL_ACTIVITY_ID = "101500502";
    public static final String CASE_SUBMIT_PWA_BY_CALL_ACTIVITY_ID = "101500503";
    public static final String CASE_SUBMIT_PWA_BY_LEAVE_MSG_ACTIVITY_ID = "101500505";

    /* ACTIVITY LOG MESSAGE */
    public static final String ACTIVITY_LOG_INVESTMENT_STATUS_TRACKING = "Buy holding fund ";
    public static final String ACTIVITY_LOG_INVESTMENT_CHANNEL = "mb";
    public static final String ACTIVITY_LOG_INVESTMENT_APP_VERSION = "1.0.0";
    public static final String ACTIVITY_LOG_INVESTMENT_FAILURE = "Off business hour";
    public static final String ACTIVITY_LOG_INVESTMENT_UNIT_HOLDER = "PT000000000000587870";
    public static final String ACTIVITY_LOG_INVESTMENT_OPEN_PORTFOLIO_YES = "Yes";
    public static final String ACTIVITY_LOG_INVESTMENT_OPEN_PORTFOLIO_NO = "No";
    public static final String ACTIVITY_LOG_INVESTMENT_OPEN_PORTFOLIO_ACCEPT_TERM_AND_CONDITION = "Yes";

    /* CRM Format */
    public static final String CRM_ID_PREFIX = "0011000000000000";
    public static final String CRM_ID_FORMAT = "00000000000000";
    public static final Integer DIGIT_OF_CRM_ID = 14;

    /* CUSTOMER CARD EXPIRE */
    public static final String EXPIRE_DATE_DEFAULT = "1357-01-01";

    public static final String SERVICE_TYPE_MATRIC_CODE_PWA_CALL_TO_ADVISOR = "O0011";
    public static final String SERVICE_TYPE_MATRIC_CODE_PWA_SEND_MESSAGE_TO_ADVISOR = "O0012";
    public static final String SERVICE_TYPE_MATRIC_CODE_PWA_SEND_EMAIL_TO_ADVISOR = "O0013";

    public static final String SERVICE_HOUR_MODULE = "service_hour";
    public static final String HEADER_SERVICE_HOUR_MODULE = "X-SERVICE-MODULE";
    public static final String EKYC_SERVICE_HOUR_MODULE = "ekyc";
    public static final String MISSING_SERVICE_HOUR_HEADER = "Invalid Service Hour Module";
    public static final String MISSING_HEADER = "Missing Mandatory Header";
    public static final String SERVICE_HOUR_ERROR_CODE = "Service_Hour_Error";
    public static final String SERVICE_HOUR_ERROR_MESSAGE = "Out of Service Hour";
    public static final String FAILED_ERROR_CODE = "0001";
    public static final String INVALID_REQUEST_FAILURE_CODE = "0010";
    public static final String FAIL_MESSAGE = "Fail";

    /* INVESTMENT JOINT FLAG */
    public static final String INVESTMENT_JOINT_FLAG_INDIVIDUAL = "N";
    public static final String INVESTMENT_JOINT_FLAG_JOINT = "Y";

    /* INVESTMENT PORTFOLIO TYPE */
    public static final String INVESTMENT_PORTFOLIO_JOINT_TYPE = "j";
}
