package com.tmb.oneapp.productsexpservice.service.productexperience.alternative;

import com.tmb.oneapp.productsexpservice.service.productexperience.customer.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class SellAndSwitchAbstractService {
    protected final AlternativeService alternativeService;

    protected final CustomerService customerService;

    @Autowired
    protected SellAndSwitchAbstractService(AlternativeService alternativeService,
                                    CustomerService customerService) {
        this.alternativeService = alternativeService;
        this.customerService = customerService;
    }
}
