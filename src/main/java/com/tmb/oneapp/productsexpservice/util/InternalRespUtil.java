package com.tmb.oneapp.productsexpservice.util;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import com.tmb.common.model.ErrorStatusInfo;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.model.creditcard.SilverlakeErrorStatus;
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
	public static ResponseEntity generatedResponseFromService(HttpHeaders responseHeaders,
			TmbOneServiceResponse oneServiceResponse, List<ErrorStatusInfo> errorStatusInfo) {

		if (CollectionUtils.isNotEmpty(errorStatusInfo)) {
			ErrorStatusInfo errorInfo = errorStatusInfo.get(0);
			oneServiceResponse.setStatus(new TmbStatus(errorInfo.getErrorCode(), errorInfo.getDescription(),
					ResponseCode.FAILED.getService()));
		} else {
			oneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
					ResponseCode.FAILED.getService()));
		}

		return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
	}

	/**
	 * Convert error from source to caller
	 * 
	 * @param responseHeaders
	 * @param serviceResponse
	 * @param status
	 */
	public static ResponseEntity generatedResponseFromService(HttpHeaders responseHeaders,
			TmbOneServiceResponse serviceResponse, TmbStatus status) {
		serviceResponse.setStatus(new TmbStatus(status.getCode(), status.getDescription(), status.getService()));
		return ResponseEntity.badRequest().headers(responseHeaders).body(serviceResponse);
	}

	/**
	 * 
	 * @param responseHeaders
	 * @param oneServiceResponse
	 * @param errorStatus
	 * @return
	 */
	public static ResponseEntity generatedResponseFromSilverLake(HttpHeaders responseHeaders,
			TmbOneServiceResponse oneServiceResponse, List<SilverlakeErrorStatus> errorStatus) {
		if (CollectionUtils.isNotEmpty(errorStatus)) {
			SilverlakeErrorStatus silverLakeError = errorStatus.get(0);
			oneServiceResponse.setStatus(new TmbStatus(silverLakeError.getErrorCode(), silverLakeError.getDescription(),
					ResponseCode.FAILED.getService()));
		} else {
			oneServiceResponse.setStatus(new TmbStatus(ResponseCode.FAILED.getCode(), ResponseCode.FAILED.getMessage(),
					ResponseCode.FAILED.getService()));
		}
		return ResponseEntity.badRequest().headers(responseHeaders).body(oneServiceResponse);
	}

}
