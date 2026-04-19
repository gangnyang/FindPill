package com.example.findpill.data.api

import com.example.findpill.data.model.PillSearchResponse
import com.example.findpill.data.model.UploadResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ImageApi{
    @Multipart
    @POST("api/pill/analyze")
    suspend fun uploadImage(
        @Part front: MultipartBody.Part,
        @Part back: MultipartBody.Part
    ): Response<PillSearchResponse>

    @GET("api/pill/analyze/{jobId}")
    suspend fun pollAnalyze(
        @Path("jobId") jobId: String
    ): Response<PillSearchResponse>
}

