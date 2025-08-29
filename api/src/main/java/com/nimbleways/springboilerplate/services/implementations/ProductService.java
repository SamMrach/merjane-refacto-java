package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.strategies.AvailabilityStrategy;
import com.nimbleways.springboilerplate.strategies.AvailabilityStrategyFactory;
import org.springframework.stereotype.Service;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;

@Service

public class ProductService {

    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    public ProductService(ProductRepository productRepository, NotificationService notificationService) {
        this.productRepository = productRepository;
        this.notificationService = notificationService;
    }

    public void processProduct(Product product) {
        AvailabilityStrategy strategy = AvailabilityStrategyFactory.getStrategy(product.getType());
        strategy.process(product, productRepository, notificationService);
    }
}