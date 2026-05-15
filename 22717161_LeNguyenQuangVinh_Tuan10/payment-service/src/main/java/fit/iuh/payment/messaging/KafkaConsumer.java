package fit.iuh.payment.messaging;

import fit.iuh.payment.dto.OrderEvent;
import fit.iuh.payment.service.PaymentProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {

    private final PaymentProcessorService paymentProcessorService;

    @KafkaListener(topics = "ORDER_CREATED", groupId = "payment-group")
    public void consumeOrderEvent(OrderEvent orderEvent) {
        log.info("[Payment Service] Received ORDER_CREATED for Order #{}", orderEvent.getOrderId());
        
        // Gọi service xử lý payment
        paymentProcessorService.processPayment(orderEvent);
    }
}
