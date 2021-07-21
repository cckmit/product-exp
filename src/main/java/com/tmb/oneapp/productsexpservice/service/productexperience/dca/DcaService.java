package com.tmb.oneapp.productsexpservice.service.productexperience.dca;

import com.tmb.common.logger.TMBLogger;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.dto.aip.validation.DcaValidationDto;
import com.tmb.oneapp.productsexpservice.mapper.dca.DcaMapper;
import com.tmb.oneapp.productsexpservice.model.productexperience.aip.request.AipValidationRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.aip.response.AipValidationResponseBody;
import com.tmb.oneapp.productsexpservice.model.productexperience.dca.validation.request.DcaValidationRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.transaction.request.TransactionValidationRequest;
import com.tmb.oneapp.productsexpservice.model.productexperience.transaction.response.TransactionValidationResponseBody;
import com.tmb.oneapp.productsexpservice.service.productexperience.async.InvestmentAsyncService;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class DcaService {

    private static final TMBLogger<DcaService> logger = new TMBLogger<>(DcaService.class);

    private InvestmentAsyncService investmentAsyncService;

    private DcaMapper dcaMapper;

    @Autowired
    public DcaService(InvestmentAsyncService investmentAsyncService, DcaMapper dcaMapper) {
        this.investmentAsyncService = investmentAsyncService;
        this.dcaMapper = dcaMapper;
    }

    /**
     * Method getValidation to call MF Service transaction validation and aip validation
     *
     * @param correlationId
     * @param crmId
     * @param dcaValidationRequest
     * @return DcaValidationDto
     */
    public DcaValidationDto getValidation(String correlationId, String crmId, DcaValidationRequest dcaValidationRequest) {
        Map<String, String> investmentRequestHeader = UtilMap.createHeader(correlationId);
        try {
            TransactionValidationRequest transactionValidationRequest = dcaMapper.dcaValidationRequestToTransactionValidationRequest(dcaValidationRequest);
            CompletableFuture<TransactionValidationResponseBody> fetchTransactionValidation = investmentAsyncService.fetchTransactionValidation(investmentRequestHeader, crmId, transactionValidationRequest);

            AipValidationRequest aipValidationRequest = dcaMapper.dcaValidationRequestToAipValidationRequest(dcaValidationRequest);
            CompletableFuture<AipValidationResponseBody> fetchAipValidation = investmentAsyncService.fetchAipValidation(investmentRequestHeader, aipValidationRequest);
            CompletableFuture.allOf(fetchTransactionValidation, fetchAipValidation);
            return DcaValidationDto.builder()
                    .transactionValidation(fetchTransactionValidation.get())
                    .aipValidation(fetchAipValidation.get())
                    .build();
        } catch (Exception ex) {
            logger.error(ProductsExpServiceConstant.EXCEPTION_OCCURED, ex);
            return null;
        }
    }
}
