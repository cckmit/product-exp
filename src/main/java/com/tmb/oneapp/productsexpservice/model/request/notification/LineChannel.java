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
@ApiModel(description = "SMS Channel which need to send outgoing message to customer.")
public class LineChannel {

	@ApiModelProperty(value = "LINE uid of TMB customers to send message into his/her LINE application")
	@JsonProperty("endpoint")
	private String lineEndpoint;

	@ApiModelProperty(value = "- If value is true, ENCS system WILL search for LINE uid of TMB customers from ODS system using given RM_ID\n"
			+ "- If value is false, ENCS system WILL NOT search for LINE uid of TMB customers from ODS system")
	@JsonProperty("search")
	private boolean lineSearch = false;

	@Override
	public String toString() {
		return "LineChannel [lineEndpoint=" + lineEndpoint + ", lineSearch=" + lineSearch + "]";
	}
	
	

}
