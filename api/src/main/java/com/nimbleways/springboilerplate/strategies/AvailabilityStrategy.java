package com.nimbleways.springboilerplate.strategies;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
public interface AvailabilityStrategy {

    boolean isAvailable(Product product);
    String getAvailabilityMessage(Product product);

    void process(Product product, ProductRepository productRepo, NotificationService ns);
}
