package com.yuricosta.real_state_ai_backend.properties;

import com.yuricosta.real_state_ai_backend.properties.enums.ListingType;
import com.yuricosta.real_state_ai_backend.properties.enums.PropertyStatus;
import com.yuricosta.real_state_ai_backend.properties.enums.PropertyType;
import com.yuricosta.real_state_ai_backend.shared.BaseEntity;
import com.yuricosta.real_state_ai_backend.users.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Builder
@AllArgsConstructor
@Getter @Setter
@Table(name = "properties")
public class Property extends BaseEntity {

    // -- MONEY RELATED --
    @Column
    private BigDecimal rentPrice;
    @Column
    private BigDecimal salePrice;
    @Column
    private BigDecimal condoFee;
    @Column
    private BigDecimal propertyTax; // IPTU

    @Column(length = 2000)
    private String description;

    // -- PROPERTY TYPES --
    @Enumerated(EnumType.STRING)
    private PropertyType propertyType; // APARTMENT, HOUSE, STUDIO, KITNET, COMMERCIAL
    @Enumerated(EnumType.STRING)
    private ListingType listingType; // RENT, SALE
    @Enumerated(EnumType.STRING)
    private PropertyStatus status; // DRAFT, PUBLISHED, PAUSED

    // -- PROPERTY CHARACTERISTICS --
    @Column(nullable = false)
    private Integer bedrooms;
    @Column(nullable = false)
    private Integer bathrooms;
    @Column(nullable = false)
    private Integer parkingSpaces;
    @Column(nullable = false)
    private BigDecimal area; // m²
    @Column(nullable = false)
    private Boolean isFurnished;
    @Column(nullable = false)
    private Boolean acceptsPets;
    @Embedded
    private Address address;

    private Double latitude;
    private Double longitude;

    // -- RELATIONSHIPS --
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    public Property() {
    }

}
