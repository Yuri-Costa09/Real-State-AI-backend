package com.yuricosta.real_state_ai_backend.properties.useCases;

import com.yuricosta.real_state_ai_backend.properties.Address;
import com.yuricosta.real_state_ai_backend.properties.Property;
import com.yuricosta.real_state_ai_backend.properties.dtos.CreatePropertyForRentalDto;
import com.yuricosta.real_state_ai_backend.properties.enums.ListingType;
import com.yuricosta.real_state_ai_backend.properties.repositories.PropertyRepository;
import com.yuricosta.real_state_ai_backend.users.User;
import com.yuricosta.real_state_ai_backend.users.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CreatePropertyForRentalUseCase {

    private final PropertyRepository propertyRepository;
    private final UserService userService;

    public CreatePropertyForRentalUseCase(PropertyRepository propertyRepository, UserService userService) {
        this.propertyRepository = propertyRepository;
        this.userService = userService;
    }

    @Transactional
    public Property execute(CreatePropertyForRentalDto dto, UUID ownerId) {
        Address address = new Address(
                dto.address().street(),
                dto.address().number(),
                dto.address().complement(),
                dto.address().neighborhood(),
                dto.address().city(),
                dto.address().state(),
                dto.address().zipcode(),
                null);
        address.generateFormattedAddress();

        User owner = userService.findById(ownerId);

        // TODO: resolve latitude/longitude through a geocoding provider
        Property property = Property.builder()
                .listingType(ListingType.RENT)
                .rentPrice(dto.rentPrice())
                .condoFee(dto.condoFee())
                .propertyTax(dto.propertyTax())
                .description(dto.description())
                .propertyType(dto.propertyType())
                .bedrooms(dto.bedrooms())
                .bathrooms(dto.bathrooms())
                .parkingSpaces(dto.parkingSpaces())
                .area(dto.area())
                .isFurnished(dto.isFurnished())
                .acceptsPets(dto.acceptsPets())
                .address(address)
                .owner(owner)
                .build();

        return propertyRepository.save(property);
    }
}
