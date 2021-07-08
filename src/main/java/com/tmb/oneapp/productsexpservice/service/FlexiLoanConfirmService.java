package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.logger.TMBLogger;
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
import com.tmb.oneapp.productsexpservice.constant.LegacyResponseCodeEnum;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.RSLProductCodeEnum;
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
    private final FileGeneratorService fileGeneratorService;
    private final SFTPClientImp sftpClientImp;

    private static final String E_APP_TEMPLATE = "fop/e_app.xsl";

    public FlexiLoanConfirmResponse confirm(Map<String, String> requestHeaders, FlexiLoanConfirmRequest request) throws ServiceException, IOException, FOPException, TransformerException, ParseException {

        ResponseApplication applicationResp = getApplicationInfo(request.getCaID());
        String appRefNo = applicationResp.getBody().getAppRefNo();

//        ResponseInstantLoanSubmit submitApplicationResp = submitApplication(BigDecimal.valueOf(request.getCaID()));

        FlexiLoanConfirmResponse response = parseFlexiLoanConfirmResponse(request.getCaID(), request.getProductCode());
        FlexiLoanSubmissionWrapper wrapper = parseFlexiLoanSubmissionWrapper(response, request, appRefNo);

        String fileName = parseCompletePDFFileName(appRefNo);
        String filePath = generateFlexiLoanConfirmReport(wrapper, fileName);
        storeEAppFile(requestHeaders, appRefNo, filePath);

        String letterOfConsentAttachments = getLetterOfConsentSFTPFilePath(appRefNo, applicationResp);

        String eAppAttachments = String.format("sftp://%s/users/enotiftp/SIT/MIB/TempAttachments/%s.pdf", sftpClientImp.getRemoteHost(), fileName);

        List<String> notificationAttachments = new ArrayList<>();
        notificationAttachments.add(eAppAttachments);
        notificationAttachments.add(letterOfConsentAttachments);

//        wrapper.setEmail("pb.noon@odds.team");
        wrapper.setEmail("oranuch@odds.team");
        sendNotification(requestHeaders, wrapper);

        return response;
    }

    private void sendNotification(Map<String, String> requestHeaders, FlexiLoanSubmissionWrapper wrapper) {
        try {
            notificationService.sendNotifyFlexiLoanSubmission(requestHeaders.get(ProductsExpServiceConstant.X_CORRELATION_ID),
                    requestHeaders.get(ProductsExpServiceConstant.ACCOUNT_ID.toLowerCase()),
                    requestHeaders.get(ProductsExpServiceConstant.X_CRMID.toLowerCase()),
                    wrapper);
        } catch (Exception e) {
            logger.error("sendNotifyFlexiLoanSubmission error: {}", e);
            throw e;
        }
    }

    private Facility getFacility(Long caID) throws ServiceException, RemoteException {
        ResponseFacility response = getFacilityInfoClient.searchFacilityInfoByCaID(caID);
        if (LegacyResponseCodeEnum.SUCCESS.getCode().equals(response.getHeader().getResponseCode())) {
            return response.getBody().getFacilities()[0];
        }
        throw new ExportException("get facility fail");
    }

    private Individual getCustomer(Long caID) throws ServiceException, RemoteException {
        ResponseIndividual response = getCustomerInfoClient.searchCustomerInfoByCaID(caID);
        if (LegacyResponseCodeEnum.SUCCESS.getCode().equals(response.getHeader().getResponseCode())) {
            return response.getBody().getIndividuals()[0];
        }
        throw new ExportException("get individual fail");
    }

    private CreditCard getCreditCard(Long caID, String productCode) throws ServiceException, RemoteException {
        if (ProductsExpServiceConstant.CREDIT_CARDS_CODE.contains(productCode)) {
            ResponseCreditcard response = getCreditCardInfoClient.searchCreditcardInfoByCaID(caID);
            if (LegacyResponseCodeEnum.SUCCESS.getCode().equals(response.getHeader().getResponseCode())) {
                return response.getBody().getCreditCards()[0];
            }
            throw new ExportException("get credit card fail");
        }

        return new CreditCard();

    }

