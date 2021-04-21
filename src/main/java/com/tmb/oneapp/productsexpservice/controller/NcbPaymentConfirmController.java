package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.model.response.ncb.NcbPaymentConfirmResponse;
import com.tmb.oneapp.productsexpservice.service.NcbPaymentConfirmService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.*;

/**
 * NcbPaymentConfirmController request mapping will handle apis call and then navigate
 * to respective method
 */
@RestController
@Api(tags = "NCB Payment Confirm Controller")
public class NcbPaymentConfirmController {
    private static final TMBLogger<NcbPaymentConfirmController> logger = new TMBLogger<>(
            NcbPaymentConfirmController.class);

    private final NcbPaymentConfirmService ncbPaymentConfirmService;

    @Autowired
    public NcbPaymentConfirmController(NcbPaymentConfirmService ncbPaymentConfirmService) {
        super();
        this.ncbPaymentConfirmService = ncbPaymentConfirmService;
    }

    /**
     * @param requestHeaders
     * @param serviceTypeId
     * @param firstnameTh
     * @param lastnameTh
     * @param firstnameEn
     * @param lastnameEn
     * @param email
     * @param address
     * @param deliveryMethod
     * @param accountNumber
     * @return
     */
    @LogAround
    @ApiOperation(value = "NCB Payment Confirm")
    @PostMapping(value = "/NCB/paymentConfirm")
    @ApiImplicitParams({
            @ApiImplicitParam(name = X_CORRELATION_ID, defaultValue = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", required = true, paramType = "header"),
            @ApiImplicitParam(name = X_CRMID, defaultValue = "001100000000000000000000051187", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = DEVICE_ID, defaultValue = "34cec72b26b7a30ae0a3eaa48d45d82bc2f69728472d9145d57565885", required = true, dataType = "string", paramType = "header"),
    })
    public ResponseEntity<TmbOneServiceResponse<NcbPaymentConfirmResponse>> confirmNcbPayment(
            @ApiParam(hidden = true) @RequestHeader Map<String, String> requestHeaders,
            @ApiParam(value = "Service Type Id", defaultValue = "NCBR", required = true) @RequestParam("service_type_id") String serviceTypeId,
            @ApiParam(value = "FirstnameTh", defaultValue = "NAME", required = true) @RequestParam("firstname_th") String firstnameTh,
            @ApiParam(value = "LastnameTh", defaultValue = "TEST", required = true) @RequestParam("lastname_th") String lastnameTh,
            @ApiParam(value = "FirstnameEn", defaultValue = "NAME", required = true) @RequestParam("firstname_en") String firstnameEn,
            @ApiParam(value = "LastnameEn", defaultValue = "TEST", required = true) @RequestParam("lastname_en") String lastnameEn,
            @ApiParam(value = "Email", defaultValue = "abc@tmb.com", required = true) @RequestParam("email") String email,
            @ApiParam(value = "Address", defaultValue = "123/12 abcesafaefae", required = true) @RequestParam("address") String address,
            @ApiParam(value = "Delivery Method", defaultValue = "by email", required = true) @RequestParam("delivery_method") String deliveryMethod,
            @ApiParam(value = "Account Number", defaultValue = "1234567890", required = true) @RequestParam("account_number") String accountNumber) {

        TmbOneServiceResponse<NcbPaymentConfirmResponse> response = new TmbOneServiceResponse<>();

        try {
            NcbPaymentConfirmResponse ncbPaymentConfirmResponse =
                    ncbPaymentConfirmService.confirmNcbPayment(requestHeaders, serviceTypeId, firstnameTh, lastnameTh, firstnameEn, lastnameEn, email, address, deliveryMethod, accountNumber);

            response.setData(ncbPaymentConfirmResponse);
            response.setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(),
                    ResponseCode.SUCESS.getMessage(), ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));

            return ResponseEntity.status(HttpStatus.OK)
                    .headers(TMBUtils.getResponseHeaders())
                    .body(response);

        } catch (Exception e) {
            logger.error("Error calling GET /application/status : {}", e);
            response.setStatus(new TmbStatus(ResponseCode.GENERAL_ERROR.getCode(),
                    ResponseCode.GENERAL_ERROR.getMessage(), ResponseCode.GENERAL_ERROR.getService()));
            return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(response);
        }
    }

}
