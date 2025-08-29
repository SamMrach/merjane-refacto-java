package com.nimbleways.springboilerplate.strategies;

import com.nimbleways.springboilerplate.entities.ProductType;
import com.nimbleways.springboilerplate.strategies.Implementations.ExpirableProductStrategy;
import com.nimbleways.springboilerplate.strategies.Implementations.NormalProductStrategy;
import com.nimbleways.springboilerplate.strategies.Implementations.SeasonalProductStrategy;

public class AvailabilityStrategyFactory {

    public static AvailabilityStrategy getStrategy(ProductType type) {
        return switch (type) {
            case NORMAL -> new NormalProductStrategy();
            case SEASONAL -> new SeasonalProductStrategy();
            case EXPIRABLE -> new ExpirableProductStrategy();
            default -> throw new IllegalArgumentException("Unknown product type: " + type);
        };
    }
}