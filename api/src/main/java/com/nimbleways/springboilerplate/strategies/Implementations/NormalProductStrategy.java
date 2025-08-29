package com.nimbleways.springboilerplate.strategies.Implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import com.nimbleways.springboilerplate.strategies.AvailabilityStrategy;

public class NormalProductStrategy implements AvailabilityStrategy {

    @Override
    public boolean isAvailable(Product product) {
        return product.getAvailable() > 0;
    }

    @Override
    public String getAvailabilityMessage(Product product) {
        return isAvailable(product)
                ? "Available: " + product.getAvailable()
                : "Restock in " + product.getLeadTime() + " days";
    }

    @Override
    public void process(Product product, ProductRepository repo, NotificationService ns) {
        if (isAvailable(product)) {
            product.setAvailable(Integer.valueOf(product.getAvailable() - 1));
        } else  {
            ns.sendDelayNotification(product.getLeadTime(), product.getName());
        }
        repo.save(product);
    }
}
