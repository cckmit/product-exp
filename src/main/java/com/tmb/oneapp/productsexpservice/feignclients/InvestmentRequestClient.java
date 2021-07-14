package com.tmb.oneapp.productsexpservice.feignclients;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.model.client.request.RelationshipRequest;
import com.tmb.oneapp.productsexpservice.model.client.response.RelationshipResponseBody;
import com.tmb.oneapp.productsexpservice.model.customer.account.purpose.response.AccountPurposeResponseBody;
import com.tmb.oneapp.productsexpservice.model.customer.account.redeem.request.AccountRedeemRequest;
import com.tmb.oneapp.productsexpservice.model.customer.account.redeem.response.AccountRedeemResponseBody;
import com.tmb.oneapp.productsexpservice.model.customer.request.CustomerRequest;
import com.tmb.oneapp.productsexpservice.model.customer.response.CustomerResponseBody;
import com.tmb.oneapp.productsexpservice.model.fund.dailynav.response.DailyNavBody;
import com.tmb.oneapp.productsexpservice.model.fund.information.request.FundCodeRequestBody;
import com.tmb.oneapp.productsexpservice.model.fund.information.response.InformationBody;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.request.UnitHolder;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundSummaryResponse;
import com.tmb.oneapp.productsexpservice.model.portfolio.nickname.request.PortfolioNicknameRequest;
import com.tmb.oneapp.productsexpservice.model.portfolio.nickname.response.PortfolioNicknameResponseBody;
import com.tmb.oneapp.productsexpservice.model.portfolio.request.OpenPortfolioRequest;
import com.tmb.oneapp.productsexpservice.model.portfolio.response.OpenPortfolioResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.accountdetail.request.ViewAipRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.accountdetail.response.ViewAipResponseBody;
import com.tmb.oneapp.productsexpservice.model.request.accdetail.FundAccountRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fund.countprocessorder.CountToBeProcessOrderRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fundallocation.FundAllocationRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fundffs.FfsRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fundrule.FundRuleRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.fundsummary.PtesBodyRequest;
import com.tmb.oneapp.productsexpservice.model.request.stmtrequest.OrderStmtByPortRequest;
import com.tmb.oneapp.productsexpservice.model.request.suitability.SuitabilityBody;
import com.tmb.oneapp.productsexpservice.model.response.PtesDetail;
import com.tmb.oneapp.productsexpservice.model.response.fund.countprocessorder.CountOrderProcessingResponseBody;
import com.tmb.oneapp.productsexpservice.model.response.fund.fundallocation.FundAllocationResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundfavorite.CustomerFavoriteFundData;
import com.tmb.oneapp.productsexpservice.model.response.fundffs.FfsResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundholiday.FundHolidayBody;
import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundListBody;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleBody;
import com.tmb.oneapp.productsexpservice.model.response.fundsummary.FundSummaryByPortResponse;
import com.tmb.oneapp.productsexpservice.model.response.investment.AccountDetailBody;
import com.tmb.oneapp.productsexpservice.model.response.stmtresponse.StatementResponse;
import com.tmb.oneapp.productsexpservice.model.response.suitability.SuitabilityInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * InvestmentClient interface consume account details from investment service
 */
@FeignClient(name = "${investment.service.name}", url = "${investment.service.url}")
public interface InvestmentRequestClient {

    /**
     * Call investment fund rule service response entity.
     *
     * @param headers             callAccountService method consume account details from core banking
     * @param fundRuleRequestBody the fund rule request body
     * @return response entity
     */
    @PostMapping(value = "${investment.service.fund.rule.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<FundRuleBody>> callInvestmentFundRuleService(
            @RequestHeader Map<String, String> headers, @RequestBody FundRuleRequestBody fundRuleRequestBody);

    /**
     * Call investment fund acc detail service response entity.
     *
     * @param headers       callAccountService method consume account details from core banking
     * @param fundAccountRq the fund account rq
     * @return response entity
     */
    @PostMapping(value = "${investment.service.account.detail.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<AccountDetailBody>> callInvestmentFundAccDetailService(
            @RequestHeader Map<String, String> headers, @RequestBody FundAccountRequestBody fundAccountRq);

