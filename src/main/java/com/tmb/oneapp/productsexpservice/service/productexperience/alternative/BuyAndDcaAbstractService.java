package com.tmb.oneapp.productsexpservice.service.productexperience.alternative;

import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.service.ProductsExpService;
import com.tmb.oneapp.productsexpservice.service.productexperience.customer.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BuyAndDcaAbstractService {
    protected final AlternativeService alternativeService;

    protected final CustomerService customerService;

    protected final ProductsExpService productsExpService;

    protected final InvestmentRequestClient investmentRequestClient;


    @Autowired
    protected BuyAndDcaAbstractService(AlternativeService alternativeService,
                                 CustomerService customerService,
                                 ProductsExpService productsExpService,
                                InvestmentRequestClient investmentRequestClient) {
        this.alternativeService = alternativeService;
        this.customerService = customerService;
        this.productsExpService = productsExpService;
        this.investmentRequestClient = investmentRequestClient;
    }
}
