package com.example.findpill.ui.utils

import androidx.compose.runtime.Composable
import com.example.findpill.ui.viewmodel.CalendarViewModel
import com.example.findpill.ui.viewmodel.FavoriteViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun DeleteFavorite(viewModel: FavoriteViewModel){
    CoroutineScope(Dispatchers.IO).launch {
        viewModel.clearFavorite()
    }
}

fun DeleteCalendar(viewModel: CalendarViewModel){
    CoroutineScope(Dispatchers.IO).launch {
        viewModel.clearAllPlan()
    }
}