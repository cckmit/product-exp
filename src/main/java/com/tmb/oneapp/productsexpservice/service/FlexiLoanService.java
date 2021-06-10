package com.tmb.oneapp.productsexpservice.service;

import com.tmb.common.logger.TMBLogger;
import com.tmb.common.model.legacy.rsl.common.ob.creditcard.CreditCard;
import com.tmb.common.model.legacy.rsl.common.ob.facility.Facility;
import com.tmb.common.model.legacy.rsl.common.ob.individual.Individual;
import com.tmb.common.model.legacy.rsl.common.ob.pricing.Pricing;
import com.tmb.common.model.legacy.rsl.ws.creditcard.response.ResponseCreditcard;
import com.tmb.common.model.legacy.rsl.ws.facility.response.ResponseFacility;
import com.tmb.common.model.legacy.rsl.ws.individual.response.ResponseIndividual;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionGetCreditCardInfoClient;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionGetCustomerInfoClient;
import com.tmb.oneapp.productsexpservice.feignclients.loansubmission.LoanSubmissionGetFacilityInfoClient;
import com.tmb.oneapp.productsexpservice.model.request.flexiloan.SubmissionInfoRequest;
import com.tmb.oneapp.productsexpservice.model.response.flexiloan.*;
import com.tmb.oneapp.productsexpservice.model.response.loan.LoanCustomerPricing;
import io.netty.util.internal.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.xml.rpc.ServiceException;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class FlexiLoanService {
    private static final TMBLogger<FlexiLoanService> logger = new TMBLogger<>(FlexiLoanService.class);

    private final LoanSubmissionGetFacilityInfoClient getFacilityInfoClient;
    private final LoanSubmissionGetCustomerInfoClient getCustomerInfoClient;
    private final LoanSubmissionGetCreditCardInfoClient getCreditCardInfoClient;

    private static final List<String> CREDIT_CARD_CODE_LIST = List.of("VJ", "VP", "VM", "VH", "VI", "VB");

    public SubmissionInfoResponse getSubmissionInfo(SubmissionInfoRequest request) throws ServiceException, RemoteException {

        Facility facilityInfo = getFacility(request.getCaID());
        Individual customerInfo = getCustomer(request.getCaID());
        CreditCard creditCardInfo = getCreditCard(request.getCaID());
        return parseSubmissionInfoResponse(request.getProductCode(), facilityInfo, customerInfo, creditCardInfo);
    }

    private SubmissionInfoResponse parseSubmissionInfoResponse(String productCode,
                                                               Facility facilityInfo,
                                                               Individual customerInfo,
                                                               CreditCard creditCardInfo) {
        SubmissionInfoResponse response = new SubmissionInfoResponse();

        SubmissionCustomerInfo customer = new SubmissionCustomerInfo();
        customer.setName(customerInfo == null ? null : String.format("%s %s", customerInfo.getThaiName(), customerInfo.getThaiSurName()));
        customer.setCitizenId(customerInfo == null ? null : customerInfo.getIdNo1());

        SubmissionPricingInfo pricingInfo = new SubmissionPricingInfo();
        List<LoanCustomerPricing> pricingList = new ArrayList<>();
        if (facilityInfo != null) {
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
        payment.setEStatement(customerInfo == null ? null : customerInfo.getEmail());
        payment.setFeatureType(facilityInfo == null ? null : facilityInfo.getFeatureType());
        payment.setPaymentMethod(setPaymentMethod(productCode, facilityInfo, creditCardInfo));
        payment.setOtherBank(facilityInfo == null ? null : facilityInfo.getLoanWithOtherBank());
        payment.setOtherBankInProgress(facilityInfo == null ? null : facilityInfo.getConsiderLoanWithOtherBank());

        SubmissionReceivingInfo receiving = new SubmissionReceivingInfo();
        receiving.setOsLimit(facilityInfo == null ? null : facilityInfo.getOsLimit());
        receiving.setHostAcfNo(facilityInfo == null ? null : facilityInfo.getHostAcfNo());
        receiving.setDisburseAccount(facilityInfo == null ? null : String.format("TMB%s", facilityInfo.getFeature().getDisbAcctNo()));

        response.setCustomerInfo(customer);
        response.setPricingInfo(pricingInfo);
        response.setReceivingInfo(receiving);
        response.setSubmissionInfo(payment);
        return response;
    }

    private Facility getFacility(Long caID) throws ServiceException, RemoteException {
        ResponseFacility response = getFacilityInfoClient.searchFacilityInfoByCaID(caID);
        return response.getBody().getFacilities() == null ? null : response.getBody().getFacilities()[0];
    }

    private Individual getCustomer(Long caID) throws ServiceException, RemoteException {
        ResponseIndividual response = getCustomerInfoClient.searchCustomerInfoByCaID(caID);
        return response.getBody().getIndividuals() == null ? null : response.getBody().getIndividuals()[0];
    }

    private CreditCard getCreditCard(Long caID) throws ServiceException, RemoteException {
        ResponseCreditcard response = getCreditCardInfoClient.searchCreditcardInfoByCaID(caID);
        return response.getBody().getCreditCards() == null ? null : response.getBody().getCreditCards()[0];
    }

    private String parseRate(Pricing pricing) {
        if (StringUtil.isNullOrEmpty(pricing.getRateType())) {
            return String.format("%.2f", pricing.getRateVaraince().multiply(BigDecimal.valueOf(100)));
        } else {
            return String.format("%s %s %.2f", pricing.getRateType(), pricing.getPercentSign(), pricing.getRateVaraince().multiply(BigDecimal.valueOf(100)));
        }

    }

    private String setPaymentMethod(String productCode, Facility facilityInfo, CreditCard creditCardInfo) {
        if(CREDIT_CARD_CODE_LIST.contains(productCode)) {
            return creditCardInfo == null ? null : creditCardInfo.getPaymentMethod();
        }
        return facilityInfo == null ? null : facilityInfo.getPaymentMethod();
    }


}
