package com.tmb.oneapp.productsexpservice.feignclients;

import com.tmb.common.model.CustGeneralProfileResponse;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.CustomerFirstUsage;
import com.tmb.oneapp.productsexpservice.model.applyestatement.ApplyEStatementResponse;
import com.tmb.oneapp.productsexpservice.model.applyestatement.StatementFlag;
import com.tmb.oneapp.productsexpservice.model.request.crm.CrmSearchBody;
import com.tmb.oneapp.productsexpservice.model.request.crm.CustomerCaseSubmitBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.model.response.statustracking.CaseStatusCase;
import com.tmb.oneapp.productsexpservice.service.productexperience.customer.calculatecustomerrisk.request.EkycRiskCalculateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "${feign.customers.service.name}", url = "${feign.customers.service.url}")
public interface CustomerServiceClient {

    /**
     * Get first time usage status of customer for specified service.
     *
     * @param crmId         customer ID
     * @param deviceId      device ID
     * @param serviceTypeId service type ID
     * @return data() in form of json but return null if customer has never used this service.
     */
    @GetMapping(value = "/apis/customers/firstTimeUsage")
    ResponseEntity<TmbOneServiceResponse<CustomerFirstUsage>> getFirstTimeUsage(
            @RequestHeader(value = ProductsExpServiceConstant.X_CRMID) String crmId,
            @RequestHeader(value = ProductsExpServiceConstant.DEVICE_ID) String deviceId,
            @RequestParam(value = "serviceTypeId") String serviceTypeId
    );

    /**
     * Post first time usage status of customer for specified service.
     *
     * @param crmId         customer ID
     * @param deviceId      device ID
     * @param serviceTypeId service type ID
     * @return String of insert first time usage status
     */
    @PostMapping(value = "/apis/customers/firstTimeUsage")
    ResponseEntity<TmbOneServiceResponse<String>> postFirstTimeUsage(
            @RequestHeader(value = ProductsExpServiceConstant.X_CRMID) String crmId,
            @RequestHeader(value = ProductsExpServiceConstant.DEVICE_ID) String deviceId,
            @RequestParam(value = "serviceTypeId") String serviceTypeId
    );

    /**
     * Put first time usage status of customer for specified service.
     *
     * @param crmId         customer ID
     * @param deviceId      device ID
     * @param serviceTypeId service type ID
     * @return String of update first time usage status
     */
    @PutMapping(value = "/apis/customers/firstTimeUsage")
    ResponseEntity<TmbOneServiceResponse<String>> putFirstTimeUsage(
            @RequestHeader(value = ProductsExpServiceConstant.X_CRMID) String crmId,
            @RequestHeader(value = ProductsExpServiceConstant.DEVICE_ID) String deviceId,
            @RequestParam(value = "serviceTypeId") String serviceTypeId
    );

    /**
     * Get all case statuses for customer
     *
     * @param crmId customer ID
     * @return data() in form of json but return null if customer has never used this service.
     */
    @GetMapping(value = "/apis/customers/case/status/{CRM_ID}")
    ResponseEntity<TmbOneServiceResponse<List<CaseStatusCase>>> getCaseStatus(
            @RequestHeader(value = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @PathVariable("CRM_ID") String crmId
    );

    /**
     * @param crmId getCustDetails method consume crmId from
     *              customers-service
     */
    @GetMapping(value = "/apis/customers", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<TmbOneServiceResponse<CustGeneralProfileResponse>> getCustomerProfile(@RequestHeader(name = ProductsExpServiceConstant.X_CRMID) String crmId);

    /**
     * Post submit NCB customer case
     *
     * @param crmId         customer ID
     * @param correlationId correlationId
     * @param requestBody   CustomerCaseSubmitBody
     * @return Map<String, String>
     */
    @PostMapping(value = "/apis/customers/case/submit")
    ResponseEntity<TmbOneServiceResponse<Map<String, String>>> submitCustomerCase(
            @RequestHeader(value = ProductsExpServiceConstant.X_CRMID) String crmId,
            @RequestHeader(value = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @RequestBody CustomerCaseSubmitBody requestBody
    );

    /**
     * Post submit NCB customer case
     *
     * @param crmId         customer ID
     * @param correlationId correlationId
     * @param requestBody   CustomerCaseSubmitBody
     * @return Map<String, String>
     */
    @PostMapping(value = "/apis/customers/ecprofile")
    ResponseEntity<TmbOneServiceResponse<List<CustomerSearchResponse>>> customerSearch(
            @RequestHeader(value = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @RequestHeader(value = ProductsExpServiceConstant.X_CRMID) String crmId,
            @RequestBody CrmSearchBody requestBody
    );

    /**
     * Get e-statement
     *
     * @param crmId         customer ID
     * @param correlationId correlationId
     * @return
     */
    @GetMapping(value = "/apis/customers/profile/get-e-statement")
    ResponseEntity<TmbOneServiceResponse<ApplyEStatementResponse>> getCustomerEStatement(
            @RequestHeader(value = ProductsExpServiceConstant.X_CRMID) String crmId,
            @RequestHeader(value = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId);

    /**
     * Update email statment
     *
     * @param requestHeaders
     * @param statementFlag
     * @return
     */
    @PostMapping(value = "/apis/customers/profile/update-e-statement")
    ResponseEntity<TmbOneServiceResponse<ApplyEStatementResponse>> updateEStatement(
            @RequestHeader Map<String, String> requestHeaders, @RequestBody StatementFlag statementFlag);

    /**
     * Post submit ekyc calculate customer risk level
     *
     * @param correlationId correlationId
     * @param requestBody   EkycRiskCalculateRequest
     * @return String
     */
    @PostMapping(value = "/apis/customers/ekyc/risk/calculate")
    ResponseEntity<TmbOneServiceResponse<String>> customerEkycRiskCalculate(
            @RequestHeader(value = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @RequestBody EkycRiskCalculateRequest requestBody
    );
}
