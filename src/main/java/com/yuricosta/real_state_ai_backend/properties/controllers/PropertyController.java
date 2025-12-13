package com.yuricosta.real_state_ai_backend.properties.controllers;

import com.yuricosta.real_state_ai_backend.properties.Property;
import com.yuricosta.real_state_ai_backend.properties.dtos.*;
import com.yuricosta.real_state_ai_backend.properties.useCases.CreatePropertyForRentalUseCase;
import com.yuricosta.real_state_ai_backend.properties.useCases.CreatePropertyForSaleUseCase;
import com.yuricosta.real_state_ai_backend.properties.useCases.PropertyService;
import com.yuricosta.real_state_ai_backend.properties.useCases.UpdatePropertyUseCase;
import com.yuricosta.real_state_ai_backend.shared.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// TODO: Utilize mappers like MapStruct to convert between DTOs and Entities
// TODO: Implement pagination and filtering for GET /properties endpoint
// TODO: Add logging for important events and errors
// TODO: Implement caching for frequently accessed data
// TODO: Add Swagger/OpenAPI documentation for the endpoints
// TODO: Implement user id validation (if resource belongs to the user)

@RestController
@RequestMapping("/api/v1/properties")
public class PropertyController {
        private final CreatePropertyForSaleUseCase createPropertyForSaleUseCase;
        private final CreatePropertyForRentalUseCase createPropertyForRentalUseCase;
        private final PropertyService propertyService;
        private final UpdatePropertyUseCase updatePropertyUseCase;

        public PropertyController(CreatePropertyForSaleUseCase createPropertyForSaleUseCase,
                                  CreatePropertyForRentalUseCase createPropertyForRentalUseCase,
                                  PropertyService propertyService, UpdatePropertyUseCase updatePropertyUseCase) {
                this.createPropertyForSaleUseCase = createPropertyForSaleUseCase;
                this.createPropertyForRentalUseCase = createPropertyForRentalUseCase;
                this.propertyService = propertyService;
                this.updatePropertyUseCase = updatePropertyUseCase;
        }

        @GetMapping
        public ResponseEntity<ApiResponse<List<PropertyResponseDto>>> getAllProperties() {
                List<PropertyResponseDto> properties = propertyService.getAllProperties().stream().map(x -> {
                        AddressDto addressDto = new AddressDto(
                                        x.getAddress().getStreet(),
                                        x.getAddress().getNumber(),
                                        x.getAddress().getComplement(),
                                        x.getAddress().getNeighborhood(),
                                        x.getAddress().getCity(),
                                        x.getAddress().getState(),
                                        x.getAddress().getZipcode());

                        return new PropertyResponseDto(
                                        x.getId(),
                                        x.getCreatedAt(),
                                        x.getRentPrice(),
                                        x.getSalePrice(),
                                        x.getCondoFee(),
                                        x.getPropertyTax(),
                                        x.getDescription(),
                                        x.getPropertyType(),
                                        x.getListingType(),
                                        x.getStatus(),
                                        x.getBedrooms(),
                                        x.getBathrooms(),
                                        x.getParkingSpaces(),
                                        x.getArea(),
                                        x.getIsFurnished(),
                                        x.getAcceptsPets(),
                                        addressDto,
                                        x.getLatitude(),
                                        x.getLongitude(),
                                        x.getOwner().getId());
                }).toList();

                ApiResponse<List<PropertyResponseDto>> apiResponse = ApiResponse.success(
                                properties,
                                "Properties retrieved successfully");
                return ResponseEntity.ok(apiResponse);
        }

        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse<PropertyResponseDto>> getPropertyById(@PathVariable UUID id) {
                Property x = propertyService.getPropertyById(id);
                AddressDto addressDto = new AddressDto(
                                x.getAddress().getStreet(),
                                x.getAddress().getNumber(),
                                x.getAddress().getComplement(),
                                x.getAddress().getNeighborhood(),
                                x.getAddress().getCity(),
                                x.getAddress().getState(),
                                x.getAddress().getZipcode());

                PropertyResponseDto responseDto = new PropertyResponseDto(
                                x.getId(),
                                x.getCreatedAt(),
                                x.getRentPrice(),
                                x.getSalePrice(),
                                x.getCondoFee(),
                                x.getPropertyTax(),
                                x.getDescription(),
                                x.getPropertyType(),
                                x.getListingType(),
                                x.getStatus(),
                                x.getBedrooms(),
                                x.getBathrooms(),
                                x.getParkingSpaces(),
                                x.getArea(),
                                x.getIsFurnished(),
                                x.getAcceptsPets(),
                                addressDto,
                                x.getLatitude(),
                                x.getLongitude(),
                                x.getOwner().getId());

                ApiResponse<PropertyResponseDto> apiResponse = ApiResponse.success(
                                responseDto,
                                "Property retrieved successfully");
                return ResponseEntity.ok(apiResponse);
        }

