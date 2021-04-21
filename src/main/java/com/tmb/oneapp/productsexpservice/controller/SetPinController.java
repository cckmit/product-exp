package com.tmb.oneapp.productsexpservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.exception.model.TMBCommonExceptionWithResponse;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CreditCardClient;
import com.tmb.oneapp.productsexpservice.feignclients.OneappAuthClient;
import com.tmb.oneapp.productsexpservice.model.setpin.*;
import com.tmb.oneapp.productsexpservice.service.CreditCardLogService;
import com.tmb.oneapp.productsexpservice.service.NotificationService;
import feign.FeignException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * SetPinController request mapping will handle apis call and then navigate to
 * respective method
 */
@RestController
@Api(tags = "Set Pin Api")
public class SetPinController {
    private final OneappAuthClient oneappAuthClient;
    private final CreditCardClient creditCardClient;
    private final CreditCardLogService creditCardLogService;
    private final NotificationService notificationService;
    private static final TMBLogger<SetPinController> logger = new TMBLogger<>(SetPinController.class);

    /**
     * Constructor
     *
     * @param oneappAuthClient
     * @param creditCardClient
     * @param creditCardLogService
     */

    @Autowired
    public SetPinController(OneappAuthClient oneappAuthClient, CreditCardClient creditCardClient,
                            CreditCardLogService creditCardLogService, NotificationService notificationService) {
        this.oneappAuthClient = oneappAuthClient;
        this.creditCardClient = creditCardClient;
        this.creditCardLogService = creditCardLogService;
        this.notificationService = notificationService;
    }

    /**
     * set pin api
     *
     * @param requestBodyParameter
     * @param requestHeadersParameter
     * @return set pin api response
     */
    @LogAround
    @ApiOperation(value = "Set Pin Api")
    @PostMapping(value = "/credit-card/set-pin", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TmbOneServiceResponse<SetPinResponse>> getSetPin(
            @RequestHeader Map<String, String> requestHeadersParameter,
            @RequestBody SetPinReqParameter requestBodyParameter) throws UnsupportedEncodingException,
            JsonProcessingException, TMBCommonException, TMBCommonExceptionWithResponse {
        HttpHeaders responseHeaders = new HttpHeaders();
        try {
            String correlationId = requestHeadersParameter.get(ProductsExpServiceConstant.X_CORRELATION_ID);
            String accountId = requestBodyParameter.getAccountId();
            String activityDate = Long.toString(System.currentTimeMillis());
            String crmId = requestHeadersParameter.get(ProductsExpServiceConstant.X_CRMID);

            TmbOneServiceResponse<SetPinResponse> oneServiceResponse = new TmbOneServiceResponse<>();
            TranslatePinRes translatePinRes = oneappAuthClient.fetchEcasTranslatePinData(correlationId,
                    requestBodyParameter);
            if (translatePinRes != null && translatePinRes.getResult() != null) {
                String buffer = translatePinRes.getResult().getBuffer();
                byte[] decoded = Base64.decodeBase64(buffer);
                String pin = Hex.encodeHexString(decoded);
                SetPinQuery setPinQuery = new SetPinQuery();
                setPinQuery.setAccountId(accountId);
                setPinQuery.setPin(pin.toUpperCase());
                ResponseEntity<TmbOneServiceResponse<SetPinResponse>> res = creditCardClient.setPin(correlationId,
                        setPinQuery);
                SetPinResponse setPinResponse = res.getBody().getData();
                if (setPinResponse.getStatus().getStatusCode() == 0) {
                    creditCardLogService.finishSetPinActivityLog(ProductsExpServiceConstant.SUCCESS,
                            ProductsExpServiceConstant.SET_PIN_ACTIVITY_LOG, correlationId, activityDate, accountId,
                            "");
                    oneServiceResponse
                            .setStatus(new TmbStatus(ResponseCode.SUCESS.getCode(), ResponseCode.SUCESS.getMessage(),
                                    ResponseCode.SUCESS.getService(), ResponseCode.SUCESS.getDesc()));
                    notificationService.doNotifySuccessForSetPin(correlationId, accountId, crmId);
                    return ResponseEntity.ok().headers(responseHeaders).body(oneServiceResponse);
                } else {
                    List<SilverlakeErrorStatus> errorStatus = setPinResponse.getStatus().getErrorStatus();
                    String code = errorStatus.get(0).getErrorCode();
                    String desc = errorStatus.get(0).getDescription();
                    creditCardLogService.finishSetPinActivityLog(ProductsExpServiceConstant.FAILURE,
                            ProductsExpServiceConstant.SET_PIN_ACTIVITY_LOG, correlationId, activityDate, accountId,
                            desc);
                    oneServiceResponse.setStatus(new TmbStatus(code, ResponseCode.FAILED.getMessage(),
                            ResponseCode.FAILED.getService(), desc));
                    return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
                }

            } else {
                throw new TMBCommonException(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
                        ResponseCode.FAILED.getService(), HttpStatus.BAD_REQUEST, null);
            }

        } catch (FeignException ex) {
            logger.error("Received error in call to fetchAllAccount service {} ", ex);
            TmbServiceResponse<List<Object>> err = convertExceptionResposeToExceptionRespose(ex);
            if (err == null) {
                logger.error("Response is empty : {} ");
                throw new TMBCommonException(ResponseCode.GENERAL_ERROR.getCode(),
                        ResponseCode.GENERAL_ERROR.getMessage(), ResponseCode.GENERAL_ERROR.getService(),
                        HttpStatus.BAD_REQUEST, null);
            }
            throw new TMBCommonExceptionWithResponse(err.getStatus().getCode(), err.getStatus().getMessage(),
                    ResponseCode.FAILED.getService(), HttpStatus.BAD_REQUEST, null, null);
        } catch (Exception ex) {
            throw new TMBCommonException(ResponseCode.GENERAL_ERROR.getCode(), ResponseCode.GENERAL_ERROR.getMessage(),
                    ResponseCode.GENERAL_ERROR.getService(), HttpStatus.BAD_REQUEST, null);
        }

    }

    /**
     * @param ex
     * @return
     * @throws UnsupportedEncodingException
     * @throws JsonProcessingException
     */
    private TmbServiceResponse<List<Object>> convertExceptionResposeToExceptionRespose(FeignException ex)
            throws UnsupportedEncodingException, JsonProcessingException {
        Optional<ByteBuffer> response = ex.responseBody();
        if (response.isPresent()) {
            ByteBuffer responseBuffer = response.get();
            String responseObj = new String(responseBuffer.array(), ProductsExpServiceConstant.UTF_8);
            logger.info("responseObj : {}", responseObj);
            return (TmbServiceResponse) TMBUtils.convertStringToJavaObj(responseObj, TmbServiceResponse.class);
        }
        return null;
    }
}
