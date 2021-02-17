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

}
