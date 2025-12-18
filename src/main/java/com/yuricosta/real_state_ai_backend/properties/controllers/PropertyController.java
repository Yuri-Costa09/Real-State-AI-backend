package com.yuricosta.real_state_ai_backend.properties.controllers;

import com.yuricosta.real_state_ai_backend.properties.Property;
import com.yuricosta.real_state_ai_backend.properties.ai.SemanticSearchService;
import com.yuricosta.real_state_ai_backend.properties.dtos.*;
import com.yuricosta.real_state_ai_backend.properties.mappers.PropertyMapper;
import com.yuricosta.real_state_ai_backend.properties.useCases.CreatePropertyForRentalUseCase;
import com.yuricosta.real_state_ai_backend.properties.useCases.CreatePropertyForSaleUseCase;
import com.yuricosta.real_state_ai_backend.properties.useCases.PropertyService;
import com.yuricosta.real_state_ai_backend.properties.useCases.UpdatePropertyUseCase;
import com.yuricosta.real_state_ai_backend.shared.ApiResponse;
import com.yuricosta.real_state_ai_backend.shared.PagedResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

// TODO: Add logging for important events and errors
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
        private final SemanticSearchService semanticSearchService;

        public PropertyController(CreatePropertyForSaleUseCase createPropertyForSaleUseCase,
                                  CreatePropertyForRentalUseCase createPropertyForRentalUseCase,
                                  PropertyService propertyService, UpdatePropertyUseCase updatePropertyUseCase,
                                  PropertyMapper propertyMapper, SemanticSearchService semanticSearchService) {
                this.createPropertyForSaleUseCase = createPropertyForSaleUseCase;
                this.createPropertyForRentalUseCase = createPropertyForRentalUseCase;
                this.propertyService = propertyService;
                this.updatePropertyUseCase = updatePropertyUseCase;
                this.propertyMapper = propertyMapper;
                this.semanticSearchService = semanticSearchService;
        }

        @GetMapping
        public ResponseEntity<ApiResponse<PagedResponse<PropertyResponseDto>>> getAllProperties(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "15") int size,
                        @ModelAttribute PropertyFilter filter) {
                var pageResult = propertyService.getAllProperties(page, size, filter);

                PagedResponse<PropertyResponseDto> pagedResponse = new PagedResponse<>(
                                propertyMapper.toResponseDtoList(pageResult.getContent()),
                                pageResult.getNumber(),
                                pageResult.getSize(),
                                pageResult.getTotalElements(),
                                pageResult.getTotalPages(),
                                pageResult.hasNext());

                ApiResponse<PagedResponse<PropertyResponseDto>> apiResponse = ApiResponse.success(
                                pagedResponse,
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

        @PostMapping("/search")
        public ResponseEntity<ApiResponse<PropertyFilter>> semanticSearch(
                        @Valid @RequestBody SemanticSearchRequest request) throws Exception {
                PropertyFilter filter = semanticSearchService.performSemanticSearch(request.text());

                ApiResponse<PropertyFilter> apiResponse = ApiResponse.success(
                                filter,
                                "AI search completed successfully");
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
