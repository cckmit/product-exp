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
	public static final String X_COR_ID_DEFAULT = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da";
	public static final String INVESTMENT_SERVICE_RESPONSE = "Response from investment service : {}";
	public static final String CUSTOMER_EXP_SERVICE_RESPONSE = "Response from customer-exp service : {}";
	public static final String SUCCESS_CODE = "0000";
	public static final String SUCCESS_MESSAGE = "success";
	public static final String SERVICE_NAME = "product-exp-service";
	public static final String FUND_CODE_ACCDETAIL = "TMB50";
	public static final String FUND_CODE_RULE = "TESEQDSSFX";
	public static final String FUND_HOUSE_CODE_RULE = "TFUND";
	public static final String DATA_NOT_FOUND_CODE = "0009";
	public static final String DATA_NOT_FOUND_MESSAGE = "DATA NOT FOUND";
	public static final String X_CORRELATION_ID = "x-correlation-id";
	public static final String ACCOUNT_ID = "account-id";
	public static final int ZERO = 0;
	public static final String EMPTY = "";
	public static final String CVV = "cvv";
	public static final String CARD_EXPIRY = "card-expiry";
	public static final String REDIS_KEY_PORTLIST = "invesment-portlist";
	public static final String REDIS_KEY_FUND_SUMMARY = "invesment-fundsummary";
	public static final String ACC_TYPE_SDA = "SDA";
	public static final String ACC_TYPE_DDA = "DDA";
	public static final String ACC_TYPE_SAVING = "S";
	public static final String ACC_TYPE_CURRENT = "A";
	public static final String FUND_RULE_TRANS_TYPE = "1";
	public static final String FAILED_MESSAGE = "failed";

	public static final String MF_TIME_HHMM = "HHmm";
	public static final String MF_DATE_YYYYMMDD = "yyyy-MM-dd";
	public static final String PAGE_SIZE = "pageSize";
	public static final String PAGE_NO = "pageNo";

	/* SERVICE OUR CLOSE */
	public static final String SERVICE_OUR_CLOSE = "msg_al_01";
	public static final String SERVICE_OUR_CLOSE_MESSAGE = "NOT ALLOW TO PROCESSING";
	public static final String SERVICE_OUR_CLOSE_DESC = "Error";

	/* OF SHELF */
	public static final String OF_SHELF_FUND_CODE = "msg_al_05";
	public static final String OF_SHELF_FUND_MESSAGE = "NOT ALLOW TO PURCHASE";
	public static final String OF_SHELF_FUND_DESC = "FUND DO NOT ALLOW TO PURCHASE";

	/* SUITABILITY EXPIRED */
	public static final String SUITABILITY_EXPIRED_CODE = "msg_al_04";
	public static final String SUITABILITY_EXPIRED_MESSAGE = "SUITABILITY EXPIRED";
	public static final String SUITABILITY_EXPIRED_DESC = "Error";

	/* ID EXPIRED */
	public static final String ID_EXPIRED_CODE = "msg_al_09";
	public static final String ID_EXPIRED_MESSAGE = "ID CARD EXPIRED";
	public static final String ID_EXPIRED_DESC = "Error";

	/* FUND RULE RETURN NOT FOUND */
	public static final String SERVICE_NOT_FOUND = "MSG999";
	public static final String SERVICE_NOT_FOUND_MESSAGE = "DATA NOT FOUND";
	public static final String SERVICE_NOT_FOUND_DESC = "FUND LIST INFO NOT FOUND";
	public static final String FUND_RULE_NOT_FOUND_DESC = "FUND RULE NOT FOUND";

	/* BUSINESS OUR CLOSE */
	public static final String BUSINESS_HOURS_CLOSE_CODE = "msg_al_06";
	public static final String BUSINESS_HOURS_CLOSE_MESSAGE = "NOT ALLOW TO DO";
	public static final String BUSINESS_HOURS_CLOSE_DESC = "TRANSACTION DO NOT ALLOW TO PROCESS";

	/* CASA DORMANT OUR CLOSE */
	public static final String CASA_DORMANT_ACCOUNT_CODE = "msg_al_03";
	public static final String CASA_DORMANT_ACCOUNT_MESSAGE = "ACCOUNT NOT FOUND";
	public static final String CASA_DORMANT_ACCOUNT_DESC = "ACCOUNT NOT FOUND OR NOT READY TO DO THE TRANSACTION";

	public static final String ACTIVITY_ID_INVESTMENT_STATUS_TRACKING = "101000101";
	public static final String ACTIVITY_TYPE_INVESTMENT_STATUS_TRACKING = "Buy holding fund ";
	public static final String ACTIVITY_LOG_CHANNEL = "mb";
	public static final String ACTIVITY_LOG_APP_VERSION = "1.0.0";
	public static final String ACTIVITY_LOG_SUCCESS = "Success";
	public static final String ACTIVITY_LOG_FAILURE = "Off business hour";
	public static final String ACTIVITY_LOG_VERIFY_FLAG = "Verify flag = ";
	public static final String ACTIVITY_LOG_REASON = "Reason = ";
	public static final String ACTIVITY_LOG_VERIFY_FLAG_ID = "verify_flag";
	public static final String ACTIVITY_LOG_REASON_ID = "reason";

	public static final String ACTIVE_STATUS = "ACTIVE";
	public static final String INACTIVE_STATUS = "INACTIVE";
	public static final String DORMANT_STATUS = "DORMANT";

	public static final String PROCESS_FLAG_Y = "Y";
	public static final String BUSINESS_HR_CLOSE = "N";
	public static final String ACTIVITY_ID_TEMP = "00700200";
	public static final String ACTIVITY_ID_TEMP_REASON_OF_REQUEST = "00700201";
	public static final String CHANGE_TEMP_CONFIRM_PIN_SUCCESS_CASE = "00700202";
	public static final String CHANGE_TEMP_COMPLETE_ADJUST_USAGE_LIMIT = "00700203";
	public static final String CHANGE_TYPE_PERMANENT = "change-type-permanent";
	public static final String CHANGE_TYPE_TEMP = "req-temp-limit";
	public static final String SUCCESS = "Success";
	public static final String FAILURE = "Failure";
	public static final String X_FORWARD_FOR = "x-forward-for";
	public static final String OS_VERSION = "os-version";
	public static final String CHANNEL = "channel";
	public static final String APP_VERSION = "app-version";
	public static final String X_CRMID = "x-crmid";
	public static final String DEVICE_ID = "device-id";
	public static final String DEVICE_MODEL = "device-model";
	public static final String MODE_PERMANENT = "permanent";
	public static final String MODE_TEMPORARY = "temporary";
	public static final String ACTIVITY_ID_VERIFY_CARD_NO = "00700100";
	public static final String ACTIVITY_ID_ENTERED_CVV_NO = "00700101";
	public static final String ACTIVITY_ID_CONFIRM_PIN_SUCCESS = "00700102";
	public static final String ACTIVITY_ID_CARD_ACTIVATION = "00700103";
	public static final String METHOD = "SCAN/KEY IN";
	public static final String UNIT_HOLDER = "PT000000000000587870";
	public static final String SUITABILITY_EXPIRED = "2";

	public static final String SERVICE_TYPE_ID_CST = "CST";
	public static final String CASE_STATUS_IN_PROGRESS = "In Progress";
	public static final String CASE_STATUS_CLOSED = "Closed";

	public static final int ERROR_CODE_404 = 404;
	public static final String CUST_CRMID = "crmId";
	public static final String FINISH_BLOCK_CARD_ACTIVITY_ID = "00700402";

	public static final String FIXED_START_PAGE = "1";
	public static final String FIXED_END_PAGE = "5";
	public static final String INTERNAL_SERVER = "Internal Server Error";

}
