package com.microservices.orderservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//EnabledEureka Client
@SpringBootApplication
public class OrderServicesApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServicesApplication.class, args);
    }

}
