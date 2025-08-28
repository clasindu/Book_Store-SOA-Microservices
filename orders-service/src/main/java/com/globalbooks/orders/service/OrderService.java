package com.globalbooks.orders.service;

import com.globalbooks.orders.dto.*;
import com.globalbooks.orders.exception.OrderNotFoundException;
import com.globalbooks.orders.exception.InvalidOrderException;
import com.globalbooks.orders.model.Order;
import com.globalbooks.orders.model.OrderItem;
import com.globalbooks.orders.model.OrderStatus;
import com.globalbooks.orders.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public OrderResponseDTO createOrder(OrderCreateDTO orderDTO) {
        log.info("Creating new order for customer: {}", orderDTO.getCustomerId());

        // Generate order ID
        String orderId = generateOrderId();

        // Create order entity
        Order order = new Order();
        order.setOrderId(orderId);
        order.setCustomerId(orderDTO.getCustomerId());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentMethod(orderDTO.getPaymentMethod());

        // Convert shipping address to JSON
        try {
            String addressJson = objectMapper.writeValueAsString(orderDTO.getShippingAddress());
            order.setShippingAddress(addressJson);
        } catch (Exception e) {
            throw new InvalidOrderException("Invalid shipping address format");
        }

        // Create order items
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItemDTO itemDTO : orderDTO.getItems()) {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(itemDTO.getProductId());
            item.setQuantity(itemDTO.getQuantity());

            // In real scenario, fetch price from CatalogService
            BigDecimal price = itemDTO.getPrice() != null ? itemDTO.getPrice() : new BigDecimal("29.99");
            item.setUnitPrice(price);

            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(itemDTO.getQuantity()));
            item.setSubtotal(subtotal);

            totalAmount = totalAmount.add(subtotal);
            order.getItems().add(item);
        }

        order.setTotalAmount(totalAmount);

        // Save order
        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with ID: {}", savedOrder.getOrderId());

        return convertToResponseDTO(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderById(String orderId) {
        log.info("Fetching order: {}", orderId);

        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));

        return convertToResponseDTO(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrdersByCustomer(String customerId) {
        log.info("Fetching orders for customer: {}", customerId);

        List<Order> orders = orderRepository.findByCustomerId(customerId);
        return orders.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getAllOrders() {
        log.info("Fetching all orders");

        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponseDTO updateOrderStatus(String orderId, OrderStatusUpdateDTO statusUpdate) {
        log.info("Updating order {} status to {}", orderId, statusUpdate.getStatus());

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));

        // Validate status transition
        validateStatusTransition(order.getStatus(), statusUpdate.getStatus());

        order.setStatus(statusUpdate.getStatus());
        Order updatedOrder = orderRepository.save(order);

        log.info("Order {} status updated successfully", orderId);
        return convertToResponseDTO(updatedOrder);
    }

    @Transactional
    public void deleteOrder(String orderId) {
        log.info("Deleting order: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));

        // Only allow deletion of PENDING or CANCELLED orders
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CANCELLED) {
            throw new InvalidOrderException("Cannot delete order in status: " + order.getStatus());
        }

        orderRepository.delete(order);
        log.info("Order {} deleted successfully", orderId);
    }

    private String generateOrderId() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        // Define valid transitions
        boolean validTransition = switch (currentStatus) {
            case PENDING -> newStatus == OrderStatus.CONFIRMED || newStatus == OrderStatus.CANCELLED;
            case CONFIRMED -> newStatus == OrderStatus.SHIPPED || newStatus == OrderStatus.CANCELLED;
            case SHIPPED -> newStatus == OrderStatus.DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };

        if (!validTransition) {
            throw new InvalidOrderException(
                    String.format("Invalid status transition from %s to %s", currentStatus, newStatus)
            );
        }
    }

    private OrderResponseDTO convertToResponseDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setOrderId(order.getOrderId());
        dto.setCustomerId(order.getCustomerId());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());

        // Convert shipping address from JSON
        try {
            if (order.getShippingAddress() != null) {
                ShippingAddressDTO address = objectMapper.readValue(
                        order.getShippingAddress(),
                        ShippingAddressDTO.class
                );
                dto.setShippingAddress(address);
            }
        } catch (Exception e) {
            log.error("Error parsing shipping address", e);
        }

        // Convert items
        List<OrderItemDTO> itemDTOs = order.getItems().stream()
                .map(item -> {
                    OrderItemDTO itemDTO = new OrderItemDTO();
                    itemDTO.setProductId(item.getProductId());
                    itemDTO.setQuantity(item.getQuantity());
                    itemDTO.setPrice(item.getUnitPrice());
                    return itemDTO;
                })
                .collect(Collectors.toList());
        dto.setItems(itemDTOs);

        return dto;
    }
}