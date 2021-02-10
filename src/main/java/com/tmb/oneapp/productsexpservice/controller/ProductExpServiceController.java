package com.tmb.oneapp.productsexpservice.controller;

import com.tmb.common.logger.LogAround;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.request.accdetail.FundAccountRq;
import com.tmb.oneapp.productsexpservice.model.response.accdetail.FundAccountRs;
import com.tmb.oneapp.productsexpservice.service.ProductsExpService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import com.tmb.common.logger.TMBLogger;
import javax.validation.Valid;
import org.springframework.http.HttpHeaders;
import java.time.Instant;


/**
 * ProductExpServiceController request mapping will handle apis call and
 * then navigate to respective method to get MF account Detail
 *
 */
@RequestMapping("/product")
@RestController
@Api(tags = "Get fund detail and fund rule than return to front-end")
public class ProductExpServiceController {

	private static final TMBLogger<ProductExpServiceController> logger = new TMBLogger<>(ProductExpServiceController.class);
	private ProductsExpService productsExpService;


	@Autowired
	public ProductExpServiceController(ProductsExpService productsExpService) {
		this.productsExpService = productsExpService;
	}

	/**
	 * Description:- Inquiry MF Service
	 *
	 * @param correlationId
	 * @return return account full details
	 */
	@ApiOperation(value = "Fetch Fund Detail based on Unit Holder No, Fund House Code And FundCode")
	@LogAround
	@PostMapping(value = "/account/detail", consumes= MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TmbOneServiceResponse<FundAccountRs>> getFundAccountDetail(
			@ApiParam(value = ProductsExpServiceConstant.HEADER_CORRELATION_ID_DESC, defaultValue = ProductsExpServiceConstant.X_COR_ID_DEFAULT, required = true)
			@Valid @RequestHeader(ProductsExpServiceConstant.HEADER_CORRELATION_ID) String correlationId,
			@Valid @RequestBody FundAccountRq fundAccountRq) {

		TmbOneServiceResponse<FundAccountRs> oneServiceResponse = new TmbOneServiceResponse<>();

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(ProductsExpServiceConstant.HEADER_TIMESTAMP, String.valueOf(Instant.now().toEpochMilli()));
		FundAccountRs fundAccountRs = productsExpService.getFundAccountDetail(correlationId, fundAccountRq);

		if(!StringUtils.isEmpty(fundAccountRs)){
			oneServiceResponse.setData(fundAccountRs);
			oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.SUCCESS_CODE,
					ProductsExpServiceConstant.SUCCESS_MESSAGE,
					ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.SUCCESS_MESSAGE));
			return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse);
		} else {
			oneServiceResponse.setStatus(new TmbStatus(ProductsExpServiceConstant.DATA_NOT_FOUND_CODE,
					ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE,
					ProductsExpServiceConstant.SERVICE_NAME, ProductsExpServiceConstant.DATA_NOT_FOUND_MESSAGE));
			oneServiceResponse.setData(null);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse);
		}

	}

	
}
