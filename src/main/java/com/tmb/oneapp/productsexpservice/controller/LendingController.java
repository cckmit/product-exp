package com.tmb.oneapp.productsexpservice.controller;

import com.google.common.base.Strings;
import com.tmb.common.logger.LogAround;
import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.common.model.TmbStatus;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.constant.ResponseCode;
import com.tmb.oneapp.productsexpservice.feignclients.CommonServiceClient;
import com.tmb.oneapp.productsexpservice.feignclients.CustomerServiceClient;
import com.tmb.oneapp.productsexpservice.model.activatecreditcard.*;
import com.tmb.oneapp.productsexpservice.model.activitylog.CreditCardEvent;
import com.tmb.oneapp.productsexpservice.model.loan.EligibleLeadResponse;
import com.tmb.oneapp.productsexpservice.model.loan.Preload;
import com.tmb.oneapp.productsexpservice.model.loan.PreloadResponse;
import com.tmb.oneapp.productsexpservice.model.response.ConfigData;
import com.tmb.oneapp.productsexpservice.model.response.NodeDetails;
import com.tmb.oneapp.productsexpservice.service.AsyncApplicationStatusService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;
import java.util.*;

@RestController

public class LendingController {

    @Autowired
    private  CommonServiceClient commonServiceClient;

//    public LendingController() { }
//
//    public LendingController(List<Preload> preloads, CommonServiceClient commonServiceClient) {
//        this.preloads = preloads;
//        this.commonServiceClient = commonServiceClient;
//
//    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/preload")
    public void addPreload() {
        List<Preload> preloads = new ArrayList<>();
        preloads.add(new Preload(1,222));
    }

    @GetMapping("/productservice/lending/get_preload")
    @ResponseStatus(HttpStatus.OK)
    @LogAround
    @ApiOperation(value = "Fetch Application config based on channel from mongo DB")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-Correlation-ID", defaultValue = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da", required = true, dataType = "string", paramType = "header", example = "32fbd3b2-3f97-4a89-ae39-b4f628fbc8da")
    })
    public ResponseEntity<TmbOneServiceResponse<List<ConfigData>>> getConfig() {
        TmbOneServiceResponse<List<ConfigData>> oneTmbOneServiceResponse = new TmbOneServiceResponse<>();
        try {
            ResponseEntity<TmbOneServiceResponse<List<ConfigData>>> nodeTextResponse = commonServiceClient.getAllConfig("mb");
            oneTmbOneServiceResponse.setData(nodeTextResponse.getBody().getData());
        }catch(Exception e) {
            System.out.println(e);

        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Timestamp", String.valueOf(Instant.now().toEpochMilli()));
        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(oneTmbOneServiceResponse);
    }


//    public List<ConfigData> getPreload(@PathVariable int crmId) {
//        ResponseEntity<TmbOneServiceResponse<List<ConfigData>>> response = commonServiceClient.getAllConfig();
//
//        return Objects.requireNonNull(response.getBody()).getData();
//
//
//
//       // boolean success = preloads.stream().anyMatch(t -> t.getCrmId() == crmId);
////        ResponseEntity<TmbOneServiceResponse<List<ConfigData>>> response = commonServiceClient
////                .getAllConfig();
//     //  return commonServiceClient.getAllConfig().getBody().getData().stream().findFirst().get().getChannel();
////        TmbOneServiceResponse<PreloadResponse> serviceResponse = new TmbOneServiceResponse<>();
////
////        if (success) {
////           return true;
////        } else {
////            return true;
////        }
//    }

}
