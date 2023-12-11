package com.microservices.orderservices.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {


    //Creating bean to autowire restclient
    @Bean
    public RestClient restClient(){
        return RestClient.create();
    }
}
