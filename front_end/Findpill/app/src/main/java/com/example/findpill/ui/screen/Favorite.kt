package com.example.findpill.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.findpill.data.model.PillInfo
import com.example.findpill.ui.component.Pill
import com.example.findpill.ui.component.TopBar
import com.example.findpill.ui.component.dummyPillList
import com.example.findpill.ui.viewmodel.FavoriteViewModel

@Composable
fun Favorite(navController: NavController, viewModel: FavoriteViewModel = hiltViewModel()){

    LaunchedEffect(Unit){
        viewModel.loadFavoritePills()
    }

    val result by viewModel.pillList.collectAsState()

    val whichpill = if(result.isNullOrEmpty()) emptyList() else result

    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.secondary)
    ){
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            TopBar(title = "즐겨찾기", onBackClick = {navController.popBackStack()})

            if (whichpill.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "등록한 즐겨찾기가 없습니다",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 26.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(whichpill) { pill ->
                        Pill(pill = pill, onClick = { navController.navigate("detail/${pill.idx}") })
                    }
                }
            }

        }
    }
}