package com.andre.orders_apis.dto;

import com.andre.orders_apis.entity.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequestDto {

    @NotBlank
    @Size(min = 1, max = 100)
    private String brand;

    @NotBlank
    @Size(min = 1, max = 100)
    private String model;

    @NotNull
    private BigDecimal price;

    @NotNull
    private Category category;

    @NotNull
    private Integer stockQuantity;

    @NotBlank
    @Size(min = 1, max = 200)
    private String description;

}