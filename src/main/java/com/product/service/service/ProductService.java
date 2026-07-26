package com.product.service.service;

import com.product.service.dto.ProductRequest;
import com.product.service.dto.ProductResponse;
import com.product.service.entity.ProductEntity;
import com.product.service.exception.DuplicateProductException;
import com.product.service.exception.ExternalServiceException;
import com.product.service.exception.InvalidProductIdException;
import com.product.service.exception.ResourceNotFoundException;
import com.product.service.mapper.ProductMapper;
import com.product.service.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

@Service
public class ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public ProductResponse createProduct(ProductRequest productRequest) {
        log.info("Creating product");
        return executeDatabaseOperation(() -> {
            var entity = productMapper.toEntity(productRequest);
            var savedProduct = saveProduct(entity);
            log.info("Product created successfully: id={}", savedProduct.getId());
            return productMapper.toResponse(savedProduct);
        }, "Error de base de datos al crear el producto");
    }

    public List<ProductResponse> getAllProducts() {
        log.debug("Fetching all products");
        return executeDatabaseOperation(() ->
                        productRepository.findAll()
                                .stream()
                                .map(productMapper::toResponse)
                                .toList(),
                "Error de base de datos al consultar productos");
    }

    public ProductResponse getProductById(Long id) {
        log.debug("Fetching product by id={}", id);
        return productMapper.toResponse(findProduct(id));
    }

    public List<ProductResponse> getProductsByCustomerId(Long customerId) {
        log.debug("Fetching products by customerId={}", customerId);
        if (customerId == null || customerId <= 0) {
            log.warn("Product lookup by customer rejected: invalid customerId={}", customerId);
            throw new InvalidProductIdException("El id del cliente debe ser mayor a cero");
        }

        return executeDatabaseOperation(() ->
                        productRepository.findByCustomerId(customerId)
                                .stream()
                                .map(productMapper::toResponse)
                                .toList(),
                "Error de base de datos al consultar productos por customerId");
    }

    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        log.info("Updating product: id={}", id);
        return executeDatabaseOperation(() -> {
            var entity = findProduct(id);
            productMapper.updateEntityFromRequest(productRequest, entity);
            var savedProduct = saveProduct(entity);
            log.info("Product updated successfully: id={}", savedProduct.getId());
            return productMapper.toResponse(savedProduct);
        }, "Error de base de datos al actualizar el producto");
    }

    public void deleteProductById(Long id) {
        log.info("Deleting product: id={}", id);
        executeDatabaseOperation(() -> {
            var entity = findProduct(id);
            productRepository.delete(entity);
            log.info("Product deleted successfully: id={}", id);
            return null;
        }, "Error de base de datos al eliminar el producto");
    }

    private ProductEntity findProduct(Long id) {
        if (id == null || id <= 0) {
            log.warn("Product lookup rejected: invalid id={}", id);
            throw new InvalidProductIdException("El id del producto debe ser mayor a cero");
        }

        return executeDatabaseOperation(() -> {
                    var product = productRepository.findById(id);
                    if (product.isEmpty()) {
                        log.warn("Product not found: id={}", id);
                        throw new ResourceNotFoundException("Producto no encontrado con id: " + id);
                    }
                    return product.get();
                },
                "Error de base de datos al consultar el producto por id");
    }

    private ProductEntity saveProduct(ProductEntity product) {
        try {
            return productRepository.save(product);
        } catch (DataIntegrityViolationException ex) {
            log.warn("Duplicate product detected (productNumber)");
            throw new DuplicateProductException(
                    "Ya existe un producto con el mismo productNumber",
                    ex
            );
        }
    }

    private <T> T executeDatabaseOperation(Supplier<T> action, String errorMessage) {
        try {
            return action.get();
        } catch (DataAccessException ex) {
            log.error(errorMessage, ex);
            throw new ExternalServiceException(errorMessage, ex);
        }
    }
}
