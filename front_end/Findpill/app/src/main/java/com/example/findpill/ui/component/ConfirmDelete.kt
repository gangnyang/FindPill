package com.example.findpill.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ConfirmDelete(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
){
    AlertDialog(
        onDismissRequest = {onDismiss()},
        title = { Text("정말 삭제하겠습니까?")},
        text = { Text("삭제 시 되돌릴 수 없습니다.")},
        confirmButton = {
            Text(
                "삭제",
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable{ onConfirm() }.padding(start = 24.dp)
            )
        },
        dismissButton = {
            Text(
                "취소",
                color = Color.Black,
                modifier = Modifier.clickable{ onDismiss() }
            )
        }
    )
}