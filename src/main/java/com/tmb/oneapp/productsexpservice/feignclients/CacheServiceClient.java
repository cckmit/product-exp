package com.tmb.oneapp.productsexpservice.feignclients;

import com.tmb.common.model.TmbOneServiceResponse;
import com.tmb.oneapp.productsexpservice.constant.ProductsExpServiceConstant;
import com.tmb.oneapp.productsexpservice.model.request.cache.CacheModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


@FeignClient(name = "${feign.cache.service.name}", url = "${feign.cache.service.url}")
public interface CacheServiceClient {

    /**
     * Call cache service for get cache by key.
     *
     * @param correlationID  the headers parameter
     * @param key the path parameter
     * @return the cache response
     */
    @GetMapping(value = "/apis/cache/{key}")
    ResponseEntity<TmbOneServiceResponse<String>> getCacheByKey(
            @RequestHeader(value = ProductsExpServiceConstant.HEADER_X_CORRELATION_ID) String correlationID,
            @PathVariable("key") String key);


    /**
     * Call cache service for get cache by key.
     *
     * @param headers  the headers parameter
     * @param cacheModel
     * @return the cache response
     */
    @PostMapping(value = "/apis/cache")
    @ResponseBody
    ResponseEntity<TmbOneServiceResponse<String>> putCacheByKey(@RequestHeader Map<String, String> headers,
                                                                @RequestBody CacheModel cacheModel);


}
