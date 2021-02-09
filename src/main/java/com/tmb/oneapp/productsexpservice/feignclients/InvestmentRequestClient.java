package com.tmb.oneapp.productsexpservice.feignclients;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.model.request.accdetail.FundAccountRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fundrule.FundRuleRequestBody;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleBody;
import com.tmb.oneapp.productsexpservice.model.response.investmentrs.AccDetailBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.Map;

/**
 * InvestmentClient interface consume account details from investment service
 */
@FeignClient(name = "${investment.service.name}", url = "${investment.service.url}")
public interface InvestmentRequestClient {

    /**
     * @param headers     callAccountService method consume account details from
     *                    core banking
     * @param fundRuleRequestBody
     * @return
     */
    @PostMapping(value = "${investment.service.fundrule.url}")
    @ResponseBody
    public ResponseEntity<TmbOneServiceResponse<FundRuleBody>> callInvestmentFundRuleService(@RequestHeader Map<String, String> headers, @RequestBody FundRuleRequestBody fundRuleRequestBody);

    /**
     * @param headers     callAccountService method consume account details from
     *                    core banking
     * @param fundAccountRq
     * @return
     */
    @PostMapping(value = "${investment.service.accountdetail.url}")
    @ResponseBody
    public ResponseEntity<TmbOneServiceResponse<AccDetailBody>> callInvestmentFundAccDetailService(@RequestHeader Map<String, String> headers, @RequestBody FundAccountRequestBody fundAccountRq);

}
