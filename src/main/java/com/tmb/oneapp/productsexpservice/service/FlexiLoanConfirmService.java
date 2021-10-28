package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.CommonData;
import com.tmb.common.model.RslCode;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.legacy.rsl.common.ob.apprmemo.creditcard.ApprovalMemoCreditCard;
import com.tmb.common.model.legacy.rsl.common.ob.apprmemo.facility.ApprovalMemoFacility;
import com.tmb.common.model.legacy.rsl.common.ob.creditcard.CreditCard;
import com.tmb.common.model.legacy.rsl.common.ob.facility.Facility;
import com.tmb.common.model.legacy.rsl.common.ob.individual.Individual;
import com.tmb.common.model.legacy.rsl.common.ob.pricing.Pricing;
import com.tmb.common.model.legacy.rsl.ws.application.response.ResponseApplication;
import com.tmb.common.model.legacy.rsl.ws.creditcard.response.ResponseCreditcard;
import com.tmb.common.model.legacy.rsl.ws.facility.response.ResponseFacility;
import com.tmb.common.model.legacy.rsl.ws.individual.response.ResponseIndividual;
import com.tmb.common.model.legacy.rsl.ws.instant.calculate.uw.request.Body;
import com.tmb.common.model.legacy.rsl.ws.instant.calculate.uw.request.RequestInstantLoanCalUW;
import com.tmb.common.model.legacy.rsl.ws.instant.calculate.uw.response.ResponseInstantLoanCalUW;
import com.tmb.oneapp.productsexpservice.constant.RslResponseCodeEnum;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.RSLProductCodeEnum;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.SFTPClientImp;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.*;
import com.tmb.oneapp.productsexpservice.model.SFTPStoreFileInfo;
import com.tmb.oneapp.productsexpservice.model.request.flexiloan.FlexiLoanConfirmRequest;
import com.tmb.oneapp.productsexpservice.model.request.notification.FlexiLoanSubmissionWrapper;
import com.tmb.oneapp.productsexpservice.model.response.flexiloan.*;
import com.tmb.oneapp.productsexpservice.model.response.loan.LoanCustomerPricing;
import io.netty.util.internal.StringUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.fop.apps.FOPException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.xml.rpc.ServiceException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@AllArgsConstructor
public class FlexiLoanConfirmService {
    private static final TMBLogger<FlexiLoanConfirmService> logger = new TMBLogger<>(FlexiLoanConfirmService.class);

    private final LoanSubmissionGetFacilityInfoClient getFacilityInfoClient;
    private final LoanSubmissionGetCustomerInfoClient getCustomerInfoClient;
    private final LoanSubmissionGetCreditCardInfoClient getCreditCardInfoClient;
    private final LoanSubmissionGetApplicationInfoClient getApplicationInfoClient;
    private final LoanSubmissionInstantLoanCalUWClient instantLoanCalUWClient;
    private final NotificationService notificationService;
    private final LoanSubmissionInstantLoanSubmitApplicationClient submitApplicationClient;
    private final CommonServiceClient commonServiceClient;
    private final FileGeneratorService fileGeneratorService;
    private final SFTPClientImp sftpClientImp;

    private static final String E_APP_TEMPLATE = "fop/e_app.xsl";

    public FlexiLoanConfirmResponse confirm(Map<String, String> requestHeaders, FlexiLoanConfirmRequest request) throws FOPException, IOException, TransformerException, ServiceException, ParseException {

        ResponseApplication applicationResp = getApplicationInfo(request.getCaID());
        String appRefNo = applicationResp.getBody().getAppRefNo();

        FlexiLoanConfirmResponse response = parseFlexiLoanConfirmResponse(request.getCaID(), request.getProductCode());
        FlexiLoanSubmissionWrapper wrapper = parseFlexiLoanSubmissionWrapper(response, request, appRefNo);

        String fileName = parseCompletePDFFileName(appRefNo);
        String filePath = generateFlexiLoanConfirmReport(wrapper, fileName);
        storeEAppFile(requestHeaders, appRefNo, filePath);

        List<String> notificationAttachments = new ArrayList<>();

        String letterOfConsentAttachments = getLetterOfConsentFilePath(appRefNo, applicationResp);

        List<RslCode> rslConfigs = getRslConfig(requestHeaders.get(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID));
        if (!rslConfigs.isEmpty()) {
            String saleSheetAttachments = getSaleSheetFilePath(rslConfigs);
            String termAndConditionAttachments = getTermAndConditionFilePath(rslConfigs);

            notificationAttachments.add(saleSheetAttachments);
            notificationAttachments.add(termAndConditionAttachments);
        }

        String eAppAttachments = String.format("sftp://%s/users/enotiftp/SIT/MIB/TempAttachments/%s.pdf", sftpClientImp.getRemoteHost(), fileName);

        notificationAttachments.add(eAppAttachments);
        notificationAttachments.add(letterOfConsentAttachments);

        wrapper.setEmail("oranuch@odds.team");
        sendNotification(requestHeaders, wrapper);

        return response;
    }


