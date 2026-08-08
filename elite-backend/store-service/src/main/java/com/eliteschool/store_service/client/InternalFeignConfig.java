package com.eliteschool.store_service.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InternalFeignConfig {

    @Bean
    public RequestInterceptor internalServiceInterceptor() {
        return (RequestTemplate template) ->
                template.header("e-internal-service", "store-service");
    }
}
