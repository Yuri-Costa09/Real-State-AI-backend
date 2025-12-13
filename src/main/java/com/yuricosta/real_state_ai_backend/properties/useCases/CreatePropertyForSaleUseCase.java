package com.yuricosta.real_state_ai_backend.properties.useCases;

import com.yuricosta.real_state_ai_backend.properties.Address;
import com.yuricosta.real_state_ai_backend.properties.Property;
import com.yuricosta.real_state_ai_backend.properties.dtos.CreatePropertyForSaleDto;
import com.yuricosta.real_state_ai_backend.properties.enums.ListingType;
import com.yuricosta.real_state_ai_backend.properties.repositories.PropertyRepository;
import com.yuricosta.real_state_ai_backend.users.User;
import com.yuricosta.real_state_ai_backend.users.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CreatePropertyForSaleUseCase {

    private final PropertyRepository propertyRepository;
    private final UserService userService;

    public CreatePropertyForSaleUseCase(PropertyRepository propertyRepository, UserService userService) {
        this.propertyRepository = propertyRepository;
        this.userService = userService;
    }

    @Transactional
    public Property execute(CreatePropertyForSaleDto dto, UUID ownerId) {

        // -- Mapping DTO to Address entity --
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

        // -- Getting user from ownerId --
        User owner = userService.findById(ownerId);

        // -- Mapping DTO to Property entity --
        // TODO: get latitude and longitude from address using geocoding service
        Property property = new Property().builder()
                .listingType(ListingType.SALE)
                .salePrice(dto.salePrice())
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