    private String parseRate(Pricing pricing) {
        if (StringUtil.isNullOrEmpty(pricing.getRateType())) {
            return String.format("%.2f", pricing.getRateVaraince().multiply(BigDecimal.valueOf(100)));
        } else {
            return String.format("%s %s %.2f", pricing.getRateType(), pricing.getPercentSign(), pricing.getRateVaraince().multiply(BigDecimal.valueOf(100)));
        }
    }

    private String generateFlexiLoanConfirmReport(FlexiLoanSubmissionWrapper wrapper, String fileName) throws FOPException, IOException, TransformerException {
        return fileGeneratorService.generateFlexiLoanSubmissionPdf(wrapper, fileName, E_APP_TEMPLATE);
    }

    private void storeEAppFile(Map<String, String> requestHeaders, String appRefNo, String eAppFilePath) {
        storeFileOnSFTP("/users/mibuser", "u01/datafile/mib/mibshare/ApplyLoan/" + requestHeaders.get(ProductsExpServiceConstant.X_CRMID) + "/" + appRefNo, eAppFilePath);
        storeFileOnSFTP("/users/mibuser", "u01/datafile/mib/mibshare", eAppFilePath);
        storeFileOnSFTP("/users/enotiftp/SIT/MIB", "TempAttachments", eAppFilePath);
    }

    private void storeFileOnSFTP(String rootPath, String dstDir, String srcFile) {
        SFTPStoreFileInfo sftpStoreFileInfo = new SFTPStoreFileInfo();
        sftpStoreFileInfo.setRootPath(rootPath);
        sftpStoreFileInfo.setDstDir(dstDir);
        sftpStoreFileInfo.setSrcFile(srcFile);
        List<SFTPStoreFileInfo> sftpClientImpList = new ArrayList<>();
        sftpClientImpList.add(sftpStoreFileInfo);
        sftpClientImp.storeFile(sftpClientImpList);
    }

    private FlexiLoanConfirmResponse parseFlexiLoanConfirmResponse(Long caId, String productCode) throws ServiceException, RemoteException {
        ResponseInstantLoanCalUW loanCalUWResponse = getInstantLoanCalUW(BigDecimal.valueOf(caId), "N");
        Individual individualInfo = getCustomer(caId);
        SubmissionCustomerInfo customerInfo = parseSubmissionCustomerInfo(individualInfo);
        if (!isTypeCC(productCode)) {
            Facility facilityInfo = getFacility(caId);
            SubmissionPaymentInfo paymentInfo = parseSubmissionPaymentInfo(facilityInfo, individualInfo, null, loanCalUWResponse, productCode);
            SubmissionPricingInfo pricingInfo = parseSubmissionPricingInfo(facilityInfo.getPricings());
            SubmissionReceivingInfo receivingInfo = parseSubmissionReceivingInfo(facilityInfo);
            return setResponse(paymentInfo, receivingInfo, customerInfo, pricingInfo);
        }
        CreditCard creditCardInfo = getCreditCard(caId, productCode);
        SubmissionPaymentInfo paymentInfo = parseSubmissionPaymentInfo(null, individualInfo, creditCardInfo, loanCalUWResponse, productCode);
        SubmissionPricingInfo pricingInfo = parseSubmissionPricingInfo(creditCardInfo.getPricings());
        return setResponse(paymentInfo, null, customerInfo, pricingInfo);
    }

