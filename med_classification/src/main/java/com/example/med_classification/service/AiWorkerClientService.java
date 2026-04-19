package com.example.med_classification.service;

import com.example.med_classification.model.dto.response.OcrWorkerResponseDto;
import com.example.med_classification.model.dto.response.YoloWorkerResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AiWorkerClientService {

    @Value("${app.ai.base-url:http://127.0.0.1:8000}")
    private String aiBaseUrl;

    public OcrWorkerResponseDto requestOcr(byte[] frontBytes, byte[] backBytes) {
        RestTemplate restTemplate = buildRestTemplate(5000, 15000);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = buildMultipartRequest(frontBytes, backBytes);
        ResponseEntity<OcrWorkerResponseDto> response = restTemplate.exchange(
                aiBaseUrl + "/ocr",
                HttpMethod.POST,
                requestEntity,
                OcrWorkerResponseDto.class
        );
        return response.getBody();
    }

    public YoloWorkerResponseDto requestYolo(byte[] frontBytes, byte[] backBytes) {
        RestTemplate restTemplate = buildRestTemplate(5000, 60000);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = buildMultipartRequest(frontBytes, backBytes);
        ResponseEntity<YoloWorkerResponseDto> response = restTemplate.exchange(
                aiBaseUrl + "/yolo",
                HttpMethod.POST,
                requestEntity,
                YoloWorkerResponseDto.class
        );
        return response.getBody();
    }

    private RestTemplate buildRestTemplate(int connectTimeoutMs, int readTimeoutMs) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeoutMs);
        factory.setReadTimeout(readTimeoutMs);
        return new RestTemplate(factory);
    }

    private HttpEntity<MultiValueMap<String, Object>> buildMultipartRequest(byte[] frontBytes, byte[] backBytes) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("front", namedPart(frontBytes, "front.jpg"));
        body.add("back", namedPart(backBytes, "back.jpg"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return new HttpEntity<>(body, headers);
    }

    private HttpEntity<ByteArrayResource> namedPart(byte[] bytes, String filename) {
        ByteArrayResource resource = new ByteArrayResource(bytes) {
            @Override
            public String getFilename() {
                return filename;
            }
        };
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new HttpEntity<>(resource, headers);
    }
}

