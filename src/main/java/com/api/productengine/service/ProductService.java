package com.api.productengine.service;

import com.api.productengine.dto.ProductDTO;
import com.api.productengine.dto.ProductStockDTO;
import com.api.productengine.exception.BusinessException;
import com.api.productengine.exception.ProductNotFoundException;
import com.api.productengine.model.Product;
import com.api.productengine.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Product create(ProductDTO productDto) {
        Product product = new Product(productDto.name(), productDto.description(), productDto.price(), productDto.stock());
        return repository.save(product);
    }

    public List<Product> findAll() {
        return repository.findAll();
    }

    public Product findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    public Product update(Long id, ProductDTO updated) {
        Product existing = findById(id);

        existing.setName(updated.name());
        existing.setDescription(updated.description());
        existing.setPrice(updated.price());
        existing.setStock(updated.stock());

        return repository.save(existing);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        repository.deleteById(id);
    }

    @Transactional
    public Product reserveOneUnit(Long id) {

        Product product = findById(id);

        if (product.getStock() <= 0) {
            throw new BusinessException("Product doesn't have available stock");
        }

        product.setStock(product.getStock() - 1);
        return repository.save(product);
    }

    public Double getTotalStockValue() {
        return repository.findTotalStockValue();
    }

    public Product updateStock(Long id, ProductStockDTO productStockDTO) {
        if (productStockDTO.stock() < 0) {
            throw new BusinessException("Product stock can't be less than 0");
        }

        int rowsAffected = repository.updateProductStock(id, productStockDTO.stock());

        if (rowsAffected == 0) {
            throw new ProductNotFoundException(id);
        }

        return findById(id);
    }

    public BigDecimal getAveragePrice() {
        return repository.findAveragePrice();
    }
}
