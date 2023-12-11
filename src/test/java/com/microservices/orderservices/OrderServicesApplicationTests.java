package com.microservices.orderservices;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.orderservices.Dto.OrderRequest;
import com.microservices.orderservices.Dto.OrderRequestLineDto;
import com.microservices.orderservices.Repository.OrderRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class OrderServicesApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Container
    private static final MySQLContainer mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:latest"));

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
        dynamicPropertyRegistry.add("spring.datasource.url",mySQLContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username",mySQLContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password",mySQLContainer::getPassword);
    }

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldPlaceOrder() throws Exception {


        OrderRequestLineDto orderRequestLineDto = OrderRequestLineDto.builder()
                .skuCode(UUID.randomUUID().toString())
                .price(BigDecimal.valueOf(10))
                .quantity(10)
                .build();

        OrderRequest orderRequest = OrderRequest.builder()
                .orderRequestLineDtoList(Stream.of(orderRequestLineDto).toList())
                .build();

        String orderRequestString = objectMapper.writeValueAsString(orderRequest);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderRequestString))
                .andExpect(status().isCreated());
        Assertions.assertThat(orderRepository.findAll().size() == 1).isTrue();
        orderRepository.deleteAll();
    }

}
