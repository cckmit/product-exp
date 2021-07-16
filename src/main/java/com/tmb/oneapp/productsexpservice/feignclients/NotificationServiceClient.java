package com.tmb.oneapp.productsexpservice.feignclients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.common.model.request.notification.NotificationRequest;
import com.tmb.common.model.response.notification.NotificationResponse;

@FeignClient(name = "notification-service", url = "${notification-service.url}")
public interface NotificationServiceClient {

	@PostMapping(value = "${notification-service.e-noti.send-message.endpoint}")
	TmbOneServiceResponse<NotificationResponse> sendMessage(
			@RequestHeader(value = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID, required = true) final String xCorrelationId,
			NotificationRequest request);
}
