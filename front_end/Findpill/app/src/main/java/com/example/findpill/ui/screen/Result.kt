package com.example.findpill.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.findpill.data.model.PillInfo
import com.example.findpill.data.repository.UploadImage
import com.example.findpill.ui.component.Pill
import com.example.findpill.ui.component.TopBar
import com.example.findpill.ui.component.dummyPillList
import com.example.findpill.ui.viewmodel.FavoriteViewModel
import kotlinx.coroutines.delay

@Composable
fun Result(navController: NavController){
    var result by remember { mutableStateOf<List<PillInfo>>(emptyList()) }
    val pillIds = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<List<Int>>("pill_ids")
    val initialStatus = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<String>("status")
    val jobId = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<String>("job_id")

    var currentStatus by remember { mutableStateOf(initialStatus ?: "1") }

    val context = LocalContext.current
    val uploadRepo = remember { UploadImage(context) }
    val viewmodel: FavoriteViewModel = hiltViewModel()

    Log.d("ResultScreen", "ids=$pillIds, status=$initialStatus, jobId=$jobId")

    LaunchedEffect(pillIds){
        if(!pillIds.isNullOrEmpty()){
            result = viewmodel.loadPillsByIds(pillIds)
        }
    }

    LaunchedEffect(jobId) {
        if (jobId == null) return@LaunchedEffect

        while (true) {
            if (currentStatus == "2") break
            delay(3000L)

            val polled = uploadRepo.poll(jobId) ?: continue
            currentStatus = polled.status

            val polledResult = polled.pill
                .filterNotNull()
                .filter { it.name.isNotBlank() && it.company.isNotBlank() && it.idx != 0 }
                .sortedBy { it.label?.toIntOrNull() }

            if (polledResult.isNotEmpty()) {
                result = polledResult
            }

            if (polled.phase == "DONE" || polled.status == "2") {
                break
            }
        }
    }

    val whichpill = if(result.isEmpty()) dummyPillList else result
    val resultisnull = result.isEmpty()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
    ){
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            TopBar(
                title = "${if(pillIds.isNullOrEmpty()) 0 else pillIds.size} result",
                onBackClick = {
                    navController.popBackStack()
                    navController.navigate("photosearch")
                }
            )

            val (color, message) = when (currentStatus) {
                "2" -> Color.Green to "Final result is ready."
                "1" -> Color.Yellow to "OCR result first. YOLO finalizing..."
                "0" -> Color.Red to "No reliable result yet."
                else -> Color.Gray to "Checking status..."
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(color, shape = CircleShape)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = message,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if(resultisnull){
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No search result yet.",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 26.sp
                    )
                }
            }else{
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ){
                    items(whichpill) { pill ->
                        Pill(pill = pill, onClick = {navController.navigate("detail/${pill.idx}")})
                    }
                }
            }
        }
    }
}

