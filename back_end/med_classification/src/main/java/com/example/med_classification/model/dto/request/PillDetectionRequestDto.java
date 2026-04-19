package com.example.med_classification.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PillDetectionRequestDto {
    private String label;
    private String front;
    private String back;
    @JsonProperty("class")
    private String clazz; // 'class'는 예약어이므로 'clazz'로
}
