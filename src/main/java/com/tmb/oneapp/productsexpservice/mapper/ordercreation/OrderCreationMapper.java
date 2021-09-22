package com.tmb.oneapp.productsexpservice.mapper.ordercreation;

import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request.OrderCreationPaymentRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request.SellAndSwitchRequestBody;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderCreationMapper {

    SellAndSwitchRequestBody orderCreationBodyToSellAndSwtichRequestBody(OrderCreationPaymentRequestBody orderCreationPaymentRequestBody);

}
