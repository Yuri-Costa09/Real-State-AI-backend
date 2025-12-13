package com.yuricosta.real_state_ai_backend.properties.dtos;

import com.yuricosta.real_state_ai_backend.properties.enums.ListingType;
import com.yuricosta.real_state_ai_backend.properties.enums.PropertyStatus;
import com.yuricosta.real_state_ai_backend.properties.enums.PropertyType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for {@link com.yuricosta.real_state_ai_backend.properties.Property}
 */
public record PropertyResponseDto(UUID id, Instant createdAt, BigDecimal rentPrice, BigDecimal salePrice,
                                  BigDecimal condoFee, BigDecimal propertyTax, String description,
                                  PropertyType propertyType, ListingType listingType, PropertyStatus status,
                                  Integer bedrooms, Integer bathrooms, Integer parkingSpaces, BigDecimal area,
                                  Boolean isFurnished, Boolean acceptsPets, AddressDto address, Double latitude,
                                  Double longitude, UUID ownerId) implements Serializable {
}