package com.example.med_classification.model.dto.response;

import com.example.med_classification.model.dto.request.PillDetectionRequestDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class YoloWorkerResponseDto {
    private Integer status;
    private List<PillDetectionRequestDto> results = new ArrayList<>();
}

