package com.tmb.oneapp.productsexpservice.model.request.notification;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "Email Channel which need to send outgoing message to customer.")
public class EmailChannel {

	@ApiModelProperty(value = "The Email address of customer who we need to sending out going message. ENCS could send to more than one email addresses by using comma \",\" as separator for each email address")
	@JsonProperty("endpoint")
	private String emailEndpoint;

	@ApiModelProperty(value = "The Email address of customer who we need to sending out going message as carbon-copy (CC). Can send to multiple email addresses by using comma \",\" as separator for each email address")
	private String cc;

	@ApiModelProperty(value = "This flag is using for search endpoint of customer in ODS following the channel which contain in request.\n"
			+ "- If value is true, ENCS system WILL search for email address of TMB customers from ODS system using given RM_ID\n"
			+ "- If value is false, ENCS system WILL NOT search for email address of TMB customers from ODS system")
	@JsonProperty("search")
	private boolean emailSearch = false;

	@Override
	public String toString() {
		return "EmailChannel [emailEndpoint=" + emailEndpoint + ", cc=" + cc + ", emailSearch=" + emailSearch + "]";
	}
	
	

}
