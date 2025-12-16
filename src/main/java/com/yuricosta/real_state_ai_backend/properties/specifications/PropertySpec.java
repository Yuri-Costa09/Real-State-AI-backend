package com.yuricosta.real_state_ai_backend.properties.specifications;

import com.yuricosta.real_state_ai_backend.properties.Property;
import com.yuricosta.real_state_ai_backend.properties.enums.ListingType;
import com.yuricosta.real_state_ai_backend.properties.enums.PropertyType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class PropertySpec {
    private static Specification<Property> empty() {
        return (root, query, criteriaBuilder) -> null;
    }

    public static Specification<Property> hasCity(String city) {
        if (city == null)
            return empty();

        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("address").get("city"), city);
    }

    public static Specification<Property> hasState(String state) {
        if (state == null)
            return empty();

        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("address").get("state"), state);
    }

    public static Specification<Property> hasMaxPriceOf(BigDecimal maxPrice) {
        if (maxPrice == null)
            return empty();

        return (root, query, criteriaBuilder) -> criteriaBuilder.or(
                criteriaBuilder.and(
                        criteriaBuilder.isNotNull(root.get("salePrice")),
                        criteriaBuilder.lessThanOrEqualTo(root.get("salePrice"), maxPrice)),
                criteriaBuilder.and(
                        criteriaBuilder.isNotNull(root.get("rentPrice")),
                        criteriaBuilder.lessThanOrEqualTo(root.get("rentPrice"), maxPrice)));
    }

    public static Specification<Property> hasPropertyTypeOf(PropertyType propertyType) {
        if (propertyType == null)
            return empty();

        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("propertyType"), propertyType);
    }

    public static Specification<Property> hasListingTypeOf(ListingType listingType) {
        if (listingType == null)
            return empty();

        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("listingType"), listingType);
    }

    public static Specification<Property> hasMinBedroomsOf(Integer minBedrooms) {
        if (minBedrooms == null)
            return empty();

        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("bedrooms"),
                minBedrooms);
    }

    public static Specification<Property> hasMinBathroomsOf(Integer minBathrooms) {
        if (minBathrooms == null)
            return empty();

        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("bathrooms"),
                minBathrooms);
    }

    public static Specification<Property> hasMinParkingSpacesOf(Integer minParkingSpaces) {
        if (minParkingSpaces == null)
            return empty();

        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("parkingSpaces"),
                minParkingSpaces);
    }

    public static Specification<Property> HasMinAreaOf(BigDecimal minArea) {
        if (minArea == null)
            return empty();

        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("area"), minArea);
    }

    public static Specification<Property> hasMaxAreaOf(BigDecimal maxArea) {
        if (maxArea == null)
            return empty();

        return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("area"), maxArea);
    }

    public static Specification<Property> isFurnished(Boolean isFurnished) {
        if (isFurnished == null)
            return empty();

        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isFurnished"), isFurnished);
    }

    public static Specification<Property> acceptsPets(Boolean acceptsPets) {
        if (acceptsPets == null)
            return empty();

        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("acceptsPets"), acceptsPets);
    }
}
