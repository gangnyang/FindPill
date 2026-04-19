package com.example.med_classification.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class AnalyzeResponseDto {
    private String status;
    private String phase;
    @JsonProperty("job_id")
    private String jobId;
    private List<PillLookupResponseDto> results;
}

