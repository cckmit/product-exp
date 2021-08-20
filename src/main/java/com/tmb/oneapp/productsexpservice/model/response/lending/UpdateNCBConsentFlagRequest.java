package com.tmb.oneapp.productsexpservice.model.response.lending;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UpdateNCBConsentFlagRequest {

	@NotNull
	private String caId;
	@NotNull
    private String appType;
    private String appRefNo;
    private String productDescTH;
    private String ncbConsentFlag;
    private String ncbModelFlag;
}
