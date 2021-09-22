package com.tmb.oneapp.productsexpservice.controller.productexperience.fund;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.common.util.TMBUtils;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request.OrderCreationPaymentRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.response.OrderCreationPaymentResponse;
import com.tmb.oneapp.productsexpservice.service.productexperience.fund.OrderCreationService;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * OrderCreationController request will handle to call apis for combining the data from order transaction
 */
@RestController("/funds")
public class OrderCreationController {

    private final OrderCreationService orderCreationService;

    public OrderCreationController(OrderCreationService orderCreationService) {
        this.orderCreationService = orderCreationService;
    }

    @PostMapping(value = "/orderCreationPayment")
    public ResponseEntity<TmbOneServiceResponse<OrderCreationPaymentResponse>> orderCreationPayment(
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId,
            @Valid @RequestBody OrderCreationPaymentRequestBody request) {

        TmbOneServiceResponse<OrderCreationPaymentResponse> oneServiceResponse =
                orderCreationService.makeTransaction(correlationId, crmId, request);


        if (!StringUtils.isEmpty(serviceResponse) &&
                !StringUtils.isEmpty(serviceResponse.getHeader()) &&
                !StringUtils.isEmpty(serviceResponse.getHeader().getStatus())) {

            if (InvestmentServiceConstant.SUCCESS_CODE.equals(serviceResponse.getHeader().getStatus().getCode())) {

                oneServiceResponse.setStatus(new TmbStatus(InvestmentServiceConstant.SUCCESS_CODE,
                        InvestmentServiceConstant.SUCCESS_MESSAGE,
                        InvestmentServiceConstant.SERVICE_NAME, InvestmentServiceConstant.SUCCESS_MESSAGE));
                OrderCreationPaymentResponse response = new OrderCreationPaymentResponse(serviceResponse.getBody());
                oneServiceResponse.setData(response);
                return ResponseEntity.ok().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse);

            } else {

                oneServiceResponse.setStatus(TmbStatusUtil.mappingTmbStatusResponse(serviceResponse.getHeader()));
                oneServiceResponse.setData(null);
                return ResponseEntity.badRequest().headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse);

            }

        } else {

            oneServiceResponse.setStatus(new TmbStatus(InvestmentServiceConstant.DATA_NOT_FOUND_CODE,
                    InvestmentServiceConstant.DATA_NOT_FOUND_MESSAGE,
                    InvestmentServiceConstant.SERVICE_NAME, InvestmentServiceConstant.DATA_NOT_FOUND_MESSAGE));
            oneServiceResponse.setData(null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(TMBUtils.getResponseHeaders()).body(oneServiceResponse);

        }


    }

}
