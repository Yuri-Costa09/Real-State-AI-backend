package com.yuricosta.real_state_ai_backend.properties.mappers;

import com.yuricosta.real_state_ai_backend.properties.Address;
import com.yuricosta.real_state_ai_backend.properties.Property;
import com.yuricosta.real_state_ai_backend.properties.dtos.AddressDto;
import com.yuricosta.real_state_ai_backend.properties.dtos.PropertyResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class PropertyMapper {
    public PropertyResponseDto toResponseDto(Property property) {
        if (property == null) return null;

        return new PropertyResponseDto(
                property.getId(),
                property.getCreatedAt(),
                property.getRentPrice(),
                property.getSalePrice(),
                property.getCondoFee(),
                property.getPropertyTax(),
                property.getDescription(),
                property.getPropertyType(),
                property.getListingType(),
                property.getStatus(),
                property.getBedrooms(),
                property.getBathrooms(),
                property.getParkingSpaces(),
                property.getArea(),
                property.getIsFurnished(),
                property.getAcceptsPets(),
                toAddressDto(property.getAddress()),
                property.getLatitude(),
                property.getLongitude(),
                property.getOwner().getId()
        );
    }

    public AddressDto toAddressDto(Address address) {
        if (address == null) return null;

        return new AddressDto(
                address.getStreet(),
                address.getNumber(),
                address.getComplement(),
                address.getNeighborhood(),
                address.getCity(),
                address.getState(),
                address.getZipcode()
        );
    }

    public List<PropertyResponseDto> toResponseDtoList(List<Property> properties) {
        return properties.stream()
                .map(this::toResponseDto)
                .toList();
    }
}
