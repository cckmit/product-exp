package com.tmb.oneapp.productsexpservice.util;

import com.tmb.oneapp.productsexpservice.model.request.notification.NotifyCommon;

public class NotificationUtil {
	
	private NotificationUtil() {}
	
	/**
	 * Create object for notifycommon wrapper information
	 * @param xCorrelationId
	 * @param channelNameEn
	 * @param channelNameTh
	 * @param productNameEN
	 * @param productNameTH
	 * @param custFullNameEn
	 * @param custFullNameTH
	 * @return
	 */
	public static NotifyCommon generateNotifyCommon(String xCorrelationId, String channelNameEn, String channelNameTh,
			String productNameEN, String productNameTH, String custFullNameEn, String custFullNameTH) {
		NotifyCommon notifyCommon = new NotifyCommon();
		notifyCommon.setChannelNameEn(channelNameEn);
		notifyCommon.setChannelNameTh(channelNameTh);
		notifyCommon.setCustFullNameEn(custFullNameEn);
		notifyCommon.setCustFullNameTH(custFullNameTH);
		notifyCommon.setProductNameEN(productNameEN);
		notifyCommon.setProductNameTH(productNameTH);
		notifyCommon.setXCorrelationId(xCorrelationId);
		return notifyCommon;
	}

}