    private FlexiLoanConfirmResponse setResponse(SubmissionPaymentInfo paymentInfo, SubmissionReceivingInfo receivingInfo,
                                                 SubmissionCustomerInfo customerInfo, SubmissionPricingInfo pricingInfo) {
        FlexiLoanConfirmResponse response = new FlexiLoanConfirmResponse();
        response.setPaymentInfo(paymentInfo);
        response.setCustomerInfo(customerInfo);
        response.setPricingInfo(pricingInfo);
        response.setReceivingInfo(receivingInfo);
        return response;
    }

    private FlexiLoanSubmissionWrapper parseFlexiLoanSubmissionWrapper(FlexiLoanConfirmResponse response, FlexiLoanConfirmRequest request, String appRefNo) throws ParseException {
        FlexiLoanSubmissionWrapper wrapper = new FlexiLoanSubmissionWrapper();
        wrapper.setProductCode(request.getProductCode());
        wrapper.setFeatureType(response.getPaymentInfo().getFeatureType());
        wrapper.setAppRefNo(appRefNo);
        wrapper.setProductName(request.getProductNameTH());
        wrapper.setCustomerName(response.getCustomerInfo().getName());
        wrapper.setIdCardNo(response.getCustomerInfo().getCitizenId());
        wrapper.setFinalLoanAmount(parseNumberFormat(response.getPaymentInfo().getCreditLimit()));
        wrapper.setTenor(response.getPaymentInfo().getTenure());
        wrapper.setRequestAmount(parseNumberFormat(response.getPaymentInfo().getRequestAmount()));
        wrapper.setPaymentMethod(response.getPaymentInfo().getPaymentMethod());
        wrapper.setEmail(response.getPaymentInfo().getEStatement());
        wrapper.setBotAnswer1("-");
        wrapper.setBotAnswer2("-");
        wrapper.setDisburseAccountNo(response.getPaymentInfo().getDisburstAccountNo());
        if (request.getProductCode().contains("C2G")) {
            wrapper.setDueDate(parseDateThaiFormat(response.getPaymentInfo().getPayDate()));
            wrapper.setApplyDate(parseDateThaiFormat(response.getPaymentInfo().getPayDate()));
        }
        wrapper.setFirstPaymentDueDate(response.getPaymentInfo().getFirstPaymentDueDate());
        wrapper.setNextPaymentDueDate(response.getPaymentInfo().getNextPaymentDueDate());
        wrapper.setInterestRate(parseNumberFormat(response.getPaymentInfo().getInterestRate()));
        wrapper.setInstallment(parseNumberFormat(response.getPaymentInfo().getInstallmentAmount()));
        if (response.getPaymentInfo().getLoanContractDate() != null) {
            wrapper.setConsentDate(parseDateThaiFormat(response.getPaymentInfo().getLoanContractDate().getTime()));
        }
        wrapper.setNcbConsentFlag("Y");
        wrapper.setCashDisbursement(parseNumberFormat(response.getPaymentInfo().getOutStandingBalance()));
        if (request.getProductCode().contains("RC") || request.getProductCode().contains("C2G")) {
            wrapper.setCurrentLoan(parseNumberFormat(response.getReceivingInfo().getOsLimit()));
            wrapper.setCurrentAccount(response.getReceivingInfo().getHostAcfNo());
        }
        wrapper.setRateTypeValue(response.getPaymentInfo().getRateType());
        wrapper.setUnderwriting(response.getPaymentInfo().getUnderwriting());

        return wrapper;
    }

    private boolean isTypeCC(String productCode) {
        return productCode.equals("VM") || productCode.equals("VC")
                || productCode.equals("VG") || productCode.equals("VP")
                || productCode.equals("VT") || productCode.equals("VJ")
                || productCode.equals("VH") || productCode.equals("VI")
                || productCode.equals("VB")
                || productCode.equals("MT") || productCode.equals("MS");
    }

