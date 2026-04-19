package com.example.findpill.data.model

import com.google.gson.annotations.SerializedName

data class PillInfo(
    val image: String,
    val idx: Int,
    val name: String,
    val color: String,
    val material: String,
    val company: String,
    val shape: String,
    val print_front: String,
    val print_back: String,
    val warning: String,
    val effect: String,
    val dosage: String,
    val label: String? = null
)
/*

data class PillInfo(
    val pill_id: String,
    val pill_name: String,
    val description: String,
    val manufacturer: String,
    val main_ingredient: String,
    val image_url: String,
    val ocr_front: String,
    val ocr_back: String
)


 */

data class PillSearchResponse(
    val status: String,
    val phase: String? = null,
    @SerializedName("job_id")
    val jobId: String? = null,

    @SerializedName(value = "pill", alternate = ["results"])
    val pill: List<PillInfo?> = emptyList()
)

