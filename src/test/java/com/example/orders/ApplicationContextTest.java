package com.example.orders;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationContextTest {
    @Autowired OrderService service;
    @Autowired PaymentClient paymentClient;

    @Test void contextLoadsAndWiresBeans() {
        assertNotNull(service);
        assertNotNull(paymentClient);
    }
}
