package com.flashsale.order.controller;

import com.flashsale.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/checkout")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Class tạm dùng làm DTO nhận dữ liệu
    public static class CheckoutRequest {
        private String userId;
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
    }

    @PostMapping
    public ResponseEntity<?> checkout(@RequestBody CheckoutRequest request) {
        try {
            String orderId = orderService.checkout(request.getUserId());
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Đặt hàng thành công",
                    "orderId", orderId
            ));
        } catch (IllegalStateException e) {
            // Trả về lỗi 409 Conflict nếu hết hàng hoặc lỗi logic giỏ hàng
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "status", "failed",
                    "error", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "error", "Lỗi server: " + e.getMessage()
            ));
        }
    }
}
