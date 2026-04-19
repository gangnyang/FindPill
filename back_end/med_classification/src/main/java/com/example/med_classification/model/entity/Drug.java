package com.example.med_classification.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "drug")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Drug {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT와 매칭
    private Integer idx;  // 약 고유 ID

    @Column(nullable = false, length = 255)
    private String name;  // 약 이름

    @Column(length = 100)
    private String color;  // 색상

    @Column(length = 255)
    private String material;  // 재료

    @Column(length = 255)
    private String company;  // 제조사

    @Column(length = 100)
    private String shape;  // 모양

    @Column(name = "print_front", length = 100)
    private String printFront;  // 앞면 각인

    @Column(name = "print_back", length = 100)
    private String printBack;  // 뒷면 각인

    @Column(columnDefinition = "TEXT")
    private String effect;  // 효과

    @Column(columnDefinition = "TEXT")
    private String dosage;  // 복용법

    @Column(columnDefinition = "TEXT")
    private String warning;  // 주의사항

    @Column(columnDefinition = "TEXT")
    private String image;  // 이미지 URL
}
