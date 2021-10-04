package com.tmb.oneapp.productsexpservice.activitylog.transaction.service;

import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.oneapp.productsexpservice.activitylog.buy.service.BuyActivityLogService;
import com.tmb.oneapp.productsexpservice.activitylog.sellandswitch.service.SellActivityLogService;
import com.tmb.oneapp.productsexpservice.activitylog.sellandswitch.service.SwitchActivityLogService;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.request.OrderCreationPaymentRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.ordercreation.response.OrderCreationPaymentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The enter pin is correct activity log service.
 */
@Service
public class EnterPinIsCorrectActivityLogService {

    private final BuyActivityLogService buyActivityLogService;

    private final SellActivityLogService sellActivityLogService;

    private final SwitchActivityLogService switchActivityLogService;

    private static final TMBLogger<EnterPinIsCorrectActivityLogService> logger = new TMBLogger<>(EnterPinIsCorrectActivityLogService.class);

    @Autowired
    public EnterPinIsCorrectActivityLogService(BuyActivityLogService buyActivityLogService,
                                               SellActivityLogService sellActivityLogService,
                                               SwitchActivityLogService switchActivityLogService) {
        this.buyActivityLogService = buyActivityLogService;
        this.sellActivityLogService = sellActivityLogService;
        this.switchActivityLogService = switchActivityLogService;
    }

    /**
     * Generic Method to check type of order, then calling buy, sell or switch to save activity log when enter pin is correct for customer care system
     *
     * @param correlationId       the correlation id
     * @param crmId               the crm id
     * @param paymentRequestBody  the order creation payment request body
     * @param paymentResponseBody the order creation payment response body
     * @return
     */
    @LogAround
    public void save(String correlationId, String crmId,
                     OrderCreationPaymentRequestBody paymentRequestBody,
                     String status,
                     OrderCreationPaymentResponse paymentResponseBody,
                     String orderType) {

        switch (orderType) {

            case ProductsExpServiceConstant.PURCHASE_TRANSACTION_LETTER_TYPE:
                buyActivityLogService.enterEnterPinIsCorrect(correlationId, crmId, status, paymentRequestBody, paymentResponseBody);
                break;

            case ProductsExpServiceConstant.REDEEM_TRANSACTION_LETTER_TYPE:
                sellActivityLogService.enterEnterPinIsCorrect(correlationId, crmId, status, paymentRequestBody, paymentResponseBody);
                break;

            case ProductsExpServiceConstant.SWITCH_TRANSACTION_LETTER_TYPE:
                switchActivityLogService.enterEnterPinIsCorrect(correlationId, crmId, status, paymentRequestBody, paymentResponseBody);
                break;

            default:
                logger.info("========== no match order type ==========");
        }
    }
}
