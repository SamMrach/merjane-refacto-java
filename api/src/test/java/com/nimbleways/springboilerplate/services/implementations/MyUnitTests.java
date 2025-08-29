package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.ProductType;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@UnitTest
public class MyUnitTests {

    @Mock
    private NotificationService notificationService;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product createNormalProduct(int available, int leadTime, String name) {
        return new Product(null, leadTime, available, ProductType.NORMAL, name,
                (LocalDate) null, (LocalDate) null, (LocalDate) null);
    }

    private Product createSeasonalProduct(int available, int leadTime, String name, LocalDate start, LocalDate end) {
        return new Product(null, leadTime, available, ProductType.SEASONAL, name,
                (LocalDate) null, start, end);
    }

    private Product createExpirableProduct(int available, int leadTime, String name, LocalDate expiryDate) {
        return new Product(null, leadTime, available, ProductType.EXPIRABLE, name,
                expiryDate, (LocalDate) null, (LocalDate) null);
    }

    @Test
    public void testNormalProductOutOfStock() {
        Product product = createNormalProduct(0, 15, "RJ45 Cable");
        Mockito.when(productRepository.save(product)).thenReturn(product);
        productService.processProduct(product);
        assertEquals(0, product.getAvailable());
        assertEquals(15, product.getLeadTime());
        Mockito.verify(productRepository, Mockito.times(1)).save(product);
        Mockito.verify(notificationService, Mockito.times(1))
                .sendDelayNotification(product.getLeadTime(), product.getName());
    }

    @Test
    public void testNormalProductInStock() {
        Product product = createNormalProduct(10, 5, "Mouse");
        Mockito.when(productRepository.save(product)).thenReturn(product);
        productService.processProduct(product);
        assertEquals(9, product.getAvailable());
        Mockito.verify(productRepository, Mockito.times(1)).save(product);
        Mockito.verify(notificationService, Mockito.never())
                .sendDelayNotification(Mockito.anyInt(), Mockito.anyString());
    }

    @Test
    public void testSeasonalProductOutOfSeason() {
        LocalDate start = LocalDate.now().plusDays(10);
        LocalDate end = LocalDate.now().plusDays(20);
        Product product = createSeasonalProduct(0, 5, "Christmas Tree", start, end);
        Mockito.when(productRepository.save(product)).thenReturn(product);
        productService.processProduct(product);
        assertEquals(0, product.getAvailable());
        Mockito.verify(productRepository, Mockito.times(1)).save(product);
        Mockito.verify(notificationService, Mockito.times(1))
                .sendOutOfStockNotification(product.getName());
    }

    @Test
    public void testSeasonalProductInSeasonInStock() {
        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now().plusDays(10);
        Product product = createSeasonalProduct(3, 5, "Pumpkin", start, end);

        productService.processProduct(product);
        assertEquals(2, product.getAvailable());
        Mockito.verify(productRepository, Mockito.times(1)).save(product);
        Mockito.verify(notificationService, Mockito.never())
                .sendOutOfStockNotification(Mockito.anyString());
    }

    @Test
    public void testExpirableProductExpired() {
        Product product = createExpirableProduct(5, 5, "Milk", LocalDate.now().minusDays(1));
        Mockito.when(productRepository.save(product)).thenReturn(product);
        productService.processProduct(product);
        assertEquals(0, product.getAvailable());
        Mockito.verify(productRepository, Mockito.times(1)).save(product);
        Mockito.verify(notificationService, Mockito.times(1))
                .sendExpirationNotification(product.getName(), product.getExpiryDate());
    }

    @Test
    public void testExpirableProductValid() {
        Product product = createExpirableProduct(5, 5, "Cheese", LocalDate.now().plusDays(2));
        Mockito.when(productRepository.save(product)).thenReturn(product);
        productService.processProduct(product);
        assertEquals(4, product.getAvailable());
        Mockito.verify(productRepository, Mockito.times(1)).save(product);
        Mockito.verify(notificationService, Mockito.never())
                .sendExpirationNotification(Mockito.anyString(), Mockito.any());
    }

}