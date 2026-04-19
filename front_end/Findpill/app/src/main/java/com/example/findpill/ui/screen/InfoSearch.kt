package com.example.findpill.ui.screen

import ColorChips
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.findpill.data.model.InfoSearchRequest
import com.example.findpill.ui.component.SelectableBox
import com.example.findpill.ui.component.TopBar
import com.example.findpill.ui.viewmodel.InfoSearchViewModel


@Composable
fun InfoSearch(navController: NavController){
    var print1 by remember {mutableStateOf("")}
    var print2 by remember { mutableStateOf("") }

    var Form by remember { mutableStateOf("전체") }
    var Shape by remember { mutableStateOf("전체") }
    var Color by remember { mutableStateOf("전체") }
    var Divided by remember { mutableStateOf("전체") }

    val formOption = listOf("정제", "경질캡슐", "연질캡슐", "기타", "전체")
    val shapeOption = listOf("원형", "타원형", "장방형", "삼각형", "팔각형", "전체")
    val colorOption = listOf("하양", "노랑", "주황", "분홍", "빨강", "초록", "파랑", "보라", "회색", "검정", "전체")
    val dividedOption = listOf("없음", "+형", "-형", "기타", "전체")


    val viewModel: InfoSearchViewModel = hiltViewModel()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current

    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.secondary)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item{
                TopBar(
                    title = "알약 식별 검색",
                    onBackClick = { navController.popBackStack() }
                )
            }

            item{
                OutlinedTextField(
                    value = print1,
                    onValueChange = { print1 = it },
                    label = { Text("각인 문자 1", color = MaterialTheme.colorScheme.onSurface) },
                    modifier = Modifier.fillMaxWidth().padding(20.dp)
                )
            }


            item{
                OutlinedTextField(
                    value = print2,
                    onValueChange = {print2 = it},
                    label = {Text("각인 문자 2", color = MaterialTheme.colorScheme.onSurface)},
                    modifier = Modifier.fillMaxWidth().padding(20.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            item{
                Column(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text("제형(모르면 전체 선택)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    SelectableBox(options = formOption, selected = Form){
                        Form = it
                    }
                    Text("모양", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    SelectableBox(options = shapeOption, selected = Shape){
                        Shape = it
                    }
                    Text("색상", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    ColorChips(colorOptions = colorOption, selected = Color){
                        Color = it
                    }
                    Text("분할선", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    SelectableBox(options = dividedOption, selected = Divided){
                        Divided = it
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            val request = InfoSearchRequest(
                                color = if (Color == "전체") null else Color,
                                print_front = print1.takeIf { it.isNotBlank() },
                                print_back = print2.takeIf { it.isNotBlank() },
                                shape = if (Shape == "전체") null else Shape,
                                //divided = if (Divided == "전체") null else Divided,
                                //form = if (Form == "전체") null else Form
                            )

                            viewModel.search(request){
                                val idList = viewModel.result.value.pill.filterNotNull().map {it.idx}
                                val status = viewModel.result.value.status
                                navController.currentBackStackEntry?.savedStateHandle?.set("pill_ids", idList)
                                navController.currentBackStackEntry?.savedStateHandle?.set("status", status)
                                navController.navigate("result")
                            }
                                  },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(top = 24.dp, bottom = 48.dp)
                    ) {
                        Text("✔\uFE0F 알약 검색하기", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = MaterialTheme.colorScheme.onTertiary)
                    }
                    if (loading) {
                        CircularProgressIndicator()
                    }
                    error?.let {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                }
            }



        }
    }
}