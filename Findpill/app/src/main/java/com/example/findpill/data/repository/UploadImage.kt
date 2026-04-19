package com.example.findpill.data.repository

import android.content.Context
import android.util.Log
import com.example.findpill.data.api.ImageApi
import com.example.findpill.data.model.PillSearchResponse
import com.example.findpill.ui.utils.ChangeImagePath
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class UploadImage(private val context: Context){
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(40, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://beatmania.app:9998/") // Spring orchestrator
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    private val api = retrofit.create(ImageApi::class.java)

    suspend fun upload(pill1: String, pill2: String): PillSearchResponse? {
        val front = ChangeImagePath(context, pill1, "front")
        val back = ChangeImagePath(context, pill2, "back")

        return try{
            val response: Response<PillSearchResponse> = api.uploadImage(front, back)

            Log.d("UploadImage", "Response code: ${response.code()}")
            Log.d("UploadImage", "Response successful: ${response.isSuccessful}")
            if(response.isSuccessful){
                Log.d("UploadImage", "Response body: ${response.body()}")
                response.body()
            }else{
                Log.e("UploadImage", "Upload failed: ${response.errorBody()?.string()}")
                null
            }
        }catch(e:Exception){
            Log.e("UploadImage", "api connect error", e)
            null
        }
    }

    suspend fun poll(jobId: String): PillSearchResponse? {
        return try {
            val response = api.pollAnalyze(jobId)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            Log.e("UploadImage", "polling error", e)
            null
        }
    }
}

