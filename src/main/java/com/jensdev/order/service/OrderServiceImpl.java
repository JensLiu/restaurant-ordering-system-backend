package com.jensdev.order.service;

import com.jensdev.menu.repository.MenuItemFlavourRepository;
import com.jensdev.menu.repository.MenuItemRepository;
import com.jensdev.menu.repository.MenuItemSizeRepository;
import com.jensdev.order.dto.OrderItemRequestDto;
import com.jensdev.order.repository.OrderItemRepository;
import com.jensdev.order.repository.OrderRepository;
import com.jensdev.user.modal.User;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.jensdev.order.modal.Order;
import com.jensdev.order.modal.OrderItem;
import com.jensdev.order.modal.OrderStatus;
import com.jensdev.order.dto.OrderRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
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

        List<OrderItem> savedItems = orderItemRepository.saveAll(items);

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
        Order order = orderRepository.findById(orderId).orElseThrow();
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
        Order order = orderRepository.findAllByStripeSessionId(stripeSessionId).get(0);
        log.info("Order: " + order);
        order.setStripeIntentId(stripeIntent);
        order.setPaidAt(new Date());
        order.setStatus(OrderStatus.WAITING);
        // TODO: send notification
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
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow();
    }

    @Override
    public Order updateOrderStatus(Long id, OrderStatus newStatus) {
        Order order = orderRepository.findById(id).orElseThrow();
        order.setStatus(newStatus);
        return orderRepository.save(order);
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

    private String createStripeCustomSession(Double totalPrice) throws StripeException {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(totalPrice.longValue() * 100)
                .setCurrency("usd")
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods
                                .builder()
                                .setEnabled(true)
                                .build()
                )
                .build();
        // Create a PaymentIntent with the order amount and currency
        PaymentIntent paymentIntent = PaymentIntent.create(params);
        return paymentIntent.getClientSecret();
    }
}
