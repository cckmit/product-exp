package com.tmb.oneapp.productsexpservice.feignclients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.setpin.SetPinReqParameter;
import com.tmb.oneapp.productsexpservice.model.setpin.TranslatePinRes;

@FeignClient(name = "${feign.oneapp-auth-service.name}", url = "${feign.oneapp-auth-service.url}")
public interface OneappAuthClient {

	@PostMapping(value = "${feign.oneapp-auth-service.endpoint}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    TranslatePinRes fetchEcasTranslatePinData(
            @RequestHeader(value = ProductsExpServiceConstant.X_CORRELATION_ID) String correlationID,
            @RequestBody SetPinReqParameter requestBodyParameter);

}
