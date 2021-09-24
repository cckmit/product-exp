package com.tmb.oneapp.productsexpservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.CommonData;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.dto.fund.fundallocation.*;
import com.tmb.oneapp.productsexpservice.enums.AlternativeBuySellSwitchDcaErrorEnums;
import com.tmb.oneapp.productsexpservice.feignclients.AccountRequestClient;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.request.UnitHolder;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundClass;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundClassList;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundSearch;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.FundSummaryBody;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.byport.FundSummaryByPortBody;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.byport.PortfolioByPort;
import com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.request.FundAccountRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.request.FundAccountRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.accdetail.response.FundAccountResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.countprocessorder.request.CountToBeProcessOrderRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.countprocessorder.response.CountOrderProcessingResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fundallocation.request.FundAllocationRequestBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.fundallocation.response.FundAllocationResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.fundallocation.response.FundSuggestAllocationList;
import com.tmb.oneapp.productsexpservice.model.request.fundlist.FundListRequest;
import com.tmb.oneapp.productsexpservice.model.request.fundpayment.FundPaymentDetailRequest;
import com.tmb.oneapp.productsexpservice.model.request.fundrule.FundRuleRequestBody;
import com.tmb.oneapp.productsexpservice.model.request.stmtrequest.OrderStmtByPortRequest;
import com.tmb.oneapp.productsexpservice.model.response.PtesDetail;
import com.tmb.oneapp.productsexpservice.model.response.fundfavorite.CustomerFavoriteFundData;
import com.tmb.oneapp.productsexpservice.model.response.fundholiday.FundHolidayBody;
import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundClassListInfo;
import com.tmb.oneapp.productsexpservice.model.response.fundpayment.FundPaymentDetailResponse;
import com.tmb.oneapp.productsexpservice.model.response.fundrule.FundRuleResponse;
import com.tmb.oneapp.productsexpservice.model.response.investment.AccountDetailResponse;
import com.tmb.oneapp.productsexpservice.model.response.stmtresponse.StatementResponse;
import com.tmb.oneapp.productsexpservice.model.response.suitability.SuitabilityInfo;
import com.tmb.oneapp.productsexpservice.service.productexperience.customer.CustomerService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.INVESTMENT_JOINT_FLAG_INDIVIDUAL;


/**
 * ProductsExpService class will get fund Details from MF Service
 */
@Service
public class ProductsExpService {

    @Value("${com.tmb.oneapp.service.activity.topic.name}")
    private String topicName;

    private static final TMBLogger<ProductsExpService> logger = new TMBLogger<>(ProductsExpService.class);

    private final InvestmentRequestClient investmentRequestClient;

    private final AccountRequestClient accountRequestClient;

    private final ProductExpAsyncService productExpAsyncService;

    private final CustomerService customerService;

    @Autowired
    public ProductsExpService(InvestmentRequestClient investmentRequestClient,
                              AccountRequestClient accountRequestClient,
                              ProductExpAsyncService productExpAsyncService,
                              CustomerService customerService) {

        this.investmentRequestClient = investmentRequestClient;
        this.accountRequestClient = accountRequestClient;
        this.productExpAsyncService = productExpAsyncService;
        this.customerService = customerService;

    }

