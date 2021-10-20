package com.tmb.oneapp.productsexpservice.service.productexperience;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import feign.FeignException;
import org.springframework.http.HttpStatus;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class TmbErrorHandle {

    private static final TMBLogger<TmbErrorHandle> logger = new TMBLogger<>(TmbErrorHandle.class);

    protected void handleFeignException(FeignException feignException) throws TMBCommonException {
        if (feignException.status() == HttpStatus.BAD_REQUEST.value()) {
            try {
                TmbOneServiceResponse<String> response = getResponsesFromBadRequest(feignException);
                TmbStatus tmbStatus = response.getStatus();
                throw new TMBCommonException(
                        tmbStatus.getCode(),
                        tmbStatus.getMessage(),
                        tmbStatus.getService(),
                        HttpStatus.BAD_REQUEST,
                        null);
            } catch (JsonProcessingException e) {
                logger.info("cant parse json : {}", e);
            }
        } else if (feignException.status() == HttpStatus.NOT_FOUND.value()) {
            TmbStatus tmbStatus = TmbStatusUtil.notFoundStatus();
            throw new TMBCommonException(
                    tmbStatus.getCode(),
                    tmbStatus.getMessage(),
                    tmbStatus.getService(),
                    HttpStatus.NOT_FOUND,
                    null);
        } else {
            throw new TMBCommonException(
                    ResponseCode.FAILED.getCode(),
                    ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(),
                    HttpStatus.BAD_REQUEST,
                    null);
        }
    }

    @SuppressWarnings("unchecked")
    <T> TmbOneServiceResponse<T> getResponsesFromBadRequest(final FeignException ex) throws JsonProcessingException {

        TmbOneServiceResponse<T> response = new TmbOneServiceResponse<>();
        Optional<ByteBuffer> responseBody = ex.responseBody();

        if (responseBody.isPresent()) {

            ByteBuffer responseBuffer = responseBody.get();
            String responseObj = new String(responseBuffer.array(), StandardCharsets.UTF_8);
            logger.info("response msg fail {}", responseObj);

            response = ((TmbOneServiceResponse<T>) TMBUtils.convertStringToJavaObj(responseObj,
                    TmbOneServiceResponse.class));

        }
        return response;
    }

}
