package com.example.findpill.ui.utils

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

@Composable
fun RequestCameraPermission(onGranted: () -> Unit) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onGranted()
        } else {
            Toast.makeText(context, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(android.Manifest.permission.CAMERA)
    }
}

@Composable
fun RequestAlbumPermission(onGranted: () -> Unit){
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {isGranted ->
        if(isGranted){
            onGranted()
        }else{
            Toast.makeText(context, "앨범 선택 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }

    }
    LaunchedEffect(Unit){
        permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
    }
}