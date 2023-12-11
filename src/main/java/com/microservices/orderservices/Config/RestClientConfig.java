package com.microservices.orderservices.Config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {


    //Creating bean to autowire restclient
    //Allow LoadBalancing to use multiple instances of Inventory Service
    @Bean
    @LoadBalanced
    public RestClient.Builder restClient(){
        return RestClient.builder();
    }
}
