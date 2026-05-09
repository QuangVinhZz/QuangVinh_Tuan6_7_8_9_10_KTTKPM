package com.flashsale.cart.component;

import com.flashsale.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CartService cartService;

    @Autowired
    public DataSeeder(CartService cartService) {
        this.cartService = cartService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("---------------------------------------------------------");
        System.out.println("====== BẮT ĐẦU TẠO DỮ LIỆU MẪU (SEED DATA) VÀO REDIS ======");
        
        try {
            // Khởi tạo giỏ hàng cho user1
            cartService.addToCart("user1", "P001", 1);
            cartService.addToCart("user1", "P002", 2);
            
            // Khởi tạo giỏ hàng cho user2
            cartService.addToCart("user2", "P003", 1);
            
            // Khởi tạo giỏ hàng cho user3
            cartService.addToCart("user3", "P004", 3);
            cartService.addToCart("user3", "P005", 5);

            System.out.println("====== TẠO DỮ LIỆU MẪU THÀNH CÔNG! ======");
            System.out.println("Bạn có thể mở trình duyệt hoặc Postman để test thử ngay các link sau:");
            System.out.println("👉 GET http://localhost:8082/cart?userId=user1");
            System.out.println("👉 GET http://localhost:8082/cart?userId=user2");
            System.out.println("👉 GET http://localhost:8082/cart?userId=user3");
            System.out.println("---------------------------------------------------------");
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tạo dữ liệu mẫu. Hãy kiểm tra xem Redis đã chạy chưa: " + e.getMessage());
        }
    }
}
