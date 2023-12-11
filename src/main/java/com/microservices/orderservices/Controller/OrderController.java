package com.microservices.orderservices.Controller;

import com.microservices.orderservices.Dto.OrderRequest;
import com.microservices.orderservices.Services.OrderServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderServiceImpl orderService;

    @PostMapping
    public ResponseEntity placeOrder(@RequestBody OrderRequest orderRequest){
        try{
            orderService.placeOrder(orderRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("Order Placed Successfully");
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        }
    }
}
