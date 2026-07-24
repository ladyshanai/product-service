package com.product.service.dto;

import com.product.service.enums.ProductType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductRequest(
        @NotNull(message = "customerId es obligatorio")
        @Positive(message = "customerId debe ser mayor a 0")
        Long customerId,
        @NotNull(message = "productType es obligatorio")
        ProductType productType,
        @NotBlank(message = "productNumber es obligatorio")
        @Size(max = 50, message = "productNumber no debe superar 50 caracteres")
        String productNumber,
        @DecimalMin(value = "0.00", message = "balance no puede ser negativo")
        @Digits(integer = 13, fraction = 2, message = "balance debe tener hasta 13 enteros y 2 decimales")
        BigDecimal balance,
        Boolean active
) {
}