    /**
     * Call investment fund summary service fund summary response.
     *
     * @param headers    the headers
     * @param unitHolder the unit holder
     * @return the fund summary response
     */
    @PostMapping(value = "${investment.service.fund.summary.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<FundSummaryResponse>> callInvestmentFundSummaryService(
            @RequestHeader Map<String, String> headers, @RequestBody UnitHolder unitHolder);

    /***
     * Call investment to get fund sumaary by port
     * @param headers
     * @param unitHolder
     * @return
     */
    @PostMapping(value = "${investment.service.fund.summary.by.port.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<FundSummaryByPortResponse>> callInvestmentFundSummaryByPortService(
            @RequestHeader Map<String, String> headers, @RequestBody UnitHolder unitHolder);

    /***
     * Call investment to get fund sumaary by port
     * @param headers
     * @param countToBeProcessOrderRequestBody
     * @return
     */
    @PostMapping(value = "${investment.service.fund.processed.order.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<CountOrderProcessingResponseBody>> callInvestmentCountProcessOrderService(
            @RequestHeader Map<String, String> headers, @RequestBody CountToBeProcessOrderRequestBody countToBeProcessOrderRequestBody);

    /**
     * Call investment fund summary service fund summary response.
     *
     * @param headers  the headers
     * @param fundCode the fund code
     * @return the fund summary response
     */
    @GetMapping(value = "${investment.service.fund.holiday.url}")
    ResponseEntity<TmbOneServiceResponse<FundHolidayBody>> callInvestmentFundHolidayService(
            @RequestHeader Map<String, String> headers, @PathVariable("fundCode") String fundCode);


    /**
     * Call investment fund listInfo service fund summary response.
     *
     * @param headers the headers
     * @return the fund summary response
     */
    @PostMapping(value = "${investment.service.fund.listinfo.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<FundListBody>> callInvestmentFundListInfoService(
            @RequestHeader Map<String, String> headers);

    /**
     * Call investment fund summary service fund summary response.
     *
     * @param headers        the headers
     * @param ffsRequestBody the unit holder
     * @return the fund fact sheet response
     */
    @PostMapping(value = "${investment.service.fund.factsheet.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<FfsResponse>> callInvestmentFundFactSheetService(
            @RequestHeader Map<String, String> headers, @RequestBody FfsRequestBody ffsRequestBody);

    /**
     * Call investment fund summary service fund summary response.
     *
     * @param headers         the headers
     * @param suitabilityBody the rmID
     * @return the Suitability response
     */
    @PostMapping(value = "${investment.service.fund.suitability.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<SuitabilityInfo>> callInvestmentFundSuitabilityService(
            @RequestHeader Map<String, String> headers, @RequestBody SuitabilityBody suitabilityBody);


    /**
     * Call investment fund summary service fund summary response.
     *
     * @param headers                the headers
     * @param orderStmtByPortRequest the rmID
     * @return the Suitability response
     */
    @PostMapping(value = "${investment.service.fund.statement.by.port.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<StatementResponse>> callInvestmentStatementByPortService(
            @RequestHeader Map<String, String> headers, @RequestBody OrderStmtByPortRequest orderStmtByPortRequest);


    /**
     * Call investment fund favorite service fund favorite response.
     *
     * @param headers the headers
     * @return the fund favorite response
     */
    @PostMapping(value = "${investment.service.fund.listfavorite.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<List<CustomerFavoriteFundData>>> callInvestmentFundFavoriteService(
            @RequestHeader Map<String, String> headers);


    /**
     * Call investment to get ptest port
     *
     * @param headers
     * @param request
     * @return
     */
    @PostMapping(value = "${investment.service.fund.get.ptes.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<List<PtesDetail>>> getPtesPort(
            @RequestHeader Map<String, String> headers, @RequestBody PtesBodyRequest request);

