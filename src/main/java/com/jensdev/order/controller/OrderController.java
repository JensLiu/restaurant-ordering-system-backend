package com.jensdev.order.controller;

import com.jensdev.order.modal.Order;
import com.jensdev.order.dto.OrderDto;
import com.jensdev.order.service.OrderService;
import com.jensdev.user.modal.User;
import com.jensdev.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Log4j2
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    @GetMapping("/me/orders")
    public ResponseEntity<List<OrderDto>> getOrdersByUser(Authentication authentication) {
        User user = userService.getUser(authentication);
        List<Order> orders = orderService.getOrdersForUser(user.getId());
        log.info("orders for " + user.getEmail() + ": " + orders);
        List<OrderDto> dtos = orders.stream().map(OrderDto::fromDomain).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderDto>> getOrders() {
        List<Order> orders = orderService.getAllOrders();
        List<OrderDto> dtos = orders.stream().map(OrderDto::fromDomain).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/chef/orders")
    public ResponseEntity<List<OrderDto>> getWaitingOrders() {
        List<Order> orders = orderService.getWaitingOrdersForToday();
        log.info("waiting orders: " + orders);
        List<OrderDto> dtos = orders.stream().map(OrderDto::fromDomain).toList();
        return ResponseEntity.ok(dtos);
    }

}
