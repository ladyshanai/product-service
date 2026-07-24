package com.product.service.mapper;

import com.product.service.dto.ProductRequest;
import com.product.service.dto.ProductResponse;
import com.product.service.entity.ProductEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponse toResponse(ProductEntity entity);

    @Mapping(target = "id", ignore = true)
    ProductEntity toEntity(ProductRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(ProductRequest request, @MappingTarget ProductEntity entity);

    @AfterMapping
    default void setDefaults(ProductRequest request, @MappingTarget ProductEntity entity) {
        if (entity.getBalance() == null) {
            entity.setBalance(BigDecimal.ZERO);
        }

        if (entity.getActive() == null) {
            entity.setActive(Boolean.TRUE);
        }

        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }

        entity.setUpdatedAt(LocalDateTime.now());
    }
}
