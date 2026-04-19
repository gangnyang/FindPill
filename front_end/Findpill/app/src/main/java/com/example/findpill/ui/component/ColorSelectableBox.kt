import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ColorChips(
    colorOptions: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    val colorMap = mapOf(
        "하양" to Color.White,
        "노랑" to Color(0xFFFFFF99),
        "주황" to Color(0xFFFFCC80),
        "분홍" to Color(0xFFFFB6C1),
        "빨강" to Color.Red,
        "초록" to Color.Green,
        "파랑" to Color.Blue,
        "보라" to Color(0xFFB39DDB),
        "회색" to Color.Gray,
        "검정" to Color.Black,
        "전체" to MaterialTheme.colorScheme.onSurface
    )

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 10.dp)
    ) {
        colorOptions.forEach { colorName ->
            val backgroundColor = colorMap[colorName] ?: MaterialTheme.colorScheme.onSurface
            FilterChip(
                selected = selected == colorName,
                onClick = { onSelect(colorName) },
                label = {
                    Text(
                        colorName,
                        color = if (backgroundColor.luminance() < 0.5f)
                            Color.White else Color.Black
                    )
                },
                modifier = Modifier
                    .defaultMinSize(minWidth = 72.dp)
                    .height(36.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = backgroundColor.copy(alpha = 1.0f),
                    containerColor = backgroundColor.copy(alpha = 0.4f)
                )
            )
        }
    }
}
