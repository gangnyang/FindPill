package com.example.findpill.ui.component

import androidx.compose.foundation.layout.Column
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

@Composable
fun TimeSwitch(
    title: String,
    time: String,
    isChecked: Boolean,
    pillId: String,
    viewModel: CalendarViewModel
){
    var checked by remember { mutableStateOf(isChecked) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
        Switch(
            checked = checked,
            onCheckedChange = {
                checked = it
                if (it) viewModel.addPlan(time, pillId)
                else viewModel.removePlan(time, pillId)
            }
        )
    }
}