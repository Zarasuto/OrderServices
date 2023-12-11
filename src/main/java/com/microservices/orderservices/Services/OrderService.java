package com.microservices.orderservices.Services;

import com.microservices.orderservices.Dto.OrderRequest;

public interface OrderService {

    void placeOrder(OrderRequest orderRequest);
}
