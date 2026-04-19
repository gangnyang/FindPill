import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

@Composable
fun StatusIndicator(status: String) {
    val (color, message) = when (status) {
        "good" -> Color(0xFF4CAF50) to "알약을 잘 찾아냈습니다."       // 초록
        "soso" -> Color(0xFFFFC107) to "알약 정보가 틀릴 수 있습니다." // 노랑
        "bad"  -> Color(0xFFF44336) to "대부분의 알약 정보가 틀릴 수 있습니다." // 빨강
        else   -> Color.Gray to "알 수 없는 상태입니다."
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, shape = CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = message,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewStatusGood() {
    StatusIndicator(status = "good")
}

@Preview(showBackground = true)
@Composable
fun PreviewStatusSoso() {
    StatusIndicator(status = "soso")
}

@Preview(showBackground = true)
@Composable
fun PreviewStatusBad() {
    StatusIndicator(status = "bad")
}
