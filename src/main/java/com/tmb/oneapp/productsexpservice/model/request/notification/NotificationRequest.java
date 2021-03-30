package com.tmb.oneapp.productsexpservice.model.request.notification;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel(description = "Request to send message to customers")
public class NotificationRequest {
	@NotEmpty
	@NotNull
	@ApiModelProperty(value = "Information of outgoing message(s) in array format can be 1-on-1 message or multiple records", required = true)

	private List<NotificationRecord> records;

	@Override
	public String toString() {
		return "NotificationRequest [records=" + records + "]";
	}

}