    private SubmissionPaymentInfo parseSubmissionPaymentInfo(Facility facilityInfo, Individual customerInfo, CreditCard creditCardInfo, ResponseInstantLoanCalUW loanCalUWResponse, String productCode) {
        SubmissionPaymentInfo paymentInfo = new SubmissionPaymentInfo();

        if (Objects.nonNull(loanCalUWResponse.getBody().getApprovalMemoFacilities()) && !isTypeCC(productCode)) {

            mapPaymentFromFacility(paymentInfo, facilityInfo, loanCalUWResponse, productCode);

        } else if (Objects.nonNull(loanCalUWResponse.getBody().getApprovalMemoCreditCards()) && isTypeCC(productCode)) {

            mapPaymentFromCredit(paymentInfo, creditCardInfo, loanCalUWResponse);
        }
        if (Objects.nonNull(customerInfo)) {
            paymentInfo.setEStatement(customerInfo.getEmail());
        }

        return paymentInfo;
    }


    private SubmissionPaymentInfo mapPaymentFromFacility(SubmissionPaymentInfo paymentInfo, Facility facilityInfo, ResponseInstantLoanCalUW loanCalUWResponse, String productCode) {
        ApprovalMemoFacility approvalMemoFacility = loanCalUWResponse.getBody().getApprovalMemoFacilities()[0];
        paymentInfo.setInterestRate(approvalMemoFacility.getInterestRate());
        paymentInfo.setRateTypePercent(approvalMemoFacility.getRateTypePercent());
        paymentInfo.setUnderwriting(approvalMemoFacility.getUnderwritingResult());
        paymentInfo.setCreditLimit(approvalMemoFacility.getCreditLimit());
        paymentInfo.setInstallmentAmount(approvalMemoFacility.getInstallmentAmount());
        paymentInfo.setTenure(approvalMemoFacility.getTenor());
        paymentInfo.setDisburstAccountNo(approvalMemoFacility.getDisburstAccountNo());
        paymentInfo.setRateType(approvalMemoFacility.getRateType());
        paymentInfo.setLoanContractDate(approvalMemoFacility.getLoanContractDate());
        paymentInfo.setFirstPaymentDueDate(approvalMemoFacility.getFirstPaymentDueDate());
        paymentInfo.setPayDate(approvalMemoFacility.getPayDate());

        mapRequestAmount(paymentInfo, facilityInfo, loanCalUWResponse, approvalMemoFacility, productCode);

        if (Objects.nonNull(facilityInfo)) {
            paymentInfo.setFeatureType(facilityInfo.getFeatureType());
            paymentInfo.setOtherBank(facilityInfo.getLoanWithOtherBank());
            paymentInfo.setOtherBankInProgress(facilityInfo.getConsiderLoanWithOtherBank());
            paymentInfo.setPaymentMethod(facilityInfo.getPaymentMethod());
        }
        return paymentInfo;
    }

    private SubmissionPaymentInfo mapRequestAmount(SubmissionPaymentInfo paymentInfo, Facility facilityInfo, ResponseInstantLoanCalUW loanCalUWResponse, ApprovalMemoFacility approvalMemoFacility, String productCode) {
        if (productCode.equals(RSLProductCodeEnum.FLASH_CARD_PLUS.getProductCode())) {
            paymentInfo.setRequestAmount(facilityInfo.getFeature().getRequestAmount());
            if (facilityInfo.getFeatureType().equals("S")) {
                paymentInfo.setDisburstAccountNo(facilityInfo.getFeature().getDisbAcctNo());
            }
        } else if (productCode.equals(RSLProductCodeEnum.CASH_2_GO.getProductCode())) {
            paymentInfo.setRequestAmount(loanCalUWResponse.getBody().getApprovalMemoFacilities()[0].getCreditLimit());
        } else if (productCode.equals(RSLProductCodeEnum.CASH_2_GO_TOPUP.getProductCode())) {
            paymentInfo.setRequestAmount(loanCalUWResponse.getBody().getApprovalMemoFacilities()[0].getOutstandingBalance());
            paymentInfo.setOutStandingBalance(approvalMemoFacility.getOutstandingBalance());
        }
        return paymentInfo;
    }

    private SubmissionPaymentInfo mapPaymentFromCredit(SubmissionPaymentInfo paymentInfo, CreditCard creditCardInfo, ResponseInstantLoanCalUW loanCalUWResponse) {
        ApprovalMemoCreditCard approvalMemoCreditCard = loanCalUWResponse.getBody().getApprovalMemoCreditCards()[0];
        paymentInfo.setLoanContractDate(approvalMemoCreditCard.getLoanContractDate());
        paymentInfo.setCreditLimit(approvalMemoCreditCard.getCreditLimit());

        if (Objects.nonNull(creditCardInfo)) {
            paymentInfo.setFeatureType(creditCardInfo.getFeatureType());
            paymentInfo.setPaymentMethod(creditCardInfo.getPaymentMethod());
        }
        return paymentInfo;
    }

