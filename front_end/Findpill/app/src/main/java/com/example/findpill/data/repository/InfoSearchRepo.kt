package com.example.findpill.data.repository

import com.example.findpill.data.api.InfoSearchApi
import com.example.findpill.data.model.InfoSearchRequest
import com.example.findpill.data.model.PillInfo
import com.example.findpill.data.model.PillSearchResponse
import javax.inject.Inject

class InfoSearchRepo @Inject constructor(
    private val api: InfoSearchApi
) {
    suspend fun pillInfoSearch(request: InfoSearchRequest): PillSearchResponse? {
        return try {
            val response = api.infoSearch(request)
            if (response.isSuccessful) {
                response.body()
            } else {
                android.util.Log.e("InfoSearch", "❌ 실패: ${response.code()} - ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            android.util.Log.e("InfoSearch", "❌ 예외 발생: ${e.message}", e)
            null
        }
    }
}