package com.microservices.orderservices.Services;

import com.microservices.orderservices.Config.RestClientConfig;
import com.microservices.orderservices.Dto.InventoryResponse;
import com.microservices.orderservices.Dto.OrderRequest;
import com.microservices.orderservices.Dto.OrderRequestLineDto;
import com.microservices.orderservices.Model.Order;
import com.microservices.orderservices.Model.OrderLineItems;
import com.microservices.orderservices.Repository.OrderRepository;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final RestClient.Builder restClient;

    @Autowired
    private Tracer tracer;

    @Override
    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItemsList = orderRequest.getOrderRequestLineDtoList()
                .stream()
                .map(orderRequestLineDto -> mapToOrderLineItem(orderRequestLineDto))
                .toList();
        order.setOrderLineItemsList(orderLineItemsList);
        List<String> skuCodeList = order.getOrderLineItemsList().stream().map(OrderLineItems::getSkuCode).toList();

        log.info("calling inventory service");
        Span inventorySpan = tracer.nextSpan().name("calling-inventoryService").start();
        //Call inventory service to see if the product is in stock
        InventoryResponse[] inventoryResponses = restClient.build().get()
                .uri("http://inventory-service/api/inventory",uriBuilder -> uriBuilder.queryParam("sku-code",skuCodeList).build())
                .retrieve()
                .body(InventoryResponse[].class);
        inventorySpan.end();

        boolean AllMatch = Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isInStock);


        if(AllMatch && inventoryResponses.length== skuCodeList.size()){
            orderRepository.save(order);
            log.info("Order {} is saved",order.getId());
        }else{
            List<String> notInStock = Arrays.stream(inventoryResponses).map(InventoryResponse -> getNotInStock(InventoryResponse)).toList();
            throw new IllegalArgumentException("Product "+ notInStock +" is not in stock, please try again later");
        }

    }

    private String getNotInStock(InventoryResponse inventoryResponse) {
        if(!inventoryResponse.isInStock()){
            return inventoryResponse.getSkuCode();
        } else return "";
    }

    private OrderLineItems mapToOrderLineItem(OrderRequestLineDto orderRequestLineDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setSkuCode(orderRequestLineDto.getSkuCode());
        orderLineItems.setPrice(orderRequestLineDto.getPrice());
        orderLineItems.setQuantity(orderRequestLineDto.getQuantity());
        return orderLineItems;
    }
}
