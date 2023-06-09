package dev.jens.order.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import dev.jens.menu.dto.MenuItemSizesDto;
import dev.jens.menu.modal.MenuItemSize;
import dev.jens.menu.repository.MenuItemFlavourRepository;
import dev.jens.menu.repository.MenuItemRepository;
import dev.jens.menu.repository.MenuItemSizeRepository;
import dev.jens.order.Order;
import dev.jens.order.OrderItem;
import dev.jens.order.OrderStatus;
import dev.jens.order.dto.OrderItemRequestDto;
import dev.jens.order.dto.OrderRequestDto;
import dev.jens.order.repository.OrderItemRepository;
import dev.jens.order.repository.OrderRepository;
import dev.jens.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class OrderServiceImpl implements OrderService {

    @Value("${app.stripe.api}")
    private String stripeApi;

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final MenuItemSizeRepository sizeRepository;
    private final MenuItemFlavourRepository flavourRepository;
    private final MenuItemRepository menuItemRepository;

    @Override
    public Optional<Session> handleCheckoutInitiation(OrderRequestDto requestDto, User user) {

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
            return Optional.of(checkoutSession);
        } catch (Exception e) {
            return Optional.empty();
        }


    }

    @Override
    public Order handleSuccessfulPayment(String stripeIntent, String stripeSessionId) {
        log.info("Payment intent: " + stripeIntent);
        Order order = orderRepository.findAllByStripeSessionId(stripeSessionId).get(0);
        log.info("Order: " + order);
        order.setStripeIntentId(stripeIntent);
        order.setPaidAt(new Date());
        order.setStatus(OrderStatus.PAID);
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
        Stripe.apiKey = stripeApi;
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
