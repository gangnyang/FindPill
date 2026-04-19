package com.example.findpill.ui.screen

import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.findpill.ui.component.TopBar
import com.example.findpill.ui.viewmodel.CameraPreview
import com.example.findpill.ui.utils.RequestCameraPermission
import java.io.File



@Composable
fun Photoing(navController: NavController){
    var permissionGranted by remember { mutableStateOf(false) }

    RequestCameraPermission {
        permissionGranted = true
    }

    if (!permissionGranted) {
        Text("카메라 권한을 허용해주세요")
        return
    }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cnt = remember { mutableStateOf(0)}
    val path = remember { mutableStateOf<String?>(null) }
    val isCameraReady = remember { mutableStateOf(false) }
    val imageCapture = remember {
        ImageCapture.Builder().build()
    }

    fun takePhoto(){
        val filename = "pill_${cnt.value}.jpg"
        val pFile = File(context.cacheDir, filename)

        val output = ImageCapture.OutputFileOptions.Builder(pFile).build()

        imageCapture.takePicture(
            output,
            ContextCompat.getMainExecutor(context),
            object: ImageCapture.OnImageSavedCallback{
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults){
                    if(cnt.value ==0){
                        path.value = pFile.absolutePath
                        cnt.value +=1
                    }
                    else{
                        val spath = pFile.absolutePath

                        navController.currentBackStackEntry?.savedStateHandle?.apply{
                            set("temp_pill1", path.value)
                            set("temp_pill2", spath)
                        }

                        navController.navigate("confirm")
                    }
                }

                override fun onError(exception: ImageCaptureException){
                    Log.e("Camera", "사진 촬영 실패", exception)
                }
            }
        )
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.secondary)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            TopBar(
                title = "알약 사진 촬영",
                onBackClick = {navController.popBackStack()}
            )
            Text(
                text = if (cnt.value == 0) "앞면을 촬영하세요" else "뒷면을 촬영하세요",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp,
                modifier = Modifier.padding(vertical = 32.dp)
            )
            CameraPreview(context, lifecycleOwner, modifier = Modifier.fillMaxWidth().fillMaxHeight(0.6f), imageCapture = imageCapture, onCameraReady ={
                Log.d("Camera", "카메라 준비됨")
                isCameraReady.value = true
            })
            // 촬영하기 버튼
            Box(
                modifier = Modifier
                    .padding(vertical = 32.dp)
                    .size(60.dp)
                    .shadow(6.dp, shape = CircleShape, clip = true)
                    .background(Color.White, CircleShape)
                    .clickable {
                        if(isCameraReady.value){
                            takePhoto()
                            Log.d("촬영", "촬영 버튼 클릭")
                        }else{
                            Log.d("촬영", "카메라 연결 안됨")
                        }
                    }
            )
        }
    }
}
