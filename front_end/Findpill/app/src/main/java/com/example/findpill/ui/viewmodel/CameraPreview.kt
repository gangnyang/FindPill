package com.example.findpill.ui.viewmodel

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner

// 카메라 미리보기
@Composable
fun CameraPreview(context: Context, lifecycleOwner: LifecycleOwner, modifier: Modifier = Modifier, imageCapture: ImageCapture, onCameraReady: () -> Unit ) {
    var previewing = remember { PreviewView(context) }
    AndroidView(
        factory = {
            previewing.apply {
                clipToOutline = true
                clipChildren = true
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        modifier = modifier
    ){
        val Pcamera = ProcessCameraProvider.getInstance(context)
        Pcamera.addListener({
            val cameraP = Pcamera.get()

            val preview = Preview.Builder().build().also{
                it.setSurfaceProvider(previewing.surfaceProvider)
            }
            
            val cameraS = CameraSelector.DEFAULT_BACK_CAMERA
            
            try{
                cameraP.unbindAll()
                cameraP.bindToLifecycle(
                    lifecycleOwner,
                    cameraS,
                    preview,
                    imageCapture
                )
                Log.d("Camera", "카메라 바인딩 완료")
                onCameraReady()
            } catch(e:Exception){
                Log.e("Camera", "카메라 바인딩 오류", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }
}