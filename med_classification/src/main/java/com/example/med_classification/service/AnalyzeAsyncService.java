package com.example.med_classification.service;

import com.example.med_classification.model.dto.request.PillDetectionRequestDto;
import com.example.med_classification.model.dto.response.AnalyzeJobStateDto;
import com.example.med_classification.model.dto.response.PillLookupResponseDto;
import com.example.med_classification.model.dto.response.YoloWorkerResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnalyzeAsyncService {
    private final AiWorkerClientService aiWorkerClientService;
    private final RedisJobService redisJobService;
    private final PillService pillService;

    @Async
    public void runYoloJob(String jobId, byte[] frontBytes, byte[] backBytes) {
        Optional<AnalyzeJobStateDto> existing = redisJobService.get(jobId);
        if (existing.isEmpty()) {
            return;
        }

        AnalyzeJobStateDto running = existing.get();
        running.setPhase("YOLO_RUNNING");
        redisJobService.save(running);

        try {
            YoloWorkerResponseDto yolo = aiWorkerClientService.requestYolo(frontBytes, backBytes);
            List<PillDetectionRequestDto> detections = yolo != null && yolo.getResults() != null ? yolo.getResults() : List.of();
            List<PillLookupResponseDto> finalResults = new ArrayList<>();
            int successCount = 0;

            for (PillDetectionRequestDto detection : detections) {
                try {
                    finalResults.add(pillService.findBestMatch(detection));
                    successCount++;
                } catch (RuntimeException ex) {
                    finalResults.add(null);
                }
            }

            String status = successCount == detections.size() ? "2" : (successCount > 0 ? "1" : "0");
            AnalyzeJobStateDto done = AnalyzeJobStateDto.builder()
                    .jobId(jobId)
                    .phase("DONE")
                    .status(status)
                    .preliminaryResults(running.getPreliminaryResults())
                    .finalResults(finalResults)
                    .build();
            redisJobService.save(done);
        } catch (Exception e) {
            AnalyzeJobStateDto failed = AnalyzeJobStateDto.builder()
                    .jobId(jobId)
                    .phase("FAILED")
                    .status("0")
                    .preliminaryResults(running.getPreliminaryResults())
                    .finalResults(running.getFinalResults())
                    .errorMessage(e.getMessage())
                    .build();
            redisJobService.save(failed);
        }
    }
}

