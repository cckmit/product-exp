package com.tmb.oneapp.productsexpservice.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.CommonData;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;

import feign.FeignException;

@Component
public class ServiceHourInterceptor extends HandlerInterceptorAdapter {
	private static final TMBLogger<ServiceHourInterceptor> logger = new TMBLogger<>(ServiceHourInterceptor.class);
	private final CommonServiceClient commonServiceFeignClient;

	/**
	 * Constructor
	 * 
	 * @param commonServiceFiengClient
	 * @throws TMBCommonException
	 */
	@Autowired
	public ServiceHourInterceptor(CommonServiceClient commonServiceFiengClient) {
		this.commonServiceFeignClient = commonServiceFiengClient;
	}

	/**
	 * Method responsible for fetching Common Config Service HOur Details
	 * 
	 * @return
	 * @throws TMBCommonException
	 */
	@LogAround
	public CommonData fetchCommonConfig() throws TMBCommonException {
		try {
			ResponseEntity<TmbOneServiceResponse<List<CommonData>>> res = commonServiceFeignClient
					.getCommonConfigByModule(TMBUtils.getUUID(), ProductsExpServiceConstant.SERVICE_HOUR_MODULE);
			if (res.getStatusCode().equals(HttpStatus.OK)) {
				return res.getBody().getData().get(0);
			}
			return null;
		} catch (FeignException e) {
			throw new TMBCommonException(ProductsExpServiceConstant.FAILED_ERROR_CODE,
					ProductsExpServiceConstant.FAIL_MESSAGE.toLowerCase(), ProductsExpServiceConstant.SERVICE_NAME,
					HttpStatus.BAD_REQUEST, null);
		}
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		logger.info("Service Hour Header : {}",
				request.getHeader(ProductsExpServiceConstant.HEADER_SERVICE_HOUR_MODULE));
		String serviceHourModule = validateServiceModule(
				request.getHeader(ProductsExpServiceConstant.HEADER_SERVICE_HOUR_MODULE));
		boolean serviceHours = serviceHourCheck(serviceHourModule);
		logger.info("Service Hour Interceptor Flag : {} ", serviceHours);
		if (!serviceHours) {
			throw new TMBCommonException(ProductsExpServiceConstant.SERVICE_HOUR_ERROR_CODE.toLowerCase(),
					ProductsExpServiceConstant.SERVICE_HOUR_ERROR_MESSAGE.toLowerCase(),
					ProductsExpServiceConstant.SERVICE_NAME, HttpStatus.BAD_REQUEST, null);
		}
		return true;
	}

	/**
	 * Method responsible for validating service header
	 * 
	 * @param header
	 * @return
	 * @throws TMBCommonException
	 */
	@LogAround
	private String validateServiceModule(String header) throws TMBCommonException {
		if (StringUtils.isEmpty(header)) {
			throw new TMBCommonException(ProductsExpServiceConstant.INVALID_REQUEST_FAILURE_CODE,
					ProductsExpServiceConstant.MISSING_HEADER.toLowerCase(), ProductsExpServiceConstant.SERVICE_NAME,
					HttpStatus.BAD_REQUEST, null);
		}
		return header;
	}

	/**
	 * Method responsible for call of service based on Common Module Service Hour
	 * 
	 * @param module
	 * @return
	 * @throws ParseException
	 * @throws TMBCommonException
	 */
	@LogAround
	private boolean serviceHourCheck(String module) throws ParseException, TMBCommonException {
		List<String> startEndTime = fetchStartEndTImeBasedOnModule(module, fetchCommonConfig());
		Boolean serviceHourFlag = Boolean.FALSE;
		Calendar serviceHourStart = getCalendarObj(startEndTime.get(0));
		Calendar serviceHourEnd = getCalendarObj(startEndTime.get(1));
		Calendar currentTime = getCalendarObj(getCurrentTime());
		Date currentDate = currentTime.getTime();
		if (currentDate.equals(serviceHourStart.getTime()) || currentDate.equals(serviceHourEnd.getTime())
				|| (serviceHourEnd.getTime().after(currentDate) && serviceHourStart.getTime().before(currentDate))) {
			serviceHourFlag = Boolean.TRUE;
		}
		return serviceHourFlag;
	}

	/**
	 * Method responsible for fetching start time and end time based on Module Name
	 * 
	 * @param module
	 * @param fetchCommonConfig
	 * @return
	 * @throws TMBCommonException
	 */
	@LogAround
	private List<String> fetchStartEndTImeBasedOnModule(String module, CommonData fetchCommonConfig)
			throws TMBCommonException {
		List<String> res = new ArrayList<>();
		if (module.equalsIgnoreCase(ProductsExpServiceConstant.EKYC_SERVICE_HOUR_MODULE)) {
			res.add(0, fetchCommonConfig.getNdidNoneServiceHour().getStart());
			res.add(1, fetchCommonConfig.getNdidNoneServiceHour().getEnd());
		} else {
			throw new TMBCommonException(ProductsExpServiceConstant.INVALID_REQUEST_FAILURE_CODE,
					ProductsExpServiceConstant.MISSING_SERVICE_HOUR_HEADER.toLowerCase(),
					ProductsExpServiceConstant.SERVICE_NAME, HttpStatus.BAD_REQUEST, null);
		}
		return res;
	}

	/**
	 * Method responsible to convert time to date Format
	 * 
	 * @param time
	 * @return
	 * @throws ParseException
	 */
	@LogAround
	private Calendar getCalendarObj(String time) throws ParseException {
		Date time1 = new SimpleDateFormat("HH:mm").parse(time);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(time1);
		calendar.add(Calendar.DATE, 1);
		logger.info("Created Calendar Obj for  {} ", time);
		return calendar;
	}

	/**
	 * Method responsible for fetching current time
	 * 
	 * @return
	 */
	@LogAround
	private String getCurrentTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
		Date date = new Date();
		String currentMinute = formatter.format(date);
		logger.info("currentMinute is  {} ", currentMinute);
		return currentMinute;

	}

}
