package com.example.med_classification.service;

import com.example.med_classification.model.dto.response.AnalyzeJobStateDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisJobService {
    private static final Duration JOB_TTL = Duration.ofMinutes(5);
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void save(AnalyzeJobStateDto jobState) {
        String key = key(jobState.getJobId());
        try {
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(jobState), JOB_TTL);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize job state", e);
        }
    }

    public Optional<AnalyzeJobStateDto> get(String jobId) {
        String raw = redisTemplate.opsForValue().get(key(jobId));
        if (raw == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(raw, AnalyzeJobStateDto.class));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    private String key(String jobId) {
        return "findpill:job:" + jobId;
    }
}

