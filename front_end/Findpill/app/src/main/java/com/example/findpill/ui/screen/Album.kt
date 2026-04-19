package com.example.findpill.ui.screen

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.findpill.ui.component.TopBar
import com.example.findpill.ui.utils.RequestAlbumPermission

@Composable
fun Album(navController: NavController){
    var permissionGranted by remember { mutableStateOf(false) }

    RequestAlbumPermission {
        permissionGranted = true
    }

    if (!permissionGranted) {
        Text("앨범 권한을 허용해주세요", textAlign = TextAlign.Center)
        return
    }

    val selected = remember { mutableStateOf<List<Uri>>(emptyList())}

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ){  uris: List<Uri> ->
        selected.value = uris.take(2)
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
                "앞면과 뒷면을 골라주세요.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp,
                modifier = Modifier.padding(vertical = 32.dp)
            )
            // 앨범 선택 화면
            if(selected.value.size!=2){
                Button(
                    onClick = {imagePickerLauncher.launch("image/*")},
                    modifier = Modifier
                        .padding(vertical = 32.dp)
                        .fillMaxWidth(0.8f)
                ){
                    Text("사진 선택", fontWeight= FontWeight.Bold, fontSize= 20.sp, color = MaterialTheme.colorScheme.onTertiary)
                }
            }
            Row( // 선택한 사진 미리보기
                modifier = Modifier.fillMaxWidth(0.9f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                selected.value.forEach { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
            }
            if(selected.value.size==2){ // 두 개 다 선택하면 버튼 출력
                Button(
                    onClick = {
                        navController.currentBackStackEntry?.savedStateHandle?.apply{
                            set("temp_pill1", selected.value[0].toString())
                            set("temp_pill2", selected.value[1].toString())
                        }
                        navController.navigate("confirm")
                    },
                    modifier = Modifier.fillMaxWidth(0.8f).padding(vertical= 24.dp)
                ) {
                    Text("\uD83D\uDDBC\uFE0F 다음으로", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = MaterialTheme.colorScheme.onTertiary)
                }
            }
        }
    }
}