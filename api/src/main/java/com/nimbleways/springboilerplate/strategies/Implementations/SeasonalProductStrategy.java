package com.nimbleways.springboilerplate.strategies.Implementations;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import com.nimbleways.springboilerplate.strategies.AvailabilityStrategy;

import java.time.LocalDate;
public class SeasonalProductStrategy implements AvailabilityStrategy {

    @Override
    public boolean isAvailable(Product product) {
        LocalDate today = LocalDate.now();
        boolean inSeason = !today.isBefore(product.getSeasonStartDate())
                && !today.isAfter(product.getSeasonEndDate());

        if (!inSeason) return false;
        if (product.getAvailable() > 0) return true;

        LocalDate restockDate = today.plusDays(product.getLeadTime());
        return !restockDate.isAfter(product.getSeasonEndDate());
    }

    @Override
    public String getAvailabilityMessage(Product product) {
        return isAvailable(product)
                ? "Available: " + product.getAvailable()
                : "Not available this season";
    }

    @Override
    public void process(Product product, ProductRepository repo, NotificationService ns) {
        LocalDate today = LocalDate.now();
        LocalDate restockDate = today.plusDays(product.getLeadTime());

        if (today.isBefore(product.getSeasonStartDate()) || restockDate.isAfter(product.getSeasonEndDate())) {
            ns.sendOutOfStockNotification(product.getName());
            product.setAvailable(Integer.valueOf(0));
        } else {
            if (product.getAvailable() > 0) {
                product.setAvailable(Integer.valueOf(product.getAvailable() - 1));
            } else {
                ns.sendDelayNotification(product.getLeadTime(), product.getName());
            }
        }
        repo.save(product);
    }
}