    private SubmissionPricingInfo parseSubmissionPricingInfo(Pricing[] pricings) {
        SubmissionPricingInfo pricingInfo = new SubmissionPricingInfo();
        List<LoanCustomerPricing> pricingList = new ArrayList<>();
        if (Objects.nonNull(pricings)) {
            for (Pricing p : pricings) {
                LoanCustomerPricing customerPricing = new LoanCustomerPricing();
                customerPricing.setMonthFrom(p.getMonthFrom());
                customerPricing.setMonthTo(p.getMonthTo());
                customerPricing.setYearFrom(p.getYearFrom());
                customerPricing.setYearTo(p.getYearTo());
                customerPricing.setRateVariance(p.getRateVaraince().multiply(BigDecimal.valueOf(100)));
                customerPricing.setRate(parseRate(p));

                pricingList.add(customerPricing);
            }
            pricingInfo.setPricing(pricingList);
        }
        return pricingInfo;

    }

    private SubmissionReceivingInfo parseSubmissionReceivingInfo(Facility facilityInfo) {
        SubmissionReceivingInfo receiving = new SubmissionReceivingInfo();
        if (Objects.nonNull(facilityInfo)) {
            receiving.setOsLimit(facilityInfo.getOsLimit());
            receiving.setHostAcfNo(facilityInfo.getHostAcfNo());
            receiving.setDisburseAccount(String.format("TMB%s", facilityInfo.getFeature().getDisbAcctNo()));
        }
        return receiving;
    }

    private SubmissionCustomerInfo parseSubmissionCustomerInfo(Individual individualInfo) {
        SubmissionCustomerInfo submissionCustomerInfo = new SubmissionCustomerInfo();
        if (individualInfo != null) {
            submissionCustomerInfo.setName(String.format("%s %s", individualInfo.getThaiName(), individualInfo.getThaiSurName()));
            submissionCustomerInfo.setCitizenId(individualInfo.getIdNo1());
        }

        return submissionCustomerInfo;
    }


