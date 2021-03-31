package com.tmb.oneapp.productsexpservice.model.request.notification;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "SMS Channel which need to send outgoing message to customer.")
public class SmsChannel {
	@ApiModelProperty(value = "The mobile number of customers for sending SMS ENCS will look up in ODS to find out the mobile number by CRMID which send in request\n"
			+ "*ENCS will not be able to find out the endpoint in ODS if the request does not contain with CRMID")
	@JsonProperty("endpoint")
	private String smsEdpoint;

	@ApiModelProperty(value = "As ENCS has logic to switch sending via SMS to Push notification depending on user profiles of mobile app.  \n"
			+ "- If value is true, ENCS system will send outgoing message to customer via SMS, then looking for user profile for consider to send push notification.\n"
			+ "- If value is false, ENCS system will consider the channel which send to customer following the SMS working flow which mentioned in document")
	@JsonProperty("force")
	private boolean smsForce = false;

	@ApiModelProperty(value = "This flag is using for search endpoint of customer in ODS following the channel which contain in request.\n"
			+ "- If value is true, ENCS system WILL search for mobile number of TMB customers from ODS system using given RM_ID\n"
			+ "- If value is false, ENCS system WILL NOT search for mobile number of TMB customers from ODS system")
	@JsonProperty("search")
	private boolean smsSearch = false;

	@Override
	public String toString() {
		return "SmsChannel [smsEdpoint=" + smsEdpoint + ", smsForce=" + smsForce + ", smsSearch=" + smsSearch + "]";
	}
	
	
}
