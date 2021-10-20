package com.tmb.oneapp.productsexpservice.service.productexperience.fund;

import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.LogAround;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.request.UnitHolder;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.byport.FundSummaryByPortBody;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.byport.PortfolioByPort;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.portfolio.response.PortfolioResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.fund.portfolio.response.PortfolioResponseBody;
import com.tmb.oneapp.productsexpservice.service.ProductsExpService;
import com.tmb.oneapp.productsexpservice.service.productexperience.TmbErrorHandle;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.*;

/**
 * PortfolioService class will get portfolio list from api services, and filter it with type from request
 */
@Service
public class PortfolioService extends TmbErrorHandle {

    private static final TMBLogger<PortfolioService> logger = new TMBLogger<>(PortfolioService.class);

    private final ProductsExpService productsExpService;

    private final InvestmentRequestClient investmentRequestClient;

    @Autowired
    public PortfolioService(ProductsExpService productsExpService, InvestmentRequestClient investmentRequestClient) {
        this.productsExpService = productsExpService;
        this.investmentRequestClient = investmentRequestClient;
    }

    /**
     * Method getPortfolioList to call MF Service account saving and fund summary by port
     *
     * @param correlationId
     * @param crmId
     * @param type
     * @return PortfolioResponse
     */
    @LogAround
    public PortfolioResponse getPortfolioList(String correlationId, String crmId, String type) throws TMBCommonException {
        List<PortfolioByPort> portfolioByPortList;
        Map<String, String> headerParameter = UtilMap.createHeader(correlationId);

        try {
            List<String> ports = productsExpService.getPortList(headerParameter, crmId, false);
            UnitHolder unitHolder = new UnitHolder();
            unitHolder.setUnitHolderNumber(ports.stream().map(String::valueOf).collect(Collectors.joining(",")));

            if (ports.isEmpty()) {
                return PortfolioResponse.builder().portfolioResponseBody(List.of()).build();
            }
            ResponseEntity<TmbOneServiceResponse<FundSummaryByPortBody>> summaryByPortResponse = investmentRequestClient.callInvestmentFundSummaryByPortService(headerParameter, unitHolder);

            List<PortfolioByPort> portfolioList = summaryByPortResponse.getBody().getData().getPortfolioList();
            portfolioByPortList = filterTypeOfPortfolioByPorts(type, portfolioList);
            List<PortfolioResponseBody> portfolioResponseBodyList = buildPortfolioResponseBodyList(portfolioByPortList);
            return PortfolioResponse.builder().portfolioResponseBody(portfolioResponseBodyList).build();

        } catch (FeignException feignException) {
            handleFeignException(feignException);
        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURRED, ex);
        }
        return null;
    }

    private List<PortfolioByPort> filterTypeOfPortfolioByPorts(String type, List<PortfolioByPort> portfolioList) {
        List<PortfolioByPort> portfolioByPortList;
        if (INVESTMENT_PORTFOLIO_JOINT_TYPE.equals(type)) {
            portfolioByPortList = portfolioList.stream().filter(portfolioByPort ->
                    INVESTMENT_JOINT_FLAG_JOINT.equals(portfolioByPort.getJointFlag()))
                    .collect(Collectors.toList());
        } else if (INVESTMENT_PORTFOLIO_NORMAL_TYPE.equals(type)) {
            portfolioByPortList = portfolioList.stream().filter(portfolioByPort ->
                    INVESTMENT_JOINT_FLAG_INDIVIDUAL.equals(portfolioByPort.getJointFlag()))
                    .collect(Collectors.toList());
        } else {
            portfolioByPortList = portfolioList;
        }
        return portfolioByPortList;
    }

    private List<PortfolioResponseBody> buildPortfolioResponseBodyList(List<PortfolioByPort> portfolioList) {
        return portfolioList.stream()
                .map(portfolioByPort -> PortfolioResponseBody.builder()
                        .portfolioNumber(portfolioByPort.getPortfolioNumber())
                        .nickname(portfolioByPort.getNickName())
                        .jointFlag(portfolioByPort.getJointFlag())
                        .build()).collect(Collectors.toList());
    }
}
