package com.example.findpill.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.findpill.ui.component.TopBar

@Composable
fun Confirm2(navController: NavController){
    val pill1 = navController.previousBackStackEntry?.savedStateHandle?.get<String>("temp_pill1")
    val pill2 = navController.previousBackStackEntry?.savedStateHandle?.get<String>("temp_pill2")
    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.secondary)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            TopBar(
                title = "알약 사진 선택",
                onBackClick = {navController.navigate("photosearch")}
            )
            Text(
                "두 개의 사진이 맞나요?",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp,
                modifier = Modifier.padding(vertical = 32.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                // 촬영한 두 개의 사진 출력
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        "앞면",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                    pill1?.let{
                        Image(
                            painter = rememberAsyncImagePainter(model = it),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth(0.95f).aspectRatio(1f)
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        "뒷면",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                    pill2?.let{
                        Image(
                            painter = rememberAsyncImagePainter(model = it),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth(0.95f).aspectRatio(1f)
                        )
                    }
                }
            }

            navController.currentBackStackEntry?.savedStateHandle?.apply{
                set("pill1", pill1)
                set("pill2", pill2)
            }

            Button(
                onClick = { navController.navigate("album") },
                modifier = Modifier.fillMaxWidth(0.8f).padding(vertical = 24.dp)
            ) {
                Text("\uD83D\uDDBC\uFE0F 다시 고르기", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = MaterialTheme.colorScheme.onTertiary)
            }

            Button(
                onClick = { navController.navigate("loading") },
                modifier = Modifier.fillMaxWidth(0.8f).padding(vertical = 24.dp)
            ) {
                Text("✔\uFE0F 알약 검색하기", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = MaterialTheme.colorScheme.onTertiary)
            }
        }

    }
}