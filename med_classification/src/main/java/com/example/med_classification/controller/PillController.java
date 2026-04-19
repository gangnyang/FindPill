package com.example.med_classification.controller;

import com.example.med_classification.model.dto.request.PillDetectionRequestDto;
import com.example.med_classification.model.dto.request.PillDetectionBatchRequestDto;
import com.example.med_classification.model.dto.request.PillInfoRequestDto;
import com.example.med_classification.model.dto.response.AnalyzeJobStateDto;
import com.example.med_classification.model.dto.response.AnalyzeResponseDto;
import com.example.med_classification.model.dto.response.OcrWorkerResponseDto;
import com.example.med_classification.model.dto.response.PillLookupResponseDto;
import com.example.med_classification.service.AiWorkerClientService;
import com.example.med_classification.service.AnalyzeAsyncService;
import com.example.med_classification.service.PillService;
import com.example.med_classification.service.RedisJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/api/pill")
@RequiredArgsConstructor
public class PillController {

    private final PillService pillService;
    private final AiWorkerClientService aiWorkerClientService;
    private final AnalyzeAsyncService analyzeAsyncService;
    private final RedisJobService redisJobService;


    @GetMapping("/getpill/{id}")
    public ResponseEntity<Object> getPill(@PathVariable Integer id) {
        try {
            PillLookupResponseDto pillDto = pillService.findById(id);
            return ResponseEntity.ok(pillDto); // ???⑥씪 媛앹껜 洹몃?濡?諛섑솚
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of("message", "?뚯빟 ?뺣낫瑜?李얠쓣 ???놁뒿?덈떎.")
            );
        }
    }





//    @GetMapping("/getpills")
//    public ResponseDto<List<PillLookupResponseDto>> getPills(@RequestParam("ids") List<Integer> ids) {
//        List<PillLookupResponseDto> result = pillService.findByIds(ids);
//        return new ResponseDto<>(true, result);
//    }

    @PostMapping("/getpillbyinfo")
    public ResponseEntity<Object> getPillByInfo(@RequestBody PillInfoRequestDto dto) {
        try {
            List<PillLookupResponseDto> pillDtoList = pillService.findByInfo(dto);
            return ResponseEntity.ok(
                    Map.of("status", "2", "pill", pillDtoList)  // ??status: "2"
            );
        } catch (RuntimeException e) {
            return ResponseEntity.ok(  // ??error ?묐떟??status = "2"
                    Map.of("status", "0", "pill", List.of())   // 鍮?由ъ뒪??諛섑솚
            );
        }
    }




    @PostMapping("/lookup")
    public ResponseEntity<Object> lookup(@RequestBody PillDetectionRequestDto request) {
        PillLookupResponseDto result = pillService.findBestMatch(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/lookup/batch")
    public ResponseEntity<Object> lookupBatch(@RequestBody PillDetectionBatchRequestDto batchRequest) {
        List<PillDetectionRequestDto> items = batchRequest != null ? batchRequest.getItems() : null;
        if (items == null || items.isEmpty()) {
            return ResponseEntity.ok(Map.of("status", 0, "results", List.of()));
        }

        List<PillLookupResponseDto> results = new ArrayList<>();
        int successCount = 0;

        for (PillDetectionRequestDto item : items) {
            try {
                PillLookupResponseDto result = pillService.findBestMatch(item);
                results.add(result);
                successCount++;
            } catch (RuntimeException e) {
                // Keep positional alignment with request items for client-side label mapping.
                results.add(null);
            }
        }

        int status = successCount == items.size() ? 2 : (successCount > 0 ? 1 : 0);
        return ResponseEntity.ok(Map.of("status", status, "results", results));
    }

    @PostMapping("/analyze")
    public ResponseEntity<Object> analyze(@RequestParam("front") MultipartFile front,
                                          @RequestParam("back") MultipartFile back) throws IOException {
        byte[] frontBytes = front.getBytes();
        byte[] backBytes = back.getBytes();
        String jobId = UUID.randomUUID().toString();

        List<PillLookupResponseDto> preliminary = List.of();
        String initialStatus = "0";
        try {
            OcrWorkerResponseDto ocr = aiWorkerClientService.requestOcr(frontBytes, backBytes);
            preliminary = pillService.findByOcrSimilarity(
                    ocr != null ? ocr.getFrontTexts() : List.of(),
                    ocr != null ? ocr.getBackTexts() : List.of(),
                    0.70
            );
            initialStatus = preliminary.isEmpty() ? "0" : "1";
        } catch (Exception ignored) {
            // Keep processing with YOLO async job even if OCR worker is temporarily unavailable.
        }

        AnalyzeJobStateDto initialState = AnalyzeJobStateDto.builder()
                .jobId(jobId)
                .phase("OCR_DONE")
                .status(initialStatus)
                .preliminaryResults(preliminary)
                .finalResults(List.of())
                .build();
        redisJobService.save(initialState);

        analyzeAsyncService.runYoloJob(jobId, frontBytes, backBytes);

        AnalyzeResponseDto response = AnalyzeResponseDto.builder()
                .jobId(jobId)
                .phase("OCR_DONE")
                .status(initialStatus)
                .results(preliminary)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/analyze/{jobId}")
    public ResponseEntity<Object> pollAnalyze(@PathVariable String jobId) {
        return redisJobService.get(jobId)
                .<ResponseEntity<Object>>map(state -> {
                    List<PillLookupResponseDto> results =
                            "DONE".equals(state.getPhase()) ? state.getFinalResults() : state.getPreliminaryResults();
                    AnalyzeResponseDto response = AnalyzeResponseDto.builder()
                            .jobId(state.getJobId())
                            .phase(state.getPhase())
                            .status(state.getStatus())
                            .results(results == null ? List.of() : results)
                            .build();
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        Map.of("status", "0", "message", "Job not found or expired")
                ));
    }






}

