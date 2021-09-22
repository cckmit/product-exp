package com.tmb.oneapp.productsexpservice.feignclients;

import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request.OrderCreationPaymentRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request.SellAndSwitchRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.response.OrderCreationPaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * The interface Order request client.
 */
@FeignClient(name = "${mf.order.service.name}", url = "${mf.order.transaction.url}")
public interface OrderRequestClient {

    /**
     * Call MF create order creation payment response service.
     *
     * @param request the order creation request
     * @return the order creation payment data
     */
    @PostMapping(value = "${mf.order.create.transaction.path}")
    OrderCreationPaymentResponse createOrderPayment(@RequestBody OrderCreationPaymentRequest request);


    /**
     * Call MF create order creation payment service.
     *
     * @param request the sell and switch request
     * @return the order creation payment data
     */
    @PostMapping(value = "${mf.order.create.transaction.path}")
    OrderCreationPaymentResponse createSellAndSwitchTransaction(@RequestBody SellAndSwitchRequest request);


}
