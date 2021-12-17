package com.tmb.oneapp.productsexpservice.controller.productexperience.transaction;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.LogAround;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.productexperience.transaction.orderaip.request.OrderAIPRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.transaction.orderaip.response.OrderAIPResponseBody;
import com.tmb.oneapp.productsexpservice.service.productexperience.transaction.AipService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.tmb.oneapp.productsexpservice.util.TmbStatusUtil.notFoundStatus;

@Api(tags = "for inquiry first trade and occupation require flag")
@RequestMapping("/transaction")
@RestController
public class AipController {

    private final AipService aipService;

    @Autowired
    public AipController(AipService aipService) {
        this.aipService = aipService;
    }

    /**
     * Description:- method call MF service to create aip order
     *
     * @param correlationId the correlation id
     * @param crmId         the crm id
     * @param ipAddress     the ip address
     * @return return order AIP created
     */
    @LogAround
    @PostMapping(value = "/createAIPOrder")
    public ResponseEntity<TmbOneServiceResponse<OrderAIPResponseBody>> createAPIOrder(
            @ApiParam(value = ProductsExpServiceConstant.HEADER_CORRELATION_ID_DESC, defaultValue = ProductsExpServiceConstant.X_COR_ID_DEFAULT, required = true)
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationId,
            @Valid @RequestHeader(ProductsExpServiceConstant.HEADER_X_CRM_ID) String crmId,
            @Valid @RequestHeader(ProductsExpServiceConstant.X_FORWARD_FOR) String ipAddress,
            @Valid @RequestBody OrderAIPRequestBody orderAIPRequestBody) throws TMBCommonException {

        TmbOneServiceResponse<OrderAIPResponseBody> oneServiceResponse = aipService.createAipOrder(correlationId, crmId, ipAddress, orderAIPRequestBody);
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