    /**
     * Generic Method to call MF Service getFundAccDetail
     *
     * @param fundAccountRequest
     * @param correlationId
     * @return
     */
    @LogAround
    public FundAccountResponse getFundAccountDetail(String correlationId, FundAccountRequest fundAccountRequest) throws TMBCommonException {
        FundAccountResponse fundAccountResponse = null;
        FundAccountRequestBody fundAccountRequestBody = UtilMap.mappingRequestFundAcc(fundAccountRequest);
        FundRuleRequestBody fundRuleRequestBody = UtilMap.mappingRequestFundRule(fundAccountRequest);
        OrderStmtByPortRequest orderStmtByPortRequest = UtilMap.mappingRequestStmtByPort(fundAccountRequest,
                ProductsExpServiceConstant.FIXED_START_PAGE, ProductsExpServiceConstant.FIXED_END_PAGE);

        Map<String, String> header = UtilMap.createHeader(correlationId);
        try {
            CompletableFuture<AccountDetailResponse> fetchFundAccountDetail = productExpAsyncService.fetchFundAccountDetail(header, fundAccountRequestBody);
            CompletableFuture<FundRuleResponse> fetchFundRule = productExpAsyncService.fetchFundRule(header, fundRuleRequestBody);
            CompletableFuture<StatementResponse> fetchStmtByPort = productExpAsyncService.fetchStatementByPort(header, orderStmtByPortRequest);
            CompletableFuture.allOf(fetchFundAccountDetail, fetchFundRule, fetchStmtByPort);

            AccountDetailResponse accountDetailResponse = fetchFundAccountDetail.get();
            FundRuleResponse fundRuleResponse = fetchFundRule.get();
            StatementResponse statementResponse = fetchStmtByPort.get();
            fundAccountResponse = UtilMap.validateTMBResponse(accountDetailResponse, fundRuleResponse, statementResponse);
        } catch (ExecutionException e) {
            if(e.getCause() instanceof TMBCommonException){
                throw (TMBCommonException) e.getCause();
            }
            errorHandle();
        }  catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, ex);
            return null;
        }
        return fundAccountResponse;
    }

    void errorHandle() throws TMBCommonException {
        throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                ResponseCode.FAILED.getMessage(),
                ResponseCode.FAILED.getService(), HttpStatus.BAD_REQUEST, null);
    }

    /**
     * Get fund summary fund summary response.
     *
     * @param correlationId the correlation id
     * @param crmId         the crm id
     * @return the fund summary response
     */
    @LogAround
    public FundSummaryBody getFundSummary(String correlationId, String crmId) {
        FundSummaryBody result = new FundSummaryBody();
        ResponseEntity<TmbOneServiceResponse<FundSummaryBody>> fundSummary;
        UnitHolder unitHolder = new UnitHolder();
        ResponseEntity<TmbOneServiceResponse<FundSummaryByPortBody>> summaryByPortResponse;
        Map<String, String> header = UtilMap.createHeader(correlationId);
        ResponseEntity<TmbOneServiceResponse<CountOrderProcessingResponseBody>> countOrderProcessingResponse;

        try {
            List<String> ports = getPortList(header, crmId, true);
            result.setPortsUnitHolder(ports);
            unitHolder.setUnitHolderNumber(ports.stream().map(String::valueOf).collect(Collectors.joining(",")));
            logger.info(unitHolder.toString());
            fundSummary = investmentRequestClient.callInvestmentFundSummaryService(header, unitHolder);
            summaryByPortResponse = investmentRequestClient.callInvestmentFundSummaryByPortService(header, unitHolder);
            countOrderProcessingResponse = investmentRequestClient.callInvestmentCountProcessOrderService(header, crmId,
                    CountToBeProcessOrderRequestBody.builder().serviceType("1").build());

            logger.info(ProductsExpServiceConstant.INVESTMENT_SERVICE_RESPONSE + "{}", fundSummary);

            if (HttpStatus.OK.value() == fundSummary.getStatusCode().value()) {
                this.setFundSummaryBody(result, ports, fundSummary.getBody(), summaryByPortResponse.getBody());
            }

            result.setCountProcessedOrder("0");
            if (HttpStatus.OK.value() == countOrderProcessingResponse.getStatusCode().value()) {
                result.setCountProcessedOrder(countOrderProcessingResponse.getBody().getData().getCountProcessOrder());
            }
            return result;
        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, ex);
            return null;
        }
    }

    /**
     * Get port list response.
     *
     * @param header                 the header
     * @param crmId                  the crm id
     * @param isIncludePtesPortfolio the status to include ptes portfolio
     * @return the port list
     */
    @LogAround
    public List<String> getPortList(Map<String, String> header, String crmId, boolean isIncludePtesPortfolio) throws JsonProcessingException {
        List<String> ports = new ArrayList<>();
        List<String> ptestPortList = new ArrayList<>();
        String portData = customerService.getAccountSaving(header.get(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID), crmId);
        logger.info(ProductsExpServiceConstant.INVESTMENT_SERVICE_RESPONSE, portData);

        if (!StringUtils.isEmpty(portData)) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readValue(portData, JsonNode.class);
            JsonNode dataNode = node.get("data");
            JsonNode portList = dataNode.get("mutual_fund_accounts");
            ports = mapper.readValue(portList.toString(), new TypeReference<>() {
            });
        }
        if (isIncludePtesPortfolio) {
            ResponseEntity<TmbOneServiceResponse<List<PtesDetail>>> ptesDetailResult = investmentRequestClient.getPtesPort(header, UtilMap.halfCrmIdFormat(crmId));

            Optional<List<PtesDetail>> ptesDetailList = Optional.ofNullable(ptesDetailResult)
                    .map(ResponseEntity::getBody)
                    .map(TmbOneServiceResponse::getData);
            if (ptesDetailList.isPresent()) {
                ptestPortList = ptesDetailList.get().stream()
                        .filter(ptesDetail -> ProductsExpServiceConstant.PTES_PORT_FOLIO_FLAG.equalsIgnoreCase(ptesDetail.getPortfolioFlag()))
                        .map(PtesDetail::getPortfolioNumber)
                        .collect(Collectors.toList());
            }
        }
        ports.addAll(ptestPortList);
        return ports;
    }

    /***
     * Set The FundSummaryBody
     * @param result
     * @param fundSummary
     * @param fundSummaryByPort
     */
    private void setFundSummaryBody(FundSummaryBody result, List<String> ports,
                                    TmbOneServiceResponse<FundSummaryBody> fundSummary,
                                    TmbOneServiceResponse<FundSummaryByPortBody> fundSummaryByPort) {

        if (fundSummary != null) {
            FundClassList fundClassList = fundSummary.getData().getFundClassList();
            List<FundClass> fundClass = fundClassList.getFundClass();
            List<FundClass> fundClassData = UtilMap.mappingFundListData(fundClass);
            List<FundSearch> searchList = UtilMap.mappingFundSearchListData(fundClass);
            result.setFundClass(fundClassData);
            result.setSearchList(searchList);
            result.setFundClassList(null);
            result.setFeeAsOfDate(fundSummary.getData().getFeeAsOfDate());
            result.setPercentOfFundType(fundSummary.getData().getPercentOfFundType());
            result.setSumAccruedFee(fundSummary.getData().getSumAccruedFee());
            result.setUnrealizedProfitPercent(fundSummary.getData().getUnrealizedProfitPercent());
            result.setSummaryMarketValue(fundSummary.getData().getSummaryMarketValue());
            result.setSummaryUnrealizedProfit(fundSummary.getData().getSummaryUnrealizedProfit());
            result.setSummaryUnrealizedProfitPercent(fundSummary.getData().getSummaryUnrealizedProfitPercent());
            result.setSummarySmartPortMarketValue(fundSummary.getData().getSummarySmartPortMarketValue());
            result.setSummarySmartPortUnrealizedProfit(fundSummary.getData().getSummarySmartPortUnrealizedProfit());
            result.setSummarySmartPortUnrealizedProfitPercent(fundSummary.getData().getSummarySmartPortUnrealizedProfitPercent());

            List<FundClass> smartPort = fundClassData.stream()
                    .filter(port -> ProductsExpServiceConstant.SMART_PORT_CODE.equalsIgnoreCase(port.getFundClassCode()))
                    .collect(Collectors.toList());
            result.setSmartPortList(smartPort);
            if (!smartPort.isEmpty()) {
                result.setIsSmartPort(Boolean.TRUE);
            }

            List<FundClass> ptPort = fundClassData.stream()
                    .filter(port -> !ProductsExpServiceConstant.SMART_PORT_CODE.equalsIgnoreCase(port.getFundClassCode()))
                    .collect(Collectors.toList());
            result.setPtPortList(ptPort);

            if (!isPortfolioListEmpty(fundSummaryByPort)) {
                result.setSummaryByPort(fundSummaryByPort.getData().getPortfolioList());

                boolean individualAccountExist = isIndividualAccountExist(fundSummaryByPort);
                result.setIsJointPortOnly(!individualAccountExist);
            }

            List<String> ptPorts = ports.stream().filter(port -> port.startsWith("PT")).collect(Collectors.toList());
            if (!ptPorts.isEmpty()) {
                result.setIsPt(Boolean.TRUE);
            }

            List<String> ptestPorts = ports.stream().filter(port -> port.startsWith("PTES")).collect(Collectors.toList());
            if (!ptestPorts.isEmpty()) {
                result.setIsPtes(Boolean.TRUE);
            }
        }
    }

    /**
     * Generic Method to call MF Service getFundAccDetail
     *
     * @param correlationId
     * @param crmId
     * @param fundPaymentDetailRequest
     * @return FundPaymentDetailResponse
     */
    @LogAround
    public TmbOneServiceResponse<FundPaymentDetailResponse> getFundPrePaymentDetail(String correlationId, String crmId, FundPaymentDetailRequest fundPaymentDetailRequest) throws TMBCommonException {
        TmbOneServiceResponse<FundPaymentDetailResponse> tmbOneServiceResponse = new TmbOneServiceResponse();
        tmbOneServiceResponse.setStatus(TmbStatusUtil.successStatus());
        FundRuleRequestBody fundRuleRequestBody = UtilMap.mappingRequestFundRule(fundPaymentDetailRequest);
        Map<String, String> headerParameter = UtilMap.createHeader(correlationId);
        FundPaymentDetailResponse fundPaymentDetailResponse;
        try {
            CompletableFuture<FundRuleResponse> fetchFundRule = productExpAsyncService.fetchFundRule(headerParameter, fundRuleRequestBody);
            CompletableFuture<FundHolidayBody> fetchFundHoliday = productExpAsyncService.fetchFundHoliday(headerParameter, fundRuleRequestBody.getFundCode());
            CompletableFuture<String> fetchCustomerExp = productExpAsyncService.fetchCustomerExp(headerParameter, UtilMap.halfCrmIdFormat(crmId));
            CompletableFuture<List<CommonData>> fetchCommonConfigByModule = productExpAsyncService.fetchCommonConfigByModule(correlationId, ProductsExpServiceConstant.INVESTMENT_MODULE_VALUE);

            CompletableFuture.allOf(fetchFundRule, fetchFundHoliday, fetchCustomerExp, fetchCommonConfigByModule);
            FundRuleResponse fundRuleResponse = fetchFundRule.get();
            FundHolidayBody fundHolidayBody = fetchFundHoliday.get();
            String customerExp = fetchCustomerExp.get();
            List<CommonData> commonDataList = fetchCommonConfigByModule.get();
            UtilMap map = new UtilMap();
            fundPaymentDetailResponse = map.mappingPaymentResponse(fundRuleResponse, fundHolidayBody, commonDataList, customerExp);

            if(fundPaymentDetailResponse.getDepositAccountList().isEmpty()){
                TmbStatus status = tmbOneServiceResponse.getStatus();
                status.setCode(AlternativeBuySellSwitchDcaErrorEnums.CASA_DORMANT.getCode());
                status.setDescription(AlternativeBuySellSwitchDcaErrorEnums.CASA_DORMANT.getDesc());
                status.setMessage(AlternativeBuySellSwitchDcaErrorEnums.CASA_DORMANT.getMsg());
                status.setService(ProductsExpServiceConstant.SERVICE_NAME);
                return tmbOneServiceResponse;
            }

            tmbOneServiceResponse.setData(fundPaymentDetailResponse);

        } catch (ExecutionException e) {
            if(e.getCause() instanceof TMBCommonException){
                throw (TMBCommonException) e.getCause();
            }
            errorHandle();
        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, ex);
            tmbOneServiceResponse.setStatus(null);
            tmbOneServiceResponse.setData(null);
            return tmbOneServiceResponse;
        }
        return tmbOneServiceResponse;
    }

    /**
     * Generic Method to call MF Service getFundList
     *
     * @param correlationId
     * @param crmId
     * @param fundListRequest
     * @return List<FundClassListInfo>
     */
    @LogAround
    public List<FundClassListInfo> getFundList(String correlationId, String crmId, FundListRequest fundListRequest) throws TMBCommonException {
        Map<String, String> headerParameter = UtilMap.createHeader(correlationId);
        List<FundClassListInfo> listFund = new ArrayList<>();
        try {
            UnitHolder unitHolder = new UnitHolder();
            String unitHolderList = fundListRequest.getUnitHolderNumber().stream().collect(Collectors.joining(","));
            unitHolder.setUnitHolderNumber(unitHolderList);

            CompletableFuture<List<FundClassListInfo>> fetchFundListInfo =
                    productExpAsyncService.fetchFundListInfo(headerParameter, correlationId, ProductsExpServiceConstant.INVESTMENT_CACHE_KEY);
            CompletableFuture<FundSummaryBody> fetchFundSummary = productExpAsyncService.fetchFundSummary(headerParameter, unitHolder);
            CompletableFuture<List<CustomerFavoriteFundData>> fetchFundFavorite = productExpAsyncService.fetchFundFavorite(headerParameter, crmId);

            CompletableFuture.allOf(fetchFundListInfo, fetchFundSummary, fetchFundFavorite);
            listFund = fetchFundListInfo.get();
            FundSummaryBody fundSummaryResponse = fetchFundSummary.get();
            List<CustomerFavoriteFundData> customerFavoriteFundDataList = fetchFundFavorite.get();
            listFund = UtilMap.mappingFollowingFlag(listFund, customerFavoriteFundDataList);
            listFund = UtilMap.mappingBoughtFlag(listFund, fundSummaryResponse);
            return listFund;
        } catch (ExecutionException e) {
            if(e.getCause() instanceof TMBCommonException){
                throw (TMBCommonException) e.getCause();
            }
            errorHandle();
        }  catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, ex);
        }
        return listFund;
    }

    /**
     * Generic Method to call MF Service getFundList
     *
     * @param correlationId
     * @param crmId
     * @return SuggestAllocationDTO
     */
    @LogAround
    public SuggestAllocationDTO getSuggestAllocation(String correlationId, String crmId) throws TMBCommonException {
        UnitHolder unitHolder = new UnitHolder();
        Map<String, String> investmentHeaderRequest = UtilMap.createHeader(correlationId);
        try {
            List<String> portList = getPortListForFundSummary(investmentHeaderRequest, crmId);
            unitHolder.setUnitHolderNumber(portList.stream().map(String::valueOf).collect(Collectors.joining(",")));
            CompletableFuture<FundSummaryBody> fundSummary = productExpAsyncService.fetchFundSummary(investmentHeaderRequest, unitHolder);
            CompletableFuture<SuitabilityInfo> suitabilityInfo = productExpAsyncService.fetchSuitabilityInquiry(investmentHeaderRequest, crmId);
            CompletableFuture.allOf(fundSummary, suitabilityInfo);
            String suitabilityScore = suitabilityInfo.get().getSuitabilityScore();
            ResponseEntity<TmbOneServiceResponse<FundAllocationResponse>> fundAllocationResponse = investmentRequestClient.callInvestmentFundAllocation(
                    investmentHeaderRequest,
                    FundAllocationRequestBody.builder()
                            .suitabilityScore(suitabilityScore)
                            .build());
            return mappingSuggestAllocationDto(fundSummary.get().getFundClassList().getFundClass(), fundAllocationResponse.getBody().getData());
        } catch (ExecutionException e) {
            if(e.getCause() instanceof TMBCommonException){
                throw (TMBCommonException) e.getCause();
            }
            errorHandle();
        }  catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, ex);
        }
        return null;
    }

    private List<String> getPortListForFundSummary(Map<String, String> investmentHeaderRequest, String crmId) throws JsonProcessingException {
        List<String> portList = new ArrayList<>();
        String portListStr = accountRequestClient.getPortList(investmentHeaderRequest, crmId);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readValue(portListStr, JsonNode.class);
        JsonNode dataNode = node.get("data");
        ArrayNode arrayNode = (ArrayNode) dataNode.get("mutual_fund_accounts");

        for (int i = 0; i < arrayNode.size(); i++) {
            JsonNode itr = arrayNode.get(i);
            portList.add(itr.get("acct_nbr").textValue());
        }
        return portList;
    }

    private SuggestAllocationDTO mappingSuggestAllocationDto(List<FundClass> fundClass, FundAllocationResponse fundAllocationResponse) {
        List<MutualFundWithFundSuggestedAllocation> mutualFundWithFundSuggestedAllocations = mergeMutualFundWithSuggestAllocation(fundClass, fundAllocationResponse);
        return SuggestAllocationDTO.builder()
                .mutualFund(
                        fundClass.stream()
                                .filter(f -> !f.getFundClassCode().equals("090"))
                                .map(f -> MutualFund.builder()
                                        .fundClassCode(f.getFundClassCode())
                                        .fundClassNameEN(f.getFundClassNameEN())
                                        .fundClassNameTH(f.getFundClassNameTH())
                                        .fundClassPercent(f.getFundClassPercent())
                                        .build())
                                .collect(Collectors.toList()))
                .fundSuggestedAllocation(FundSuggestedAllocation.builder()
                        .modelNumber(fundAllocationResponse.getModelNumber())
                        .suitabilityScore(fundAllocationResponse.getSuitabilityScore())

                        .fundSuggestionList(fundAllocationResponse.getFundSuggestAllocationList().stream()
                                .map(fl -> FundSuggestion.builder()
                                        .fundClassCode(fl.getFundClassCode())
                                        .fundClassNameEn(fl.getFundClassNameEn())
                                        .fundClassNameTh(fl.getFundClassNameTh())
                                        .recommendedPercent(fl.getRecommendedPercent())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .mutualFundWithFundSuggestedAllocation(mutualFundWithFundSuggestedAllocations)
                .build();
    }

    private List<MutualFundWithFundSuggestedAllocation> mergeMutualFundWithSuggestAllocation(List<FundClass> fundClass, FundAllocationResponse fundAllocationResponse) {
        List<MutualFundWithFundSuggestedAllocation> mutualFundWithFundSuggestedAllocationList = new ArrayList<>();
        ArrayList<String> matchClassCode = new ArrayList<>();
        for (FundClass mutualFund : fundClass) {
            if (mutualFund.getFundClassCode().equals("090")) {
                continue;
            }
            boolean isNotMatchMutualFundAndSuggestAllocation = true;
            for (FundSuggestAllocationList suggestFundList : fundAllocationResponse.getFundSuggestAllocationList()) {
                if (mutualFund.getFundClassCode().equals(suggestFundList.getFundClassCode())
                ) {
                    matchClassCode.add(mutualFund.getFundClassCode());
                    mutualFundWithFundSuggestedAllocationList.add(MutualFundWithFundSuggestedAllocation.builder()
                            .fundClassCode(mutualFund.getFundClassCode())
                            .fundClassNameTh(mutualFund.getFundClassNameTH())
                            .fundClassNameEn(mutualFund.getFundClassNameEN())
                            .fundClassPercent(mutualFund.getFundClassPercent())
                            .recommendedPercent(suggestFundList.getRecommendedPercent())
                            .fundSuggestionList(suggestFundList.getFundList().stream()
                                    .map(fl -> SubFundSuggestion.builder()
                                            .fundShortName(fl.getFundShortName())
                                            .fundCode(fl.getFundCode())
                                            .fundPercent(fl.getFundPercent())
                                            .build())
                                    .collect(Collectors.toList()))
                            .build()
                    );
                    isNotMatchMutualFundAndSuggestAllocation = false;
                }
            }
            if (isNotMatchMutualFundAndSuggestAllocation) {
                mutualFundWithFundSuggestedAllocationList.add(MutualFundWithFundSuggestedAllocation.builder()
                        .fundClassCode(mutualFund.getFundClassCode())
                        .fundClassNameTh(mutualFund.getFundClassNameTH())
                        .fundClassNameEn(mutualFund.getFundClassNameEN())
                        .fundClassPercent(mutualFund.getFundClassPercent())
                        .recommendedPercent("-")
                        .fundSuggestionList(null)
                        .build()
                );
            }
        }

        mutualFundWithFundSuggestedAllocationList.addAll(fundAllocationResponse.getFundSuggestAllocationList().stream()
                .filter(fl -> !matchClassCode.contains(fl.getFundClassCode()))
                .map(fl -> MutualFundWithFundSuggestedAllocation.builder()
                        .fundClassCode(fl.getFundClassCode())
                        .fundClassNameEn(fl.getFundClassNameEn())
                        .fundClassNameTh(fl.getFundClassNameTh())
                        .fundClassPercent("-")
                        .recommendedPercent(fl.getRecommendedPercent())
                        .fundSuggestionList(fl.getFundList().stream()
                                .map(fle -> SubFundSuggestion.builder()
                                        .fundShortName(fle.getFundShortName())
                                        .fundCode(fle.getFundCode())
                                        .fundPercent(fle.getFundPercent())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList()));
        return mutualFundWithFundSuggestedAllocationList;
    }

    private boolean isPortfolioListEmpty(TmbOneServiceResponse<FundSummaryByPortBody> fundSummaryByPort) {
        return fundSummaryByPort == null || fundSummaryByPort.getData() == null
                 || fundSummaryByPort.getData().getPortfolioList().isEmpty();
    }

    private boolean isIndividualAccountExist(TmbOneServiceResponse<FundSummaryByPortBody> fundSummaryByPort) {
        List<PortfolioByPort> portfolioList = fundSummaryByPort.getData().getPortfolioList();
        return portfolioList.stream()
                .filter(portfolioByPort -> portfolioByPort.getPortfolioNumber().startsWith("PT"))
                .anyMatch(portfolioByPort -> INVESTMENT_JOINT_FLAG_INDIVIDUAL.equals(portfolioByPort.getJointFlag()));
    }
}