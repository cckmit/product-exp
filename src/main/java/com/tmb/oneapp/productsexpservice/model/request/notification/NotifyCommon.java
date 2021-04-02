package com.tmb.oneapp.productsexpservice.model.request.notification;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotifyCommon {
	private String xCorrelationId;
	private String channelNameEn;
	private String channelNameTh;
	private String productNameEN;
	private String productNameTH;
	private String custFullNameEn;
	private String custFullNameTH;

}
