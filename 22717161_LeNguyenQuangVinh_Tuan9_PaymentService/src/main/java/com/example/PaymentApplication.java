package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@SpringBootApplication
@RestController
@CrossOrigin(origins = "*")
public class PaymentApplication {

    public static void main(String[] args) {
        System.setProperty("server.port", "8084");
        SpringApplication.run(PaymentApplication.class, args);
    }

    @RequestMapping(value = "/payments", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<Map<String, String>> processPayment(@RequestBody(required = false) Map<String, Object> request) {
        Random random = new Random();
        if (random.nextInt(100) < 80) {
            return ResponseEntity.ok(Map.of(
                    "transactionId", UUID.randomUUID().toString(),
                    "status", "SUCCESS"
            ));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", "FAILED",
                    "reason", "Insufficient balance"
            ));
        }
    }
}
