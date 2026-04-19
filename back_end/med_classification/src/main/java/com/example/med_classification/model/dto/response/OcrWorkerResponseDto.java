package com.example.med_classification.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OcrWorkerResponseDto {
    private Integer status;
    @JsonProperty("front_texts")
    private List<String> frontTexts = new ArrayList<>();
    @JsonProperty("back_texts")
    private List<String> backTexts = new ArrayList<>();
}

