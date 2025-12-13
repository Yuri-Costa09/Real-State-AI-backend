package com.yuricosta.real_state_ai_backend.properties;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class Address {
    @Column(name = "addr_street")
    private String street;

    @Column(name = "addr_number")
    private String number;

    @Column(name = "addr_complement")
    private String complement;

    @Column(name = "addr_neighborhood")
    private String neighborhood;

    @Column(name = "addr_city")
    private String city;

    @Column(name = "addr_state", length = 2)
    private String state; // SP, RJ...

    @Column(name = "addr_zipcode", length = 8)
    private String zipcode; // só dígitos, ex: 01310930

    @Column(name = "addr_formatted", length = 300)
    private String formattedAddress;

    public void generateFormattedAddress() {
        this.formattedAddress = String.join(", ",
                street + " " + number,
                neighborhood,
                city + " - " + state,
                zipcode
        );
    }
}
