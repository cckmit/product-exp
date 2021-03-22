package com.tmb.oneapp.productsexpservice.service;

import com.google.common.base.Strings;
import com.tmb.common.exception.model.TMBCommonException;
import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.HirePurchaseExperienceClient;
import com.tmb.oneapp.productsexpservice.feignclients.LendingServiceClient;
import com.tmb.oneapp.productsexpservice.model.LoanData;
import com.tmb.oneapp.productsexpservice.model.LoanDetails;
import com.tmb.oneapp.productsexpservice.model.request.LoanStatusRequest;
import com.tmb.oneapp.productsexpservice.model.response.statustracking.LendingRslStatusResponse;
import com.tmb.oneapp.productsexpservice.util.UtilMap;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant.HIRE_PURCHASE_DATA_NOT_FOUND;
import static com.tmb.oneapp.productsexpservice.constant.ResponseCode.DATA_NOT_FOUND_ERROR;

@Service
public class AsyncApplicationStatusService {
    private static final TMBLogger<AsyncApplicationStatusService> logger =
            new TMBLogger<>(AsyncApplicationStatusService.class);
    private final HirePurchaseExperienceClient hirePurchaseExperienceClient;
    private final LendingServiceClient lendingServiceClient;

    public AsyncApplicationStatusService(HirePurchaseExperienceClient hirePurchaseExperienceClient,
                                         LendingServiceClient lendingServiceClient) {
        this.hirePurchaseExperienceClient = hirePurchaseExperienceClient;
        this.lendingServiceClient = lendingServiceClient;
    }

    @Async
    public CompletableFuture<List<LoanDetails>> getHpData(String correlationId, String language, String nationalId, String mobileNo) throws TMBCommonException {
        try {
            logger.info("Starting HP Async");
            logger.info("Calling GET /apis/hpservice/loan-status/application-list");
            List<LoanDetails> applicationList = getHPApplicationList(correlationId, language, nationalId, mobileNo);
            logger.info("GET /apis/hpservice/loan-status/application-list response : {}", applicationList);

            logger.info("Calling GET /apis/hpservice/loan-status/application-detail");
            List<LoanDetails> applicationDetailList = asyncGetHPApplicationDetails(applicationList, correlationId, nationalId, language, mobileNo);
            logger.info("GET /apis/hpservice/loan-status/application-detail : {}", applicationDetailList);

            return CompletableFuture.completedFuture(applicationDetailList);
        } catch (Exception e) {
            logger.error("Error while retrieving high purchase data: {}", e);
            return null;
        }
    }

