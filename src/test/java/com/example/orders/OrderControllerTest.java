package com.example.orders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrderController.class)
@Import({OrderService.class, OrderRepository.class, PricingService.class})
class OrderControllerTest {
    @Autowired MockMvc mvc;
    @Autowired OrderService service;

    @Test void createReturns201() throws Exception {
        mvc.perform(post("/orders")).andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("NEW"));
    }
    @Test void addItemAndTotal() throws Exception {
        String id = service.create().getId();
        mvc.perform(post("/orders/" + id + "/items").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sku\":\"A\",\"quantity\":1,\"unitPriceCents\":1000}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.subtotalCents").value(1000));
        mvc.perform(get("/orders/" + id + "/total")).andExpect(jsonPath("$.totalCents").value(1080));
    }
    @Test void unknownOrderIs404() throws Exception {
        mvc.perform(get("/orders/missing/total")).andExpect(status().isNotFound());
    }
    @Test void invalidItemIs400() throws Exception {
        String id = service.create().getId();
        mvc.perform(post("/orders/" + id + "/items").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sku\":\"A\",\"quantity\":0,\"unitPriceCents\":1}"))
                .andExpect(status().isBadRequest());
    }
}
