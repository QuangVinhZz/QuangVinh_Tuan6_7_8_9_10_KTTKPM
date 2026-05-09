package com.flashsale.order.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public DataSeeder(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("---------------------------------------------------------");
        System.out.println("====== BẮT ĐẦU TẠO DỮ LIỆU TỒN KHO (STOCK) MẪU ======");
        try {
            // Đặt số lượng tồn kho rất ít để bạn dễ test luồng bị "hết hàng"
            redisTemplate.opsForValue().set("stock:P001", "5");
            redisTemplate.opsForValue().set("stock:P002", "1"); // Chỉ có 1 cái để dễ test báo lỗi
            redisTemplate.opsForValue().set("stock:P003", "2"); 
            redisTemplate.opsForValue().set("stock:P004", "20");
            redisTemplate.opsForValue().set("stock:P005", "50");

            System.out.println("====== TẠO DỮ LIỆU TỒN KHO THÀNH CÔNG! ======");
            System.out.println("Sẵn sàng nhận request tại: POST http://localhost:8083/checkout");
            System.out.println("---------------------------------------------------------");
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tạo dữ liệu mẫu. Hãy đảm bảo Redis đang chạy: " + e.getMessage());
        }
    }
}
