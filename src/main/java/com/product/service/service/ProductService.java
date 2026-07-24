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
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public ProductResponse createProduct(ProductRequest productRequest) {
        return executeDatabaseOperation(() -> {
            var entity = productMapper.toEntity(productRequest);
            return productMapper.toResponse(saveProduct(entity));
        }, "Error de base de datos al crear el producto");
    }

    public List<ProductResponse> getAllProducts() {
        return executeDatabaseOperation(() ->
                        productRepository.findAll()
                                .stream()
                                .map(productMapper::toResponse)
                                .toList(),
                "Error de base de datos al consultar productos");
    }

    public ProductResponse getProductById(Long id) {
        return productMapper.toResponse(findProduct(id));
    }

    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        return executeDatabaseOperation(() -> {
            var entity = findProduct(id);
            productMapper.updateEntityFromRequest(productRequest, entity);
            return productMapper.toResponse(saveProduct(entity));
        }, "Error de base de datos al actualizar el producto");
    }

    public void deleteProductById(Long id) {
        executeDatabaseOperation(() -> {
            var entity = findProduct(id);
            productRepository.delete(entity);
            return null;
        }, "Error de base de datos al eliminar el producto");
    }

    private ProductEntity findProduct(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidProductIdException("El id del producto debe ser mayor a cero");
        }

        return executeDatabaseOperation(() ->
                        productRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id)),
                "Error de base de datos al consultar el producto por id");
    }

    private ProductEntity saveProduct(ProductEntity product) {
        try {
            return productRepository.save(product);
        } catch (DataIntegrityViolationException ex) {
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
            throw new ExternalServiceException(errorMessage, ex);
        }
    }
}
