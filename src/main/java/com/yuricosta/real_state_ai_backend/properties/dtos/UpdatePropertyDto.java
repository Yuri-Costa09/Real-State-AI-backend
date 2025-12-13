package com.yuricosta.real_state_ai_backend.properties.dtos;

import com.yuricosta.real_state_ai_backend.properties.enums.PropertyType;

import java.math.BigDecimal;

public record UpdatePropertyDto(
        BigDecimal rentPrice,
        BigDecimal salePrice,
        BigDecimal condoFee,
        BigDecimal propertyTax,
        String description,
        PropertyType propertyType,
        Integer bedrooms,
        Integer bathrooms,
        Integer parkingSpaces,
        BigDecimal area,
        Boolean isFurnished,
        Boolean acceptsPets,
        AddressDto address
) {
}
