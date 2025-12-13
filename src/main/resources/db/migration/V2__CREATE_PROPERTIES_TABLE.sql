CREATE TABLE properties
(
    id                UUID                        NOT NULL,
    created_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    rent_price        DECIMAL,
    sale_price        DECIMAL,
    condo_fee         DECIMAL,
    property_tax      DECIMAL,
    description       VARCHAR(2000),
    property_type     VARCHAR(255),
    listing_type      VARCHAR(255),
    status            VARCHAR(255),
    bedrooms          INTEGER                     NOT NULL,
    bathrooms         INTEGER                     NOT NULL,
    parking_spaces    INTEGER                     NOT NULL,
    area              DECIMAL                     NOT NULL,
    is_furnished      BOOLEAN                     NOT NULL,
    accepts_pets      BOOLEAN                     NOT NULL,
    latitude          DOUBLE PRECISION,
    longitude         DOUBLE PRECISION,
    owner_id          UUID,
    addr_street       VARCHAR(255),
    addr_number       VARCHAR(255),
    addr_complement   VARCHAR(255),
    addr_neighborhood VARCHAR(255),
    addr_city         VARCHAR(255),
    addr_state        VARCHAR(2),
    addr_zipcode      VARCHAR(8),
    addr_formatted    VARCHAR(300),
    CONSTRAINT pk_properties PRIMARY KEY (id)
);

ALTER TABLE properties
    ADD CONSTRAINT FK_PROPERTIES_ON_OWNER FOREIGN KEY (owner_id) REFERENCES users (id);