        @PreAuthorize("isAuthenticated()")
        @PostMapping("/sale")
        public ResponseEntity<ApiResponse<PropertyResponseDto>> createPropertyForSale(
                        @Valid @RequestBody CreatePropertyForSaleDto request,
                        Authentication authentication) {
                Jwt jwt = (Jwt) authentication.getPrincipal();
                UUID userId = UUID.fromString(jwt.getSubject());

                Property property = createPropertyForSaleUseCase.execute(request, userId);
                AddressDto addressDto = new AddressDto(
                                property.getAddress().getStreet(),
                                property.getAddress().getNumber(),
                                property.getAddress().getComplement(),
                                property.getAddress().getNeighborhood(),
                                property.getAddress().getCity(),
                                property.getAddress().getState(),
                                property.getAddress().getZipcode());

                PropertyResponseDto responseDto = new PropertyResponseDto(
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
                                addressDto,
                                property.getLatitude(),
                                property.getLongitude(),
                                property.getOwner().getId());

                ApiResponse<PropertyResponseDto> apiResponse = ApiResponse.success(
                                responseDto,
                                "Property created successfully");

                return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
        }

        @PreAuthorize("isAuthenticated()")
        @PostMapping("/rental")
        public ResponseEntity<ApiResponse<PropertyResponseDto>> createPropertyForRental(
                        @Valid @RequestBody CreatePropertyForRentalDto request,
                        Authentication authentication) {
                Jwt jwt = (Jwt) authentication.getPrincipal();
                UUID userId = UUID.fromString(jwt.getSubject());

                Property property = createPropertyForRentalUseCase.execute(request, userId);

                AddressDto addressDto = new AddressDto(
                                property.getAddress().getStreet(),
                                property.getAddress().getNumber(),
                                property.getAddress().getComplement(),
                                property.getAddress().getNeighborhood(),
                                property.getAddress().getCity(),
                                property.getAddress().getState(),
                                property.getAddress().getZipcode());

                PropertyResponseDto responseDto = new PropertyResponseDto(
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
                                addressDto,
                                property.getLatitude(),
                                property.getLongitude(),
                                property.getOwner().getId());

                ApiResponse<PropertyResponseDto> apiResponse = ApiResponse.success(
                                responseDto,
                                "Property created successfully");

                return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
        }

        @PreAuthorize("isAuthenticated()")
        @PutMapping("/{id}")
        public ResponseEntity<ApiResponse<PropertyResponseDto>> updateProperty(
                @PathVariable UUID id,
                @Valid @RequestBody UpdatePropertyDto request) {

                Property property = updatePropertyUseCase.execute(request, id);

                AddressDto addressDto = new AddressDto(
                                property.getAddress().getStreet(),
                                property.getAddress().getNumber(),
                                property.getAddress().getComplement(),
                                property.getAddress().getNeighborhood(),
                                property.getAddress().getCity(),
                                property.getAddress().getState(),
                                property.getAddress().getZipcode());

                PropertyResponseDto responseDto = new PropertyResponseDto(
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
                        addressDto,
                        property.getLatitude(),
                        property.getLongitude(),
                        property.getOwner().getId());

                ApiResponse<PropertyResponseDto> apiResponse = ApiResponse.success(
                        responseDto,
                        "Property updated successfully");

                return ResponseEntity.ok(apiResponse);
        }

        @PreAuthorize("isAuthenticated()")
        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponse<Void>> deleteProperty(@PathVariable UUID id) {
            propertyService.deleteProperty(id);
            return ResponseEntity.ok(
                    ApiResponse.success(null, "Property deleted successfully"));
        }
}
