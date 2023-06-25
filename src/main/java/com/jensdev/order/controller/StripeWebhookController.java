package com.jensdev.order.controller;

//import com.google.gson.JsonSyntaxException;
import com.jensdev.notification.dto.OrderNotificationDto;
import com.jensdev.notification.service.NotificationService;
import com.jensdev.order.service.OrderService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.LineItemCollection;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionListLineItemsParams;
import com.stripe.param.checkout.SessionRetrieveParams;
import com.jensdev.order.modal.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log4j2
public class StripeWebhookController {

    @Value("${app.stripe.webhook.secret}")
    private String endpointSecret;

    private final OrderService orderService;

    @PostMapping("/stripe/events")
    public ResponseEntity<Object> handleStripeEvent(
            @RequestHeader("Stripe-Signature") String sigHeader,
            @RequestBody String payload) throws StripeException {

        log.info("Stripe event received");
        log.debug("endpointSecret: " + endpointSecret);
        log.debug("sig header: " + sigHeader);

        if (sigHeader == null) {
            log.info("Stripe event received but no signature header was found");
            return ResponseEntity.badRequest().build();
        }

        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            // Invalid signature
            log.info("Stripe event received but signature verification failed");
            return ResponseEntity.badRequest().build();
        }

        // Handle the checkout.session.completed event
        log.info("Stripe event type: " + event.getType());
        if ("checkout.session.completed".equals(event.getType())) {

            Session sessionEvent = (Session) event.getDataObjectDeserializer().getObject().orElseThrow();

            SessionRetrieveParams params =
                    SessionRetrieveParams.builder()
                            .addExpand("line_items")
                            .build();

            Session session = Session.retrieve(sessionEvent.getId(), params, null);

            SessionListLineItemsParams listLineItemsParams =
                    SessionListLineItemsParams.builder()
                            .build();

            // Retrieve the session. If you require line items in the response, you may include them by expanding line_items.
            LineItemCollection lineItems = session.listLineItems(listLineItemsParams);
            // Fulfill the purchase...

            String paymentIntent = sessionEvent.getPaymentIntent();
            sessionEvent.getClientReferenceId();
            String sessionId = sessionEvent.getId();
            log.info("Session id: " + sessionId);
            log.info("Payment intent: " + paymentIntent);
            Order order = orderService.handleSuccessfulPayment(paymentIntent, sessionId);
            log.info("Order {} has been paid for", order.getId());
            NotificationService.notifyAllChefs(OrderNotificationDto.fromDomain(order));
//            String id = lineItems.getData().get(0).getId();
//            System.out.println("Fulfilling order for " + lineItems.getData().get(0).getDescription());
        }

        return ResponseEntity.ok(null);
    }
}