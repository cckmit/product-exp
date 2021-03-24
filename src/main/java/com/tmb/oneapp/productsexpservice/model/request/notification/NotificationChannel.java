package com.tmb.oneapp.productsexpservice.model.request.notification;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationChannel {

	@ApiModelProperty(value = "The CRMID of customer who TMB need to sending out going message. ENCS could send to more than one email addresses by using comma \",\" as separator for each email address")
	@JsonProperty("endpoint")
	private String notiEndpoint;

	@ApiModelProperty(value = "- If value is true, ENCS system will send notification messages to TMB customers WITHOUT checking condition in TMB customer profiles\n"
			+ "- If value is false, ENCS system will send notification messages to TMB customers by checking condition in TMB customer profiles")
	@JsonProperty("force")
	private boolean notiForce = false;

	@ApiModelProperty(value = "- If value is true, ENCS system WILL search for Subscription ID of TMB customers from ODS system using given RM_ID\n"
			+ "- If value is false, ENCS system WILL NOT search for Subscription ID of TMB customers from ODS system")
	@JsonProperty("search")
	private boolean notiSearch = false;

	@ApiModelProperty(value = "- If value is true, ENCS system WILL not send message when EB status is active and MB status equal 01 or 07-12\n"
			+ "- If value is false, ENCS system WILL send message by condition EB status is active and MB status equal 01 or 07-12\n")
	private boolean skipSms = false;

	@ApiModelProperty(value = "The flag is using for write notification inbox whether the message has been match and mis-match with criteria for sending push notification.")
	private boolean writeNotiInbox = false;

	@Override
	public String toString() {
		return "NotificationChannel [notiEndpoint=" + notiEndpoint + ", notiForce=" + notiForce + ", notiSearch="
				+ notiSearch + ", skipSms=" + skipSms + ", writeNotiInbox=" + writeNotiInbox + "]";
	}
	
	
	
}
