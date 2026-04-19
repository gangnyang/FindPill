package com.example.findpill.data.api

import com.example.findpill.data.model.InfoSearchRequest
import com.example.findpill.data.model.PillInfo
import com.example.findpill.data.model.PillSearchResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface InfoSearchApi{
    @POST("api/pill/getpillbyinfo")
    suspend fun infoSearch(@Body request: InfoSearchRequest): Response<PillSearchResponse>
}