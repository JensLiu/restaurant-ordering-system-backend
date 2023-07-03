package com.jensdev.order.service;

import com.jensdev.common.authException.AuthException;
import com.jensdev.common.businessException.BusinessException;
import com.jensdev.common.infrastructureException.InfrastructureException;
import com.jensdev.menu.repository.MenuItemFlavourRepository;
import com.jensdev.menu.repository.MenuItemRepository;
import com.jensdev.menu.repository.MenuItemSizeRepository;
import com.jensdev.order.dto.OrderItemRequestDto;
import com.jensdev.order.repository.OrderItemRepository;
import com.jensdev.order.repository.OrderRepository;
import com.jensdev.order.service.OrderService;
import com.jensdev.user.modal.Role;
import com.jensdev.user.modal.User;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.jensdev.order.modal.Order;
import com.jensdev.order.modal.OrderItem;
import com.jensdev.order.modal.OrderStatus;
import com.jensdev.order.dto.OrderRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class OrderServiceImpl implements OrderService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final MenuItemSizeRepository sizeRepository;
    private final MenuItemFlavourRepository flavourRepository;
    private final MenuItemRepository menuItemRepository;

    @Override
    public Session handleCheckoutInitiation(OrderRequestDto requestDto, User user) {

        List<OrderItemRequestDto> orderItems = requestDto.getOrderItems();
        List<OrderItem> items = orderItems.stream().map(this::convertToDomain).toList();

        List<OrderItem> savedItems = null;

        try {
            savedItems = orderItemRepository.saveAll(items);
        } catch (DataIntegrityViolationException e) {
            log.info("Invalid order items");
            throw new BusinessException("Invalid order items");
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new InfrastructureException("Something went wrong");
        }

        Double total = savedItems.stream()
                .mapToDouble(item -> item.getSize().getUnitPrice() * item.getQuantity())
                .sum();

        try {
            Session checkoutSession = createStripeSession(total, requestDto.getSuccessUrl(), requestDto.getCancelUrl());
            String sessionId = checkoutSession.getId();
            // we cannot get intent id for now because the payment isn't completed yet
            log.info("Stripe session id: " + sessionId);
            Order order = Order.builder().
                    user(user)
                    .totalPrice(total)
                    .status(OrderStatus.UNPAID)
                    .totalPrice(total)
                    .stripeSessionId(sessionId)
                    .createdAt(new Date())
                    .items(savedItems)
                    .build();
            Order savedOrder = orderRepository.save(order);
            log.info("Order created: " + savedOrder);
            return checkoutSession;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public Session handleContinuedCheckoutInitiation(Long orderId, String successUrl, String cancelUrl) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        try {
            Session stripeSession = createStripeSession(order.getTotalPrice(), successUrl, cancelUrl);
            order.setStripeSessionId(stripeSession.getId());
            orderRepository.save(order);
            return stripeSession;
        } catch (Exception e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Order handleSuccessfulPayment(String stripeIntent, String stripeSessionId) {
        log.info("handle Payment intent: " + stripeIntent);
        Order order = null;
        try {
            order = orderRepository.findAllByStripeSessionId(stripeSessionId).get(0);
        } catch (IndexOutOfBoundsException e) {
            throw new BusinessException("Cannot find order with stripe session id: " + stripeSessionId);
        }
        log.info("Order: " + order);
        order.setStripeIntentId(stripeIntent);
        order.setPaidAt(new Date());
        order.setStatus(OrderStatus.WAITING);
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getOrdersForUser(Long userId) {
        return orderRepository.findAllByUserId(userId);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> getWaitingOrdersForToday() {
        return orderRepository.findAllWithStatuses(List.of(OrderStatus.WAITING, OrderStatus.PREPARING));
    }

    @Override
    public Order getOrderById(Long id, User actionUser) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        if (actionUser.getRole() == Role.ADMIN || order.getUser().equals(actionUser)) {
            return order;
        }
        throw new BusinessException("You do not have the authority to get this order");
    }

    @Override
    public Order updateOrderStatus(Long id, OrderStatus newStatus) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new BusinessException("Cannot update status of non-existing order"));
        order.setStatus(newStatus);
        if (order.getStatus() == OrderStatus.READY) {
            order.setServedAt(new Date());
        }
        return orderRepository.save(order);
    }

    @Override
    public void deleteOrder(Long orderId, User actionUser) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessException("Order not found"));
        if (actionUser.getRole() == Role.ADMIN || order.getUser().equals(actionUser)) {
            if (order.getStatus() == OrderStatus.UNPAID) {
                orderRepository.deleteById(orderId);
            } else {
                throw new BusinessException("Cannot delete a payed order");
            }
        }
        throw new AuthException("You do not have the authority to delete this order");
    }

    private OrderItem convertToDomain(OrderItemRequestDto requestDto) {
        // CANNOT create a new object with an ID without using the getReferenceById method
        // otherwise it will mess up the cache resulting in null fields in fetching nested objects
        return OrderItem.builder()
                .size(sizeRepository.getReferenceById(requestDto.getSizeId()))
                .flavour(flavourRepository.getReferenceById(requestDto.getFlavourId()))
                .menuItem(menuItemRepository.getReferenceById(requestDto.getItemId()))
                .quantity(requestDto.getQuantity())
                .build();
    }

    private Session createStripeSession(Double totalPrice, String successUrl, String cancelUrl) throws StripeException {
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .addLineItem(
                        SessionCreateParams.LineItem.builder().setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder()
                                        .setUnitAmount(Math.round(totalPrice * 100))
                                        .setCurrency("usd")
                                        .setProductData(
                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                        .setName("Checkout Session")
                                                        .setDescription("Payment for order")
                                                        .build()
                                        ).build()
                        ).setQuantity(1L).build()
                )
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .build();
        return Session.create(params);
    }

}
