package com.example.findpill.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.findpill.data.model.PillInfo
import com.example.findpill.R

@Composable
fun Pill(pill: PillInfo, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(30.dp))
            .padding(vertical=8.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = pill.image,
                    error = painterResource(R.drawable.pill1),
                    placeholder = painterResource(id = R.drawable.pill2)
                ),
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
            Column (
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = pill.name?.ifBlank { "이름 없음" } ?: "이름 없음",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = pill.effect?.ifBlank { "설명 없음" } ?: "설명 없음",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

/*
val dummyPillList = listOf(
    PillInfo(
        id = 1234,
        name = "타이레놀 정 500mg",
        description = "진통 및 해열에 사용되는 약물입니다.",
        image = R.drawable.pill1,
        color = "흰색",
        material = "아세트아미노펜",
        company = "한국얀센",
        shape = "원형",
        print_front = "TYLENOL",
        print_back = "",
        warning = "간 기능 손상 가능성. 과다 복용 주의.",
        effect = "두통, 치통, 발열 등의 증상 완화",
        method = "1일 3~4회, 1회 1정 복용",
        usage = "식후 30분 이내 복용 권장"
    ),
    PillInfo(
        id = 5678,
        name = "세로나민 캡슐",
        description = "우울증 치료에 사용되는 약물입니다.",
        image = R.drawable.pill1,
        color = "연노랑색",
        material = "플루옥세틴",
        company = "한미약품",
        shape = "캡슐형",
        print_front = "SRN",
        print_back = "",
        warning = "졸음, 어지럼증 유발 가능성. 운전 주의.",
        effect = "우울 증상 완화 및 기분 안정",
        method = "1일 1회, 아침 식후 복용",
        usage = "장기 복용 시 의사 지시 필요"
    ),
    PillInfo(
        id = 9012,
        name = "이부프로펜 정 200mg",
        description = "염증 완화 및 해열 작용이 있는 약물입니다.",
        image = R.drawable.pill1,
        color = "흰색",
        material = "이부프로펜",
        company = "대웅제약",
        shape = "타원형",
        print_front = "IBU",
        print_back = "",
        warning = "위장 장애 가능성. 공복 복용 금지.",
        effect = "근육통, 생리통, 감기 증상 완화",
        method = "1일 3회, 1회 1~2정 복용",
        usage = "최대 1일 6정 초과 금지"
    )
)
*/

val dummyPillList = listOf(
    PillInfo(
        image = "https://example.com/images/tylenol.jpg",
        idx = 321,
        name = "타이레놀 정 500mg",
        color = "하양", // 예시 색상
        material = "아세트아미노펜",
        company = "한국얀센",
        shape = "원형", // 예시 형태
        print_front = "TYLENOL",
        print_back = "",
        warning = "과다 복용 주의",
        effect = "진통, 해열",
        dosage = "1회 1정, 1일 3~4회"
    ),
    PillInfo(
        image = "https://example.com/images/seronamin.jpg",
        idx = 322,
        name = "세로나민 캡슐",
        color = "파랑", // 예시 색상
        material = "플루옥세틴",
        company = "한미약품",
        shape = "캡슐형", // 예시 형태
        print_front = "SRN",
        print_back = "",
        warning = "졸음 유발 가능",
        effect = "우울증 치료",
        dosage = "1일 1회, 1정"
    ),
    PillInfo(
        image = "https://example.com/images/ibu.jpg",
        idx = 323,
        name = "이부프로펜 정 200mg",
        color = "분홍", // 예시 색상
        material = "이부프로펜",
        company = "대웅제약",
        shape = "타원형", // 예시 형태
        print_front = "IBU",
        print_back = "",
        warning = "위장 장애 주의",
        effect = "염증 완화, 해열",
        dosage = "1회 1~2정, 필요시 복용"
    )
)
