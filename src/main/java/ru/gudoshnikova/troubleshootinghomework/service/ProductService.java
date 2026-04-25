package ru.gudoshnikova.troubleshootinghomework.service;

import ru.gudoshnikova.troubleshootinghomework.model.Product;

import java.util.List;

public interface ProductService {
    Product createProduct(Product product);
    Product getProductById(Long id);
    List<Product> getAllProducts();
    Product updateProduct(Long id, Product productDetails);
    void deleteProduct(Long id);
    Product applyDiscount(Long productId, Double discountPercent, int calculationIterations);
}
