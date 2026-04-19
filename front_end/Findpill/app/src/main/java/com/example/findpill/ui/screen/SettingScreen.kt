package com.example.findpill.ui.screen

import android.widget.CalendarView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.findpill.ui.component.ConfirmDelete
import com.example.findpill.ui.component.TopBar
import com.example.findpill.ui.utils.DeleteCalendar
import com.example.findpill.ui.utils.DeleteFavorite
import com.example.findpill.ui.viewmodel.CalendarViewModel
import com.example.findpill.ui.viewmodel.FavoriteViewModel
import com.example.findpill.ui.viewmodel.SettingViewModel
import kotlinx.coroutines.coroutineScope

@Composable
fun SettingScreen(navController: NavController, viewModel: SettingViewModel){
    val alarm by viewModel.isalarmed.collectAsState(initial = false)
    var showDel by remember { mutableStateOf(false) }
    var isfavorite by remember { mutableStateOf(false) }
    var iscalendar by remember { mutableStateOf(false) }

    val fViewModel: FavoriteViewModel = hiltViewModel()
    val cViewModel: CalendarViewModel = hiltViewModel()

    if(showDel){
        ConfirmDelete(
            onDismiss = { showDel = false },
            onConfirm = {
                showDel = false
                if(isfavorite){
                    DeleteFavorite(fViewModel)
                    isfavorite = false
                }else if(iscalendar){
                    DeleteCalendar(cViewModel)
                    iscalendar = false
                }
            }
        )
    }


    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.secondary)
    ){
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopBar(
                title = "설정",
                onBackClick = { navController.popBackStack() }
            )
            Column(
                modifier = Modifier.padding(horizontal = 32.dp)
            ){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("알림 설정", fontWeight = FontWeight.Bold)
                        Text("약을 먹어야 할 때 알림이 울려요", fontSize = 14.sp, color = Color.Gray)
                    }
                    Switch(
                        checked = alarm,
                        onCheckedChange = { viewModel.setAlarm(it) },
                        thumbContent = null
                    )
                }

                Divider(modifier = Modifier.padding(top = 32.dp))

                Text(
                    "즐겨찾기 기록 지우기",
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .clickable { showDel = true; isfavorite = true }
                        .padding(vertical = 32.dp)
                        .drawBehind {
                            drawRect(
                                color = Color(0x33A00000), // 붉은 하이라이트 (투명도 조절 가능)
                                size = size
                            )
                        }
                )

                Divider()

                Text(
                    "캘린더 기록 지우기",
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .clickable { showDel = true; iscalendar = true }
                        .padding(vertical = 32.dp)
                        .drawBehind {
                            drawRect(
                                color = Color(0x33600000), // 붉은 하이라이트 (투명도 조절 가능)
                                size = size
                            )
                        }
                )

                Divider()
            }
        }
    }
}