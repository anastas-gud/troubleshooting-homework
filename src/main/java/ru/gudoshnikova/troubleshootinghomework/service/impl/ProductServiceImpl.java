package ru.gudoshnikova.troubleshootinghomework.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gudoshnikova.troubleshootinghomework.model.Product;
import ru.gudoshnikova.troubleshootinghomework.repository.ProductRepository;
import ru.gudoshnikova.troubleshootinghomework.service.ProductService;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Product createProduct(Product product) {
        log.info("Creating new product: {}", product.getName());

        log.trace("Create product request details - name: {}, price: {}, quantity: {}",
                product.getName(), product.getPrice(), product.getQuantity());

        if (productRepository.existsByName(product.getName())) {
            log.error("Product with name {} already exists", product.getName());
            throw new IllegalArgumentException("Product with this name already exists");
        }

        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully with ID: {}", savedProduct.getId());
        log.trace("Saved product entity: {}", savedProduct);
        return savedProduct;
    }

    @Override
    public Product getProductById(Long id) {
        log.debug("Fetching product with ID: {}", id);

        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            log.error("Product not found with ID: {}", id);
            throw new EntityNotFoundException("Product not found with id: " + id);
        }

        log.debug("Product found: {}", product.get().getName());
        log.trace("Retrieved product details: {}", product.get());
        return product.get();
    }

    @Override
    public List<Product> getAllProducts() {
        log.info("Fetching all products");
        List<Product> products = productRepository.findAll();
        log.debug("Found {} products", products.size());
        return products;
    }

    @Override
    public Product updateProduct(Long id, Product productDetails) {
        log.info("Updating product with ID: {}", id);
        log.trace("Update request - id: {}, productDetails: {}", id, productDetails);

        Product existingProduct = getProductById(id);

        if (productDetails.getName() != null && !productDetails.getName().equals(existingProduct.getName())) {
            log.trace("Name field will be updated from '{}' to '{}'",
                    existingProduct.getName(), productDetails.getName());
            if (productRepository.existsByName(productDetails.getName())) {
                log.error("Cannot update: Product name {} already exists", productDetails.getName());
                throw new IllegalArgumentException("Product name already exists");
            }
            existingProduct.setName(productDetails.getName());
        }

        if (productDetails.getPrice() != null) {
            log.trace("Price field will be updated from {} to {}",
                    existingProduct.getPrice(), productDetails.getPrice());
            existingProduct.setPrice(productDetails.getPrice());
        }

        if (productDetails.getDescription() != null) {
            log.trace("Description field will be updated");
            existingProduct.setDescription(productDetails.getDescription());
        }

        if (productDetails.getQuantity() != null) {
            log.trace("Quantity field will be updated from {} to {}",
                    existingProduct.getQuantity(), productDetails.getQuantity());
            existingProduct.setQuantity(productDetails.getQuantity());
        }

        Product updatedProduct = productRepository.save(existingProduct);
        log.info("Product updated successfully: {}", updatedProduct.getName());
        log.trace("Saved updated entity: {}", updatedProduct);
        return updatedProduct;
    }

    @Override
    public void deleteProduct(Long id) {
        log.warn("Deleting product with ID: {}", id);

        Product product = getProductById(id);
        productRepository.delete(product);
        log.info("Product deleted successfully: {}", product.getName());
    }

    @Override
    public Product applyDiscount(Long productId, Double discountPercent, int calculationIterations) {
        log.info("Applying bulk discount of {}% to product ID: {} with {} iterations",
                discountPercent, productId, calculationIterations);

        Product product = getProductById(productId);

        double heavyCalculationResult = performHeavyCalculation(calculationIterations);
        log.trace("Heavy calculation result: {}", heavyCalculationResult);

        double originalPrice = product.getPrice();
        double discountedPrice = originalPrice * (1 - discountPercent / 100);

        discountedPrice = discountedPrice + (heavyCalculationResult / 1000000);

        product.setPrice(Math.round(discountedPrice * 100.0) / 100.0);

        Product updatedProduct = productRepository.save(product);
        log.info("Discount applied: Original price: {}, New price: {}",
                originalPrice, updatedProduct.getPrice());
        log.trace("Saved product after discount: {}", updatedProduct);

        return updatedProduct;
    }

    private double performHeavyCalculation(int iterations) {
        log.debug("Starting heavy calculation with {} iterations", iterations);

        double result = 0;

        for (int i = 0; i < iterations; i++) {
            double value = Math.sin(i) * Math.cos(i) * Math.tan(i % 360);
            result += value;

            if (iterations > 100000 && i % 10000 == 0) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("Heavy calculation interrupted", e);
                }
            }

            if (iterations > 50000 && i % 10000 == 0) {
                log.debug("Heavy calculation progress: {}%", (i * 100 / iterations));
            }
        }

        log.debug("Heavy calculation completed with result: {}", result);
        return result;
    }
}
