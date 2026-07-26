package com.product.service.service;

import com.product.service.dto.ProductRequest;
import com.product.service.dto.ProductResponse;
import com.product.service.entity.ProductEntity;
import com.product.service.enums.ProductType;
import com.product.service.exception.DuplicateProductException;
import com.product.service.exception.ExternalServiceException;
import com.product.service.exception.InvalidProductIdException;
import com.product.service.exception.ResourceNotFoundException;
import com.product.service.mapper.ProductMapper;
import com.product.service.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    @Test
    void createProductShouldReturnCreatedProduct() {
        var request = buildRequest();
        var entity = buildEntity(1L, 10L);
        var response = buildResponse(1L, 10L);

        when(productMapper.toEntity(request)).thenReturn(entity);
        when(productRepository.save(entity)).thenReturn(entity);
        when(productMapper.toResponse(entity)).thenReturn(response);

        var result = productService.createProduct(request);

        assertEquals(1L, result.id());
        assertEquals("ACC-100", result.productNumber());
        verify(productMapper).toEntity(request);
        verify(productRepository).save(entity);
    }

    @Test
    void createProductShouldThrowDuplicateProductExceptionWhenUniqueConstraintFails() {
        var request = buildRequest();
        var entity = buildEntity(null, 10L);

        when(productMapper.toEntity(request)).thenReturn(entity);
        when(productRepository.save(entity)).thenThrow(new DataIntegrityViolationException("duplicate"));

        var exception = assertThrows(
                DuplicateProductException.class,
                () -> productService.createProduct(request)
        );

        assertTrue(exception.getMessage().contains("mismo productNumber"));
    }

    @Test
    void getAllProductsShouldReturnMappedList() {
        var first = buildEntity(1L, 10L);
        var second = buildEntity(2L, 11L);
        var firstResponse = buildResponse(1L, 10L);
        var secondResponse = buildResponse(2L, 11L);

        when(productRepository.findAll()).thenReturn(List.of(first, second));
        when(productMapper.toResponse(first)).thenReturn(firstResponse);
        when(productMapper.toResponse(second)).thenReturn(secondResponse);

        var result = productService.getAllProducts();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals(2L, result.get(1).id());
    }

    @Test
    void getAllProductsShouldWrapDataAccessExceptionAsExternalServiceException() {
        when(productRepository.findAll()).thenThrow(new DataAccessResourceFailureException("db down"));

        var exception = assertThrows(
                ExternalServiceException.class,
                () -> productService.getAllProducts()
        );

        assertTrue(exception.getMessage().contains("consultar productos"));
    }

    @Test
    void getProductByIdShouldReturnProduct() {
        var entity = buildEntity(4L, 30L);
        var response = buildResponse(4L, 30L);

        when(productRepository.findById(4L)).thenReturn(Optional.of(entity));
        when(productMapper.toResponse(entity)).thenReturn(response);

        var result = productService.getProductById(4L);

        assertEquals(4L, result.id());
        verify(productMapper).toResponse(entity);
    }

    @Test
    void getProductByIdShouldThrowInvalidProductIdExceptionWhenIdIsNotPositive() {
        assertThrows(
                InvalidProductIdException.class,
                () -> productService.getProductById(0L)
        );
    }

    @Test
    void getProductByIdShouldThrowResourceNotFoundExceptionWhenMissing() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.getProductById(99L)
        );
    }

    @Test
    void getProductsByCustomerIdShouldReturnMappedList() {
        var entity = buildEntity(6L, 40L);
        var response = buildResponse(6L, 40L);

        when(productRepository.findByCustomerId(40L)).thenReturn(List.of(entity));
        when(productMapper.toResponse(entity)).thenReturn(response);

        var result = productService.getProductsByCustomerId(40L);

        assertEquals(1, result.size());
        assertEquals(40L, result.get(0).customerId());
    }

    @Test
    void getProductsByCustomerIdShouldThrowInvalidProductIdExceptionWhenCustomerIdIsInvalid() {
        assertThrows(
                InvalidProductIdException.class,
                () -> productService.getProductsByCustomerId(0L)
        );
    }

    @Test
    void updateProductShouldUpdateAndReturnProduct() {
        var request = buildRequest();
        var entity = buildEntity(7L, 50L);
        var response = buildResponse(7L, 50L);

        when(productRepository.findById(7L)).thenReturn(Optional.of(entity));
        when(productRepository.save(entity)).thenReturn(entity);
        when(productMapper.toResponse(entity)).thenReturn(response);

        var result = productService.updateProduct(7L, request);

        assertEquals(7L, result.id());
        verify(productMapper).updateEntityFromRequest(request, entity);
        verify(productRepository).save(entity);
    }

    @Test
    void deleteProductByIdShouldDeleteProduct() {
        var entity = buildEntity(8L, 60L);
        when(productRepository.findById(8L)).thenReturn(Optional.of(entity));

        productService.deleteProductById(8L);

        verify(productRepository).delete(entity);
    }

    @Test
    void deleteProductByIdShouldWrapDataAccessExceptionAsExternalServiceException() {
        var entity = buildEntity(9L, 70L);
        when(productRepository.findById(9L)).thenReturn(Optional.of(entity));
        doThrow(new DataAccessResourceFailureException("db down")).when(productRepository).delete(entity);

        var exception = assertThrows(
                ExternalServiceException.class,
                () -> productService.deleteProductById(9L)
        );

        assertTrue(exception.getMessage().contains("eliminar el producto"));
    }

    private ProductRequest buildRequest() {
        return new ProductRequest(
                10L,
                ProductType.ACCOUNT,
                "ACC-100",
                BigDecimal.valueOf(1200),
                true
        );
    }

    private ProductEntity buildEntity(Long id, Long customerId) {
        var entity = new ProductEntity();
        entity.setId(id);
        entity.setCustomerId(customerId);
        entity.setProductType(ProductType.ACCOUNT);
        entity.setProductNumber("ACC-100");
        entity.setBalance(BigDecimal.valueOf(1200));
        entity.setActive(true);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }

    private ProductResponse buildResponse(Long id, Long customerId) {
        return new ProductResponse(
                id,
                customerId,
                ProductType.ACCOUNT,
                "ACC-100",
                BigDecimal.valueOf(1200),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
