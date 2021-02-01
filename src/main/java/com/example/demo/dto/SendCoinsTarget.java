package com.example.demo.dto;

import com.example.demo.DestinationAddressConstraint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

public class SendCoinsTarget {
    @NotBlank
    @DestinationAddressConstraint
    public String address;

    @Positive
    public long satoshis;
}

