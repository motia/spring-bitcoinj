package com.example.demo.dto;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

public class SendCoinsRequest {
    @NotBlank
    @Size(min = 1)
    public List<SendCoinsTarget> targets;
}

