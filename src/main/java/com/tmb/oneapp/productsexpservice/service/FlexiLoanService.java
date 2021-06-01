package com.tmb.oneapp.productsexpservice.service;

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

    private final LoanSubmissionGetFacilityInfoClient getFacilityInfoClient;
    private final LoanSubmissionGetCustomerInfoClient getCustomerInfoClient;
    private final LoanSubmissionGetCreditCardInfoClient getCreditCardInfoClient;

    public SubmissionInfoResponse getSubmissionInfo(String correlationId, SubmissionInfoRequest request) throws ServiceException, RemoteException {
        Facility facilityInfo = getFacility(request.getCaID());
        Individual customerInfo = getCustomer(request.getCaID());
        CreditCard creditCardInfo = getCreditCard(request.getCaID());
        return parseSubmissionInfoResponse(facilityInfo, customerInfo, creditCardInfo);
    }

    private SubmissionInfoResponse parseSubmissionInfoResponse(Facility facilityInfo,
                                                               Individual customerInfo,
                                                               CreditCard creditCardInfo) {
        SubmissionInfoResponse response = new SubmissionInfoResponse();

        SubmissionCustomerInfo customer = new SubmissionCustomerInfo();
        customer.setName(customerInfo==null?null:String.format("%s %s", customerInfo.getThaiName(), customerInfo.getThaiSurName()));
        customer.setCitizenId(customerInfo.getIdNo1());

        response.setPaymentMethod(facilityInfo==null?null:facilityInfo.getPaymentMethod());

        SubmissionPricingInfo pricingInfo = new SubmissionPricingInfo();
        List<LoanCustomerPricing> pricingList = new ArrayList<>();
        if(facilityInfo!=null){
            for(Pricing p: facilityInfo.getPricings()) {
                LoanCustomerPricing customerPricing = new LoanCustomerPricing();
                customerPricing.setMonthFrom(p.getMonthFrom());
                customerPricing.setMonthTo(p.getMonthTo());
                if(StringUtil.isNullOrEmpty(p.getRateType())) {
                    customerPricing.setRateVariance(p.getRateVaraince().multiply(new BigDecimal(100)));
                }else{
                    customerPricing.setRateVariance(parseRateVariance(p.getPercentSign()));
                }

                pricingList.add(customerPricing);
            }
            pricingInfo.setPricing(pricingList);
        }

        SubmissionCreditCardInfo creditCard = new SubmissionCreditCardInfo();
        creditCard.setEStatement(customerInfo==null?null:customerInfo.getEmail());
        creditCard.setFeatureType("S");
        creditCard.setPaymentMethod(parsePaymentMethod(facilityInfo));

        SubmissionReceivingInfo receiving = new SubmissionReceivingInfo();
        receiving.setOsLimit(facilityInfo==null?null:facilityInfo.getOsLimit());
        receiving.setHostAcfNo(facilityInfo==null?null:facilityInfo.getHostAcfNo());
        receiving.setDisburseAccount(facilityInfo==null?null:String.format("TMB%s", facilityInfo.getFeature().getDisbAcctNo()));

        response.setCustomerInfo(customer);
        response.setPricingInfo(pricingInfo);
        response.setReceivingInfo(receiving);
        response.setCreditCardInfo(creditCard);
        return response;
    }

    private Facility getFacility(Long caID) throws ServiceException, RemoteException {
        ResponseFacility response = getFacilityInfoClient.searchFacilityInfoByCaID(caID);
        return response.getBody().getFacilities()==null?null:response.getBody().getFacilities()[0];
    }

    private Individual getCustomer(Long caID) throws ServiceException, RemoteException {
        ResponseIndividual response = getCustomerInfoClient.searchCustomerInfoByCaID(caID);
        return response.getBody().getIndividuals()==null?null:response.getBody().getIndividuals()[0];
    }

    private CreditCard getCreditCard(Long caID) throws ServiceException, RemoteException {
        ResponseCreditcard response = getCreditCardInfoClient.searchCreditcardInfoByCaID(caID);
        return response.getBody().getCreditCards() == null ? null : response.getBody().getCreditCards()[0];
    }

    private String parsePaymentMethod(Facility facility) {
        if(facility==null) {
            return null;
        }
        return "0".equals(facility.getPaymentMethod()) ? "Card":"DirectDebit";
    }

    private BigDecimal parseRateVariance(String percentSign) {
        try {
            return new BigDecimal(percentSign);
        }catch (Exception e){
            return null;
        }
    }


}
