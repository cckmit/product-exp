package com.tmb.oneapp.productsexpservice.model.request.notification;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "Information of outgoing message")
public class NotificationRecord {

	@NotEmpty
	@ApiModelProperty(position = 1, value = "TMB’s customers id", required = true)
	private String crmId;

	@ApiModelProperty(position = 2, value = "Bank account number of TMB customers")
	private String account;

	@ApiModelProperty(position = 3, value = "Language to send to customers")
	private String language;

	@NotEmpty
	@ApiModelProperty(position = 4, value = "Parameters for replace into message(s) and/or parameters for mapping templates in ENCS system, example: {\"template_name\": \"ONEAPP_XXX\",...}")
	private Map<String, Object> params;

	@ApiModelProperty(position = 5, value = "Attachment files to attach with email", example = "[\"sftp://[ip]/tmb/attachments/tmbbank.png\"]")
	private List<String> attachments;

	@ApiModelProperty(position = 6, value = "Email Channel which need to send outgoing message to customer.")
	private EmailChannel email;

	@ApiModelProperty(position = 7, value = "SMS Channel which need to send outgoing message to customer.")
	private SmsChannel sms;

	@ApiModelProperty(position = 8, value = "Notification Channel which need to send outgoing message to customer.")
	private NotificationChannel notification;

	@ApiModelProperty(position = 9, value = "Line Channel which need to send outgoing message to customer.")
	private LineChannel line;

	@Override
	public String toString() {
		return "NotificationRecord [crmId=" + crmId + ", account=" + account + ", language=" + language + ", params="
				+ params + ", attachments=" + attachments + ", email=" + email + ", sms=" + sms + ", notification="
				+ notification + ", line=" + line + "]";
	}
	
	

}
