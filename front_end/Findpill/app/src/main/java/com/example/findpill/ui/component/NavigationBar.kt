package com.example.findpill.ui.component

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavController) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colorScheme.onSecondary
    ) {
        val currentScreen = navController.currentBackStackEntryAsState().value?.destination?.route

        BottomNavigationItem(
            selected = currentScreen == "main",
            onClick = { navController.navigate("main") {
                launchSingleTop = true
            } },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("메인", fontSize = 12.sp) }
        )
        BottomNavigationItem(
            selected = currentScreen == "favorite",
            onClick = { navController.navigate("favorite") {
                launchSingleTop = true
            } },
            icon = { Icon(Icons.Default.Star, contentDescription = "Star") },
            label = { Text("즐겨찾기", fontSize = 12.sp) }
        )
        BottomNavigationItem(
            selected = currentScreen == "setting",
            onClick = { navController.navigate("setting") {
                launchSingleTop = true
            } },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Setting") } ,
            label = { Text("설정", fontSize = 12.sp) }
        )
    }
}