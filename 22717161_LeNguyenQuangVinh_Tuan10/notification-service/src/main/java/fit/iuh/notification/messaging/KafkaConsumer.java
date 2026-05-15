package fit.iuh.notification.messaging;

import fit.iuh.notification.dto.PaymentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaConsumer {

    @KafkaListener(topics = {"PAYMENT_SUCCESS", "PAYMENT_FAILED"}, groupId = "notification-group")
    public void consumePaymentEvent(PaymentEvent paymentEvent) {
        if ("SUCCESS".equalsIgnoreCase(paymentEvent.getStatus())) {
            log.info("[Notification Service]\nĐơn hàng #{} đã thanh toán thành công!", paymentEvent.getOrderId());
        } else if ("FAILED".equalsIgnoreCase(paymentEvent.getStatus())) {
            log.info("[Notification Service]\nThanh toán thất bại cho đơn hàng #{}", paymentEvent.getOrderId());
        }
    }
}
