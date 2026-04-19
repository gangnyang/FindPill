package com.example.med_classification.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PillInfoRequestDto {
    private Integer idx;
    private String name;
    private String color;
    private String material;
    private String company;
    private String shape;
    private String print_front;
    private String print_back;
    private String effect;
    private String dosage;
    private String warning;
    private String image;
}
