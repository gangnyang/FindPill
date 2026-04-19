package com.example.findpill.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.findpill.ui.screen.Favorite
import com.example.findpill.ui.viewmodel.FavoriteViewModel

@Composable
fun DetailBottomNavigationBar(navController: NavController, viewModel: FavoriteViewModel) {
    val backStackEntry = navController.currentBackStackEntryAsState().value
    val pillId = backStackEntry?.arguments?.getString("pillId")
    val favorite by viewModel.favorites.collectAsState(initial = emptySet())

    val favoriteChecked = remember(favorite) { pillId != null && favorite.contains(pillId) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary)
            .border(1.dp, MaterialTheme.colorScheme.onSurface),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Button(onClick = {
            navController.popBackStack()
            navController.navigate("main") }
        ) {
            Text("메인으로", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = MaterialTheme.colorScheme.onTertiary)
        }
        if(pillId!=null) {
            FavoriteSwitch(
                title = "⭐ 즐겨찾기 등록",
                isChecked = favoriteChecked,
                pillId = pillId,
                viewModel = viewModel
            )
        }
    }
}