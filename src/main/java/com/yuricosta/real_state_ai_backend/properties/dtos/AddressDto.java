package com.yuricosta.real_state_ai_backend.properties.dtos;

import java.io.Serializable;

/**
 * DTO for {@link com.yuricosta.real_state_ai_backend.properties.Address}
 */
public record AddressDto(String street,
                         String number,
                         String complement,
                         String neighborhood,
                         String city,
                         String state,
                         String zipcode) implements Serializable {
}