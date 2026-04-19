package com.example.findpill.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.findpill.data.repository.UploadImage
import kotlinx.coroutines.delay

@Composable
fun Loading(navController: NavController){
    val pill1 = navController.previousBackStackEntry?.savedStateHandle?.get<String>("temp_pill1")
    val pill2 = navController.previousBackStackEntry?.savedStateHandle?.get<String>("temp_pill2")
    val context = LocalContext.current
    val uploadR = remember { UploadImage(context) }

    var showSuccess by remember { mutableStateOf(false)}
    var showFailure by remember { mutableStateOf(false)}

    LaunchedEffect(Unit){
        if(pill1 != null && pill2 != null){
            try{
                val result = uploadR.upload(pill1, pill2)
                delay(500L)
                showSuccess = true

                val idList = result?.pill
                    ?.filterNotNull()
                    ?.filter{it.name.isNotBlank() && it.company.isNotBlank() && it.idx !=0 }
                    ?.sortedBy{it.label?.toIntOrNull()}
                    ?.map{it.idx}

                val status = result?.status
                val jobId = result?.jobId

                navController.currentBackStackEntry?.savedStateHandle?.apply{
                    set("pill_ids", idList)
                    set("status", status)
                    set("job_id", jobId)
                }

            }catch(e:Exception){
                showFailure = true
                Log.d("UploadImage", "loading upload failed", e)
            }
        }else{
            showFailure = true
            Log.d("UploadImage", "image is null")
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.secondary),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Analyzing pill images...",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
                )
        }

        if(showSuccess){
            AlertDialog(
                onDismissRequest= {},
                title = { Text("Success")},
                text = { Text("Initial OCR result is ready.")},
                confirmButton = {
                    Button(onClick = {
                        showSuccess = false
                        navController.navigate("result")
                    }){
                        Text("OK")
                    }
                }

            )
        }

        if(showFailure){
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Failed")},
                text = {Text("Image upload failed. Please retry.")},
                confirmButton = {
                    Button(onClick = {
                        showFailure = false
                        navController.previousBackStackEntry?.savedStateHandle?.apply {
                            set("temp_pill1", pill1)
                            set("temp_pill2", pill2)
                        }
                        navController.popBackStack()
                    }){
                        Text("OK")
                    }
                }
            )
        }
    }
}

