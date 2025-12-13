package com.yuricosta.real_state_ai_backend.properties.dtos;

import com.yuricosta.real_state_ai_backend.properties.enums.PropertyType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link com.yuricosta.real_state_ai_backend.properties.Property}
 */
public record CreatePropertyForRentalDto(
        @NotNull(message = "Rent price should not be null.")
        @Positive(message = "Rent price should be bigger than 0.")
        BigDecimal rentPrice,
        @NotNull @PositiveOrZero
        BigDecimal condoFee,
        BigDecimal propertyTax,
        @Length(max = 2000)
        String description,
        @NotNull
        PropertyType propertyType,
        @NotNull @PositiveOrZero Integer bedrooms,
        @NotNull @PositiveOrZero Integer bathrooms,
        @NotNull @PositiveOrZero Integer parkingSpaces,
        @NotNull @Positive BigDecimal area,
        @NotNull Boolean isFurnished,
        @NotNull Boolean acceptsPets,
        @NotNull AddressDto address) implements Serializable {
}