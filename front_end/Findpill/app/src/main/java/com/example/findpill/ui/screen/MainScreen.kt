package com.example.findpill.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.findpill.R
import com.example.findpill.ui.viewmodel.panelviewModel

data class Panel( // 패널 내부 컨텐츠 구조
    val title: String,
    val description: String,
    val image : Int,
    val nav : String
)

val Paneldetail = listOf( // 내부 컨텐츠 내용
    Panel("알약 사진 검색", "사진으로 알약을 검색할 수 있어요", R.drawable.main1, "photosearch"),
    Panel("알약 식별 검색", "색상, 모양 등으로 알약을 검색할 수 있어요", R.drawable.main2, "infosearch"),
    Panel("알약 즐겨찾기⭐", "즐겨찾기한 알약을 모아볼 수 있어요", R.drawable.main3, "favorite"),
    Panel("알약 캘린더", "자주 먹는 알약을 알림 받아 보세요!", R.drawable.main4, "calendar")
)

@Composable
fun MainScreen(navController: NavController){
    val viewModel: panelviewModel = viewModel()
    val showpanel by viewModel.showpanel.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.showingpanel()
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Image(
                    painter = painterResource(id = R.drawable.pill1), contentDescription = null,
                    modifier = Modifier.fillMaxWidth(0.7f),
                    contentScale = ContentScale.Crop
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Image(
                    painter = painterResource(id = R.drawable.pill2), contentDescription = null,
                    modifier = Modifier.fillMaxWidth(0.7f),
                    contentScale = ContentScale.Crop
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Image(
                    painter = painterResource(id = R.drawable.pill3), contentDescription = null,
                    modifier = Modifier.fillMaxWidth(0.7f),
                    contentScale = ContentScale.Crop
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Image(
                    painter = painterResource(id = R.drawable.pill4), contentDescription = null,
                    modifier = Modifier.fillMaxWidth(0.7f),
                    contentScale = ContentScale.Crop
                )
            }
        }

        AnimatedVisibility( // 하단 패널이 애니메이션 처럼 올라옴
            visible = showpanel,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ){
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .shadow(elevation = 4.dp)
                    .background(MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
            ){
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = PaddingValues(vertical = 24.dp)
                ){
                    items(Paneldetail) { card -> PanelCard(card, navController) }
                }
            }
        }
    }
}

@Composable
fun PanelCard(card: Panel, navController: NavController){ // 패널 내의 내용물을 정리하는 컴포저블
    Box(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(240.dp)
            .clickable{
                      navController.navigate(card.nav)
            },
        contentAlignment = Alignment.TopStart
    ){
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    card.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    contentDescription = "Next",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight()
                    .padding(top = 10.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 30.dp,
                            bottomStart = 30.dp,
                            bottomEnd = 30.dp
                        )
                    ).background(MaterialTheme.colorScheme.onPrimary)
            ) {
                Image(
                    painter = painterResource(id = card.image),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize(),
                )
                Box(
                    modifier = Modifier.fillMaxWidth().align(Alignment.BottomStart)
                        .background(MaterialTheme.colorScheme.onPrimary)
                        .padding(12.dp)
                ) {
                    Text(
                        card.description,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}