    /**
     * Get customerProfileData
     *
     * @param correlationId correlationId
     * @param language      language of response
     * @param nationalId    national ID of customer
     * @return CustomerProfileResponseData customer data
     */
    @SuppressWarnings("squid:S3655")
    private List<LoanDetails> getHPApplicationList(String correlationId, String language, String nationalId, String mobileNo)
            throws TMBCommonException {
        LoanStatusRequest listRequest = new LoanStatusRequest()
                .setNID(nationalId)
                .setLanguage(language)
                .setMobileNo(Strings.isNullOrEmpty(mobileNo) ? null : mobileNo);
        logger.info("GET /apis/hpservice/loan-status/application-list request: {}", listRequest);

        try {
            ResponseEntity<TmbOneServiceResponse<LoanData>> applicationListResponse =
                    hirePurchaseExperienceClient.postLoanStatusApplicationList(correlationId, listRequest);
            return Objects.requireNonNull(applicationListResponse.getBody()).getData().getWSRecord();

        } catch (FeignException e) {
            String respBody = null;
            if (e.responseBody().isPresent()) {
                respBody = StandardCharsets.UTF_8.decode(e.responseBody().get()).toString(); //NO SONAR
            }

            if (respBody != null && respBody.contains(HIRE_PURCHASE_DATA_NOT_FOUND)) {
                logger.info("Data not found while calling GET /apis/hpservice/loan-status/application-list");
                return new ArrayList<>();
            } else {
                logger.error("Unexpected error occurred while calling GET /apis/hpservice/loan-status/application-list: {}", e);
                throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                        ResponseCode.FAILED.getMessage(),
                        ResponseCode.FAILED.getService(), HttpStatus.BAD_REQUEST, null);
            }
        }

    }

    private LoanData postLoanStatusApplicationDetail(String correlationId, String nationalId,
                                                     String language, String applicationNumber, String mobileNo) {
        ResponseEntity<TmbOneServiceResponse<LoanData>> response =
                hirePurchaseExperienceClient.postLoanStatusApplicationDetail(correlationId,
                        new LoanStatusRequest()
                                .setAppNo(applicationNumber)
                                .setProviderUserID(nationalId)
                                .setLanguage(language)
                                .setMobileNo(mobileNo));

        return Objects.requireNonNull(response.getBody()).getData();
    }

    private List<LoanDetails> asyncGetHPApplicationDetails(
            List<LoanDetails> applicationList, String correlationId, String nationalId, String language, String mobileNo) throws TMBCommonException {
        try {
            List<CompletableFuture<LoanData>> completableFutures = new ArrayList<>(); //List to hold all the completable futures
            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

            for (LoanDetails application : applicationList) {
                CompletableFuture<LoanData> requestCompletableFuture = CompletableFuture
                        .supplyAsync(
                                () -> postLoanStatusApplicationDetail(correlationId, nationalId,
                                        language, application.getAppNo(), Strings.isNullOrEmpty(mobileNo) ? null : mobileNo),
                                executor
                        );

                completableFutures.add(requestCompletableFuture);
            }

            CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0]))
                    .join();

            List<LoanDetails> loanDetailsList = new ArrayList<>();

            for (CompletableFuture<LoanData> loanDataCompletableFuture : completableFutures) {
                loanDetailsList.addAll(loanDataCompletableFuture.get().getWSRecord());
            }

            return loanDetailsList;
        } catch (Exception e) {
            logger.error("Error occurred while calling async POST /apis/products/loan-status/application-detail : {}", e);
            throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                    ResponseCode.FAILED.getMessage(),
                    ResponseCode.FAILED.getService(), HttpStatus.BAD_REQUEST, null);
        }

    }

    @Async
    public CompletableFuture<List<LendingRslStatusResponse>> getRSLData(String correlationId, String nationalId, String mobileNo)
            throws TMBCommonException {
        try {
            logger.info("Starting RSL Async");

            //PENDING // NEW ENDPOINT -> SUMO
            //GET /apis/lending-service/rsl/status   Lending Application List LENDING SERVICE
            List<LendingRslStatusResponse> rslApplications = getLendingRslStatus(correlationId, nationalId, mobileNo);

            //Map RSL data
            return CompletableFuture.completedFuture(rslApplications);
        } catch (Exception e) {
            logger.error("Error while retrieving RSL data: {}", e);
            return null;
        }

    }

    @SuppressWarnings("all")
    private List<LendingRslStatusResponse> getLendingRslStatus(String correlationId, String nationalId, String mobileNo)
            throws TMBCommonException {
        try {
            logger.info("Calling GET /apis/lending-service/rsl/status");
            ResponseEntity<TmbOneServiceResponse<List<LendingRslStatusResponse>>> response =
                    lendingServiceClient.getLendingRslStatus(correlationId, nationalId, mobileNo);
            logger.info("GET /apis/lending-service/rsl/status response: {}", response);

            return Objects.requireNonNull(response.getBody()).getData();

        } catch (FeignException e) {
            TmbOneServiceResponse response = UtilMap.mapTmbOneServiceResponse(e.responseBody()); // NO SONAR

            if (response != null && response.getStatus() != null && DATA_NOT_FOUND_ERROR.getCode().equals(response.getStatus().getCode())) {
                logger.info("Data not found while calling GET /apis/lending-service/rsl/status. crmId: {}, nationalId {}, mobileNo: {}",
                        correlationId, nationalId, mobileNo);
                return new ArrayList<>();
            } else {
                logger.error("Unexpected error occured while calling GET /apis/lending-service/rsl/status: {}", e);
                throw new TMBCommonException(ResponseCode.FAILED.getCode(),
                        ResponseCode.FAILED.getMessage(),
                        ResponseCode.FAILED.getService(), HttpStatus.BAD_REQUEST, null);
            }
        }

    }


}
