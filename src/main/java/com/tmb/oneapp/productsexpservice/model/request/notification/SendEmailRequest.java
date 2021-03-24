package com.tmb.oneapp.productsexpservice.model.request.notification;

import javax.validation.constraints.NotEmpty;

public class SendEmailRequest {
	private String crmId;
	@NotEmpty
	private String email;
}
