package com.tmb.oneapp.productsexpservice.controller;

import com.google.common.base.Strings;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.AccountRequestClient;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.ProductConfig;
import com.tmb.oneapp.productsexpservice.model.homeloan.AccountId;
import com.tmb.oneapp.productsexpservice.model.homeloan.LoanDetailsFullResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;

@RestController
@Api(tags = "Fetch Home loan account details")
public class LoanDetailsController {
    private static final TMBLogger<LoanDetailsController> log = new TMBLogger<>(LoanDetailsController.class);
    private final AccountRequestClient accountRequestClient;
    private final CommonServiceClient commonServiceClient;

    /**
     * Constructor
     *
     * @param accountRequestClient
     * @param commonServiceClient
     */
    @Autowired
    public LoanDetailsController(AccountRequestClient accountRequestClient, CommonServiceClient commonServiceClient) {
        this.accountRequestClient = accountRequestClient;
        this.commonServiceClient = commonServiceClient;
    }


    
    
    /**
     * @param correlationId
     * @param requestBody
     * @return
     */
    @LogAround
    @PostMapping(value = "/creditcard/get-account-detail", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<LoanDetailsFullResponse>> getLoanAccountDetail(
            @ApiParam(value = "Correlation ID", defaultValue = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", required = true) @Valid @RequestHeader("X-Correlation-ID") String correlationId,
            @ApiParam(value = "Account ID", defaultValue = "00016109738001", required = true) @RequestBody AccountId requestBody) {

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
        TmbOneServiceResponse<LoanDetailsFullResponse> oneServiceResponse = new TmbOneServiceResponse<>();

        try {
            String accountId = requestBody.getAccountNo();
            if (!Strings.isNullOrEmpty(accountId)) {
                ResponseEntity<TmbOneServiceResponse<LoanDetailsFullResponse>> homeLoanResponse = accountRequestClient.getLoanAccountDetail(correlationId,requestBody);
                int statusCodeValue = homeLoanResponse.getStatusCodeValue();
                HttpStatus statusCode = homeLoanResponse.getStatusCode();
               
                if (homeLoanResponse.getBody() != null && statusCodeValue == 200 && statusCode == HttpStatus.OK) {

                	LoanDetailsFullResponse loanDetails = homeLoanResponse.getBody().getData();
                    String productId = homeLoanResponse.getBody().getData().getAccount().getProductId();
                    ResponseEntity<TmbOneServiceResponse<List<ProductConfig>>> fetchProductConfigList = commonServiceClient
                            .getProductConfig(correlationId);

                    List<ProductConfig> list  = fetchProductConfigList.getBody().getData();
                    Iterator<ProductConfig> iterator = list.iterator();
                    while (iterator.hasNext()) {
                        ProductConfig productConfig = iterator.next();
                        if(productConfig.getProductCode().equalsIgnoreCase(productId)){
                        	loanDetails.setProductConfig(productConfig);
                        }
                    }
                    
                    oneServiceResponse.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                            ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
                    oneServiceResponse.setData(loanDetails);
                    return ResponseEntity.ok().headers(responseHeaders).body(oneServiceResponse);
                } else {
                    oneServiceResponse.setStatus(new TmbStatus(ResponseCode.DATA_NOT_FOUND_ERROR.getCode(),
                            ResponseCode.DATA_NOT_FOUND_ERROR.getMessage(), ResponseCode.DATA_NOT_FOUND_ERROR.getService(),
                            ResponseCode.DATA_NOT_FOUND_ERROR.getDesc()));
                    return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);

                }
            } else {
                oneServiceResponse.setStatus(new TmbStatus(ResponseCode.DATA_NOT_FOUND_ERROR.getCode(),
                        ResponseCode.DATA_NOT_FOUND_ERROR.getMessage(), ResponseCode.DATA_NOT_FOUND_ERROR.getService(),
                        ResponseCode.DATA_NOT_FOUND_ERROR.getDesc()));
                return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
            }

        } catch (Exception e) {
            log.error("Error while getLoanAccountDetails: {}", e);
            oneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService()));
            return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
        }

    }
}
