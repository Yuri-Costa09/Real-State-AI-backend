package com.yuricosta.real_state_ai_backend.properties.useCases;

import com.yuricosta.real_state_ai_backend.properties.Property;
import com.yuricosta.real_state_ai_backend.properties.dtos.UpdatePropertyDto;
import com.yuricosta.real_state_ai_backend.properties.repositories.PropertyRepository;
import com.yuricosta.real_state_ai_backend.properties.enums.ListingType;
import com.yuricosta.real_state_ai_backend.shared.errors.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UpdatePropertyUseCase {
    private final PropertyRepository propertyRepository;

    public UpdatePropertyUseCase(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    @Transactional
    public Property execute(UpdatePropertyDto dto, UUID id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Property not found"));

        // Update common fields
        property.setDescription(dto.description());
        property.setBedrooms(dto.bedrooms());
        property.setBathrooms(dto.bathrooms());
        property.setParkingSpaces(dto.parkingSpaces());
        property.setArea(dto.area());
        property.setIsFurnished(dto.isFurnished());
        property.setAcceptsPets(dto.acceptsPets());
        property.setCondoFee(dto.condoFee());
        property.setPropertyTax(dto.propertyTax());
        property.setPropertyType(dto.propertyType());

        if (property.getAddress() != null && dto.address() != null) {
            property.getAddress().setStreet(dto.address().street());
            property.getAddress().setNumber(dto.address().number());
            property.getAddress().setComplement(dto.address().complement());
            property.getAddress().setNeighborhood(dto.address().neighborhood());
            property.getAddress().setCity(dto.address().city());
            property.getAddress().setState(dto.address().state());
            property.getAddress().setZipcode(dto.address().zipcode());
            property.getAddress().generateFormattedAddress();
        }

        // Handle specific fields based on listing type
        ListingType listingType = property.getListingType();
        if (listingType == ListingType.SALE) {
            if (dto.salePrice() == null) {
                throw new IllegalArgumentException("Sale price is required for sale listings");
            }
            property.setSalePrice(dto.salePrice());
            property.setRentPrice(null);
        } else if (listingType == ListingType.RENT) {
            if (dto.rentPrice() == null) {
                throw new IllegalArgumentException("Rent price is required for rental listings");
            }
            property.setRentPrice(dto.rentPrice());
            property.setSalePrice(null);
        }

        return propertyRepository.save(property);
    }
}