    private String parseCompletePDFFileName(String appRefNo) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String dateStr = formatter.format(date);
        dateStr = dateStr.replaceAll("[/: ]", "");
        dateStr = dateStr.substring(2);
        String docType = "00111";
        return String.format("01_%s_%s_%s", dateStr, appRefNo, docType);
    }

    private String getLetterOfConsentFilePath(String appRefNo, ResponseApplication application) {
        String dateStr = application.getBody().getApplicationDate();
        dateStr = dateStr.replaceAll("[-:T ]", "");
        dateStr = dateStr.substring(2, 14);
        String docType = "00111";
        String letterOfConsentFilePath = String.format("sftp://%s/users/enotiftp/SIT/MIB/TempAttachments/01_%s_%s_%s.JPG", sftpClientImp.getRemoteHost(), dateStr, appRefNo, docType);
        logger.info("letterOfConsentFilePath: {}", letterOfConsentFilePath);
        return letterOfConsentFilePath;
    }

    private String getSaleSheetFilePath(List<RslCode> rslConfigs) {
        String saleSheetFile = rslConfigs.get(0).getSalesheetName();
        String saleSheetFilePath = String.format("sftp://%s/users/enotiftp/SIT/MIB/%s", sftpClientImp.getRemoteHost(), saleSheetFile);
        logger.info("saleSheetFilePath: {}", saleSheetFilePath);
        return saleSheetFilePath;
    }

    private String getTermAndConditionFilePath(List<RslCode> rslConfigs) {
        String tncFile = rslConfigs.get(0).getTncName();
        String tncFilePath = String.format("sftp://%s/users/enotiftp/SIT/MIB/%s", sftpClientImp.getRemoteHost(), tncFile);
        logger.info("tncFilePath: {}", tncFilePath);
        return tncFilePath;
    }


    private String parseNumberFormat(BigDecimal number) {
        return Objects.isNull(number) ? "-" : NumberFormat.getIntegerInstance().format(number);
    }

    private String parseDateThaiFormat(String dateStr) throws ParseException {
        if (StringUtils.isNotEmpty(dateStr)) {
            SimpleDateFormat formatter = new SimpleDateFormat("d MMM yy", new Locale("th", "TH"));
            Date date = formatter.parse(dateStr);
            return formatter.format(date);
        }
        return "-";
    }

    private String parseDateThaiFormat(Date date) {
        if (Objects.nonNull(date)) {
            SimpleDateFormat formatter = new SimpleDateFormat("d MMM yy", new Locale("th", "TH"));
            return formatter.format(date);
        }
        return "-";
    }


    private List<RslCode> getRslConfig(String correlationId) {
        ResponseEntity<TmbOneServiceResponse<List<CommonData>>> config = commonServiceClient.getCommonConfigByModule(correlationId, "lending_module");
        if (ResponseCode.SUCESS.getCode().equals(config.getBody().getStatus().getCode())) {
            return config.getBody().getData().get(0).getDefaultRslCode();
        }
        return new ArrayList<>();
    }

    private void sendNotification(Map<String, String> requestHeaders, FlexiLoanSubmissionWrapper wrapper) {
        try {
            notificationService.sendNotifyFlexiLoanSubmission(requestHeaders.get(ProductsExpServiceConstant.HEADER_X_CORRELATION_ID),
                    requestHeaders.get(ProductsExpServiceConstant.ACCOUNT_ID.toLowerCase()),
                    requestHeaders.get(ProductsExpServiceConstant.X_CRMID.toLowerCase()),
                    wrapper);
        } catch (Exception e) {
            logger.error("sendNotifyFlexiLoanSubmission error: {}", e);
            throw e;
        }
    }

    private ResponseApplication getApplicationInfo(long caID) throws ServiceException, RemoteException {
        ResponseApplication response = getApplicationInfoClient.getApplicationInfo(caID);
        if (RslResponseCodeEnum.SUCCESS.getCode().equals(response.getHeader().getResponseCode())) {
            return response;
        }
        throw new ExportException("get application info fail");
    }

    private ResponseInstantLoanCalUW getInstantLoanCalUW(BigDecimal caID, String triggerFlag) throws RemoteException, ServiceException {
        RequestInstantLoanCalUW request = new RequestInstantLoanCalUW();
        Body body = new Body();
        body.setCaId(caID);
        body.setTriggerFlag(triggerFlag);
        request.setBody(body);

        ResponseInstantLoanCalUW response = instantLoanCalUWClient.getCalculateUnderwriting(request);
        if (RslResponseCodeEnum.SUCCESS.getCode().equals(response.getHeader().getResponseCode())) {
            return response;
        }
        throw new ExportException("get instantLoanCalUW fail");
    }

    private Facility getFacility(Long caID) throws ServiceException, RemoteException {
        ResponseFacility response = getFacilityInfoClient.searchFacilityInfoByCaID(caID);
        if (RslResponseCodeEnum.SUCCESS.getCode().equals(response.getHeader().getResponseCode())) {
            return response.getBody().getFacilities()[0];
        }
        throw new ExportException("get facility fail");
    }

    private Individual getCustomer(Long caID) throws ServiceException, RemoteException {
        ResponseIndividual response = getCustomerInfoClient.searchCustomerInfoByCaID(caID);
        if (RslResponseCodeEnum.SUCCESS.getCode().equals(response.getHeader().getResponseCode())) {
            return response.getBody().getIndividuals()[0];
        }
        throw new ExportException("get individual fail");
    }

    private CreditCard getCreditCard(Long caID, String productCode) throws ServiceException, RemoteException {
        if (ProductsExpServiceConstant.CREDIT_CARDS_CODE.contains(productCode)) {
            ResponseCreditcard response = getCreditCardInfoClient.searchCreditcardInfoByCaID(caID);
            if (RslResponseCodeEnum.SUCCESS.getCode().equals(response.getHeader().getResponseCode())) {
                return response.getBody().getCreditCards()[0];
            }
            throw new ExportException("get credit card fail");
        }

        return new CreditCard();

    }
}
