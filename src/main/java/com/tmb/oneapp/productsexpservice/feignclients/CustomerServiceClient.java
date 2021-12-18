package com.tmb.oneapp.productsexpservice.feignclients;

import com.tmb.common.model.CustGeneralProfileResponse;
import com.tmb.common.model.StatementFlag;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbServiceResponse;
import com.tmb.common.model.creditcard.UpdateEStatmentResp;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.CustomerFirstUsage;
import com.tmb.oneapp.productsexpservice.model.customer.calculaterisk.request.EkycRiskCalculateRequest;
import com.tmb.oneapp.productsexpservice.model.customer.calculaterisk.response.EkycRiskCalculateResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.model.request.crm.CrmSearchBody;
import com.tmb.oneapp.productsexpservice.model.request.crm.CustomerCaseSubmitBody;
import com.tmb.oneapp.productsexpservice.model.response.statustracking.CaseStatusCase;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "${feign.customers.service.name}", url = "${feign.customers.service.url}")
public interface CustomerServiceClient {

    /**
     * Get submit to get first time status usage of customer for specified service.
     *
     * @param crmId         the crm id
     * @param deviceId      the device id
     * @param serviceTypeId the service type id
     * @return CustomerFirstUsage => data() in form of json but return null if customer has never used this service.
     */
    @GetMapping(value = "/apis/customers/firstTimeUsage")
    ResponseEntity<TmbOneServiceResponse<CustomerFirstUsage>> getFirstTimeUsage(
            @RequestHeader(value = ProductsExpServiceConstant.X_CRMID) String crmId,
            @RequestHeader(value = ProductsExpServiceConstant.DEVICE_ID) String deviceId,
            @RequestParam(value = "serviceTypeId") String serviceTypeId
    );

    /**
     * Post submit to create first time of status usage of customer for specified service.
     *
     * @param crmId         the crm id
     * @param deviceId      the device id
     * @param serviceTypeId the service type id
     * @return String of insert first time status usage
     */
    @PostMapping(value = "/apis/customers/firstTimeUsage")
    ResponseEntity<TmbOneServiceResponse<String>> postFirstTimeUsage(
            @RequestHeader(value = ProductsExpServiceConstant.X_CRMID) String crmId,
            @RequestHeader(value = ProductsExpServiceConstant.DEVICE_ID) String deviceId,
            @RequestParam(value = "serviceTypeId") String serviceTypeId
    );

    /**
     * Put submit to update first time of status usage of customer for specified service.
     *
     * @param crmId         the crm id
     * @param deviceId      the device id
     * @param serviceTypeId the service type id
     * @return String of updated first time status usage
     */
    @PutMapping(value = "/apis/customers/firstTimeUsage")
    ResponseEntity<TmbOneServiceResponse<String>> putFirstTimeUsage(
            @RequestHeader(value = ProductsExpServiceConstant.X_CRMID) String crmId,
            @RequestHeader(value = ProductsExpServiceConstant.DEVICE_ID) String deviceId,
            @RequestParam(value = "serviceTypeId") String serviceTypeId
    );

    /**
     * Get submit to get all case status of customer
     *
     * @param crmId the customer id
     * @return CaseStatusCase => data() in form of json but return null if customer has never used this service.
     */
    @GetMapping(value = "/apis/customers/case/status/{CRM_ID}")
    ResponseEntity<TmbOneServiceResponse<List<CaseStatusCase>>> getCaseStatus(
            @RequestHeader(value = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @PathVariable("CRM_ID") String crmId
    );

    /**
     * Get submit to get customer profile
     *
     * @param crmId the crm id
     * @return CustGeneralProfileResponse
     */
    @GetMapping(value = "/apis/customers", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<TmbOneServiceResponse<CustGeneralProfileResponse>> getCustomerProfile(
            @RequestHeader(name = ProductsExpServiceConstant.X_CRMID) String crmId
    );

    /**
     * Post submit of NCB customer case
     *
     * @param crmId         the customer id
     * @param correlationId the correlation id
     * @param requestBody   the customer case submit body
     * @return Map<String, String>
     */
    @PostMapping(value = "/apis/customers/case/submit")
    ResponseEntity<TmbOneServiceResponse<Map<String, String>>> submitCustomerCase(
            @RequestHeader(value = ProductsExpServiceConstant.X_CRMID) String crmId,
            @RequestHeader(value = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @RequestBody CustomerCaseSubmitBody requestBody
    );

    /**
     * Post submit to search customer
     *
     * @param correlationId the correlation id
     * @param crmId         the customer id
     * @param requestBody   the customer search
     * @return CustomerSearchResponse
     */
    @PostMapping(value = "/apis/customers/ecprofile")
    ResponseEntity<TmbOneServiceResponse<List<CustomerSearchResponse>>> customerSearch(
            @RequestHeader(value = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @RequestHeader(value = ProductsExpServiceConstant.X_CRMID) String crmId,
            @RequestBody CrmSearchBody requestBody
    );

    /**
     * Get submit to get e-statement
     *
     * @param crmId         customer id
     * @param correlationId correlation id
     * @return UpdateEStatmentResp
     */
    @GetMapping(value = "/apis/customers/profile/get-e-statement")
    ResponseEntity<TmbOneServiceResponse<UpdateEStatmentResp>> getCustomerEStatement(
            @RequestHeader(value = ProductsExpServiceConstant.X_CRMID) String crmId,
            @RequestHeader(value = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId
    );

    /**
     * Post sumit to update email statement
     *
     * @param requestHeaders the request header
     * @param statementFlag  the statement flag
     * @return UpdateEStatmentResp
     */
    @PostMapping(value = "/apis/customers/profile/update-e-statement")
    ResponseEntity<TmbOneServiceResponse<UpdateEStatmentResp>> updateEStatement(
            @RequestHeader Map<String, String> requestHeaders, @RequestBody StatementFlag statementFlag
    );

    /**
     * Post submit of ekyc calculate customer risk level
     *
     * @param correlationId the correlation id
     * @param requestBody   the ekyc risk calculation request
     * @return EkycRiskCalculateResponse
     */
    @PostMapping(value = "/apis/customers/ekyc/risk/calculate")
    ResponseEntity<TmbServiceResponse<EkycRiskCalculateResponse>> customerEkycRiskCalculate(
            @RequestHeader(value = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @RequestBody EkycRiskCalculateRequest requestBody
    );
}
