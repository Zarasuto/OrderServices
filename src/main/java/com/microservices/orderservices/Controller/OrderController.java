package com.microservices.orderservices.Controller;

import com.microservices.orderservices.Dto.OrderRequest;
import com.microservices.orderservices.Services.OrderServiceImpl;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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

    /**
     * Difference between try-catch black and circuit breaker
     * https://stackoverflow.com/questions/52343763/what-is-advantage-of-circuit-breaker-design-pattern-in-api-architecture
     */

    @PostMapping
    @CircuitBreaker(name="inventory",fallbackMethod = "OrderFallBack")
    public ResponseEntity placeOrder(@RequestBody OrderRequest orderRequest){
        try{
            orderService.placeOrder(orderRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("Order Placed Successfully");
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getMessage());
        }
    }

    //Fallback method. must be the same as the endpoint
    public ResponseEntity OrderFallBack(OrderRequest orderRequest, RuntimeException runtimeException){
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Request Failed, Please try again after a few seconds!");
    }
}
