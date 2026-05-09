package com.flashsale.cart.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CartService {

    private static final String CART_KEY_PREFIX = "cart:";

    private final HashOperations<String, String, String> hashOperations;

    @Autowired
    public CartService(RedisTemplate<String, Object> redisTemplate) {
        this.hashOperations = redisTemplate.opsForHash();
    }

    /**
     * Thêm sản phẩm vào giỏ hàng
     * Sử dụng Hash để lưu cấu trúc giỏ hàng: Key = cart:{userId}, HashKey = {productId}, HashValue = {quantity}
     */
    public void addToCart(String userId, String productId, int quantity) {
        if (userId == null || userId.isEmpty() || productId == null || productId.isEmpty() || quantity <= 0) {
            throw new IllegalArgumentException("Dữ liệu đầu vào không hợp lệ.");
        }

        String cartKey = CART_KEY_PREFIX + userId;

        try {
            // increment tự động tạo key nếu chưa tồn tại, và cộng dồn số lượng nếu sản phẩm đã có trong giỏ
            hashOperations.increment(cartKey, productId, quantity);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tương tác với Redis: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy danh sách sản phẩm trong giỏ hàng
     */
    public Map<String, String> getCart(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("UserId không hợp lệ.");
        }

        String cartKey = CART_KEY_PREFIX + userId;

        try {
            // Lấy toàn bộ HashKey và HashValue (sản phẩm và số lượng) của user
            return hashOperations.entries(cartKey);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi đọc dữ liệu từ Redis: " + e.getMessage(), e);
        }
    }
}
