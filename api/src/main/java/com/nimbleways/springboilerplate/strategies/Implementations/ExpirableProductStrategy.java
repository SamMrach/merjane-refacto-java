package com.nimbleways.springboilerplate.strategies.Implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import com.nimbleways.springboilerplate.strategies.AvailabilityStrategy;

import java.time.LocalDate;

public class ExpirableProductStrategy implements AvailabilityStrategy {

    @Override
    public boolean isAvailable(Product product) {
        LocalDate today = LocalDate.now();
        return product.getAvailable() > 0
                && (product.getExpiryDate() == null || today.isBefore(product.getExpiryDate()));
    }

    @Override
    public String getAvailabilityMessage(Product product) {
        return isAvailable(product)
                ? "Available until " + product.getExpiryDate()
                : "Expired";
    }

    @Override
    public void process(Product product, ProductRepository repo, NotificationService ns) {
        if (isAvailable(product)) {
            product.setAvailable(Integer.valueOf(product.getAvailable() - 1));
        } else {
            ns.sendExpirationNotification(product.getName(), product.getExpiryDate());
            product.setAvailable(Integer.valueOf(0));
        }
        repo.save(product);
    }
}