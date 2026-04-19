package com.example.findpill.ui.utils

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

fun ChangeImagePath(context: Context, path: String, partName: String): MultipartBody.Part{
    val request = if(path.startsWith("content://")){
        val uri = Uri.parse(path)
        val input = context.contentResolver.openInputStream(uri)!!
        val bytes = input.readBytes()
        input.close()
        bytes.toRequestBody("image/*".toMediaTypeOrNull())
    } else{
        val file = File(path)
        file.asRequestBody("image/*".toMediaTypeOrNull())
    }

    val filename = if (path.startsWith("content://")){
        "image_${System.currentTimeMillis()}.jpg"
    }else{
        File(path).name
    }

    return MultipartBody.Part.createFormData(partName, filename, request)
}