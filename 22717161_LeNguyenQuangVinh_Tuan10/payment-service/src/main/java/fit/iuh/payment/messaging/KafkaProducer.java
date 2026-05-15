package fit.iuh.payment.messaging;

import fit.iuh.payment.dto.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPaymentEvent(String topic, PaymentEvent event) {
        kafkaTemplate.send(topic, event);
        log.debug("Published event to topic {}: {}", topic, event);
    }
}
