package com.tmb.oneapp.productsexpservice.model.response.notification;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationResponse {

	private boolean success;
	private String guid;
	private String message;
	private int status;

}