//    private ResponseInstantLoanSubmit submitApplication(BigDecimal caID) throws Exception {
//        try {
//            ResponseInstantLoanSubmit response = submitApplicationClient.submitApplication(caID, "Y");
//            if (!LegacyResponseCodeEnum.SUCCESS.getCode().equals(response.getHeader().getResponseCode())) {
//                throw new Exception("submit application fail");
//            }
//            return response;
//        } catch (Exception e) {
//            logger.error("submissionApplication error: {}", e);
//            throw e;
//        }
//    }

    private String parseRate(Pricing pricing) {
        if (StringUtil.isNullOrEmpty(pricing.getRateType())) {
            return String.format("%.2f", pricing.getRateVaraince().multiply(BigDecimal.valueOf(100)));
        } else {
            return String.format("%s %s %.2f", pricing.getRateType(), pricing.getPercentSign(), pricing.getRateVaraince().multiply(BigDecimal.valueOf(100)));
        }
    }

    private String setPaymentMethod(String productCode, Facility facilityInfo, CreditCard creditCardInfo) {
        return ProductsExpServiceConstant.CREDIT_CARDS_CODE.contains(facilityInfo.getProductCode()) ? creditCardInfo.getPaymentMethod() : facilityInfo.getPaymentMethod();
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
        Individual individualInfo = getCustomer(caId);
        Facility facilityInfo = getFacility(caId);
        CreditCard creditCardInfo = getCreditCard(caId, productCode);
        individualInfo.setThaiName("พรพรรษา");
        individualInfo.setThaiSurName("บุญลือ");
        ResponseInstantLoanCalUW loanCalUWResponse = getInstantLoanCalUW(BigDecimal.valueOf(caId));

        SubmissionPaymentInfo paymentInfo = parseSubmissionPaymentInfo(facilityInfo, individualInfo, creditCardInfo, loanCalUWResponse, productCode);
        SubmissionPricingInfo pricingInfo = parseSubmissionPricingInfo(facilityInfo);
        SubmissionReceivingInfo receivingInfo = parseSubmissionReceivingInfo(facilityInfo);
        SubmissionCustomerInfo customerInfo = parseSubmissionCustomerInfo(individualInfo);

        FlexiLoanConfirmResponse response = new FlexiLoanConfirmResponse();
        response.setPaymentInfo(paymentInfo);
        response.setCustomerInfo(customerInfo);
        response.setPricingInfo(pricingInfo);
        response.setReceivingInfo(receivingInfo);
        response.setPaymentInfo(paymentInfo);
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
        wrapper.setDueDate(parseDateThaiFormat(response.getPaymentInfo().getPayDate()));
        wrapper.setFirstPaymentDueDate(response.getPaymentInfo().getFirstPaymentDueDate());
        wrapper.setNextPaymentDueDate(response.getPaymentInfo().getNextPaymentDueDate());
        wrapper.setApplyDate(parseDateThaiFormat(response.getPaymentInfo().getPayDate()));
        wrapper.setInterestRate(parseNumberFormat(response.getPaymentInfo().getInterestRate()));
        wrapper.setInstallment(parseNumberFormat(response.getPaymentInfo().getInstallmentAmount()));
        if(response.getPaymentInfo().getLoanContractDate()!=null){
            wrapper.setConsentDate(parseDateThaiFormat(response.getPaymentInfo().getLoanContractDate().getTime()));
        }
        wrapper.setNcbConsentFlag("Y");
        wrapper.setCashDisbursement(parseNumberFormat(response.getPaymentInfo().getOutStandingBalance()));
        wrapper.setCurrentLoan(parseNumberFormat(response.getReceivingInfo().getOsLimit()));
        wrapper.setCurrentAccount(response.getReceivingInfo().getHostAcfNo());
        wrapper.setRateTypeValue(response.getPaymentInfo().getRateType());
        wrapper.setUnderwriting(response.getPaymentInfo().getUnderwriting());

        return wrapper;
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

    private String getLetterOfConsentSFTPFilePath(String appRefNo, ResponseApplication application) {
        String dateStr = application.getBody().getApplicationDate();
        dateStr = dateStr.replaceAll("[-:T ]", "");
        dateStr = dateStr.substring(2, 14);
        String docType = "00111";
        String letterOfConsentSFTPFilePath = String.format("sftp://%s/users/enotiftp/SIT/MIB/TempAttachments/01_%s_%s_%s.JPG", sftpClientImp.getRemoteHost(), dateStr, appRefNo, docType);
        logger.info("letterOfConsentSFTPFilePath: {}", letterOfConsentSFTPFilePath);
        return letterOfConsentSFTPFilePath;
    }

    private ResponseApplication getApplicationInfo(long caID) throws ServiceException, RemoteException {
        ResponseApplication response = getApplicationInfoClient.getApplicationInfo(caID);
        if (LegacyResponseCodeEnum.SUCCESS.getCode().equals(response.getHeader().getResponseCode())) {
            return response;
        }
        throw new ExportException("get application info fail");
    }

    private ResponseInstantLoanCalUW getInstantLoanCalUW(BigDecimal caID) throws RemoteException, ServiceException {
        RequestInstantLoanCalUW request = new RequestInstantLoanCalUW();
        Body body = new Body();
        body.setCaId(caID);
        body.setTriggerFlag("N");
        request.setBody(body);

        ResponseInstantLoanCalUW response = instantLoanCalUWClient.getCalculateUnderwriting(request);
        if (LegacyResponseCodeEnum.SUCCESS.getCode().equals(response.getHeader().getResponseCode())) {
            return response;
        }
        throw new ExportException("get instantLoanCalUW fail");
    }

    private String parseNumberFormat(BigDecimal number) {
        return number == null ? "-" : NumberFormat.getIntegerInstance().format(number);
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
        if (date != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("d MMM yy", new Locale("th", "TH"));
            return formatter.format(date);
        }
        return "-";
    }

    private SubmissionPaymentInfo parseSubmissionPaymentInfo(Facility facilityInfo, Individual customerInfo, CreditCard creditCardInfo, ResponseInstantLoanCalUW loanCalUWResponse, String productCode) {
        SubmissionPaymentInfo paymentInfo = new SubmissionPaymentInfo();
        if (productCode.equals(RSLProductCodeEnum.FLASH_CARD_PLUS.getProductCode())) {
            paymentInfo.setRequestAmount(facilityInfo.getFeature().getRequestAmount());
        } else if (productCode.equals(RSLProductCodeEnum.CASH_2_GO_TOPUP.getProductCode()) && loanCalUWResponse.getBody().getApprovalMemoFacilities() != null) {
            paymentInfo.setRequestAmount(loanCalUWResponse.getBody().getApprovalMemoFacilities()[0].getOutstandingBalance());
        }


        if (loanCalUWResponse.getBody().getApprovalMemoFacilities() != null) {
            ApprovalMemoFacility approvalMemoFacility = loanCalUWResponse.getBody().getApprovalMemoFacilities()[0];

            paymentInfo.setTenure(approvalMemoFacility.getTenor());
            paymentInfo.setPayDate(approvalMemoFacility.getPayDate());
            paymentInfo.setInterestRate(approvalMemoFacility.getInterestRate());
            paymentInfo.setDisburstAccountNo(approvalMemoFacility.getDisburstAccountNo());
            paymentInfo.setCreditLimit(approvalMemoFacility.getCreditLimit());
            paymentInfo.setFirstPaymentDueDate(approvalMemoFacility.getFirstPaymentDueDate());
            paymentInfo.setLoanContractDate(approvalMemoFacility.getLoanContractDate());
            paymentInfo.setInstallmentAmount(approvalMemoFacility.getInstallmentAmount());
            paymentInfo.setRateType(approvalMemoFacility.getRateType());
            paymentInfo.setRateTypePercent(approvalMemoFacility.getRateTypePercent());
            paymentInfo.setUnderwriting(approvalMemoFacility.getUnderwritingResult());
        }

        if (customerInfo != null) {
            paymentInfo.setEStatement(customerInfo.getEmail());
        }

        if (facilityInfo != null) {
            paymentInfo.setFeatureType(facilityInfo.getFeatureType());
            paymentInfo.setOtherBank(facilityInfo.getLoanWithOtherBank());
            paymentInfo.setOtherBankInProgress(facilityInfo.getConsiderLoanWithOtherBank());
        }
        paymentInfo.setPaymentMethod(setPaymentMethod(productCode, facilityInfo, creditCardInfo));

        return paymentInfo;
    }

    private SubmissionPricingInfo parseSubmissionPricingInfo(Facility facilityInfo) {
        SubmissionPricingInfo pricingInfo = new SubmissionPricingInfo();
        List<LoanCustomerPricing> pricingList = new ArrayList<>();
        if (facilityInfo != null && facilityInfo.getPricings() != null) {
            for (Pricing p : facilityInfo.getPricings()) {
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
        if (facilityInfo != null) {
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
}