    /**
     * Call investment fund information service to get fund information response.
     *
     * @param header the headers
     * @return the fund information response
     */
    @PostMapping(value = "${investment.service.fund.information.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<InformationBody>> getFundInformation(
            @RequestHeader Map<String, String> header, @RequestBody FundCodeRequestBody fundCodeRequestBody);

    /**
     * Call investment fund daily nav service to get fund daily nav response.
     *
     * @param header the headers
     * @return the fund daily nav response
     */
    @PostMapping(value = "${investment.service.fund.daily.nav.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<DailyNavBody>> getFundDailyNav(
            @RequestHeader Map<String, String> header, @RequestBody FundCodeRequestBody fundCodeRequestBody);

    /**
     * Call investment fund allocation service to get fund allocation response.
     *
     * @param header the headers
     * @return the fund allocation response
     */
    @PostMapping(value = "${investment.service.fund.allocation.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<FundAllocationResponse>> callInvestmentFundAllocation(
            @RequestHeader Map<String, String> header, @RequestBody FundAllocationRequestBody fundAllocationRequestBody);

    /**
     * Call investment fund allocation service to get fund allocation response.
     *
     * @param header the headers
     * @return the fund allocation response
     */
    @PostMapping(value = "${investment.service.customer.create.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<CustomerResponseBody>> createCustomer(
            @RequestHeader Map<String, String> header, @RequestBody CustomerRequest customerRequest);

    /**
     * Call investment account purpose service to get account purpose response.
     *
     * @param header the headers
     * @return the account purpose response
     */
    @PostMapping(value = "${investment.service.customer.account.purpose.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<AccountPurposeResponseBody>> getCustomerAccountPurpose(
            @RequestHeader Map<String, String> header);

    /**
     * Call investment account redeem service to get account redeem response.
     *
     * @param header               the headers
     * @param accountRedeemRequest the accountRedeemRequest
     * @return the account redeem response
     */
    @PostMapping(value = "${investment.service.customer.account.redeem.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<AccountRedeemResponseBody>> getCustomerAccountRedeem(
            @RequestHeader Map<String, String> header, @RequestBody AccountRedeemRequest accountRedeemRequest);

    /**
     * Call investment client relationship service to update client relationship.
     *
     * @param header              the headers
     * @param relationshipRequest the relationshipRequest
     * @return the client relationship response
     */
    @PostMapping(value = "${investment.service.client.relationship.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<RelationshipResponseBody>> updateClientRelationship(
            @RequestHeader Map<String, String> header, @RequestBody RelationshipRequest relationshipRequest);

    /**
     * Call investment open portfolio service to open portfolio.
     *
     * @param header               the headers
     * @param openPortfolioRequest the openPortfolioRequest
     * @return the open portfolio response
     */
    @PostMapping(value = "${investment.service.open.portfolio.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<OpenPortfolioResponseBody>> openPortfolio(
            @RequestHeader Map<String, String> header, @RequestBody OpenPortfolioRequest openPortfolioRequest);

    /**
     * Call investment portfolio nickname service to create or update portfolio nickname.
     *
     * @param header                   the headers
     * @param portfolioNicknameRequest the portfolioNicknameRequest
     * @return the portfolio nickname response
     */
    @PostMapping(value = "${investment.service.portfolio.nickname.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<PortfolioNicknameResponseBody>> updatePortfolioNickname(
            @RequestHeader Map<String, String> header, @RequestBody PortfolioNicknameRequest portfolioNicknameRequest);

    /**
     * Call investment portfolio nickname service to create or update portfolio nickname.
     *
     * @param header         the headers
     * @param viewAipRequest the viewAipRequest
     * @return the AIP plans response
     */
    @PostMapping(value = "${investment.service.view.aip.url}")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<ViewAipResponseBody>> getViewAipPlans(
            @RequestHeader Map<String, String> header, @RequestBody ViewAipRequest viewAipRequest);
}
