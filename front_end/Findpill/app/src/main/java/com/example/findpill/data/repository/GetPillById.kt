package com.example.findpill.data.repository

import android.util.Log
import com.example.findpill.data.api.GetPillByIdApi
import com.example.findpill.data.model.PillInfo
import javax.inject.Inject

class GetPillById @Inject constructor(
    private val api: GetPillByIdApi
) {
    suspend fun getPillById(id: String): PillInfo?{
        return try{
            api.getPillById(id)
        }catch (e:Exception){
            Log.e("GetPillById", "API call failed: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}