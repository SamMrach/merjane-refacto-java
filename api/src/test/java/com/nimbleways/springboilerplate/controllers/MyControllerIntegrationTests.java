package com.nimbleways.springboilerplate.controllers;

import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.ProductType;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.assertEquals;

// import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Specify the controller class you want to test
// This indicates to spring boot to only load UsersController into the context
// Which allows a better performance and needs to do less mocks
@SpringBootTest
@AutoConfigureMockMvc
public class MyControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void processOrderShouldReturn() throws Exception {
        List<Product> allProducts = createProducts();
        Set<Product> orderItems = new HashSet<>(allProducts);
        Order order = createOrder(orderItems);
        productRepository.saveAll(allProducts);
        order = orderRepository.save(order);

        mockMvc.perform(post("/orders/{orderId}/processOrder", order.getId())
                        .contentType("application/json"))
                .andExpect(status().isOk());

        Order resultOrder = orderRepository.findById(order.getId()).get();
        assertEquals(resultOrder.getId(), order.getId());
    }

    private static Order createOrder(Set<Product> products) {
        Order order = new Order();
        order.setItems(products);
        return order;
    }

    private static List<Product> createProducts() {
        List<Product> products = new ArrayList<>();
        products.add(new Product(null, Integer.valueOf(15), Integer.valueOf(30), ProductType.NORMAL, "USB Cable",
                (LocalDate) null, (LocalDate) null, (LocalDate) null));
        products.add(new Product(null, Integer.valueOf(10), Integer.valueOf(0), ProductType.NORMAL, "USB Dongle",
                (LocalDate) null, (LocalDate) null, (LocalDate) null));
        products.add(new Product(null, Integer.valueOf(15), Integer.valueOf(30), ProductType.EXPIRABLE, "Butter",
                LocalDate.now().plusDays(26), (LocalDate) null, (LocalDate) null));
        products.add(new Product(null, Integer.valueOf(90), Integer.valueOf(6), ProductType.EXPIRABLE, "Milk",
                LocalDate.now().minusDays(2), (LocalDate) null, (LocalDate) null));
        products.add(new Product(null, Integer.valueOf(15), Integer.valueOf(30), ProductType.SEASONAL, "Watermelon",
                (LocalDate) null, LocalDate.now().minusDays(2), LocalDate.now().plusDays(58)));
        products.add(new Product(null, Integer.valueOf(15), Integer.valueOf(30), ProductType.SEASONAL, "Grapes",
                (LocalDate) null, LocalDate.now().plusDays(180), LocalDate.now().plusDays(240)));
        return products;
    }
}