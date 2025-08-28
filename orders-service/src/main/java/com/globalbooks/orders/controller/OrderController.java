package com.globalbooks.orders.controller;

import com.globalbooks.orders.dto.*;
import com.globalbooks.orders.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Orders", description = "Order management endpoints")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Create new order")
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderCreateDTO orderDTO) {
        log.info("REST request to create order: {}", orderDTO);
        OrderResponseDTO createdOrder = orderService.createOrder(orderDTO);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<OrderResponseDTO> getOrder(@PathVariable String orderId) {
        log.info("REST request to get order: {}", orderId);
        OrderResponseDTO order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    @Operation(summary = "Get all orders or filter by customer")
    public ResponseEntity<List<OrderResponseDTO>> getOrders(
            @RequestParam(required = false) String customerId) {
        log.info("REST request to get orders, customerId: {}", customerId);

        List<OrderResponseDTO> orders;
        if (customerId != null) {
            orders = orderService.getOrdersByCustomer(customerId);
        } else {
            orders = orderService.getAllOrders();
        }

        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{orderId}/status")
    @Operation(summary = "Update order status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable String orderId,
            @Valid @RequestBody OrderStatusUpdateDTO statusUpdate) {
        log.info("REST request to update order {} status to {}", orderId, statusUpdate.getStatus());
        OrderResponseDTO updatedOrder = orderService.updateOrderStatus(orderId, statusUpdate);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{orderId}")
    @Operation(summary = "Cancel/Delete an order")
    public ResponseEntity<Void> deleteOrder(@PathVariable String orderId) {
        log.info("REST request to delete order: {}", orderId);
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}