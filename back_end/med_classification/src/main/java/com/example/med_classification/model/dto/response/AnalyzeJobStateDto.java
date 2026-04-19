package com.example.med_classification.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyzeJobStateDto {
    private String jobId;
    private String phase;
    private String status;
    private List<PillLookupResponseDto> preliminaryResults;
    private List<PillLookupResponseDto> finalResults;
    private String errorMessage;
}

