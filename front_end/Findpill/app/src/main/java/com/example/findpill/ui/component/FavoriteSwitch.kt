package com.example.findpill.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.findpill.ui.viewmodel.CalendarViewModel
import com.example.findpill.ui.viewmodel.FavoriteViewModel

@Composable
fun FavoriteSwitch(
    title: String,
    isChecked: Boolean,
    pillId: String,
    viewModel: FavoriteViewModel
){
    var checked by remember { mutableStateOf(isChecked) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(if (isChecked) "⭐ 등록됨" else "☆ 등록하기",
            fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
        Switch(
            checked = checked,
            onCheckedChange = {
                checked = it
                if (it) viewModel.addFavorite(pillId)
                else viewModel.removeFavorite(pillId)
            }
        )
    }
}