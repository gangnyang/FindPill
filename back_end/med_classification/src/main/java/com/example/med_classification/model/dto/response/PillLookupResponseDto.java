package com.example.med_classification.model.dto.response;

import com.example.med_classification.model.entity.Drug;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PillLookupResponseDto {
    private Integer idx;
    private String name;
    private String color;
    private String material;
    private String company;
    private String shape;
    private String print_front;
    private String print_back;
    private String effect;
    private String dosage;
    private String warning;
    private String image;
    private String label;

    public PillLookupResponseDto(Drug drug) {
        this.idx = drug.getIdx();
        this.name = drug.getName();
        this.color = drug.getColor();
        this.material = drug.getMaterial();
        this.company = drug.getCompany();
        this.shape = drug.getShape();
        this.print_front = drug.getPrintFront();
        this.print_back = drug.getPrintBack();
        this.effect = drug.getEffect();
        this.dosage = drug.getDosage();
        this.warning = drug.getWarning();
        this.image = drug.getImage();
    }

    public static PillLookupResponseDto from(Drug drug, String label) {
        return PillLookupResponseDto.builder()
                .label(label)
                .idx(drug.getIdx())
                .name(drug.getName())
                .color(drug.getColor())
                .material(drug.getMaterial())
                .company(drug.getCompany())
                .shape(drug.getShape())
                .print_front(drug.getPrintFront())
                .print_back(drug.getPrintBack())
                .effect(drug.getEffect())
                .dosage(drug.getDosage())
                .warning(drug.getWarning())
                .image(drug.getImage())
                .build();
    }
}
