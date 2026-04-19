package com.example.med_classification.model.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PillDetectionBatchRequestDto {
    private List<PillDetectionRequestDto> items;
}
