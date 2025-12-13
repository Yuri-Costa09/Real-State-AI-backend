package com.yuricosta.real_state_ai_backend.properties.controllers;

import com.yuricosta.real_state_ai_backend.properties.Property;
import com.yuricosta.real_state_ai_backend.properties.dtos.*;
import com.yuricosta.real_state_ai_backend.properties.mappers.PropertyMapper;
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
        private final PropertyMapper propertyMapper;

        public PropertyController(CreatePropertyForSaleUseCase createPropertyForSaleUseCase,
                                  CreatePropertyForRentalUseCase createPropertyForRentalUseCase,
                                  PropertyService propertyService, UpdatePropertyUseCase updatePropertyUseCase, PropertyMapper propertyMapper) {
                this.createPropertyForSaleUseCase = createPropertyForSaleUseCase;
                this.createPropertyForRentalUseCase = createPropertyForRentalUseCase;
                this.propertyService = propertyService;
                this.updatePropertyUseCase = updatePropertyUseCase;
                this.propertyMapper = propertyMapper;
        }

        @GetMapping
        public ResponseEntity<ApiResponse<List<PropertyResponseDto>>> getAllProperties() {
                List<Property> properties = propertyService.getAllProperties();

                ApiResponse<List<PropertyResponseDto>> apiResponse = ApiResponse.success(
                                propertyMapper.toResponseDtoList(properties),
                                "Properties retrieved successfully");
                return ResponseEntity.ok(apiResponse);
        }

        @GetMapping("/{id}")
        public ResponseEntity<ApiResponse<PropertyResponseDto>> getPropertyById(@PathVariable UUID id) {
                Property property = propertyService.getPropertyById(id);

                ApiResponse<PropertyResponseDto> apiResponse = ApiResponse.success(
                                propertyMapper.toResponseDto(property),
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

                ApiResponse<PropertyResponseDto> apiResponse = ApiResponse.success(
                                propertyMapper.toResponseDto(property),
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

                ApiResponse<PropertyResponseDto> apiResponse = ApiResponse.success(
                                propertyMapper.toResponseDto(property),
                                "Property created successfully");

                return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
        }

        @PreAuthorize("isAuthenticated()")
        @PutMapping("/{id}")
        public ResponseEntity<ApiResponse<PropertyResponseDto>> updateProperty(
                @PathVariable UUID id,
                @Valid @RequestBody UpdatePropertyDto request) {

                Property property = updatePropertyUseCase.execute(request, id);

                ApiResponse<PropertyResponseDto> apiResponse = ApiResponse.success(
                        propertyMapper.toResponseDto(property),
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
