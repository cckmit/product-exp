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

import static com.tmb.oneapp.productsexpservice.util.TmbStatusUtil.notFoundStatus;

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

        if (!StringUtils.isEmpty(oneServiceResponse.getStatus())) {
            if (!oneServiceResponse.getStatus().getCode().equals(ProductsExpServiceConstant.SUCCESS_CODE)) {
                return ResponseEntity.badRequest().body(oneServiceResponse);
            }
            return ResponseEntity.ok(oneServiceResponse);
        } else {
            oneServiceResponse.setStatus(notFoundStatus());
            return new ResponseEntity(oneServiceResponse, HttpStatus.NOT_FOUND);
        }

    }

}
