package com.yuricosta.real_state_ai_backend.properties.dtos;

import com.yuricosta.real_state_ai_backend.properties.enums.ListingType;
import com.yuricosta.real_state_ai_backend.properties.enums.PropertyType;
import lombok.Data;

import java.math.BigDecimal;

public record PropertyFilter(
        BigDecimal maxPrice,
        String city,
        String state,
        PropertyType propertyType, // APARTMENT, HOUSE, STUDIO, KITNET, COMMERCIAL
        ListingType listingType, // RENT, SALE
        Integer minBedrooms,
        Integer minBathrooms,
        Integer minParkingSpaces,
        BigDecimal minArea,
        BigDecimal maxArea,
        Boolean isFurnished,
        Boolean acceptsPets
) {
}
