// ?뱚 service/PillService.java
package com.example.med_classification.service;

import com.example.med_classification.model.dto.request.PillDetectionRequestDto;
import com.example.med_classification.model.dto.request.PillInfoRequestDto;
import com.example.med_classification.model.dto.request.PillLookupRequestDto;
import com.example.med_classification.model.dto.response.PillLookupResponseDto;
import com.example.med_classification.model.entity.Drug;
import com.example.med_classification.repository.DrugRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.text.similarity.LevenshteinDistance;

@Service
@RequiredArgsConstructor
public class PillService {


    private final DrugRepository drugRepository;
    private static final int MAX_PRELIMINARY_RESULTS = 10;

    public PillLookupResponseDto findBestMatch(PillDetectionRequestDto request) {
        String label = request.getLabel();
        String clazz = request.getClazz();

        if (clazz == null || !clazz.contains("_")) {
            throw new RuntimeException("?뺤떇???섎せ??class?낅땲??");
        }

        String[] parts = clazz.split("_");
        String color = parts[0];
        String shape = parts[1];

        String front = request.getFront();
        String back = request.getBack();

        LevenshteinDistance distance = new LevenshteinDistance();

        List<Drug> allDrugs = drugRepository.findAll();

        // 1. color + shape + front + back ?뺥솗 ?쇱튂
        for (Drug drug : allDrugs) {
            if (color.equals(drug.getColor()) &&
                    shape.equals(drug.getShape()) &&
                    front.equals(drug.getPrintFront()) &&
                    (back == null || back.equals(drug.getPrintBack()))) {
                return PillLookupResponseDto.from(drug, label);
            }
        }

        // 2. ?몄쭛嫄곕━ 怨꾩궛 諛??꾨낫 ?섏쭛
        List<Drug> candidatesWithinDistance = new ArrayList<>();
        Drug closestDrug = null;
        int bestScore = Integer.MAX_VALUE;

        for (Drug drug : allDrugs) {
            int frontScore = front != null ? distance.apply(front, Optional.ofNullable(drug.getPrintFront()).orElse("")) : 100;
            int backScore = 0;

            // back??null???꾨땺 寃쎌슦?먮쭔 鍮꾧탳
            if (back != null) {
                backScore = distance.apply(back, Optional.ofNullable(drug.getPrintBack()).orElse(""));
            }

            int totalScore = frontScore + (back != null ? backScore : 0);

            if (totalScore < bestScore) {
                bestScore = totalScore;
                closestDrug = drug;
            }

            // frontScore? backScore 紐⑤몢 ?좏슚??寃쎌슦留??꾨낫濡?異붽?
            if (frontScore <= 3 && (back == null || backScore <= 3)) {
                candidatesWithinDistance.add(drug);
            }
        }

        // 3. ?몄쭛嫄곕━ ?꾨꼍 ?쇱튂 (front, back 紐⑤몢)
        for (Drug drug : candidatesWithinDistance) {
            int f = distance.apply(front, Optional.ofNullable(drug.getPrintFront()).orElse(""));
            int b = (back != null) ? distance.apply(back, Optional.ofNullable(drug.getPrintBack()).orElse("")) : 0;
            if (f == 0 && (back == null || b == 0)) {
                return PillLookupResponseDto.from(drug, label);
            }
        }

        // 4. front or back 以??섎굹留??꾨꼍 ?쇱튂
        for (Drug drug : candidatesWithinDistance) {
            int f = distance.apply(front, Optional.ofNullable(drug.getPrintFront()).orElse(""));
            int b = (back != null) ? distance.apply(back, Optional.ofNullable(drug.getPrintBack()).orElse("")) : 100;
            if (f == 0 || b == 0) {
                return PillLookupResponseDto.from(drug, label);
            }
        }

        // 5. ?몄쭛嫄곕━ ?꾨낫 以?color + shape ?쇱튂
        for (Drug drug : candidatesWithinDistance) {
            if (color.equals(drug.getColor()) && shape.equals(drug.getShape())) {
                return PillLookupResponseDto.from(drug, label);
            }
        }

        // 6. 洹몃옒???놁쑝硫?媛??媛源뚯슫 ?꾨낫 諛섑솚
        if (closestDrug != null) {
            return PillLookupResponseDto.from(closestDrug, label);
        }

        throw new RuntimeException("?좎궗???뚯빟??李얠쓣 ???놁뒿?덈떎.");
    }





    public PillLookupResponseDto findById(Integer id) {
        Drug drug = drugRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("?쎌쓣 李얠쓣 ???놁뒿?덈떎."));
        return new PillLookupResponseDto(drug);
    }


//    public List<PillLookupResponseDto> findByIds(List<String> ids) {
//        List<Drug> drugs = drugRepository.findAllById(ids);
//        return drugs.stream()
//                .map(PillLookupResponseDto::new)
//                .toList();
//    }

    public List<PillLookupResponseDto> findByInfo(PillInfoRequestDto dto) {
        List<Drug> results = drugRepository.findByMultipleAttributes(
                dto.getColor(),
                dto.getPrint_front(),
                dto.getPrint_back(),
                dto.getShape(),
                dto.getCompany()
        );

        if (results.isEmpty()) {
            throw new RuntimeException("?뚯빟 ?뺣낫瑜?李얠쓣 ???놁뒿?덈떎.");
        }

        return results.stream()
                .limit(10)
                .map(PillLookupResponseDto::new)
                .collect(Collectors.toList());
    }

    public List<PillLookupResponseDto> findByOcrSimilarity(List<String> frontTexts, List<String> backTexts, double threshold) {
        List<String> normalizedFront = normalizeTokens(frontTexts);
        List<String> normalizedBack = normalizeTokens(backTexts);

        if (normalizedFront.isEmpty() && normalizedBack.isEmpty()) {
            return List.of();
        }

        List<ScoredDrug> scored = new ArrayList<>();
        for (Drug drug : drugRepository.findAll()) {
            double frontScore = bestSimilarity(normalizedFront, normalize(drug.getPrintFront()));
            double backScore = bestSimilarity(normalizedBack, normalize(drug.getPrintBack()));
            double score = Math.max(frontScore, backScore);
            if (score >= threshold) {
                scored.add(new ScoredDrug(drug, score));
            }
        }

        return scored.stream()
                .sorted(Comparator.comparingDouble(ScoredDrug::score).reversed())
                .limit(MAX_PRELIMINARY_RESULTS)
                .map(scoredDrug -> new PillLookupResponseDto(scoredDrug.drug()))
                .collect(Collectors.toList());
    }

    private List<String> normalizeTokens(List<String> tokens) {
        if (tokens == null) {
            return List.of();
        }
        return tokens.stream()
                .filter(Objects::nonNull)
                .map(this::normalize)
                .filter(s -> !s.isBlank())
                .distinct()
                .toList();
    }

    private String normalize(String value) {
        return value == null ? "" : value.replaceAll("[^A-Za-z0-9]", "").toUpperCase(Locale.ROOT);
    }

    private double bestSimilarity(List<String> candidates, String target) {
        if (candidates.isEmpty() || target.isBlank()) {
            return 0.0;
        }
        LevenshteinDistance distance = new LevenshteinDistance();
        double best = 0.0;
        for (String candidate : candidates) {
            int maxLen = Math.max(candidate.length(), target.length());
            if (maxLen == 0) {
                continue;
            }
            int dist = distance.apply(candidate, target);
            double similarity = 1.0 - ((double) dist / maxLen);
            if (similarity > best) {
                best = similarity;
            }
        }
        return best;
    }

    private record ScoredDrug(Drug drug, double score) {}





}

