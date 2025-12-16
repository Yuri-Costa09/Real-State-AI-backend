package com.yuricosta.real_state_ai_backend.properties.useCases;

import com.yuricosta.real_state_ai_backend.properties.Property;
import com.yuricosta.real_state_ai_backend.properties.dtos.PropertyFilter;
import com.yuricosta.real_state_ai_backend.properties.repositories.PropertyRepository;
import com.yuricosta.real_state_ai_backend.properties.specifications.PropertySpec;
import com.yuricosta.real_state_ai_backend.shared.errors.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    public Page<Property> getAllProperties(int page, int size, PropertyFilter filter) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Specification<Property> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        spec = spec.and(PropertySpec.hasCity(filter.city()))
                .and(PropertySpec.hasState(filter.state()))
                .and(PropertySpec.hasMaxPriceOf(filter.maxPrice()))
                .and(PropertySpec.hasPropertyTypeOf(filter.propertyType()))
                .and(PropertySpec.hasListingTypeOf(filter.listingType()))
                .and(PropertySpec.hasMinBedroomsOf(filter.minBedrooms()))
                .and(PropertySpec.hasMinBathroomsOf(filter.minBathrooms()))
                .and(PropertySpec.hasMinParkingSpacesOf(filter.minParkingSpaces()))
                .and(PropertySpec.HasMinAreaOf(filter.minArea()))
                .and(PropertySpec.hasMaxAreaOf(filter.maxArea()))
                .and(PropertySpec.isFurnished(filter.isFurnished()))
                .and(PropertySpec.acceptsPets(filter.acceptsPets()));

        return propertyRepository.findAll(spec, pageable);
    }

    public Property getPropertyById(UUID id) {
        return propertyRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Property not found."));
    }

    @Transactional
    public void deleteProperty(UUID id) {
        propertyRepository.deleteById(id);
    }

}
