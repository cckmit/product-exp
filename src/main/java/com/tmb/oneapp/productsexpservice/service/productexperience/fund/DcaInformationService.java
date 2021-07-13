package com.tmb.oneapp.productsexpservice.service.productexperience.fund;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.dto.fund.dcainformation.DcaInformationDto;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerExpServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.InvestmentRequestClient;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.request.UnitHolder;
import com.tmb.oneapp.productsexpservice.model.fundsummarydata.response.fundsummary.*;
import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundClassListInfo;
import com.tmb.oneapp.productsexpservice.model.response.fundlistinfo.FundListBody;
import com.tmb.oneapp.productsexpservice.service.ProductsExpService;
import com.tmb.oneapp.productsexpservice.util.TmbStatusUtil;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DcaInformationService {

    private static final TMBLogger<DcaInformationService> logger = new TMBLogger<>(DcaInformationService.class);

    private final CustomerExpServiceClient customerExpServiceClient;

    private final InvestmentRequestClient investmentRequestClient;

    private final ProductsExpService productsExpService;

    @Autowired
    public DcaInformationService(CustomerExpServiceClient customerExpServiceClient,
                                 InvestmentRequestClient investmentRequestClient,
                                 ProductsExpService productsExpService) {
        this.customerExpServiceClient = customerExpServiceClient;
        this.investmentRequestClient = investmentRequestClient;
        this.productsExpService = productsExpService;
    }

    public TmbOneServiceResponse<DcaInformationDto> getDcaInformation(String correlationId, String crmId) {

        TmbOneServiceResponse<DcaInformationDto> dcaInformationDto = new TmbOneServiceResponse<>();
        try{
            Map<String, String> invHeaderReqParameter = UtilMap.createHeader(correlationId);
            List<String> portList = productsExpService.getPortList(crmId,invHeaderReqParameter,false);
            UnitHolder unitHolder = new UnitHolder();
            unitHolder.setUnitHolderNo(portList.stream().collect(Collectors.joining(",")));
            ResponseEntity<TmbOneServiceResponse<FundSummaryResponse>> fundSummaryResponse = investmentRequestClient.callInvestmentFundSummaryService(invHeaderReqParameter,
                    unitHolder);
            ResponseEntity<TmbOneServiceResponse<FundListBody>> fundListBody = investmentRequestClient.callInvestmentFundListInfoService(invHeaderReqParameter);
            return mappingDcaInformationDto(fundSummaryResponse,fundListBody,dcaInformationDto);
        }catch (Exception ex){
            logger.error("error : {}",ex);
            dcaInformationDto.setStatus(null);
            dcaInformationDto.setData(null);
            return dcaInformationDto;
        }
    }

    private TmbOneServiceResponse<DcaInformationDto> mappingDcaInformationDto(ResponseEntity<TmbOneServiceResponse<FundSummaryResponse>> fundSummaryResponse, ResponseEntity<TmbOneServiceResponse<FundListBody>> fundListBody, TmbOneServiceResponse<DcaInformationDto> dcaInformationDto) {
        FundSummaryBody fundSummaryBody = fundSummaryResponse.getBody().getData().getBody();
        List<FundClass> fundClass = fundSummaryBody.getFundClassList().getFundClass();
        List<FundHouse> fundHouseList = fundClass.stream()
                .map(x -> x.getFundHouseList())
                .flatMap(x -> x.stream()).collect(Collectors.toList());
        List<Fund> fundList = fundHouseList.stream().map(t -> t.getFundList().getFund()).flatMap(t -> t.stream()).collect(Collectors.toList());
        List<FundClassListInfo> fundClassListInfos = fundListBody.getBody().getData().getFundClassList();
        List<FundClassListInfo> dcaList = fundClassListInfos.stream().filter(
                t -> t.getAllowAipFlag().equals(ProductsExpServiceConstant.APPLICATION_STATUS_FLAG_TRUE)
                        && fundList.stream().map(Fund::getFundCode).anyMatch(code -> code.equals(t.getFundCode()))).collect(Collectors.toList());
        dcaInformationDto.setStatus(TmbStatusUtil.successStatus());
        dcaInformationDto.setData(DcaInformationDto.builder().fundClassList(dcaList).build());
        return dcaInformationDto;
    }


}
