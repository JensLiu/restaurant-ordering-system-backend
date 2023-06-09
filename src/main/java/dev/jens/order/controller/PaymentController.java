package dev.jens.order.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import dev.jens.order.dto.OrderRequestDto;
import dev.jens.order.service.OrderService;
import dev.jens.user.User;
import dev.jens.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Log4j2
public class PaymentController {

    private final UserService userService;
    private final OrderService orderService;

    @PostMapping("/checkout")
    public void preCheckOut(@RequestBody OrderRequestDto requestDto, Authentication authentication, HttpServletResponse response) throws IOException {
        User user = userService.getUserOrElseThrow(authentication);
        Session checkoutSession = orderService.handleCheckoutInitiation(requestDto, user).orElseThrow();
        log.info("Checkout session created: " + checkoutSession.getId());
        log.info("Redirecting to checkout page: " + checkoutSession.getUrl());
        response.setStatus(303);
        response.sendRedirect(checkoutSession.getUrl());
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
