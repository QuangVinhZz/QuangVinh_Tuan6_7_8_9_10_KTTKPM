package com.flashsale.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderService {

    private final StringRedisTemplate redisTemplate;
    private final RestTemplate restTemplate = new RestTemplate();
    
    private static final String CART_PREFIX = "cart:";
    private static final String STOCK_PREFIX = "stock:";
    private static final String ORDER_PREFIX = "order:";

    @Autowired
    public OrderService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Logic Checkout cực kỳ quan trọng trong Flash Sale:
     * Đảm bảo không bị bán âm (overselling) và xử lý giao dịch hoàn toàn trên RAM (Redis).
     */
    public String checkout(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("UserId không hợp lệ");
        }

        String cartKey = CART_PREFIX + userId;
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();

        // 1. Lấy toàn bộ thông tin giỏ hàng
        Map<String, String> cartEntries = hashOps.entries(cartKey);
        if (cartEntries == null || cartEntries.isEmpty()) {
            throw new IllegalStateException("Giỏ hàng trống");
        }

        // Map lưu lại những sản phẩm đã trừ tồn kho thành công để dùng cho việc Rollback
        Map<String, Integer> processedItems = new HashMap<>();

        // 2 & 3. Trừ tồn kho và xử lý Rollback
        try {
            for (Map.Entry<String, String> entry : cartEntries.entrySet()) {
                String productId = entry.getKey();
                int quantityToBuy = Integer.parseInt(entry.getValue());
                String stockKey = STOCK_PREFIX + productId;

                // Lệnh nguyên tử DECRBY: Trừ trực tiếp trên Redis.
                // Redis tự động xử lý tuần tự, chặn hoàn toàn race condition dù có 1000 người mua cùng 1 lúc.
                Long remainingStock = valueOps.decrement(stockKey, quantityToBuy);

                if (remainingStock != null && remainingStock < 0) {
                    // CẢNH BÁO BÁN ÂM: Sản phẩm đã hết hàng!
                    // Lập tức hoàn lại (cộng lại) số lượng cho chính sản phẩm bị âm này
                    valueOps.increment(stockKey, quantityToBuy);
                    
                    // Ném ngoại lệ để kích hoạt khối catch thực hiện Rollback các sản phẩm khác
                    throw new RuntimeException("Sản phẩm " + productId + " đã hết hàng!");
                }
                
                // Gọi API sang Inventory Service để tự trừ tồn kho
                try {
                    String inventoryUrl = "http://172.16.40.120:8084/stock/" + productId;
                    restTemplate.getForObject(inventoryUrl, String.class);
                    System.out.println("Đã gọi API Inventory thành công cho sản phẩm: " + productId);
                } catch (Exception ex) {
                    System.err.println("Lỗi gọi API Inventory cho sản phẩm " + productId + ": " + ex.getMessage());
                }
                
                // Trừ thành công, ghi nhận lại để nếu lát sau có sản phẩm khác trong giỏ bị lỗi thì còn biết đường Rollback
                processedItems.put(productId, quantityToBuy);
            }
        } catch (Exception e) {
            // ROLLBACK: Hoàn lại kho cho những sản phẩm đã trừ thành công trước đó trong cùng 1 đơn hàng
            for (Map.Entry<String, Integer> processedEntry : processedItems.entrySet()) {
                String processedStockKey = STOCK_PREFIX + processedEntry.getKey();
                valueOps.increment(processedStockKey, processedEntry.getValue());
            }
            throw new IllegalStateException("Checkout thất bại do hết hàng: " + e.getMessage());
        }

        // 4. Nếu toàn bộ sản phẩm đều trừ thành công
        // Tạo Order ID ngẫu nhiên
        String orderId = UUID.randomUUID().toString();
        String orderKey = ORDER_PREFIX + orderId;
        
        // Lưu thông tin đơn hàng xuống Redis
        hashOps.putAll(orderKey, cartEntries);

        // Xóa giỏ hàng của User
        redisTemplate.delete(cartKey);

        // 5. Trả về Order ID
        return orderId;
    }
}
