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
import org.springframework.stereotype.Service;

import javax.xml.rpc.ServiceException;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    public FlexiLoanConfirmResponse confirm(Map<String, String> requestHeaders, FlexiLoanConfirmRequest request) throws Exception {
        Facility facilityInfo = getFacility(request.getCaID());
        Individual customerInfo = getCustomer(request.getCaID());
        CreditCard creditCardInfo = getCreditCard(request.getCaID(), request.getProductCode());

        ResponseApplication applicationResp = getApplicationInfo(request.getCaID());
        String appRefNo = applicationResp.getBody().getAppRefNo();
        ResponseInstantLoanCalUW loanCalUWResponse = getInstantLoanCalUW(BigDecimal.valueOf(request.getCaID()));
        FlexiLoanSubmissionWrapper wrapper = parseFlexiLoanSubmissionWrapper(request, facilityInfo, customerInfo, creditCardInfo, loanCalUWResponse, appRefNo);

        String eAppFileName = generateFlexiLoanConfirmReport(wrapper, appRefNo);
        storeEAppFile(requestHeaders, appRefNo, eAppFileName);

        String letterOfConsentFileName = getLetterOfConsentSFTPFilePath(appRefNo, applicationResp);

        List<String> notificationAttachments = new ArrayList<>();

        notificationAttachments.add(letterOfConsentFileName);
        wrapper.setAttachments(notificationAttachments);
        wrapper.setEmail("oranuch@odds.team");
        sendNotification(requestHeaders, wrapper);
        return parseFlexiLoanConfirmResponse(request.getProductCode(), facilityInfo, customerInfo, creditCardInfo, loanCalUWResponse);
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

    private FlexiLoanConfirmResponse parseFlexiLoanConfirmResponse(String productCode,
                                                                   Facility facilityInfo,
                                                                   Individual customerInfo,
                                                                   CreditCard creditCardInfo,
                                                                   ResponseInstantLoanCalUW loanCalUWResponse) {
        FlexiLoanConfirmResponse response = new FlexiLoanConfirmResponse();
        String underWriting = loanCalUWResponse.getBody().getUnderwritingResult() == null ? "" : loanCalUWResponse.getBody().getUnderwritingResult();

        if (underWriting.equals("APPROVE")) {
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
            }

            response.setPaymentInfo(paymentInfo);
        }

        SubmissionCustomerInfo customer = new SubmissionCustomerInfo();
        if(customerInfo!=null) {
            customer.setName(String.format("%s %s", customerInfo.getThaiName(), customerInfo.getThaiSurName()));
            customer.setCitizenId(customerInfo.getIdNo1());
        }

        SubmissionPricingInfo pricingInfo = new SubmissionPricingInfo();
        List<LoanCustomerPricing> pricingList = new ArrayList<>();
        if (facilityInfo != null && facilityInfo.getPricings()!=null) {
            for (Pricing p : facilityInfo.getPricings()) {
                LoanCustomerPricing customerPricing = new LoanCustomerPricing();
                customerPricing.setMonthFrom(p.getMonthFrom());
                customerPricing.setMonthTo(p.getMonthTo());
                customerPricing.setRateVariance(p.getRateVaraince().multiply(BigDecimal.valueOf(100)));
                customerPricing.setRate(parseRate(p));

                pricingList.add(customerPricing);
            }
            pricingInfo.setPricing(pricingList);
        }

        SubmissionPaymentInfo payment = new SubmissionPaymentInfo();
        SubmissionReceivingInfo receiving = new SubmissionReceivingInfo();

        if(customerInfo!=null) {
            payment.setEStatement(customerInfo.getEmail());
        }

        if(facilityInfo!=null) {
            payment.setFeatureType(facilityInfo.getFeatureType());
            payment.setOtherBank(facilityInfo.getLoanWithOtherBank());
            payment.setOtherBankInProgress(facilityInfo.getConsiderLoanWithOtherBank());

            receiving.setOsLimit(facilityInfo.getOsLimit());
            receiving.setHostAcfNo(facilityInfo.getHostAcfNo());
            receiving.setDisburseAccount(String.format("TMB%s", facilityInfo.getFeature().getDisbAcctNo()));
        }
        payment.setPaymentMethod(setPaymentMethod(productCode, facilityInfo, creditCardInfo));

        response.setCustomerInfo(customer);
        response.setPricingInfo(pricingInfo);
        response.setReceivingInfo(receiving);
        response.setPaymentInfo(payment);
        return response;
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

    private String parseRate(Pricing pricing) {
        if (StringUtil.isNullOrEmpty(pricing.getRateType())) {
            return String.format("%.2f", pricing.getRateVaraince().multiply(BigDecimal.valueOf(100)));
        } else {
            return String.format("%s %s %.2f", pricing.getRateType(), pricing.getPercentSign(), pricing.getRateVaraince().multiply(BigDecimal.valueOf(100)));
        }

    }

    private String setPaymentMethod(String productCode, Facility facilityInfo, CreditCard creditCardInfo) {
        if (ProductsExpServiceConstant.CREDIT_CARDS_CODE.contains(productCode)) {
            return creditCardInfo == null ? null : creditCardInfo.getPaymentMethod();
        }
        return facilityInfo == null ? null : facilityInfo.getPaymentMethod();
    }

    private String generateFlexiLoanConfirmReport(FlexiLoanSubmissionWrapper wrapper, String appRefNo) {
        String fileName = parseCompletePDFFileName(appRefNo);
        fileGeneratorService.generateFlexiLoanSubmissionPdf(wrapper, fileName, E_APP_TEMPLATE);
        return String.format("sftp://10.200.125.110/users/enotiftp/SIT/MIB/TempAttachments/%s.pdf", fileName);
    }

    private void storeEAppFile(Map<String, String> requestHeaders, String appRefNo, String fileName) {
        String eAppFilePath = "./pdf/" + fileName;
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

    private FlexiLoanSubmissionWrapper parseFlexiLoanSubmissionWrapper(FlexiLoanConfirmRequest request, Facility facilityInfo, Individual customerInfo, CreditCard creditCardInfo, ResponseInstantLoanCalUW loanCalUWResponse, String appRefNo) {
        ApprovalMemoFacility approvalMemoFacility = loanCalUWResponse.getBody().getApprovalMemoFacilities() == null ? null : loanCalUWResponse.getBody().getApprovalMemoFacilities()[0];

        FlexiLoanSubmissionWrapper wrapper = new FlexiLoanSubmissionWrapper();
        String customerName = String.format("%s %s", customerInfo.getThaiName(), customerInfo.getThaiSurName());
        wrapper.setProductCode(facilityInfo.getProductCode());
        wrapper.setFeatureType(facilityInfo.getFeatureType());
        wrapper.setAppRefNo(appRefNo);
        wrapper.setProductName(request.getProductNameTH());
        wrapper.setCustomerName(customerName);
        wrapper.setIdCardNo(customerInfo.getIdNo1());
        wrapper.setFinalLoanAmount(facilityInfo.getAmountFinance());
        wrapper.setTenor(facilityInfo.getFeature().getTenure());
        wrapper.setRequestAmount(facilityInfo.getFeature().getRequestAmount());
        wrapper.setPaymentMethod(ProductsExpServiceConstant.CREDIT_CARDS_CODE.contains(facilityInfo.getProductCode()) ? creditCardInfo.getPaymentMethod() : facilityInfo.getPaymentMethod());
        wrapper.setEmail("oranuch@odds.team");
        wrapper.setBotAnswer1("-");
        wrapper.setBotAnswer2("-");
        wrapper.setDisburseAccountNo(facilityInfo.getDisburstAccountNo());
        wrapper.setDueDate(facilityInfo.getPaymentDueDate());
        wrapper.setFirstPaymentDueDate(facilityInfo.getFirstPaymentDueDate());
        wrapper.setNextPaymentDueDate(facilityInfo.getPaymentDueDate());
        if(facilityInfo.getContractDate()!=null ) {
            wrapper.setApplyDate(facilityInfo.getContractDate().toString());
        }
        if (approvalMemoFacility != null) {
            wrapper.setInterestRate(approvalMemoFacility.getInterestRate());
            wrapper.setInstallment(approvalMemoFacility.getInstallmentAmount());
        }

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
        return String.format("sftp://10.200.125.110/users/enotiftp/SIT/MIB/TempAttachments/01_%s_%s_%s.JPG", dateStr, appRefNo, docType);
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

}
