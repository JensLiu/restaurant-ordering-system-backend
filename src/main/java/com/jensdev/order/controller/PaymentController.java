package com.jensdev.order.controller;

import com.jensdev.order.dto.OrderRequestResponseDto;
import com.jensdev.order.service.OrderService;
import com.jensdev.user.modal.User;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.jensdev.order.dto.OrderRequestDto;
import com.jensdev.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Log4j2
public class PaymentController {

    private final UserService userService;
    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<OrderRequestResponseDto> initCheckout(
            @RequestBody OrderRequestDto requestDto,
            Authentication authentication) {
        log.info("checkout " + requestDto);
        User user = userService.getUser(authentication);
        Session checkoutSession = orderService.handleCheckoutInitiation(requestDto, user);
        log.info("Checkout session created: " + checkoutSession.getId());
        log.info("Redirecting to checkout page: " + checkoutSession.getUrl());
        return ResponseEntity.ok(OrderRequestResponseDto.builder()
                .redirectUrl(checkoutSession.getUrl())
                .build());
    }

    @PostMapping("/checkout/{orderId}")
    public ResponseEntity<OrderRequestResponseDto> continuedCheckout(
            @PathVariable Long orderId,
            @RequestBody OrderRequestDto requestDto
    ) {
        log.info("continued checkout for order: " + orderId);
        log.info("requestDto: " + requestDto);
        Session session = orderService
                .handleContinuedCheckoutInitiation(orderId, requestDto.getSuccessUrl(), requestDto.getCancelUrl());
        return ResponseEntity.ok(OrderRequestResponseDto.builder()
                .redirectUrl(session.getUrl())
                .build());
    }

    public void checkout(HttpServletResponse response) throws StripeException, IOException {
//        Stripe.apiKey = stripeApi;
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .addLineItem(
                        SessionCreateParams.LineItem.builder().setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder()
                                        .setUnitAmount(1000L)
                                        .setCurrency("usd")
                                        .setProductData(
                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                        .setName("Checkout Session")
                                                        .setDescription("Example Checkout Session")
                                                        .build()
                                        ).build()
                        ).setQuantity(1L).build()
                )
                .setSuccessUrl("http://localhost:3000/payment_success")
                .setCancelUrl("http://localhost:3000/payment_cancelled")
                .build();
        Session session = Session.create(params);
        String intentId = session.getPaymentIntent();
        response.sendRedirect(session.getUrl());
        response.setStatus(303);
//        URI redirect = new URI(session.getUrl());
//        return ResponseEntity.status(303).location(redirect).build();
    }
}
