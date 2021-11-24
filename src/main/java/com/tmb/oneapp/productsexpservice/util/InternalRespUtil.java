package com.tmb.oneapp.productsexpservice.util;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import com.tmb.common.model.ErrorStatusInfo;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;

public class InternalRespUtil {

	/**
	 * Convert error from source to caller
	 * 
	 * @param responseHeaders
	 * @param oneServiceResponse
	 * @param errorStatusInfo
	 * @return
	 */
	public static ResponseEntity<?> generatedResponseFromService(HttpHeaders responseHeaders,
			TmbOneServiceResponse<?> oneServiceResponse, List<ErrorStatusInfo> errorStatusInfo) {
		ErrorStatusInfo errorInfo = errorStatusInfo.get(0);
		oneServiceResponse.setStatus(
				new TmbStatus(errorInfo.getErrorCode(), errorInfo.getErrorCode(), ResponseCode.FAILED.getService()));
		return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
	}

}
