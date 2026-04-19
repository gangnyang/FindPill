package com.example.med_classification.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PillLookupRequestDto {
    private String detectedText;
    private String shape;
    private String color;

    private String printFront;
    private String printBack;

    @JsonProperty("class")
    private String clazz;  // "class"는 자바 예약어라서 내부 변수는 clazz로
}
