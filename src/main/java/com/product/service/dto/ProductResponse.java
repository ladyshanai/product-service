package com.product.service.dto;

import com.product.service.enums.ProductType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        Long customerId,
        ProductType productType,
        String productNumber,
        BigDecimal balance,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
