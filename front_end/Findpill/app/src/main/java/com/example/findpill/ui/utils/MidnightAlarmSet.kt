package com.example.findpill

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.findpill.data.CalendarKeys
import com.example.findpill.data.SettingKeys
import com.example.findpill.data.settingsDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

class MidnightAlarmSet : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val app = context.applicationContext as Application
        val dataStore = app.settingsDataStore

        CoroutineScope(Dispatchers.IO).launch {
            val prefs = dataStore.data.first()
            val alarmEnabled = prefs[SettingKeys.ISALARMED] ?: false
            if (!alarmEnabled) return@launch

            val morningSet = prefs[CalendarKeys.MORNING] ?: emptySet()
            val afternoonSet = prefs[CalendarKeys.AFTERNOON] ?: emptySet()
            val nightSet = prefs[CalendarKeys.NIGHT] ?: emptySet()

            if (morningSet.isNotEmpty()) {
                AlarmUtil.scheduleNotification(context, 9, 0, 1001, "약 복용 알림", "복용할 아침 약이 있습니다")
            }
            if (afternoonSet.isNotEmpty()) {
                AlarmUtil.scheduleNotification(context, 13, 0, 1002, "약 복용 알림", "복용할 점심 약이 있습니다")
            }
            if (nightSet.isNotEmpty()) {
                AlarmUtil.scheduleNotification(context, 20, 0, 1003, "약 복용 알림", "복용할 저녁 약이 있습니다")
            }

            AlarmUtil.scheduleMidnightRefresh(context)
        }
    }
}