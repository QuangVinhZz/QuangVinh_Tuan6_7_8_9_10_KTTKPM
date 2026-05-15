package fit.iuh.payment.service;

import fit.iuh.payment.dto.OrderEvent;
import fit.iuh.payment.dto.PaymentEvent;
import fit.iuh.payment.messaging.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentProcessorService {

    private final KafkaProducer kafkaProducer;
    private final Random random = new Random();

    public void processPayment(OrderEvent orderEvent) {
        log.info("[Payment Service] Processing payment...");
        
        try {
            // Delay giả lập 2-3 giây
            int delay = 2000 + random.nextInt(1000);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Payment processing interrupted", e);
        }

        // Random success/fail (70% success, 30% fail)
        boolean isSuccess = random.nextInt(10) < 7;
        
        PaymentEvent paymentEvent = new PaymentEvent();
        paymentEvent.setOrderId(orderEvent.getOrderId());

        if (isSuccess) {
            paymentEvent.setEvent("PAYMENT_SUCCESS");
            paymentEvent.setStatus("SUCCESS");
            log.info("[Payment Service] Payment SUCCESS for Order #{}", orderEvent.getOrderId());
            kafkaProducer.sendPaymentEvent("PAYMENT_SUCCESS", paymentEvent);
        } else {
            paymentEvent.setEvent("PAYMENT_FAILED");
            paymentEvent.setStatus("FAILED");
            log.info("[Payment Service] Payment FAILED for Order #{}", orderEvent.getOrderId());
            kafkaProducer.sendPaymentEvent("PAYMENT_FAILED", paymentEvent);
        }
    }
}
