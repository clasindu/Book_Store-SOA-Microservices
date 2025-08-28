package com.globalbooks.orders.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ShippingAddressDTO {

    @NotNull(message = "Street is required")
    private String street;

    @NotNull(message = "City is required")
    private String city;

    private String state;

    @NotNull(message = "ZIP code is required")
    private String zip;

    private String country = "USA";
}