package com.flashsale.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfig {

    // Sử dụng StringRedisTemplate (thay vì RedisTemplate<String, Object>) 
    // để Redis lưu giá trị (ví dụ: số lượng tồn kho) hoàn toàn dưới dạng văn bản (String).
    // Điều này rất quan trọng để các lệnh tính toán nguyên tử như DECRBY hoạt động chuẩn xác 
    // và không ném lỗi ép kiểu (serialization).
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }
}
